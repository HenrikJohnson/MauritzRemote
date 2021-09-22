using System.Collections.Generic;

namespace RemoteServer.Library.Catalog
{
    public class AmazonReference
    {
        public string Id { get; set; }
        public List<AmazonString> Names { get; set; }
        public List<AmazonAlternativeNames> AlternativeNames { get; set; }
    }
}
