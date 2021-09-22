using System.Collections.Generic;

namespace RemoteServer.Library.Catalog
{
    public class AmazonCatalog<T> where T : AmazonEntity
    {
        public string Type { get; set; }
        public string Version { get; set; }
        public List<AmazonLocale> Locales { get; set; }

        public List<T> Entities { get; set; }

        public AmazonCatalog()
        {
            Version = "2.0";
            Locales = new List<AmazonLocale> { new AmazonLocale() };
        }
    }
}
