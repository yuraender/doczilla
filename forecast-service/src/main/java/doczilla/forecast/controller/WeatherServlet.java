package doczilla.forecast.controller;

import doczilla.forecast.service.WeatherService;
import lombok.RequiredArgsConstructor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class WeatherServlet extends HttpServlet {

    private final WeatherService weatherService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String city = req.getParameter("city");
        if (city == null || city.isEmpty()) {
            resp.setStatus(400);
            resp.getWriter().write("Parameter 'city' is not specified.");
            return;
        }
        try {
            Map<String, Object> weatherData = weatherService.getWeather(city);
            byte[] imageData = weatherService.plotTemperatureGraph(weatherData);

            resp.setContentType("image/png");
            resp.setContentLength(imageData.length);
            resp.getOutputStream().write(imageData);
            resp.getOutputStream().flush();
        } catch (Exception ex) {
            ex.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("Error: " + ex.getMessage());
        }
    }
}
