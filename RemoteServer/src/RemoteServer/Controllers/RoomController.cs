using System;
using System.Collections.Generic;
using Microsoft.AspNetCore.Mvc;

namespace RemoteServer.Controllers
{
    [Route("remote/room")]
    public class RoomController : Controller
    {
        private static Dictionary<String, int> roomPages = new Dictionary<string, int>(StringComparer.OrdinalIgnoreCase);

        // GET api/values/5
        [HttpGet("{room}")]
        public IActionResult Get(String room)
        {
            int index;
            if (!roomPages.TryGetValue(room, out index))
                index = 0;

            return Content(index.ToString());
        }

        [HttpPut("{room}/{index}")]
        public IActionResult Put(String room, int index)
        {
            roomPages[room] = index;
            return Content("ÖK");
        }
    }
}
