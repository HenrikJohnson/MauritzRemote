using Microsoft.AspNetCore;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;

namespace RemoteServer
{
    public class Program
    {
        public static void Main(string[] args)
        {
            WebHost.CreateDefaultBuilder(args)
                .UseStartup<Startup>()
                .ConfigureLogging(loggingBuilder =>
                {
                    loggingBuilder.AddConsole();
                    loggingBuilder.AddDebug();
                })
                .Build().Run();
        }
    }
}
