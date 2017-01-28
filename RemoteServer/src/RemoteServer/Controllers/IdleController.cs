using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using RemoteServer.Remotes;

namespace RemoteServer.Controllers
{
    [Route("remote/idle")]
    public class IdleController : Controller
    {
        private IRemoteManager remoteManager;

        public IdleController(IRemoteManager remoteManager)
        {
            this.remoteManager = remoteManager;
        }

        // GET api/values/5
        [HttpGet("{category}/{identifier}")]
        public IActionResult Get(String category, String identifier)
        {
            if (remoteManager.scheduleCommand(category, identifier, true))
            {
                return Content("OK");
            }
            else
            {
                return StatusCode(404);
            }
        }
    }
}
