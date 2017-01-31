using System;
using System.Collections.Generic;

namespace RemoteServer.Config
{
    public class RootConfig
    {
        public long Version { get; set; }
        public Dictionary<String, Dictionary<String, String>> CommandData { get; set; }
        public Dictionary<String, Dictionary<String, List<RemoteCommand>>> RemoteCommands { get; set; }
    }
}