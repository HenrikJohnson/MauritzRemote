using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Data.Common;
using System.Linq;
using System.Net;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Antiforgery.Internal;
using Microsoft.AspNetCore.Mvc;
using MySql.Data.MySqlClient;
using RemoteServer.Config;
using RemoteServer.Remotes;

namespace RemoteServer.Controllers
{
    [Route("remote/songs")]
    public class SongsController : Controller
    {
        public SongsController()
        {
        }

        static Regex quoteCharacters = new Regex(@"[\x00'""\b\n\r\t\cZ\\%_]");

        static Regex split = new Regex("\\S+");

        private static string MySQLEscape(string str)
        {
            return quoteCharacters.Replace(str,
                delegate (Match match)
                {
                    string v = match.Value;
                    switch (v)
                    {
                        case "\x00": // ASCII NUL (0x00) character
                            return "\\0";
                        case "\b": // BACKSPACE character
                            return "\\b";
                        case "\n": // NEWLINE (linefeed) character
                            return "\\n";
                        case "\r": // CARRIAGE RETURN character
                            return "\\r";
                        case "\t": // TAB
                            return "\\t";
                        case "\u001A": // Ctrl-Z
                            return "\\Z";
                        default:
                            return "\\" + v;
                    }
                });
        }

        // GET api/values/5
        [HttpGet("latest/{queue}")]
        public async Task<IActionResult> Latest(String queue, int? offset, int? size)
        {
            using (MySqlConnection mysqlConnection = new MySqlConnection(Constants.ConnectionString))
            {
                await mysqlConnection.OpenAsync();

                return await GenerateResult(mysqlConnection, "order by a.entered desc", queue, offset, size);
            }
        }

        // GET api/values/5
        [HttpGet("toplist/{queue}")]
        public async Task<IActionResult> Toplist(String queue, int? offset, int? size)
        {
            using (MySqlConnection mysqlConnection = new MySqlConnection(Constants.ConnectionString))
            {
                await mysqlConnection.OpenAsync();

                return await GenerateResult(mysqlConnection, "order by toplist*pow(1.5, if(rating<=5,1,rating+1-5)) desc", queue, offset, size);
            }
        }

        // GET api/values/5
        [HttpGet("search/{queue}/{search}")]
        public async Task<IActionResult> Search(String queue, String search, int? offset, int? size)
        {
            using (MySqlConnection mysqlConnection = new MySqlConnection(Constants.ConnectionString))
            {
                await mysqlConnection.OpenAsync();

                StringBuilder searchCriteria = new StringBuilder();
                Match match = split.Match(search);
                while (match.Success)
                {
                    if (searchCriteria.Length > 0)
                    {
                        searchCriteria.Append(" AND ");
                    }
                    searchCriteria.AppendFormat("(a.Artist LIKE '%{0}%' OR a.Title LIKE '%{0}%' OR a.Volume LIKE '%{0}%')",
                        MySQLEscape(match.Groups[0].Value));
                    match = match.NextMatch();
                }

                return await GenerateResult(mysqlConnection, "and (" + searchCriteria + ") order by a.artist, a.volume, a.trackno, a.title desc", queue, offset, size);
            }
        }

        private async Task<IActionResult> GenerateResult(MySqlConnection mysqlConnection, string criteria, string queue, int? offset, int? size)
        {
            if (size == null || size <= 0)
                size = 100;
            if (offset == null)
                offset = 0;

            String queueCriteria;

            switch (queue)
            {
                case "Tv":
                case "bTv":
                case "wTv":
                    queueCriteria = "(a.volume like 'Season %' or a.volume in ('Miniseries','Single Episode'))";
                    break;

                case "Music":
                case "bMusic":
                case "wMusic":
                    queueCriteria = "((a.volume not like 'Season %' and a.volume not in ('Movie','Miniseries','DVD','Single Episode','Podcast')) or a.volume is null)";
                    break;

                case "Movie":
                case "bMovie":
                case "wMovie":
                    queueCriteria = "(a.volume in ('Movie','DVD'))";
                    break;

                default:
                    return BadRequest("Unknown queue");
            }

            using (MySqlCommand command = mysqlConnection.CreateCommand())
            {
                command.CommandText =
                    "select a.id,a.artist,a.volume,a.title,a.length,a.played,a.voted,a.trackno,a.rating,d.cover " +
                    "from songs a left join programmember c on a.id = c.songid left join programs d on c.programid = d.id and d.name != 'Backlog' and d.artist != 'Miniseries' " +
                    "where " + queueCriteria + criteria + " limit " + size + " offset " + offset;

                using (DbDataReader reader = await command.ExecuteReaderAsync())
                {
                    List<SongItem> items = new List<SongItem>();
                    HashSet<int> songIds = new HashSet<int>();
                    while (await reader.ReadAsync())
                    {
                        int songId = reader.GetInt32(0);
                        if (songIds.Add(songId))
                        {
                            String cover = reader.IsDBNull(9) ? null : reader.GetString(9);
                            if (!String.IsNullOrEmpty(cover))
                                cover = Constants.JukeboxUrl + "cover_art/" + cover;

                            items.Add(new SongItem()
                            {
                                SongId = reader.GetInt32(0),
                                Artist = reader.GetString(1),
                                Album = reader.IsDBNull(2) ? null : reader.GetString(2),
                                Title = reader.GetString(3),
                                Duration = reader.IsDBNull(4) ? 0 : reader.GetInt32(4),
                                Played = reader.IsDBNull(5) ? 0 : reader.GetInt32(5),
                                Voted = reader.IsDBNull(6) ? 0 : reader.GetInt32(6),
                                TrackNumber = reader.IsDBNull(7) ? 0 : reader.GetInt32(7),
                                Rating = reader.IsDBNull(8) ? 0 : reader.GetInt32(8),
                                CoverUrl = cover
                            });
                        }
                    }
                    return new JsonResult(items);
                }
            }
        }
    }
}
