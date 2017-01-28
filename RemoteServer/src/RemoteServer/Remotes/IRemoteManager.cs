using System;

namespace RemoteServer.Remotes
{
    public interface IRemoteManager
    {
        bool scheduleCommand(String category, String name, bool onlyIdle);
    }
}