using System;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using RemoteServer.Remotes;

namespace RemoteServer.Controllers
{
    [Route("remote/query")]
    public class QueryController : Controller
    {
        private IRemoteManager remoteManager;

        public QueryController(IRemoteManager remoteManager)
        {
            this.remoteManager = remoteManager;
        }

        // GET api/values/5
        [HttpGet("{category}/{identifier}")]
        public async Task<IActionResult> Get(String category, String identifier)
        {
            Task<String> task = remoteManager.executeCommands(category, identifier);
            if (task == null)
            {
                return StatusCode(404);
            }
            return Content(await task);
        }
    }
}
