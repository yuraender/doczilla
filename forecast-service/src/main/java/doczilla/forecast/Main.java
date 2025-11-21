package doczilla.forecast;

import com.fasterxml.jackson.databind.ObjectMapper;
import doczilla.forecast.controller.WeatherServlet;
import doczilla.forecast.service.RedisService;
import doczilla.forecast.service.WeatherService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main {

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("FORECAST_PORT", "8080"));
        String redisHost = System.getenv().getOrDefault("FORECAST_REDIS_HOST", "localhost");
        int redisPort = Integer.parseInt(System.getenv().getOrDefault("FORECAST_REDIS_PORT", "6379"));
        int cacheTTL = Integer.parseInt(System.getenv().getOrDefault("FORECAST_CACHE_TTL", "900"));

        Server server = new Server(port);
        ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
        ctx.setContextPath("/");

        ObjectMapper mapper = new ObjectMapper();

        RedisService redisService = new RedisService(redisHost, redisPort, cacheTTL);
        WeatherService weatherService = new WeatherService(redisService, mapper);

        ctx.addServlet(new ServletHolder(new WeatherServlet(weatherService)), "/weather");

        server.setHandler(ctx);
        server.start();
        server.join();
    }
}
