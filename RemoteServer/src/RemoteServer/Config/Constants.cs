using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RemoteServer.Config
{
    public static class Constants
    {
        public const String HomeDomainName = "your.home.domain";
        public const String JukeboxUrl = "https://" + HomeDomainName + "/music/";
        public const String JukeboxUser = "username";
        public const String JukeboxPassword = "password";
        public const String ConnectionString = "server=mysql." + HomeDomainName + ";user id=web;password={yourpassword};port=3306;database=music;SSL Mode=None";
        public const String KodiCredentials = JukeboxUser + ":" + JukeboxPassword;
    }
}
