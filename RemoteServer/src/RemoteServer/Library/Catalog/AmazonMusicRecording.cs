using System.Collections.Generic;

namespace RemoteServer.Library.Catalog
{
    public class AmazonMusicRecording : AmazonMusicAlbum
    {
        public List<AmazonReference> Albums { get; set; }

        public AmazonMusicRecording()
        {
        }

        public AmazonMusicRecording(AmazonRawData data)
        {
            Id = data.Id;
            Names = new List<AmazonString> { new AmazonString(data.Name) };
            Artists = new List<AmazonReference> { new AmazonReference { Id = data.ArtistId, Names = new List<AmazonString> { new AmazonString(data.Artist) } } };
            if (data.AlbumId != null) 
                Albums = new List<AmazonReference> { new AmazonReference { Id = data.AlbumId, Names = new List<AmazonString> { new AmazonString(data.Album) } } };
            Popularity = new AmazonPopularity() { Default = data.Popularity };
        }
    }
}
