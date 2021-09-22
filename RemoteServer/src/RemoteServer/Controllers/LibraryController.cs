using System;
using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using RemoteServer.Library;

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
