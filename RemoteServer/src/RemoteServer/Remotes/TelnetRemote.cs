using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using RemoteServer.Config;

namespace RemoteServer.Remotes
{
    public class TelnetRemote : IRemoteTarget
    {
        public class Factory : IRemoteTargetFactory
        {
            public IRemoteTarget createTarget(Dictionary<string, string> options, ILoggerFactory loggerFactory, IConfigurationManager config)
            {
                return new TelnetRemote(options["Host"], Int32.Parse(options["Port"]), loggerFactory, config);
            }
        }

        private String hostname;
        private int port;
        private object connectionLock = new object();
        private TcpClient client;
        private NetworkStream socket;
        private IConfigurationManager config;
        private ILogger logger;
        private CancellationTokenSource tokenSource;
        private const int TimeoutMillis = 5000;

        public TelnetRemote(String hostname, int port, ILoggerFactory loggerFactory, IConfigurationManager config)
        {
            this.hostname = hostname;
            this.port = port;
            this.config = config;

            this.logger = loggerFactory.CreateLogger<TelnetRemote>();
        }

        public virtual async Task<String> sendCommandAsync(string command)
        {
            logger.LogDebug("Entering send command {0}", command);
            try
            {
                lock (connectionLock)
                {
                    tokenSource?.Cancel();
                }
                if (socket == null)
                {
                    await reconnectAsync();
                }

                try
                {
                    await actualSend(command);
                }
                catch (Exception exc)
                {
                    logger.LogError(new EventId(1), exc, "Failed sending {0}, retrying", command);

                    await reconnectAsync();

                    try
                    {
                        await actualSend(command);
                    }
                    catch (Exception exc2)
                    {
                        CloseSocket(socket, client);

                        client = null;
                        socket = null;
                        throw new IOException("Failed to send", exc2);
                    }
                }

                tokenSource = new CancellationTokenSource();
                var timeoutToken = tokenSource.Token;
                Task task = Task.Delay(TimeoutMillis, timeoutToken).ContinueWith((t) =>
                {
                    if (!timeoutToken.IsCancellationRequested)
                    {
                        TcpClient oldClient;
                        NetworkStream oldStream;
                        lock (connectionLock)
                        {
                            oldClient = client;
                            oldStream = socket;
                            client = null;
                            socket = null;
                        }

                        logger.LogInformation("Closing connection");

                        CloseSocket(oldStream, oldClient);
                    }
                });
            }
            finally
            {
                logger.LogDebug("Leaving send command {0}", command);
            }

            return "OK";
        }

        private void CloseSocket(NetworkStream socket, TcpClient client)
        {
            try
            {
                socket?.Dispose();
            }
            catch (Exception)
            {
                logger.LogError(new EventId(2), "Failed to close stream");
            }
            try
            {
                client?.Client?.Dispose();
            }
            catch (Exception)
            {
                logger.LogError(new EventId(2), "Failed to close socket");
            }
            try
            {
                client?.Dispose();
            }
            catch (Exception)
            {
                logger.LogError(new EventId(2), "Failed to close client");
            }
        }

        private async Task actualSend(string command)
        {
            byte[] incomingData = new byte[1024];
            while (socket.DataAvailable)
            {
                if (await socket.ReadAsync(incomingData, 0, 1) <= 0)
                    await reconnectAsync();
                else
                {
                    await ProcessTelnetBytes(incomingData, 0);
                }
            }

            String commandData = config.getCommandData(command);

            byte[] data = Encoding.UTF8.GetBytes(commandData + "\r");
            await socket.WriteAsync(data, 0, data.Length);
        }

        private async Task<bool> ProcessTelnetBytes(byte[] inputData, int offset)
        {
            switch (inputData[offset])
            {
                case InterpretAsCommand:
                    if (await socket.ReadAsync(inputData, offset, 1) <= 0)
                        throw new IOException("Failed to read Telnet command");

                    byte cmd = inputData[offset];
                    switch (cmd)
                    {
                        case InterpretAsCommand:
                            return true;
                        case Will:
                        case Wont:
                        case Do:
                        case Dont:
                            if (await socket.ReadAsync(inputData, offset, 1) <= 0)
                                throw new IOException("Failed to read Telnet option");

                            byte[] outputData = new byte[2];
                            outputData[0] = InterpretAsCommand;
                            if (inputData[0] == SuppressGoAhead)
                                outputData[1] = cmd == Do ? Will : Do;
                            else
                                outputData[1] = cmd == Do ? Wont : Dont;
                            await socket.WriteAsync(outputData, 0, outputData.Length);
                            return false;
                    }
                    break;
            }
            return true;
        }

        private const byte InterpretAsCommand = 255;
        private const byte Will = 251;
        private const byte Wont = 252;
        private const byte Do = 253;
        private const byte Dont = 254;
        private const byte SuppressGoAhead = 3;

        private async Task reconnectAsync()
        {
            CloseSocket(socket, client);

            IPAddress[] addresses = await Dns.GetHostAddressesAsync(hostname);
            TcpClient newClient = new TcpClient();
            newClient.SendTimeout = 1000;
            newClient.ReceiveBufferSize = 1000;
            await newClient.ConnectAsync(addresses[0], port);
            socket = newClient.GetStream();
            client = newClient;
        }
    }
}