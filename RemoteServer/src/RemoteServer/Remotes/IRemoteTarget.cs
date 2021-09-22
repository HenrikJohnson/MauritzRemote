using System;
using System.Threading.Tasks;

namespace RemoteServer.Remotes
{
    public interface IRemoteTarget
    {
        Task<String> sendCommandAsync(String command);
    }
}