using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using RemoteServer.Config;
using RemoteServer.Library;
using RemoteServer.Remotes;

namespace RemoteServer
{
    // webapp/nFI2T97jaG9EpmBz350b

    public class Startup
    {
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
            // Add framework services.
            services.AddMvc();


            services.AddCors(options =>
            {
                options.AddPolicy(name: "Anybody",
                                  builder => { builder.AllowAnyOrigin().AllowAnyHeader().AllowAnyMethod().AllowCredentials(); });
            });
            services.AddSingleton<IConfigurationManager, ConfigurationManager>();
            services.AddSingleton<IRemoteManager, RemoteManager>();

            services.AddTransient<ILibraryRepository, JukeboxLibraryRepository>();
        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IHostingEnvironment env, ILoggerFactory loggerFactory)
        {
            loggerFactory.AddConsole(Configuration);
            loggerFactory.AddDebug();

            app.UseCors("Anybody");
            app.UseMvc();
        }
    }
}
