using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using RemoteServer.Config;

namespace RemoteServer.Remotes
{
    public class ProcessRemote : IRemoteTarget
    {
        public class Factory : IRemoteTargetFactory
        {
            public IRemoteTarget createTarget(Dictionary<string, string> options, ILoggerFactory loggerFactory, IConfigurationManager config)
            {
                return new ProcessRemote(options["Command"], options["ArgumentPrefix"], loggerFactory.CreateLogger<ProcessRemote>(), config);
            }
        }

        private readonly string command;
        private readonly string argumentPrefix;
        private readonly ILogger logger;
        private readonly IConfigurationManager config;

        public ProcessRemote(String command, string argumentPrefix, ILogger logger, IConfigurationManager config)
        {
            this.command = command;
            this.argumentPrefix = argumentPrefix;
            this.logger = logger;
            this.config = config;
        }

        private Process createProcess(string arguments)
        {
            String commandData = config.getCommandData(arguments);

            Process process = new Process();
            process.StartInfo.FileName = command;
            process.StartInfo.Arguments = argumentPrefix + commandData;
            process.StartInfo.UseShellExecute = false;
            process.StartInfo.CreateNoWindow= true;
            process.StartInfo.RedirectStandardOutput = true;
            process.StartInfo.RedirectStandardError = true;
            logger.LogInformation("Executing: \"{0}\" {1}", command, argumentPrefix + commandData);
            return process;
        }

        public Task<string> sendCommandAsync(string command)
        {
            Process process = createProcess(command);

            StringBuilder output = new StringBuilder();
            process.OutputDataReceived += (sender, data) => {
                lock (output)
                {
                    if (data.Data != null)
                    {
                        logger.LogInformation("STDOUT: {0}", data.Data);
                        output.Append(data.Data + "\n");
                    }
                }
            };
            process.ErrorDataReceived += (sender, data) => {
                lock (output)
                {
                    if (data.Data != null)
                    {
                        logger.LogWarning("STDERR: {0}", data.Data);
                        output.Append(data.Data + "\n");
                    }
                }
            };

            return Task.Run(() =>
            {
                process.Start();
                process.BeginOutputReadLine();
                process.BeginErrorReadLine();
                process.WaitForExit();
                lock (output)
                {
                    logger.LogInformation("Exited {0}", process.ExitCode);
                    return output.ToString();
                }
            });
        }
    }
}