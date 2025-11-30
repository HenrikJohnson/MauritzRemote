using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using RemoteServer.Config;

namespace RemoteServer.Remotes
{
    public class RokuRemote : HttpRemote
    {
        public new class Factory : IRemoteTargetFactory
        {
            public IRemoteTarget createTarget(Dictionary<string, string> options, ILoggerFactory loggerFactory, IRemoteConfigurationManager config)
            {
                return new RokuRemote(options["BaseUrl"], options["Category"], options["SelectDevice"], options["CommandPrefix"], loggerFactory, config);
            }
        }

        public string Category
        {
            get;
            private set;
        }

        public string SelectDevice
        {
            get;
            private set;
        }

        public RokuRemote(string baseUrl, string category, string selectDevice, string commandPrefix, ILoggerFactory loggerFactory, IRemoteConfigurationManager config)
            : base(baseUrl, commandPrefix, loggerFactory, config)
        {
            Category = category;
            SelectDevice = selectDevice;
        }

        public async Task sendSearchAsync(string command)
        {
            await sendCommandAsync("POST", "/search/browse?title=" + Uri.EscapeDataString(command) + "&launch=true");
        }

        public async Task sendKeyboardInputAsync(string command)
        {
            if (command == null)
                await sendCommandAsync("POST", "/keypress/Lit_" + Uri.EscapeDataString(" "));
            else
            {
                foreach (Char c in command)
                    await sendCommandAsync("POST", "/keypress/Lit_" + Uri.EscapeDataString(c + ""));
            }
        }
    }
}