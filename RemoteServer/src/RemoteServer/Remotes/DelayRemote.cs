using System;
using System.Collections.Generic;
using System.Globalization;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using RemoteServer.Config;

namespace RemoteServer.Remotes
{
    public class DelayRemote : IRemoteTarget
    {
        public class Factory : IRemoteTargetFactory
        {
            public IRemoteTarget createTarget(Dictionary<string, string> options, ILoggerFactory loggerFactory, IConfigurationManager config)
            {
                return new DelayRemote();
            }
        }

        public async Task<String> sendCommandAsync(string command)
        {
            int duration = Int32.Parse(command, CultureInfo.InvariantCulture);
            await Task.Delay(duration);

            return "OK";
        }
    }
}