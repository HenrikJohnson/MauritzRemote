using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using RemoteServer.Config;

namespace RemoteServer.Remotes
{
    public interface IRemoteTargetFactory
    {
        IRemoteTarget createTarget(Dictionary<String, String> options, ILoggerFactory loggerFactory, IConfigurationManager config);
    }
}
