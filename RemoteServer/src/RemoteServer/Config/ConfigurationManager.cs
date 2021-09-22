using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Logging;
using RemoteServer.Remotes;

namespace RemoteServer.Config
{
    public class ConfigurationManager : IConfigurationManager
    {
        private String configFile;
        private DateTime timetamp;
        private DateTime lastCheck;
        private RootConfig config;
        private ILogger logger;
        private Dictionary<String, IRemoteTarget> remotes;
        private static Dictionary<String, IRemoteTargetFactory> remoteTypes = new Dictionary<string, IRemoteTargetFactory>();
        private ILoggerFactory loggerFactory;

        static ConfigurationManager()
        {
            remoteTypes["GlobalCache"] = new GlobalCacheRemote.Factory();
            remoteTypes["Telnet"] = new TelnetRemote.Factory();
            remoteTypes["TelnetHTTP"] = new TelnetHttpRemote.HttpFactory();
            remoteTypes["Delay"] = new DelayRemote.Factory();
            remoteTypes["HTTP"] = new HttpRemote.Factory();
            remoteTypes["ROKU"] = new RokuRemote.Factory();
            remoteTypes["EventClient"] = new EventClientRemote.Factory();
            remoteTypes["Panasonic"] = new PanasonicRemote.Factory();
            remoteTypes["Process"] = new ProcessRemote.Factory();
        }

        public ConfigurationManager(IHostingEnvironment env, ILoggerFactory loggerFactory)
        {
            this.configFile = Path.Combine(env.ContentRootPath, "remotes.json");
            this.loggerFactory = loggerFactory;
            this.logger = loggerFactory.CreateLogger<ConfigurationManager>();
            Config.GetType();
        }

        public String configJson(long version)
        {
            if (config.Version == version)
                return JsonConvert.SerializeObject(new RootConfig {Version = version});

            return JsonConvert.SerializeObject(config);
        }

        public RootConfig Config
        {
            get
            {
                DateTime now = DateTime.UtcNow;
                if (now.Subtract(lastCheck) > new TimeSpan(0, 1, 0))
                {
                    lock (configFile)
                    {
                        if (now.Subtract(lastCheck) > new TimeSpan(0, 1, 0))
                        {
                            DateTime current = File.GetLastWriteTime(configFile);
                            if (!current.Equals(timetamp))
                            {
                                timetamp = current;

                                JsonSerializerSettings settings = new JsonSerializerSettings();
                                settings.MissingMemberHandling = MissingMemberHandling.Error;
                                try
                                {
                                    RootConfig caseRoot = JsonConvert.DeserializeObject<RootConfig>(File.ReadAllText(configFile),
                                        settings);

                                    toggleIndexes = new Dictionary<string, int>();

                                    config = new RootConfig();
                                    config.Version = new DateTime().Ticks;
                                    config.CommandData = caseRoot.CommandData;
                                    config.Properties = caseRoot.Properties;
                                    config.RemoteCommands = new Dictionary<string, Dictionary<string, List<RemoteCommand>>>(StringComparer.OrdinalIgnoreCase);
                                    config.Remotes = new Dictionary<string, Dictionary<string, string>>(StringComparer.OrdinalIgnoreCase);

                                    Dictionary<String, IRemoteTarget> newRemotes = new Dictionary<string, IRemoteTarget>(StringComparer.OrdinalIgnoreCase);
                                    foreach (KeyValuePair<String, Dictionary<String, String>> remote in caseRoot.Remotes)
                                    {
                                        try
                                        {
                                            IRemoteTargetFactory factory = remoteTypes[remote.Value["Type"]];
                                            newRemotes[remote.Key] = factory.createTarget(remote.Value,
                                                loggerFactory, this);
                                            config.Remotes[remote.Key] = remote.Value;
                                        }
                                        catch (Exception exc)
                                        {
                                            logger.LogError(null, exc, "Failed to create remote {0} of type {1}", remote.Key, remote.Value["Type"]);
                                        }
                                    }

                                    remotes = newRemotes;

                                    foreach (var pair in caseRoot.RemoteCommands)
                                    {
                                        Dictionary<String, List<RemoteCommand>> data = new Dictionary<string, List<RemoteCommand>>(StringComparer.OrdinalIgnoreCase);
                                        foreach (KeyValuePair<string, List<RemoteCommand>> keyValuePair in pair.Value)
                                        {
                                            data[keyValuePair.Key] = keyValuePair.Value;
                                        }
                                        config.RemoteCommands[pair.Key] = data;
                                    }

                                    logger.LogInformation(new EventId(2), "Loaded configuration");

                                    SortedSet<String> existingCommands = new SortedSet<string>();

                                    foreach (KeyValuePair<string, Dictionary<string, List<RemoteCommand>>> configRemoteCommand in config.RemoteCommands)
                                    {
                                        logger.LogInformation(new EventId(3), "Root Group: {0}", configRemoteCommand.Key);

                                        StringBuilder intents = new StringBuilder();
                                        StringBuilder phrases = new StringBuilder();

                                        const String intentSlotString = "    {{ \"intent\": \"{0}\", \"slots\": [ {{ \"name\": \"Device\", \"type\": \"LIST_OF_DEVICES\" }} ] }},\n";
                                        const String intentString = "    {{ \"intent\": \"{0}\"}},\n";
                                        Regex findWhere = new Regex("_in_(Media_Center|TV|Cable|Xbox|Receiver)$", RegexOptions.IgnoreCase);
                                        Regex findTurnOn = new Regex("^turn_(.*)_on$", RegexOptions.IgnoreCase);

                                        SortedSet<String> commands = new SortedSet<string>(StringComparer.CurrentCultureIgnoreCase);

                                        foreach (KeyValuePair<string, List<RemoteCommand>> keyValuePair in configRemoteCommand.Value)
                                        {
                                            foreach (RemoteCommand command in keyValuePair.Value)
                                                if (!remotes.ContainsKey(command.Remote))
                                                    logger.LogError("Addressing remote {0} that doesn't exist", command.Remote);

                                            if (!configRemoteCommand.Key.StartsWith("Legacy_"))
                                                existingCommands.Add(keyValuePair.Key);

                                            Match match = findWhere.Match(keyValuePair.Key);
                                            if (match.Success)
                                            {
                                                commands.Add(keyValuePair.Key.Substring(0, match.Index + 1));
                                            }
                                            else
                                            {
                                                match = findTurnOn.Match(keyValuePair.Key);
                                                intents.AppendFormat(intentString, keyValuePair.Key);
                                                if (match.Success)
                                                {
                                                    phrases.Append(String.Format("{0} turn on {1}\n", keyValuePair.Key,
                                                        match.Groups[1].Value.Replace("_", " ")));
                                                    phrases.Append(String.Format("{0} watch {1}\n", keyValuePair.Key,
                                                        match.Groups[1].Value.Replace("_", " ")));
                                                }
                                                phrases.Append(String.Format("{0} {1}\n", keyValuePair.Key,
                                                    keyValuePair.Key.Replace("_", " ")));
                                            }
                                        }

                                        foreach (String key in commands)
                                        {
                                            intents.AppendFormat(intentSlotString, key);
                                            phrases.Append(String.Format("{0} {1}\n", key,
                                                key.Replace("_", " ")));
                                            phrases.Append(String.Format("{0} {1}in {{Device}}\n", key,
                                                key.Replace("_", " ")));
                                        }

                                        logger.LogInformation("Alexa Schema:\n{{\n  \"intents\": [\n{0}    {{ \"intent\": \"AMAZON.HelpIntent\" }}\n  ]\n}}", intents);
                                        logger.LogInformation("Alexa Phrases:\n{0}", phrases);
                                    }

                                    StringBuilder builder = new StringBuilder();
                                    foreach (String command in existingCommands)
                                        builder.AppendFormat("<string name=\"cmd_{0}\">{0}</string>\n", command);

                                    logger.LogInformation("Android command strings:\n{0}", builder);
                                }
                                catch (Exception exc)
                                {
                                    logger.LogError(new EventId(1), exc, "Failed to load config");
                                    if (config == null)
                                    {
                                        throw new IOException("Failed to load config file", exc);
                                    }
                                }
                            }
                            lastCheck = now;
                        }
                    }
                }
                return config;
            }
        }

