using System;
using System.Collections.Generic;
using System.Data.Common;
using System.IO;
using System.Net;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using MySql.Data.MySqlClient;
using RemoteServer.Config;
using RemoteServer.Library.Catalog;

namespace RemoteServer.Library
{
    public class JukeboxLibraryRepository : ILibraryRepository
    {
        private static Regex quoteCharacters = new Regex(@"[\x00'""\b\n\r\t\cZ\\%_]");
        private static Regex split = new Regex("\\S+");

        private static string escape(string str)
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

        private static String queueName(String room, LibraryQueue queue)
        {
            if (room.ToLower() == "office")
            {
                switch (queue)
                {
                    default:
                        return "wMusic";
                    case LibraryQueue.Movie:
                        return "wMovie";
                    case LibraryQueue.Tv:
                        return "wTv";
                }
            }
            else if (room.ToLower() == "bedroom")
            {
                switch (queue)
                {
                    default:
                        return "bMusic";
                    case LibraryQueue.Movie:
                        return "bMovie";
                    case LibraryQueue.Tv:
                        return "wTv";
                }
            }
            switch (queue)
            {
                default:
                    return null;
                case LibraryQueue.Movie:
                    return "Movie";
                case LibraryQueue.Tv:
                    return "Tv";
            }
        }

        private ILogger logger;
        private string jukeboxUrl;
        private string jukeboxUser;
        private string jukeboxPassword;
        private string connectionString;

        private async Task<List<QueueItem>> GenerateQueue(MySqlConnection mysqlConnection, String room, LibraryQueue queue)
        {
            String queueSel;
            if (null == queueName(room, queue))
                queueSel = "a.user is null";
            else
                queueSel = "a.user = '" + queueName(room, queue) + "'";

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
                                cover = jukeboxUrl + "cover_art/" + cover;

                            items.Add(new QueueItem()
                            {
                                QueueId = reader.GetInt32(0),
                                ItemId = "I" + reader.GetInt32(1).ToString(),
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
                    return items;
                }
            }
        }


        public JukeboxLibraryRepository(IConfigurationManager configurationManager, ILoggerFactory loggerFactory)
        {
            connectionString = configurationManager.ConnectionString;
            jukeboxUrl = configurationManager.JukeboxUrl;
            jukeboxUser = configurationManager.JukeboxUser;
            jukeboxPassword = configurationManager.JukeboxPassword;

            this.logger = loggerFactory.CreateLogger("JukeboxLibrary");
        }

        public async Task<List<QueueItem>> GetQueue(String room, LibraryQueue queue)
        {
            using (MySqlConnection mysqlConnection = new MySqlConnection(connectionString))
            {
                await mysqlConnection.OpenAsync();
                return await GenerateQueue(mysqlConnection, room, queue);
            }
        }

        public async Task<List<QueueItem>> AddQueueItem(String room, LibraryQueue queue, string songId)
        {
            HttpWebRequest request;
            if (songId.StartsWith("I"))
            {
                String id = songId.Substring(1);
                request =
                    WebRequest.CreateHttp(jukeboxUrl + "action.php?SEL=" + Int32.Parse(id) + "&AUTH_USER=" +
                                          Uri.EscapeUriString(jukeboxUser) + "&AUTH_PWD=" +
                                          Uri.EscapeUriString(jukeboxPassword));
            }
            else if (songId.StartsWith("A"))
            {
                String id = songId.Substring(1);
                request =
                    WebRequest.CreateHttp(jukeboxUrl + "action.php?SELPRG=" + Int32.Parse(id) + "&AUTH_USER=" +
                                          Uri.EscapeUriString(jukeboxUser) + "&AUTH_PWD=" +
                                          Uri.EscapeUriString(jukeboxPassword));
            }
            else
            {
                throw new IOException("Invalid song ID");
            }

            request.Method = "GET";
            request.CookieContainer = new CookieContainer();
            if (queueName(room, queue) != null)
            {
                request.CookieContainer.Add(new Uri(jukeboxUrl),
                    new Cookie("CURRENTGROUP", queueName(room, queue)));
            }
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
                    throw;
            }
            return await GetQueue(room, queue);
        }

