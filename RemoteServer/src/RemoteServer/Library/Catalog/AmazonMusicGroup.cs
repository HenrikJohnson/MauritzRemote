using System.Collections.Generic;

namespace RemoteServer.Library.Catalog
{
    public class AmazonMusicGroup : AmazonEntity
    {
        public AmazonMusicGroup()
        {
        }

        public AmazonMusicGroup(AmazonRawData data)
        {
            this.Id = data.ArtistId;
            this.Names = new List<AmazonString> { new AmazonString(data.Artist) };
            Popularity = new AmazonPopularity() { Default = data.Popularity };
        }
    }
}