        public string JukeboxUrl
        {
            get
            {
                return Config.Properties["JukeboxUrl"];
            }
        }

        public string JukeboxUser
        {
            get
            {
                return Config.Properties["JukeboxUser"];
            }
        }

        public string JukeboxPassword
        {
            get
            {
                return Config.Properties["JukeboxPassword"];
            }
        }

        public string ConnectionString
        {
            get
            {
                return Config.Properties["ConnectionString"];
            }
        }

        private Dictionary<String, int> toggleIndexes = new Dictionary<string, int>();

        public String getCommandData(String name)
        {
            String[] pieces = name.Split('.');
            if (pieces.Length != 2)
            {
                throw new FormatException("Command name should have {Category}.{Name} format");
            }
            Dictionary<string, List<string>> map;
            if (Config.CommandData.TryGetValue(pieces[0], out map))
            {
                List<String> commands;
                if (map.TryGetValue(pieces[1], out commands))
                {
                    if (commands.Count > 1)
                    {
                        int index;
                        if (toggleIndexes.TryGetValue(name, out index))
                            index = (index + 1) % commands.Count;
                        else
                            index = 0;
                        toggleIndexes[name] = index;

                        return commands[index];
                    }
                    return commands.First();
                }
            }
            throw new FormatException("Can't find command " + name);
        }

        public List<RemoteCommand> getRemoteCommand(String category, String name)
        {
            Dictionary<string, List<RemoteCommand>> map;
            if (Config.RemoteCommands.TryGetValue(category, out map))
            {
                List<RemoteCommand> list;
                if (map.TryGetValue(name, out list))
                {
                    return list;
                }
            }
            throw new FormatException("Couldn't find remote command " + category + "." + name);
        }

        public IRemoteTarget getRemote(string remote)
        {
            return remotes[remote];
        }

        public RokuRemote getRokuRemote(string category)
        {
            foreach (IRemoteTarget remote in remotes.Values)
            {
                RokuRemote rokuRemote = remote as RokuRemote;
                if (rokuRemote != null && rokuRemote.Category == category)
                {
                    return rokuRemote;
                }
            }
            return null;
        }
    }
}