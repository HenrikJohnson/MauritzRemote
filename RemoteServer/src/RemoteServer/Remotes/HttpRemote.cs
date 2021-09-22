using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.Http;
using System.Runtime.InteropServices.WindowsRuntime;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using Microsoft.VisualBasic;
using RemoteServer.Config;

namespace RemoteServer.Remotes
{
    public class HttpRemote : IRemoteTarget
    {
        public class Factory : IRemoteTargetFactory
        {
            public IRemoteTarget createTarget(Dictionary<string, string> options, ILoggerFactory loggerFactory, IConfigurationManager config)
            {
                return new HttpRemote(options["BaseUrl"], loggerFactory, config);
            }
        }

        private ILogger logger;

        private String baseUrl;
        private IConfigurationManager config;

        public HttpRemote(string baseUrl, ILoggerFactory loggerFactory, IConfigurationManager config)
        {
            logger = loggerFactory.CreateLogger<HttpRemote>();
            this.baseUrl = baseUrl;
            this.config = config;
        }

        public async Task<String> sendCommandAsync(string command)
        {
            String commandData = config.getCommandData(command);
            String method = "GET";

            if (commandData.StartsWith("POST:"))
            {
                method = "POST";
                commandData = commandData.Substring(method.Length + 1);
            }
            else if (commandData.StartsWith("DELETE:"))
            {
                method = "DELETE";
                commandData = commandData.Substring(method.Length + 1);
            }
            else if (commandData.StartsWith("PUT:"))
            {
                method = "PUT";
                commandData = commandData.Substring(method.Length + 1);
            }
            else if (commandData.StartsWith("GET:"))
                commandData = commandData.Substring(method.Length + 1);

            return await sendCommandAsync(method, commandData);
        }

        protected async Task<String> sendCommandAsync(string method, string commandData)
        {
            Uri uri = new Uri(baseUrl + commandData);

            logger.LogInformation(new EventId(2), "{0} {1} ({2})", method, commandData, uri);

            HttpWebRequest request = WebRequest.CreateHttp(uri);
            request.Method = method;
            if (!String.IsNullOrEmpty(uri.UserInfo))
            {
                int ind = uri.UserInfo.IndexOf(":");
                request.Credentials = new NetworkCredential(uri.UserInfo.Substring(0, ind), uri.UserInfo.Substring(ind + 1));
            }

            Task<String> call = MakeCall(request, uri);
            await Task.WhenAny(call, Task.Delay(1000));
            if (call.IsCompleted)
                return call.Result;
            return "TIMEOUT";
        }

        private async Task<String> MakeCall(HttpWebRequest request, Uri uri)
        {
            try
            {
                using (HttpWebResponse response = (HttpWebResponse) await request.GetResponseAsync())
                {
                    if ((int) response.StatusCode / 100 != 2)
                        throw new IOException("Failed web request for " + uri);

                    String body = new StreamReader(response.GetResponseStream()).ReadToEnd();
                    if (String.IsNullOrEmpty(body))
                        return "OK";

                    return body;
                }
            }
            catch (Exception exc)
            {
                logger.LogError(new EventId(1), exc, "Failed to execute request");
            }

            return "ERROR";
        }
    }
}