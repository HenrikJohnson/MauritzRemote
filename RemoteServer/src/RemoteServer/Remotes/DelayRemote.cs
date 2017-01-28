using System;
using System.Globalization;
using System.Threading.Tasks;

namespace RemoteServer.Remotes
{
    public class DelayRemote : IRemoteTarget
    {
        public async Task sendCommandAsync(string command)
        {
            int duration = Int32.Parse(command, CultureInfo.InvariantCulture);
            await Task.Delay(duration);
        }
    }
}