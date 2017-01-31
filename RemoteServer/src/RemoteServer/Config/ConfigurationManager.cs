using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;

namespace RemoteServer.Config
{
    public class ConfigurationManager : IConfigurationManager
    {
        private String configFile;
        private DateTime timetamp;
        private DateTime lastCheck;
        private RootConfig config;
        private ILogger logger;

        public ConfigurationManager(IHostingEnvironment env, ILogger<ConfigurationManager> logger)
        {
            this.configFile = Path.Combine(env.ContentRootPath, "remotes.json");
            this.logger = logger;
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

                                    config = new RootConfig();
                                    config.Version = new DateTime().Ticks;
                                    config.CommandData = caseRoot.CommandData;
                                    config.RemoteCommands = new Dictionary<string, Dictionary<string, List<RemoteCommand>>>(StringComparer.OrdinalIgnoreCase);

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

                                    foreach (KeyValuePair<string, Dictionary<string, List<RemoteCommand>>> configRemoteCommand in config.RemoteCommands)
                                    {
                                        logger.LogInformation(new EventId(3), "Root Group: {0}", configRemoteCommand.Key);
                                        foreach (KeyValuePair<string, List<RemoteCommand>> keyValuePair in configRemoteCommand.Value)
                                        {
                                            logger.LogInformation(new EventId(3), "Command: {0}", keyValuePair.Key);
                                        }
                                    }
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

        public String getCommandData(String name)
        {
            String[] pieces = name.Split('.');
            if (pieces.Length != 2)
            {
                throw new FormatException("Ir command name should have {Category}.{Name} format");
            }
            Dictionary<string, string> map;
            if (Config.CommandData.TryGetValue(pieces[0], out map))
            {
                string command;
                if (map.TryGetValue(pieces[1], out command))
                {
                    return command;
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
    }
}