using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RemoteServer.Library.Catalog
{
    public class AmazonRawData
    {
        public string Id { get; set; }
        public string Name { get; set; }
        public string ArtistId { get; set; }
        public string Artist { get; set; }
        public string AlbumId { get; set; }
        public string Album { get; set; }
        public string AlbumArtistId { get; set; }
        public string AlbumArtist { get; set; }
        public int Popularity { get; set; }
    }
}
