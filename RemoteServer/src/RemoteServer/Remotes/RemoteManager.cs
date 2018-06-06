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
        private IConfigurationManager configurationManager;
        private Task task;
        private ILogger logger;
        private object lck = new Object();

        public RemoteManager(IConfigurationManager configurationManager, ILoggerFactory loggerFactory)
        {
            this.configurationManager = configurationManager;
            this.logger = loggerFactory.CreateLogger<RemoteManager>();
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

                    try
                    {
                        IRemoteTarget remote = configurationManager.getRemote(newCommand.Remote);
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
                                logger.LogInformation(new EventId(4), "Finished {0}.{1}", newCommand.Remote,
                                    newCommand.Command);
                            });
                        }
                        else
                        {
                            Task<Task> newTask = task.ContinueWith(async (t) =>
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
                                logger.LogInformation(new EventId(4), "Finished {0}.{1}", newCommand.Remote,
                                    newCommand.Command);
                            });

                            task = newTask.Unwrap();
                        }
                    }
                    catch (Exception exc)
                    {
                        logger.LogError(new EventId(5), exc, "Unknown remote identifier " + newCommand.Remote);
                    }
                }
            }
            return true;
        }
    }
}