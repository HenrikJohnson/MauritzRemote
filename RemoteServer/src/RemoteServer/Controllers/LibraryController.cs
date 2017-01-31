using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Data.Common;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Antiforgery.Internal;
using Microsoft.AspNetCore.Mvc;
using MySql.Data.MySqlClient;
using RemoteServer.Config;
using RemoteServer.Library;
using RemoteServer.Remotes;

namespace RemoteServer.Controllers
{
    [Route("remote/search")]
    public class LibraryController : Controller
    {
        private readonly ILibraryRepository repository;

        public LibraryController(ILibraryRepository repository)
        {
            this.repository = repository;
        }

        // GET api/values/5
        [HttpGet("{queue}/{type}")]
        public async Task<IActionResult> Search(LibraryQueue queue, LibrarySearchType type, int? offset, int? size)
        {
            try
            {
                return new JsonResult(await repository.Query(queue, type, null, offset, size));
            }
            catch (IOException exc)
            {
                return BadRequest(exc.Message);
            }
        }

        // GET api/values/5
        [HttpGet("{queue}/{type}/{search}")]
        public async Task<IActionResult> Search(LibraryQueue queue, LibrarySearchType type, String search, int? offset,
            int? size)
        {
            try
            {
                return new JsonResult(await repository.Query(queue, type, search, offset, size));
            }
            catch (IOException exc)
            {
                return BadRequest(exc.Message);
            }
        }
    }
}
