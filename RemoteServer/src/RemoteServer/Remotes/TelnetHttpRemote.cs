using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using RemoteServer.Config;

namespace RemoteServer.Remotes
{
    public class TelnetHttpRemote : TelnetRemote
    {
        public class HttpFactory : IRemoteTargetFactory
        {
            public IRemoteTarget createTarget(Dictionary<string, string> options, ILoggerFactory loggerFactory, IConfigurationManager config)
            {
                return new TelnetHttpRemote(options["Host"], Int32.Parse(options["Port"]), options["BaseUrl"], loggerFactory, config);
            }
        }

        private HttpRemote fallbackRemote;
        private ILogger logger;
        private DateTime lastFailure = DateTime.MinValue;
        private int failureTimeout = 60 * 5;

        public TelnetHttpRemote(String hostname, int port, String url, ILoggerFactory loggerFactory, IConfigurationManager config)
            : base(hostname, port, loggerFactory, config)
        {
            fallbackRemote = new HttpRemote(url, loggerFactory, config);
            logger = loggerFactory.CreateLogger<TelnetHttpRemote>();
        }

        public override async Task<String> sendCommandAsync(string command)
        {
            if (lastFailure.AddSeconds(failureTimeout) < new DateTime())
            {
                logger.LogWarning(new EventId(1), "Falling back to HTTP");
                await fallbackRemote.sendCommandAsync(command);
            }
            else
            {
                try
                {
                    await base.sendCommandAsync(command);
                }
                catch (Exception exc)
                {
                    lastFailure = new DateTime();
                    logger.LogWarning(new EventId(1), exc, "Falling back to HTTP");
                    await fallbackRemote.sendCommandAsync(command);
                }
            }
            return "OK";
        }
    }
}