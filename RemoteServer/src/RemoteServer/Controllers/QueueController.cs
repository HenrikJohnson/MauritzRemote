using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Data.Common;
using System.Linq;
using System.Net;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Antiforgery.Internal;
using Microsoft.AspNetCore.Mvc;
using MySql.Data.MySqlClient;
using RemoteServer.Config;
using RemoteServer.Remotes;

namespace RemoteServer.Controllers
{
    [Route("remote/queue")]
    public class QueueController : Controller
    {
        public QueueController()
        {
        }

        static Regex quoteCharacters = new Regex(@"[\x00'""\b\n\r\t\cZ\\%_]");

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
        [HttpGet("{queue}")]
        public async Task<IActionResult> Get(String queue)
        {
            using (MySqlConnection mysqlConnection = new MySqlConnection(Constants.ConnectionString))
            {
                await mysqlConnection.OpenAsync();

                return await GenerateQueue(mysqlConnection, queue);
            }
        }

        private async Task<IActionResult> GenerateQueue(MySqlConnection mysqlConnection, string queue)
        {
            String queueSel;
            if (queue == "Music")
                queueSel = "a.user is null";
            else
                queueSel = "a.user = '" + MySQLEscape(queue) + "'";

            using (MySqlCommand command = mysqlConnection.CreateCommand())
            {
                command.CommandText =
                    "select a.queueid,a.id,b.artist,b.volume,b.title,b.length,b.played,b.voted,b.trackno,b.rating,d.cover " +
                    "from queue a left join songs b on a.id = b.id left join programmember c on b.id = c.songid left join programs d on c.programid = d.id and d.name != 'Backlog' and d.artist != 'Miniseries' " +
                    "where pri < 1001 and " + queueSel + " order by a.pri, a.queueid";

                using (DbDataReader reader = await command.ExecuteReaderAsync())
                {
                    List<QueueItem> items = new List<QueueItem>();
                    HashSet<int> queueIds = new HashSet<int>();
                    while (await reader.ReadAsync())
                    {
                        int queueId = reader.GetInt32(0);
                        if (queueIds.Add(queueId))
                        {
                            String cover = reader.IsDBNull(10) ? null : reader.GetString(10);
                            if (!String.IsNullOrEmpty(cover))
                                cover = Constants.JukeboxUrl + "cover_art/" + cover;

                            items.Add(new QueueItem()
                            {
                                QueueId = reader.GetInt32(0),
                                SongId = reader.GetInt32(1),
                                Artist = reader.GetString(2),
                                Album = reader.IsDBNull(3) ? null : reader.GetString(3),
                                Title = reader.GetString(4),
                                Duration = reader.IsDBNull(5) ? 0 : reader.GetInt32(5),
                                Played = reader.IsDBNull(6) ? 0 : reader.GetInt32(6),
                                Voted = reader.IsDBNull(7) ? 0 : reader.GetInt32(7),
                                TrackNumber = reader.IsDBNull(8) ? 0 : reader.GetInt32(8),
                                Rating = reader.IsDBNull(9) ? 0 : reader.GetInt32(9),
                                CoverUrl = cover
                            });
                        }
                    }
                    return new JsonResult(items);
                }
            }
        }

        [HttpPost("{queue}/{songId}")]
        public async Task<IActionResult> Post(String queue, int songId)
        {
            HttpWebRequest request =
                WebRequest.CreateHttp(Constants.JukeboxUrl + "action.php?SEL=" + songId + "&AUTH_USER=" +
                                      Uri.EscapeUriString(Constants.JukeboxUser) + "&AUTH_PWD=" +
                                      Uri.EscapeUriString(Constants.JukeboxPassword));

            request.Method = "GET";
            request.CookieContainer = new CookieContainer();
            request.CookieContainer.Add(new Uri(Constants.JukeboxUrl), new Cookie("CURRENTGROUP", queue));
            try
            {
                using ((HttpWebResponse)await request.GetResponseAsync())
                {
                }
            }
            catch (WebException exc)
            {
                HttpWebResponse response = (HttpWebResponse)exc.Response;
                if (!response.ResponseUri.AbsolutePath.EndsWith("/left.php"))
                    throw exc;
            }
            return await Get(queue);
        }

