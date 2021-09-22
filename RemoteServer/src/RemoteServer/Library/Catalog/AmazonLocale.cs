using System;

namespace RemoteServer.Library.Catalog
{
    public class AmazonLocale
    {
        public String Country { get; set; }
        public String Language { get; set; }

        public AmazonLocale()
        {
            Country = "US";
            Language = "en";
        }
    }
}
