using System.Collections.Generic;

namespace RemoteServer.Library.Catalog
{
    public class AmazonMusicPlaylist : AmazonEntity
    {
        public AmazonMusicPlaylist()
        {
        }

        public AmazonMusicPlaylist(string id, string name)
        {
            this.Id = id;
            this.Names = new List<AmazonString> { new AmazonString(name) };
        }
    }
}