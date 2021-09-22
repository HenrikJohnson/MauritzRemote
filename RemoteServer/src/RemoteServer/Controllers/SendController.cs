using System;
using Microsoft.AspNetCore.Mvc;
using RemoteServer.Remotes;

namespace RemoteServer.Controllers
{
    [Route("remote/send")]
    public class SendController : Controller
    {
        private IRemoteManager remoteManager;

        public SendController(IRemoteManager remoteManager)
        {
            this.remoteManager = remoteManager;
        }

        // GET api/values/5
        [HttpGet("{category}/{identifier}")]
        public IActionResult Get(String category, String identifier)
        {
            if (remoteManager.scheduleCommand(category, identifier, false))
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
