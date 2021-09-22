using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using RemoteServer.Config;

namespace RemoteServer.Remotes
{
    public class GlobalCacheRemote : IRemoteTarget
    {
        public class Factory : IRemoteTargetFactory
        {
            public IRemoteTarget createTarget(Dictionary<string, string> options, ILoggerFactory loggerFactory, IConfigurationManager config)
            {
                return new GlobalCacheRemote(options["Host"], Int32.Parse(options["Port"]), options["Device"], loggerFactory, config);
            }
        }

        private String hostname;
        private int port;
        private String device;
        private ushort commandIndex;
        private TcpClient client;
        private NetworkStream socket;
        private IConfigurationManager config;
        private ILogger logger;

        public GlobalCacheRemote(String hostname, int port, String device, ILoggerFactory loggerFactory, IConfigurationManager config)
        {
            this.hostname = hostname;
            this.port = port;
            this.device = device;
            this.config = config;

            this.logger = loggerFactory.CreateLogger<GlobalCacheRemote>();
        }

        public async Task<String> sendCommandAsync(string command)
        {
            logger.LogDebug("Entering send command {0}", command);
            try
            {
                if (socket == null)
                {
                    await reconnectAsync();
                }

                try
                {
                    await actualSend(command);
                }
                catch (Exception)
                {
                    await reconnectAsync();

                    try
                    {
                        await actualSend(command);
                    }
                    catch (Exception exc2)
                    {
                        try
                        {
                            client?.Dispose();
                        }
                        catch (Exception)
                        {
                        }
                        try
                        {
                            socket?.Dispose();
                        }
                        catch (Exception)
                        {
                        }

                        client = null;
                        socket = null;
                        throw new IOException("Failed to send", exc2);
                    }
                }
            }
            finally
            {
                logger.LogDebug("Leaving send command {0}", command);
            }

            return "OK";
        }

        private async Task actualSend(string command)
        {
            byte[] incomingData = new byte[1024];
            while (socket.DataAvailable)
            {
                if (await socket.ReadAsync(incomingData, 0, 1) <= 0)
                {
                    await reconnectAsync();
                }
            }
            String cis = commandIndex.ToString(CultureInfo.InvariantCulture);
            commandIndex++;

            String commandData = config.getCommandData(command);

            byte[] data = Encoding.UTF8.GetBytes("sendir," + device + "," + cis + "," + commandData + "\r\n");
            await socket.WriteAsync(data, 0, data.Length);

            for (int i = 0; i < incomingData.Length; i++)
            {
                if (await socket.ReadAsync(incomingData, i, 1) != 1)
                {
                    throw new IOException("Failed to read response");
                }

                if (incomingData[i] == 13 || incomingData[i] == 0)
                {
                    String incomingResponse = Encoding.UTF8.GetString(incomingData, 0, i);
                    String expecting = "completeir," + device + "," + cis;
                    String expecting2 = "busyIR," + device + "," + cis;
                    if (incomingResponse.Equals(expecting) || incomingResponse.Equals(expecting2))
                    {
                        return;
                    }

                    throw new IOException("Expected \"" + expecting + "\" but got \"" + incomingResponse + "\"");
                }
            }
            throw new IOException("Failed to get response");
        }

        private async Task reconnectAsync()
        {
            try
            {
                client?.Dispose();
            }
            catch (Exception)
            {
            }
            try
            {
                socket?.Dispose();
            }
            catch (Exception)
            {
            }

            IPAddress[] addresses = await Dns.GetHostAddressesAsync(hostname);
            client = new TcpClient();
            client.SendTimeout = 1000;
            client.ReceiveBufferSize = 1000;
            await client.ConnectAsync(addresses[0], port);
            client.Client.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.KeepAlive, true);

            socket = client.GetStream();
        }
    }
}