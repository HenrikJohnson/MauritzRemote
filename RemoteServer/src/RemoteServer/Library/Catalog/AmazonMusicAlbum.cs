using System.Collections.Generic;

namespace RemoteServer.Library.Catalog
{
    public class AmazonMusicAlbum : AmazonEntity
    {
        public List<string> LanguageOfContent { get; set; }
        public List<AmazonReference> Artists { get; set; }
        public string ReleaseType { get; set; }

        public AmazonMusicAlbum()
        {
        }

        public AmazonMusicAlbum(AmazonRawData data)
        {
            Id = data.AlbumId;
            Names = new List<AmazonString> { new AmazonString(data.Album) };
            if (data.AlbumArtistId != null)
                Artists = new List<AmazonReference> { new AmazonReference { Id = data.AlbumArtistId, Names = new List<AmazonString> { new AmazonString(data.AlbumArtist) } } };
            Popularity = new AmazonPopularity() { Default = data.Popularity };
        }
    }
}
