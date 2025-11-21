package doczilla.forecast;

import com.fasterxml.jackson.databind.ObjectMapper;
import doczilla.forecast.controller.WeatherServlet;
import doczilla.forecast.service.WeatherService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main {

    public static void main(String[] args) throws Exception {
        int port = 8080;

        Server server = new Server(port);
        ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
        ctx.setContextPath("/");

        ObjectMapper mapper = new ObjectMapper();
        WeatherService weatherService = new WeatherService(mapper);

        ctx.addServlet(new ServletHolder(new WeatherServlet(weatherService, mapper)), "/weather");

        server.setHandler(ctx);
        server.start();
        server.join();
    }
}
