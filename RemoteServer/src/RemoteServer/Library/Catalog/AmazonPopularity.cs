using System.Collections.Generic;

namespace RemoteServer.Library.Catalog
{
    public class AmazonPopularity
    {
        public int Default { get; set; }

        public List<AmazonPopularityOverride> Overrides { get; set; }

        public AmazonPopularity()
        {
            Default = 100;
        }
    }
}
