using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using RemoteServer.Config;

namespace RemoteServer.Remotes
{
    public class PanasonicRemote : IRemoteTarget
    {
        public class Factory : IRemoteTargetFactory
        {
            public IRemoteTarget createTarget(Dictionary<string, string> options, ILoggerFactory loggerFactory, IConfigurationManager config)
            {
                return new PanasonicRemote(options["BaseUrl"]);
            }
        }

        private readonly Uri uri;

        public PanasonicRemote(String baseUri)
        {
            this.uri = new Uri(new Uri(baseUri), "/nrc/control_0");
        }

        public static string RequestBuilder(string command)
        {
            StringBuilder request = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>");

            request.Append("<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">");
            request.Append("<s:Body>");
            request.Append("<u:X_SendKey xmlns:u=\"urn:panasonic-com:service:p00NetworkControl:1\">");
            request.Append("<X_KeyEvent>");
            request.Append(command);
            request.Append("</X_KeyEvent>");
            request.Append("</u:X_SendKey>");
            request.Append("</s:Body>");
            request.Append("</s:Envelope>");

            return request.ToString();
        }

        // Available commands are listed here
        // https://github.com/samuelmatis/viera-control/blob/master/codes.txt

        public async Task<String> sendCommandAsync(string command)
        {
            WebRequest req = WebRequest.Create(uri);
            HttpWebRequest httpReq = (HttpWebRequest) req;

            // Setup basic HTTP method and headers
            httpReq.Method = "POST";
            httpReq.ContentType = "text/xml; charset=utf-8";
            httpReq.Credentials = CredentialCache.DefaultCredentials;
            httpReq.Headers["SOAPAction"] = "\"urn:panasonic-com:service:p00NetworkControl:1#X_SendKey\"";

            // Get data and length
            byte[] data = Encoding.UTF8.GetBytes(RequestBuilder(command));

            Task<String> call = MakeCall(httpReq, data, uri);
            await Task.WhenAny(call, Task.Delay(100));
            if (call.IsCompleted)
                return call.Result;
            return "TIMEOUT";
        }

        private async Task<String> MakeCall(HttpWebRequest request, byte[] data, Uri uri)
        {
            using (Stream requestStream = await request.GetRequestStreamAsync())
            {
                await requestStream.WriteAsync(data, 0, data.Length);
                using (var response = (await request.GetResponseAsync()))
                {
                    String body = new StreamReader(response.GetResponseStream()).ReadToEnd();
                    if (String.IsNullOrEmpty(body))
                        return "OK";

                    return body;
                }
            }
        }
    }
}