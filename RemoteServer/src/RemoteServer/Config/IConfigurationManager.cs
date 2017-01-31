using System;
using System.Collections.Generic;

namespace RemoteServer.Config
{
    public interface IConfigurationManager
    {
        String getCommandData(String name);
        List<RemoteCommand> getRemoteCommand(String category, String name);
        String configJson(long previousVersion);
    }
}