        [HttpDelete("{queue}/{queueId}")]
        public async Task<IActionResult> Delete(String queue, int queueId)
        {
            HttpWebRequest request =
                WebRequest.CreateHttp(Constants.JukeboxUrl + "action.php?REMOVE=" + queueId + "&AUTH_USER=" +
                                      Uri.EscapeUriString(Constants.JukeboxUser) + "&AUTH_PWD=" +
                                      Uri.EscapeUriString(Constants.JukeboxPassword));

            request.Method = "GET";
            request.CookieContainer = new CookieContainer();
            request.CookieContainer.Add(new Uri(Constants.JukeboxUrl), new Cookie("CURRENTGROUP", queue));
            try
            {
                using ((HttpWebResponse)await request.GetResponseAsync())
                {
                }
            }
            catch (WebException exc)
            {
                HttpWebResponse response = (HttpWebResponse)exc.Response;
                if (!response.ResponseUri.AbsolutePath.EndsWith("/left.php"))
                    throw exc;
            }
            return await Get(queue);
        }

        [HttpPost("{queue}/{queueId}/{afterQueueId}")]
        public async Task<IActionResult> Post(String queue, int queueId, int afterQueueId)
        {
            if (queueId == afterQueueId)
                return BadRequest("Can't move item before itself");

            using (MySqlConnection mysqlConnection = new MySqlConnection(Constants.ConnectionString))
            {
                await mysqlConnection.OpenAsync();

                String queueSel;
                if (queue == "Music")
                    queueSel = "a.user is null";
                else
                    queueSel = "a.user = '" + MySQLEscape(queue) + "'";

                int foundPri = -1;
                int foundQueueId = -1;
                bool found = false;
                bool foundFrom = false;

                using (MySqlCommand command = mysqlConnection.CreateCommand())
                {
                    command.CommandText = "select a.queueid,a.pri " +
                                          "from queue a where pri < 1001 and " + queueSel + " order by a.pri, a.queueid";
                    using (DbDataReader reader = await command.ExecuteReaderAsync())
                    {
                        while (await reader.ReadAsync())
                        {
                            int currentQueueId = reader.GetInt32(0);
                            int currentPri = reader.GetInt32(1);
                            if (currentQueueId == afterQueueId)
                            {
                                found = true;
                            }
                            else if (found && foundPri < 0)
                            {
                                foundPri = currentPri;
                                foundQueueId = currentQueueId;
                            }

                            if (queueId == currentQueueId)
                            {
                                if (currentPri == 0)
                                    return BadRequest("Can't move currently playing item");
                                foundFrom = true;
                            }
                        }

                    }
                }

                if (!found || !foundFrom)
                    return NotFound();

                if (foundQueueId != queueId)
                {
                    if (foundPri > 0)
                    {
                        List<int> queueIds = new List<int>();
                        using (MySqlCommand command = mysqlConnection.CreateCommand())
                        {
                            command.CommandText = "select queueid from queue where queueid >= " + foundQueueId + " order by queueid desc";
                            using (DbDataReader reader = await command.ExecuteReaderAsync())
                            {
                                while (await reader.ReadAsync())
                                {
                                    queueIds.Add(reader.GetInt32(0));
                                }
                            }
                        }

                        foreach (int changeId in queueIds)
                        {
                            if (changeId != queueId)
                            {
                                using (MySqlCommand command = mysqlConnection.CreateCommand())
                                {
                                    command.CommandText = "update queue set queueid = queueid + 1 where queueid = " +
                                                          changeId;
                                    await command.ExecuteNonQueryAsync();
                                }
                            }
                        }
                    }

                    using (MySqlCommand command = mysqlConnection.CreateCommand())
                    {
                        command.CommandText = "update queue set queueid = " + foundQueueId + ", pri = " + foundPri +
                                              " where queueid = " + queueId;
                        await command.ExecuteNonQueryAsync();
                    }
                }

                return await GenerateQueue(mysqlConnection, queue);
            }
        }
    }
}
