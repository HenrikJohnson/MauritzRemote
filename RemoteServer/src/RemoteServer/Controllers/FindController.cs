using System;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using RemoteServer.Config;
using RemoteServer.Remotes;

namespace RemoteServer.Controllers
{
    [Route("remote/find")]
    public class FindController: Controller
    {
        private IConfigurationManager configurationManager;
        private IRemoteManager remoteManager;

        public FindController(IConfigurationManager configurationManager, IRemoteManager remoteManager)
        {
            this.configurationManager = configurationManager;
            this.remoteManager = remoteManager;
        }

        // GET api/values/5
        [HttpGet("{category}/{text}")]
        public async Task<IActionResult> Get(String category, String text)
        {
            RokuRemote remote = configurationManager.getRokuRemote(category);
            if (remote != null)
            {
                remoteManager.scheduleCommand(category, remote.SelectDevice, false);
                await remote.sendSearchAsync(text);
                return Content("OK");
            }
            else
            {
                return StatusCode(404);
            }
        }
    }
}
