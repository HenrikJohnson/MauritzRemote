using System;
using System.Collections.Generic;

namespace RemoteServer.Library.Catalog
{
    public class AmazonEntity : AmazonReference
    {
        public AmazonPopularity Popularity { get; set; }
        public string LastUpdatedTime { get; set; }
        public List<AmazonLocale> Locales { get; set; }
        public Boolean? Deleted { get; set; }

        public AmazonEntity()
        {
            Popularity = new AmazonPopularity();
            LastUpdatedTime = DateTime.UtcNow.ToString("yyyy-MM-ddTHH:mm:ssZ");
            Locales = new List<AmazonLocale>() { new AmazonLocale() };
        }
    }
}