package doczilla.forecast.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import doczilla.forecast.service.WeatherService;
import lombok.RequiredArgsConstructor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class WeatherServlet extends HttpServlet {

    private final WeatherService weatherService;
    private final ObjectMapper mapper;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String city = req.getParameter("city");
        if (city == null || city.isEmpty()) {
            resp.setStatus(400);
            resp.getWriter().write("Parameter 'city' is not specified.");
            return;
        }
        try {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            mapper.writeValue(resp.getWriter(), weatherService.getWeather(city));
        } catch (Exception ex) {
            ex.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("Error: " + ex.getMessage());
        }
    }
}
