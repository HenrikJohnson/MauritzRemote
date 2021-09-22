using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using RemoteServer.Config;

namespace RemoteServer.Remotes
{
    public class EventClientRemote : IRemoteTarget
    {
        public class Factory : IRemoteTargetFactory
        {
            public IRemoteTarget createTarget(Dictionary<string, string> options, ILoggerFactory loggerFactory, IConfigurationManager config)
            {
                return new EventClientRemote(options["Host"], Int32.Parse(options["Port"]), loggerFactory);
            }
        }

        public const int DEFAULT_PORT = 9777;

        private EventClient eventClient;
        private int port;
        private string host;
        private ILogger<EventClientRemote> logger;

        public EventClientRemote(string host, int port, ILoggerFactory loggerFactory)
        {
            this.host = host;
            this.port = port;
            this.logger = loggerFactory.CreateLogger<EventClientRemote>();
        }

        public async Task<String> sendCommandAsync(string command)
        {
            if (eventClient == null)
            {
                eventClient = new EventClient();
                await eventClient.ConnectAsync(host, port);
            }

            int ind = command.IndexOf(':');
            String key = command;
            String map = "KB";
            if (ind >= 0)
            {
                key = command.Substring(0, ind);
                map = command.Substring(ind + 1);
            }

            logger.LogInformation(new EventId(1), "Sending Kodi event {0} {1}", key, map);

            await eventClient.SendButtonAsync(key, map, ButtonFlagsType.BTN_NO_REPEAT);

            return "OK";
        }
    }
}