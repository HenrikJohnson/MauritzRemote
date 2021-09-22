using System;
using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using RemoteServer.Library;

namespace RemoteServer.Controllers
{
    [Route("remote/queue")]
    public class QueueController : Controller
    {
        private readonly ILibraryRepository repository;

        public QueueController(ILibraryRepository repository)
        {
            this.repository = repository;
        }

        // GET api/values/5
        [HttpGet("{room}/{queue}")]
        public async Task<IActionResult> Get(String room, LibraryQueue queue)
        {
            try {
                return new JsonResult(await repository.GetQueue(room, queue));
            }
            catch (IOException exc)
            {
                return BadRequest(exc.Message);
            }
        }

        [HttpPost("{room}/{queue}/{songId}")]
        public async Task<IActionResult> AddItem(String room, LibraryQueue queue, String songId)
        {
            try {
                return new JsonResult(await repository.AddQueueItem(room, queue, songId));
            }
            catch (IOException exc)
            {
                return BadRequest(exc.Message);
            }
        }

        [HttpDelete("{room}/{queue}/{queueId}")]
        public async Task<IActionResult> DeleteItem(String room, LibraryQueue queue, int queueId)
        {
            try {
                return new JsonResult(await repository.DeleteQueueItem(room, queue, queueId));
            }
            catch (IOException exc)
            {
                return BadRequest(exc.Message);
            }
        }

        [HttpGet("{room}/{queue}/{queueId}/{afterQueueId}")]
        [HttpPut("{room}/{queue}/{queueId}/{afterQueueId}")]
        public async Task<IActionResult> MoveItem(String room, LibraryQueue queue, int queueId, int afterQueueId)
        {
            try
            {
                return new JsonResult(await repository.MoveQueueItem(room, queue, queueId, afterQueueId));
            }
            catch (IOException exc)
            {
                return BadRequest(exc.Message);
            }
        }
    }
}
