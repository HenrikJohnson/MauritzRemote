using System;
using System.IO;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using RemoteServer.Config;

namespace RemoteServer.Remotes
{
    public class HttpRemote : IRemoteTarget
    {
        private ILogger logger;

        private Uri baseUrl;
        private IConfigurationManager config;

        public HttpRemote(string baseUrl, ILoggerFactory loggerFactory, IConfigurationManager config)
        {
            logger = loggerFactory.CreateLogger<HttpRemote>();
            this.baseUrl = new Uri(baseUrl);
            this.config = config;
        }

        public async Task sendCommandAsync(string command)
        {
            String commandData = config.getCommandData(command);
            String method = "GET";

            if (commandData.StartsWith("POST:/"))
            {
                method = "POST";
                commandData = commandData.Substring(5);
            }
            else if (commandData.StartsWith("GET:/"))
                commandData = commandData.Substring(4);

            logger.LogInformation(new EventId(2), "{0} {1}", method, commandData);

            Uri uri = new Uri(baseUrl, commandData);
            HttpWebRequest request = WebRequest.CreateHttp(uri);
            request.Method = method;
            if (!String.IsNullOrEmpty(baseUrl.UserInfo))
            {
                int ind = baseUrl.UserInfo.IndexOf(":");
                request.Credentials = new NetworkCredential(baseUrl.UserInfo.Substring(0, ind), baseUrl.UserInfo.Substring(ind + 1));
            }
            await Task.WhenAny(MakeCall(request, uri), Task.Delay(1000));
        }

        private async Task MakeCall(HttpWebRequest request, Uri uri)
        {
            try
            {
                using (HttpWebResponse response = (HttpWebResponse) await request.GetResponseAsync())
                {
                    if ((int) response.StatusCode / 100 != 2)
                        throw new IOException("Failed web request for " + uri);
                }
            }
            catch (Exception exc)
            {
                logger.LogError(new EventId(1), exc, "Failed to execute request");
            }
        }
    }
}