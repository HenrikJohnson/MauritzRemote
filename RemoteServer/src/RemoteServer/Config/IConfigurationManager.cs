using System;
using System.Collections.Generic;
using RemoteServer.Remotes;

namespace RemoteServer.Config
{
    public interface IConfigurationManager
    {
        IRemoteTarget getRemote(String remote);
        String getCommandData(String name);
        List<RemoteCommand> getRemoteCommand(String category, String name);
        String configJson(long previousVersion);
    }
}