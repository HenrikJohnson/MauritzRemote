using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RemoteServer.Controllers
{
    public class QueueItem : SongItem
    {
        public int QueueId { get; set; }
    }
}
