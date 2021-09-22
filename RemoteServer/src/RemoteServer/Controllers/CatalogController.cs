using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;
using RemoteServer.Library;
using RemoteServer.Library.Catalog;

namespace RemoteServer.Controllers
{
    [Route("remote/catalog")]
    public class MusicRecordingController : Controller
    {
        private readonly ILibraryRepository repository;
        private JsonSerializerSettings settings = new JsonSerializerSettings()
        {
            NullValueHandling = NullValueHandling.Ignore,
            ContractResolver = new CamelCasePropertyNamesContractResolver()
        };

        public MusicRecordingController(ILibraryRepository repository)
        {
            this.repository = repository;
        }

        [HttpGet("musicrecordings")]
        public async Task<IActionResult> MusicRecordings()
        {
            return new JsonResult(await repository.Catalog("AMAZON.MusicRecording", (t) => new AmazonMusicRecording(t)), settings);
        }

        [HttpGet("musicgroups")]
        public async Task<IActionResult> MusicGroups()
        {
            return new JsonResult(await repository.Catalog("AMAZON.MusiremocGroup", (t) => new AmazonMusicGroup(t)), settings);
        }

        [HttpGet("musicalbums")]
        public async Task<IActionResult> MusicAlbums()
        {
            return new JsonResult(await repository.Catalog("AMAZON.MusicAlbums", (t) => new AmazonMusicAlbum(t)), settings);
        }
    }
}
