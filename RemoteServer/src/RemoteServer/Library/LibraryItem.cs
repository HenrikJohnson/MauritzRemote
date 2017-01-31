using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RemoteServer.Library
{
    public class LibraryItem
    {
        public String ItemId { get; set; }
        public String Artist { get; set; }
        public String Album { get; set; }
        public String Title { get; set; }
        public int Duration { get; set; }
        public int Played { get; set; }
        public int Voted { get; set; }
        public int TrackNumber { get; set; }
        public int Rating { get; set; }
        public double Toplist { get; set; }
        public String CoverUrl { get; set; }
    }
}
