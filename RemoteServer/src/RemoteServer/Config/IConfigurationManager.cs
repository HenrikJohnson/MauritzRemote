using System;
using System.Collections.Generic;
using RemoteServer.Remotes;

namespace RemoteServer.Config
{
    public interface IConfigurationManager
    {
        IRemoteTarget getRemote(string remote);
        string getCommandData(string name);
        List<RemoteCommand> getRemoteCommand(string category, string name);
        string configJson(long previousVersion);
        RokuRemote getRokuRemote(string category);
        string JukeboxUrl
        {
            get;
        }
        string JukeboxUser
        {
            get;
        }
        string JukeboxPassword
        {
            get;
        }
        string ConnectionString
        {
            get;
        }
    }
}