using System;
using System.Collections.Generic;
using Microsoft.AspNetCore.Mvc;
using RemoteServer.Config;
using RemoteServer.Remotes;

namespace RemoteServer.Controllers
{
    [Route("remote/room")]
    public class RoomController : Controller
    {
        private static String STATE_PREFIX = "Room/";

        IRemoteConfigurationManager configurationManager;

        public RoomController(IRemoteConfigurationManager configurationManager)
        {
            this.configurationManager = configurationManager;
        }

        // GET api/values/5
        [HttpGet("{room}")]
        public IActionResult Get(String room)
        {
            String value = configurationManager.GetState(STATE_PREFIX + room);
            int intVal = 0;
            if (value != null)
                int.TryParse(value, out intVal);

            return Content(intVal.ToString());
        }

        [HttpPut("{room}/{index}")]
        public IActionResult Put(String room, int index)
        {
            configurationManager.PutState(STATE_PREFIX + room.ToString(), index.ToString());
            return Content("ÖK");
        }
    }
}
