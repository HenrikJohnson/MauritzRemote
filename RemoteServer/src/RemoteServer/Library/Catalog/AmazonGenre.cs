using System.Collections.Generic;

namespace RemoteServer.Library.Catalog
{
    public class AmazonGenre : AmazonEntity
    {
        public AmazonGenre()
        {
        }

        public AmazonGenre(string id, string name)
        {
            this.Id = id;
            this.Names = new List<AmazonString> { new AmazonString(name) };
        }
    }
}