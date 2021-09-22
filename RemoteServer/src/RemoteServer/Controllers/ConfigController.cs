using System;
using System.Collections.Generic;
using Microsoft.AspNetCore.Mvc;
using Microsoft.IdentityModel.Protocols;
using RemoteServer.Config;
using RemoteServer.Remotes;

namespace RemoteServer.Controllers
{
    [Route("remote")]
    public class ConfigController : Controller
    {
        private IConfigurationManager configManager;

        public ConfigController(IConfigurationManager configManager)
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
