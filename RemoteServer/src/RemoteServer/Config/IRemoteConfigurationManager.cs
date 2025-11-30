using System;
using System.Collections.Generic;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using RemoteServer.Remotes;

namespace RemoteServer.Config
{
    public interface IRemoteConfigurationManager
    {
        IRemoteTarget getRemote(string remote);
        string getCommandData(string commandPrefix, string name);
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
        void PutState(String key, String value);
        String GetState(String key);
    }
}