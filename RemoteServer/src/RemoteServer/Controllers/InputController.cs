using System;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using RemoteServer.Config;
using RemoteServer.Remotes;

namespace RemoteServer.Controllers
{
    [Route("remote/input")]
    public class InputController: Controller
    {
        private IConfigurationManager configurationManager;

        public InputController(IConfigurationManager configurationManager)
        {
            this.configurationManager = configurationManager;
        }

        // GET api/values/5
        [HttpGet("{category}/{text}")]
        public async Task<IActionResult> Get(String category, String text)
        {
            RokuRemote remote = configurationManager.getRokuRemote(category);
            if (remote != null)
            {
                await remote.sendKeyboardInputAsync(text);
                return Content("OK");
            }
            else
            {
                return StatusCode(404);
            }
        }
    }
}
