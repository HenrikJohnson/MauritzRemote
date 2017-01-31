using System;
using System.Collections.Generic;
using Microsoft.AspNetCore.Mvc;
using RemoteServer.Library;

namespace RemoteServer.Controllers
{
    [Route("remote/activequeue")]
    public class ActiveQueueController : Controller
    {
        private static Dictionary<String, LibraryQueue> queueMap = new Dictionary<string, LibraryQueue>(StringComparer.OrdinalIgnoreCase);

        // GET api/values/5
        [HttpGet("{room}")]
        public IActionResult Get(String room)
        {
            LibraryQueue index;
            if (!queueMap.TryGetValue(room, out index))
            {
                index = LibraryQueue.Tv;
            }

            return Content(index.ToString());
        }

        [HttpPut("{room}/{index}")]
        public IActionResult Put(String room, LibraryQueue index)
        {
            queueMap[room] = index;
            return Content("ÖK");
        }
    }
}
