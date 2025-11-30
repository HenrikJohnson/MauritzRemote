using Microsoft.Extensions.Logging;
using RemoteServer.Config;
using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Threading.Tasks;
using static Org.BouncyCastle.Math.EC.ECCurve;

namespace RemoteServer.Remotes
{
    public class EventClientRemote : IRemoteTarget
    {
        public class Factory : IRemoteTargetFactory
        {
            public IRemoteTarget createTarget(Dictionary<string, string> options, ILoggerFactory loggerFactory, IRemoteConfigurationManager config)
            {
                return new EventClientRemote(options["Host"], Int32.Parse(options["Port"]), options["CommandPrefix"], loggerFactory, config);
            }
        }

        public const int DEFAULT_PORT = 9777;

        private EventClient eventClient;
        private int port;
        private string host;
        private ILogger<EventClientRemote> logger;
        private IRemoteConfigurationManager config;
        private string commandPrefix;

        public EventClientRemote(string host, int port, String commandPrefix, ILoggerFactory loggerFactory, IRemoteConfigurationManager config)
        {
            this.host = host;
            this.port = port;
            this.logger = loggerFactory.CreateLogger<EventClientRemote>();
            this.config = config;
            this.commandPrefix = commandPrefix;
        }

        public async Task<String> sendCommandAsync(string command)
        {
            if (eventClient == null)
            {
                eventClient = new EventClient();
                await eventClient.ConnectAsync(host, port);
            }

            String commandData = config.getCommandData(commandPrefix, command);

            int ind = commandData.IndexOf(':');
            String key = commandData;
            String map = "KB";
            if (ind >= 0)
            {
                key = commandData.Substring(0, ind);
                map = commandData.Substring(ind + 1);
            }

            logger.LogInformation(new EventId(1), "Sending Kodi event {0} {1}", key, map);

            await eventClient.SendButtonAsync(key, map, ButtonFlagsType.BTN_NO_REPEAT);

            return "OK";
        }
    }
}