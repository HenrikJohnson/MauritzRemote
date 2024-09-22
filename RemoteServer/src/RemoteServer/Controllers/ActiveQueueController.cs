using System;
using Microsoft.AspNetCore.Mvc;
using RemoteServer.Config;
using RemoteServer.Library;

namespace RemoteServer.Controllers
{
    [Route("remote/activequeue")]
    public class ActiveQueueController : Controller
    {
        private static String STATE_PREFIX = "Queue/";

        IRemoteConfigurationManager configurationManager;

        public ActiveQueueController(IRemoteConfigurationManager configurationManager)
        {
            this.configurationManager = configurationManager;
        }

        [HttpGet("{room}")]
        public IActionResult Get(String room)
        {
            String queue = configurationManager.GetState(STATE_PREFIX + room);
            LibraryQueue index = LibraryQueue.Tv;
            if (queue != null)
                Enum.TryParse(queue, out index);

            return Content(index.ToString());
        }

        [HttpPut("{room}/{index}")]
        public IActionResult Put(String room, LibraryQueue index)
        {
            configurationManager.PutState(STATE_PREFIX + room, index.ToString());
            return Content("ÖK");
        }
    }
}
