using System;
using System.Threading.Tasks;

namespace RemoteServer.Remotes
{
    public interface IRemoteManager
    {
        Task<String> executeCommands(String category, String name);
        bool scheduleCommand(String category, String name, bool onlyIdle);
    }
}