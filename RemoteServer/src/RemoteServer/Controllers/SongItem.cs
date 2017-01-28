using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RemoteServer.Controllers
{
    public class SongItem
    {
        public int SongId { get; set; }
        public String Artist { get; set; }
        public String Album { get; set; }
        public String Title { get; set; }
        public int Duration { get; set; }
        public int Played { get; set; }
        public int Voted { get; set; }
        public int TrackNumber { get; set; }
        public int Rating { get; set; }
        public String CoverUrl { get; set; }
    }
}
