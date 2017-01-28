using System;
using System.Collections.Generic;
using Microsoft.AspNetCore.Mvc;
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

        [HttpGet("config")]
        public IActionResult Get()
        {
            ContentResult ret = new ContentResult();
            ret.Content = configManager.configJson();
            ret.ContentType = "application/json";
            ret.StatusCode = 200;
            return ret;
        }
    }
}
