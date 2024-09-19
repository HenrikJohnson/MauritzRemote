using Microsoft.AspNetCore.Mvc;
using RemoteServer.Config;

namespace RemoteServer.Controllers
{
    [Route("remote")]
    public class ConfigController : Controller
    {
        private IRemoteConfigurationManager configManager;

        public ConfigController(IRemoteConfigurationManager configManager)
        {
            this.configManager = configManager;
        }

        [HttpGet("config/{version}")]
        public IActionResult Get(long version)
        {
            ContentResult ret = new ContentResult();
            ret.Content = configManager.configJson(version);
            ret.ContentType = "application/json";
            ret.StatusCode = 200;
            return ret;
        }
    }
}