        public async Task<List<QueueItem>> DeleteQueueItem(String room, LibraryQueue queue, int queueId)
        {
            HttpWebRequest request =
                WebRequest.CreateHttp(jukeboxUrl+ "action.php?REMOVE=" + queueId + "&AUTH_USER=" +
                                      Uri.EscapeUriString(jukeboxUser) + "&AUTH_PWD=" +
                                      Uri.EscapeUriString(jukeboxPassword));

            request.Method = "GET";
            request.CookieContainer = new CookieContainer();
            if (queueName(room, queue) != null)
            {
                request.CookieContainer.Add(new Uri(jukeboxUrl), new Cookie("CURRENTGROUP", queueName(room, queue)));
            }
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
                    throw;
            }
            return await GetQueue(room, queue);
        }

        public async Task<List<QueueItem>> MoveQueueItem(String room, LibraryQueue queue, int queueId, int afterQueueId)
        {
            if (queueId == afterQueueId)
                throw new IOException("Can't move item before itself");

            using (MySqlConnection mysqlConnection = new MySqlConnection(connectionString))
            {
                await mysqlConnection.OpenAsync();

                String queueSel;
                if (null == queueName(room, queue))
                    queueSel = "a.user is null";
                else
                    queueSel = "a.user = '" + queueName(room, queue) + "'";

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
                                    throw new IOException("Can't move currently playing item");
                                foundFrom = true;
                            }
                        }

                    }
                }

                if (!found || !foundFrom)
                    return null;

                if (foundQueueId != queueId)
                {
                    if (foundPri > 0)
                    {
                        List<int> queueIds = new List<int>();
                        using (MySqlCommand command = mysqlConnection.CreateCommand())
                        {
                            command.CommandText = "select queueid from queue where queueid >= " + foundQueueId +
                                                  " order by queueid desc";
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
                            if (changeId == queueId)
                                queueId++;

                            using (MySqlCommand command = mysqlConnection.CreateCommand())
                            {
                                command.CommandText = "update queue set queueid = queueid + 1 where queueid = " +
                                                        changeId;
                                await command.ExecuteNonQueryAsync();
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

                return await GenerateQueue(mysqlConnection, room, queue);
            }
        }

        public async Task<List<LibraryItem>> Query(LibraryQueue queue, LibrarySearchType type, string search,
            int? offset, int? limit)
        {
            if (offset == null)
                offset = 0;
            if (limit == null || limit <= 0)
                limit = 100;

            if (type != LibrarySearchType.Album)
            {
                String queueCriteria;

                switch (queue)
                {
                    case LibraryQueue.Tv:
                        queueCriteria = "(a.volume like 'Season %' or a.volume in ('Miniseries','Single Episode'))";
                        break;

                    case LibraryQueue.Music:
                        queueCriteria =
                            "((a.volume not like 'Season %' and a.volume not in ('Movie','Miniseries','DVD','Single Episode','Podcast')) or a.volume is null)";
                        break;

                    case LibraryQueue.Movie:
                        queueCriteria = "(a.volume in ('Movie','DVD'))";
                        break;

                    default:
                        throw new IOException("Unknown queue");
                }

                String criteria = "";

                if (!String.IsNullOrEmpty(search))
                {
                    StringBuilder searchCriteria = new StringBuilder();
                    Match match = split.Match(search);
                    while (match.Success)
                    {
                        if (searchCriteria.Length > 0)
                        {
                            searchCriteria.Append(" AND ");
                        }
                        searchCriteria.AppendFormat(
                            "(a.Artist LIKE '%{0}%' OR a.Title LIKE '%{0}%' OR a.Volume LIKE '%{0}%')",
                            escape(match.Groups[0].Value));
                        match = match.NextMatch();
                    }
                    if (searchCriteria.Length > 0)
                    {
                        criteria = String.Format(" AND ({0})", searchCriteria);
                    }
                }

                String backlogId = ", 0";

                switch (type)
                {
                    default:
                        criteria += " order by a.artist, a.volume, a.trackno, a.title";
                        break;
                    case LibrarySearchType.Title:
                        criteria += " order by a.title";
                        break;
                    case LibrarySearchType.Entered:
                        criteria += " order by a.entered desc";
                        break;
                    case LibrarySearchType.LastPlayed:
                        criteria += " order by a.lastplayed desc";
                        break;
                    case LibrarySearchType.Toplist:
                        criteria += " order by adjtoplist desc";
                        break;
                    case LibrarySearchType.Backlog:
                        criteria += " having backlogid is not null order by backlogid";
                        backlogId =
                            ", (select max(f.id) from programmember f, programs g where f.songid = a.id and g.id = f.programid and g.artist = 'Backlog' and g.name = 'Miniseries') backlogid";
                        break;
                }

                using (MySqlConnection mysqlConnection = new MySqlConnection(connectionString))
                {
                    await mysqlConnection.OpenAsync();

                    double maxToplist = 1;
                    using (MySqlCommand command = mysqlConnection.CreateCommand())
                    {
                        command.CommandText =
                            "select max(a.toplist*pow(1.5, if(a.rating<=5,1,a.rating+1-5))) as adjtoplist " +
                            "from songs a where " + queueCriteria;

                        using (DbDataReader reader = await command.ExecuteReaderAsync())
                        {
                            if (await reader.ReadAsync() && !reader.IsDBNull(0))
                            {
                                maxToplist = reader.GetDouble(0);
                            }
                        }
                    }

                    using (MySqlCommand command = mysqlConnection.CreateCommand())
                    {
                        command.CommandText =
                            "select a.id,a.artist,a.volume,a.title,a.length,a.played,a.voted,a.trackno,a.rating,d.cover,a.toplist*pow(1.5, if(a.rating<=5,1,a.rating+1-5)) as adjtoplist" +
                            backlogId +
                            " from songs a left join programmember c on a.id = c.songid and c.programid in (select e.id from programs e where e.cover is not null) " +
                            "left join programs d on c.programid = d.id and d.artist != 'Backlog' and d.name != 'Miniseries' " +
                            "where " + queueCriteria + criteria + " limit " + limit + " offset " + offset;

                        using (DbDataReader reader = await command.ExecuteReaderAsync())
                        {
                            List<LibraryItem> items = new List<LibraryItem>();
                            HashSet<int> songIds = new HashSet<int>();
                            while (await reader.ReadAsync())
                            {
                                int songId = reader.GetInt32(0);
                                if (songIds.Add(songId))
                                {
                                    String cover = reader.IsDBNull(9) ? null : reader.GetString(9);
                                    if (!String.IsNullOrEmpty(cover))
                                        cover = jukeboxUrl + "cover_art/" + cover;

                                    items.Add(new LibraryItem()
                                    {
                                        ItemId = "I" + reader.GetInt32(0),
                                        Artist = reader.GetString(1),
                                        Album = reader.IsDBNull(2) ? null : reader.GetString(2),
                                        Title = reader.GetString(3),
                                        Duration = reader.IsDBNull(4) ? 0 : reader.GetInt32(4),
                                        Played = reader.IsDBNull(5) ? 0 : reader.GetInt32(5),
                                        Voted = reader.IsDBNull(6) ? 0 : reader.GetInt32(6),
                                        TrackNumber = reader.IsDBNull(7) ? 0 : reader.GetInt32(7),
                                        Rating = reader.IsDBNull(8) ? 0 : reader.GetInt32(8),
                                        CoverUrl = cover,
                                        Toplist = reader.IsDBNull(10) ? 0 : reader.GetDouble(10) / maxToplist
                                    });
                                }
                            }
                            return items;
                        }
                    }
                }
            }
            else
            {
                String queueCriteria;

                switch (queue)
                {
                    case LibraryQueue.Tv:
                        queueCriteria = "(a.name like 'Season %' or a.name in ('Miniseries','Single Episode'))";
                        break;

                    case LibraryQueue.Music:
                        queueCriteria =
                            "((a.name not like 'Season %' and a.name not in ('Movie','Miniseries','DVD','Single Episode','Podcast')) or a.name is null)";
                        break;

                    case LibraryQueue.Movie:
                        queueCriteria = "(a.name in ('Movie','DVD'))";
                        break;

                    default:
                        throw new IOException("Unknown queue");
                }

                String criteria = "";

                if (!String.IsNullOrEmpty(search))
                {
                    StringBuilder searchCriteria = new StringBuilder();
                    Match match = split.Match(search);
                    while (match.Success)
                    {
                        if (searchCriteria.Length > 0)
                        {
                            searchCriteria.Append(" AND ");
                        }

                        searchCriteria.AppendFormat(
                            "(a.Artist LIKE '%{0}%' OR a.name LIKE '%{0}%')",
                            escape(match.Groups[0].Value));

                        match = match.NextMatch();
                    }
                    if (searchCriteria.Length > 0)
                    {
                        criteria = String.Format(" AND ({0})", searchCriteria);
                    }
                }

                using (MySqlConnection mysqlConnection = new MySqlConnection(connectionString))
                {
                    await mysqlConnection.OpenAsync();

                    using (MySqlCommand command = mysqlConnection.CreateCommand())
                    {
                        command.CommandText =
                            "select a.id,a.artist,a.name,(select count(1) from programmember b where a.id = b.programid),a.cover" +
                            " from programs a where " + queueCriteria + criteria + " order by a.artist,a.name limit " + limit + " offset " + offset;

                        using (DbDataReader reader = await command.ExecuteReaderAsync())
                        {
                            List<LibraryItem> items = new List<LibraryItem>();
                            HashSet<int> songIds = new HashSet<int>();
                            while (await reader.ReadAsync())
                            {
                                int songId = reader.GetInt32(0);
                                if (songIds.Add(songId))
                                {
                                    String cover = reader.IsDBNull(4) ? null : reader.GetString(4);
                                    if (!String.IsNullOrEmpty(cover))
                                        cover = jukeboxUrl + "cover_art/" + cover;

                                    items.Add(new LibraryItem()
                                    {
                                        ItemId = "A" + reader.GetInt32(0),
                                        Artist = reader.GetString(1),
                                        Album = reader.IsDBNull(2) ? null : reader.GetString(2),
                                        TrackNumber = reader.IsDBNull(3) ? 0 : reader.GetInt32(3),
                                        CoverUrl = cover
                                    });
                                }
                            }
                            return items;
                        }
                    }
                }
            }
        }

        private String strHex(string str)
        {
            byte[] bytes = Encoding.UTF8.GetBytes(str);
            StringBuilder hex = new StringBuilder(bytes.Length * 2);
            foreach (byte b in bytes)
                hex.AppendFormat("{0:x2}", b);

            return hex.ToString();
        }

        public async Task<AmazonCatalog<T>> Catalog<T>(string type, Func<AmazonRawData, T> converter) where T : AmazonEntity
        {
            using (MySqlConnection mysqlConnection = new MySqlConnection(connectionString))
            {
                await mysqlConnection.OpenAsync();

                double maxToplist = 1;
                string queueCriteria = "((a.volume not like 'Season %' and a.volume not in ('Movie','Miniseries','DVD','Single Episode','Podcast')) or a.volume is null)";
                using (MySqlCommand command = mysqlConnection.CreateCommand())
                {

                    command.CommandText =
                        "select log(max(a.toplist*pow(1.5, if(a.rating<=5,1,a.rating+1-5)))) as adjtoplist " +
                        "from songs a where " + queueCriteria;

                    using (DbDataReader reader = await command.ExecuteReaderAsync())
                    {
                        if (await reader.ReadAsync() && !reader.IsDBNull(0))
                        {
                            maxToplist = reader.GetDouble(0);
                        }
                    }
                }

                using (MySqlCommand command = mysqlConnection.CreateCommand())
                {
                    command.CommandText =
                        "select a.id, a.artist, a.volume, a.title, log(a.toplist*pow(1.5, if(a.rating<=5,1,a.rating+1-5))) as adjtoplist, d.id, d.name, d.artist" +
                        " from songs a left join programmember c on a.id = c.songid" +
                        " left join programs d on c.programid = d.id and d.artist != 'Backlog' and d.name != 'Miniseries'" +
                        " where " + queueCriteria + 
                        " order by d.name desc, d.artist desc";

                    using (DbDataReader reader = await command.ExecuteReaderAsync())
                    {
                        List<LibraryItem> items = new List<LibraryItem>();
                        HashSet<int> songIds = new HashSet<int>();
                        Dictionary<String, T> data = new Dictionary<string, T>();
                        while (await reader.ReadAsync())
                        {
                            int songId = reader.GetInt32(0);
                            if (songIds.Add(songId))
                            {
                                string artist = reader.GetString(1);
                                string album = reader.IsDBNull(2) ? null : reader.GetString(2);
                                string title = reader.GetString(3);
                                int toplist = (int)Math.Round(reader.IsDBNull(4) ? 0 : reader.GetDouble(4) * 100 / maxToplist);
                                if (toplist < 1)
                                    toplist = 1;

                                int albumId = reader.IsDBNull(5) ? 0 : reader.GetInt32(5);
                                string albumName = reader.IsDBNull(6) ? null : reader.GetString(6);
                                string albumArtist = reader.IsDBNull(7) ? null : reader.GetString(7);

                                String albumStrId;

                                if (albumId > 0)
                                {
                                    albumStrId = "a" + albumId;
                                } else
                                {
                                    albumStrId = "e" + strHex(album + "|" + albumArtist);
                                    albumName = album;
                                    albumArtist = artist;
                                }

                                AmazonRawData rawData = new AmazonRawData()
                                {
                                    Id = "b" + songId,
                                    Name = title,
                                    Popularity = toplist,
                                    ArtistId = "c" + strHex(artist),
                                    Artist = artist,
                                    Album = albumName ?? album,
                                    AlbumId = albumStrId,
                                    AlbumArtistId = String.IsNullOrEmpty(albumArtist) ? null : "c" + strHex(albumArtist),
                                    AlbumArtist = albumArtist
                                };

                                T entity = converter.Invoke(rawData);
                                T other;
                                if (data.TryGetValue(entity.Id, out other))
                                    other.Popularity.Default = Math.Max(other.Popularity.Default, entity.Popularity.Default);
                                else
                                    data[entity.Id] = entity;
                            }
                        }

                        AmazonCatalog<T> catalog = new AmazonCatalog<T>() { Type = type };
                        catalog.Entities = new List<T>(data.Values);
                        return catalog;
                    }
                }
            }
        }
    }
}
