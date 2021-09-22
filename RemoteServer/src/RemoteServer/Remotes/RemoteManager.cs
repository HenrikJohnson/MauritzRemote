using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using RemoteServer.Config;

namespace RemoteServer.Remotes
{
    public class RemoteManager : IRemoteManager
    {
        private IConfigurationManager configurationManager;
        private Dictionary<IRemoteTarget, Task<String>> activeTasks = new Dictionary<IRemoteTarget, Task<string>>();
        private ILogger logger;
        private object lck = new Object();

        public RemoteManager(IConfigurationManager configurationManager, ILoggerFactory loggerFactory)
        {
            this.configurationManager = configurationManager;
            this.logger = loggerFactory.CreateLogger<RemoteManager>();
        }

        public async Task<String> executeCommands(String category, String name)
        {
            StringBuilder ret = new StringBuilder();
            foreach (Task<String> task in scheduleCommandTasks(category, name, false))
                ret.Append(await task);

            return ret.ToString();
        }

        private List<Task<String>> scheduleCommandTasks(string category, string name, bool onlyIdle)
        {
            List<RemoteCommand> commands;
            try
            {
                commands = configurationManager.getRemoteCommand(category, name);
            }
            catch (Exception exc)
            {
                logger.LogError(new EventId(3), exc, "Failed to fetch {0}.{1}", category, name);
                return null;
            }

            List<Task<String>> tasks = new List<Task<String>>();
            lock (lck)
            {
                if (onlyIdle)
                {
                    if (activeTasks.Values.Where(t => !t.IsCompleted).Any())
                        return tasks;
                }

                foreach (RemoteCommand newCommand in commands)
                {
                    try
                    {
                        IRemoteTarget remote = configurationManager.getRemote(newCommand.Remote);
                        Task<String> task;
                        if (!activeTasks.TryGetValue(remote, out task))
                        {
                            activeTasks[remote] = Task.Run(async () =>
                            {
                                String ret = "ERROR";
                                try
                                {
                                    logger.LogInformation(new EventId(4), "Executing {0}.{1}", newCommand.Remote,
                                        newCommand.Command);
                                    ret = await remote.sendCommandAsync(newCommand.Command);
                                }
                                catch (Exception exc)
                                {
                                    logger.LogError(new EventId(2), exc, "Failed to send {0} to {1}", newCommand.Command,
                                        newCommand.Remote);
                                }

                                logger.LogInformation(new EventId(4), "Finished {0}.{1}", newCommand.Remote,
                                    newCommand.Command);
                                return ret;
                            });
                        }
                        else
                        {
                            Task<Task<String>> newTask = task.ContinueWith(async (t) =>
                            {
                                String ret = "ERROR";
                                try
                                {
                                    logger.LogInformation(new EventId(4), "Executing {0}.{1}", newCommand.Remote,
                                        newCommand.Command);
                                    ret = await remote.sendCommandAsync(newCommand.Command);
                                }
                                catch (Exception exc)
                                {
                                    logger.LogError(new EventId(2), exc, "Failed to send {0} to {1}", newCommand.Command,
                                        newCommand.Remote);
                                }

                                logger.LogInformation(new EventId(4), "Finished {0}.{1}", newCommand.Remote,
                                    newCommand.Command);
                                return ret;
                            });

                            task = newTask.Unwrap();
                            activeTasks[remote] = task;
                            tasks.Add(task);
                        }
                    }
                    catch (Exception exc)
                    {
                        logger.LogError(new EventId(5), exc, "Unknown remote identifier " + newCommand.Remote);
                    }
                }
            }

            return tasks;
        }

        public bool scheduleCommand(String category, String name, bool onlyIdle)
        {
            if (scheduleCommandTasks(category, name, onlyIdle) == null)
                return false;
            return true;
        }
    }
}