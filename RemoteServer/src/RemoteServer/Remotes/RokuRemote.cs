using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using RemoteServer.Config;

namespace RemoteServer.Remotes
{
    public class RokuRemote : HttpRemote
    {
        public class Factory : IRemoteTargetFactory
        {
            public IRemoteTarget createTarget(Dictionary<string, string> options, ILoggerFactory loggerFactory, IConfigurationManager config)
            {
                return new RokuRemote(options["BaseUrl"], options["Category"], options["SelectDevice"], loggerFactory, config);
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

        public RokuRemote(string baseUrl, string category, string selectDevice, ILoggerFactory loggerFactory, IConfigurationManager config)
            : base(baseUrl, loggerFactory, config)
        {
            Category = category;
            SelectDevice = selectDevice;
        }

        public async Task sendSearchAsync(string command)
        {
            await sendCommandAsync("POST", "/search/browse?title=" + Uri.EscapeUriString(command) + "&launch=true");
        }

        public async Task sendKeyboardInputAsync(string command)
        {
            foreach (Char c in command)
                await sendCommandAsync("POST", "/keypress/Lit_" + Uri.EscapeUriString(c + ""));
        }
    }
}