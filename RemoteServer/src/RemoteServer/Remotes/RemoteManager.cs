using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using System.Threading.Tasks;
using Microsoft.CodeAnalysis.CSharp;
using Microsoft.Extensions.Logging;
using RemoteServer.Config;

namespace RemoteServer.Remotes
{
    public class RemoteManager : IRemoteManager
    {
        private Dictionary<String, IRemoteTarget> remotes = new Dictionary<string, IRemoteTarget>(); 
        private IConfigurationManager configurationManager;
        private Task task;
        private ILogger logger;
        private object lck = new Object();

        public RemoteManager(IConfigurationManager configurationManager, ILoggerFactory loggerFactory)
        {
            this.configurationManager = configurationManager;
            this.logger = loggerFactory.CreateLogger<RemoteManager>();

            remotes.Add("LivingroomIR1", new GlobalCacheRemote(configurationManager, loggerFactory, "livingroomremote." + Constants.HomeDomainName, 4998, "1:1"));
            remotes.Add("LivingroomIR2", new GlobalCacheRemote(configurationManager, loggerFactory, "livingroomremote." + Constants.HomeDomainName, 4998, "1:2"));
            remotes.Add("LivingroomIR3", new GlobalCacheRemote(configurationManager, loggerFactory, "livingroomremote." + Constants.HomeDomainName, 4998, "1:3"));
            remotes.Add("LivingroomKodiEvent", new EventClientRemote("party." + Constants.HomeDomainName, EventClientRemote.DEFAULT_PORT, loggerFactory));
            remotes.Add("LivingroomKodiRPC", new HttpRemote("http://" + Constants.KodiCredentials + "@party." + Constants.HomeDomainName + ":8080/", loggerFactory, configurationManager));
            remotes.Add("LivingroomRoku", new HttpRemote("http://livingroomroku." + Constants.HomeDomainName + ":8060/", loggerFactory, configurationManager));
            remotes.Add("LivingroomTV", new PanasonicRemote("http://livingroomtv." + Constants.HomeDomainName + ":55000/"));

            remotes.Add("OfficeIR1", new GlobalCacheRemote(configurationManager, loggerFactory, "officeremote." + Constants.HomeDomainName, 4998, "1:1"));
            remotes.Add("OfficeIR2", new GlobalCacheRemote(configurationManager, loggerFactory, "officeremote." + Constants.HomeDomainName, 4998, "1:2"));
            remotes.Add("OfficeIR3", new GlobalCacheRemote(configurationManager, loggerFactory, "officeremote." + Constants.HomeDomainName, 4998, "1:3"));
            remotes.Add("OfficeKodiEvent", new EventClientRemote("work." + Constants.HomeDomainName, EventClientRemote.DEFAULT_PORT, loggerFactory));
            remotes.Add("OfficeKodiRPC", new HttpRemote("http://" + Constants.KodiCredentials + "@work." + Constants.HomeDomainName + ":8080/", loggerFactory, configurationManager));
            remotes.Add("OfficeTV", new PanasonicRemote("http://officetv." + Constants.HomeDomainName + ":55000/"));

            remotes.Add("BedroomIR1", new GlobalCacheRemote(configurationManager, loggerFactory, "officeremote." + Constants.HomeDomainName, 4998, "1:1"));
            remotes.Add("BedroomIR2", new GlobalCacheRemote(configurationManager, loggerFactory, "officeremote." + Constants.HomeDomainName, 4998, "1:2"));
            remotes.Add("BedroomIR3", new GlobalCacheRemote(configurationManager, loggerFactory, "officeremote." + Constants.HomeDomainName, 4998, "1:3"));
            remotes.Add("BedroomKodiEvent", new EventClientRemote("hush." + Constants.HomeDomainName, EventClientRemote.DEFAULT_PORT, loggerFactory));
            remotes.Add("BedroomKodiRPC", new HttpRemote("http://" + Constants.KodiCredentials + "@hush." + Constants.HomeDomainName + ":8080/", loggerFactory, configurationManager));
            remotes.Add("BedroomRoku", new HttpRemote("http://bedroku." + Constants.HomeDomainName + ":8060/", loggerFactory, configurationManager));

            remotes.Add("RemoteServer", new HttpRemote("http://localhost:5000/", loggerFactory, configurationManager));
        }

        public bool scheduleCommand(String category, String name, bool onlyIdle)
        {
            List<RemoteCommand> commands;
            try
            {
                commands = configurationManager.getRemoteCommand(category, name);
            }
            catch (Exception exc)
            {
                logger.LogError(new EventId(3), exc, "Failed to fetch {0}.{1}", category, name);
                return false;
            }

            lock (lck)
            {
                foreach (RemoteCommand newCommand in commands)
                {
                    if (onlyIdle)
                    {
                        if (task != null && !task.IsCompleted)
                            return true;
                    }
                    IRemoteTarget remote;
                    if (remotes.TryGetValue(newCommand.Remote, out remote))
                    {
                        if (task == null)
                        {
                            task = Task.Run(async () =>
                            {
                                try
                                {
                                    logger.LogInformation(new EventId(4), "Executing {0}.{1}", newCommand.Remote,
                                        newCommand.Command);
                                    await remote.sendCommandAsync(newCommand.Command);
                                }
                                catch (Exception exc)
                                {
                                    logger.LogError(new EventId(2), exc, "Failed to send {0} to {1}", newCommand.Command,
                                        newCommand.Remote);
                                }
                                logger.LogInformation(new EventId(4), "Finished {0}.{1}", newCommand.Remote, newCommand.Command);
                            });
                        }
                        else
                        {
                            Task<Task> newTask = task.ContinueWith(async (t) =>
                            {
                                try
                                {
                                    logger.LogInformation(new EventId(4), "Executing {0}.{1}", newCommand.Remote, newCommand.Command);
                                    await remote.sendCommandAsync(newCommand.Command);
                                }
                                catch (Exception exc)
                                {
                                    logger.LogError(new EventId(2), exc, "Failed to send {0} to {1}", newCommand.Command,
                                        newCommand.Remote);
                                }
                                logger.LogInformation(new EventId(4), "Finished {0}.{1}", newCommand.Remote, newCommand.Command);
                            });

                            task = newTask.Unwrap();
                        }
                    }
                    else
                    {
                        logger.LogError("Unknown remote identifier " + newCommand.Remote);
                    }
                }
            }
            return true;
        }
    }
}