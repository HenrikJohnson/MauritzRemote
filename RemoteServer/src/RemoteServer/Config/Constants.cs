using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RemoteServer.Config
{
    public static class Constants
    {
        public const String HomeDomainName = "home.henrik.org";
        public const String JukeboxUrl = "https://" + HomeDomainName + "/music/";
        public const String JukeboxUser = "hpj";
        public const String JukeboxPassword = "ru4it2";
        public const String ConnectionString = "server=mysql." + HomeDomainName + ";user id=web;password=web4me;port=3306;database=music;SSL Mode=None";
        public const String KodiCredentials = JukeboxUser + ":" + JukeboxPassword;
    }
}
