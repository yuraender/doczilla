package doczilla.forecast.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import doczilla.forecast.util.RequestUtil;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
public class WeatherService {

    private final RedisService redisService;
    private final ObjectMapper mapper;

    public Map<String, Object> getWeather(String city) throws Exception {
        String cached = redisService.loadCached(city);
        if (cached != null) {
            return mapper.readValue(cached, Map.class);
        }

        // 1. Получаем координаты города
        String geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + city;
        String geoResponse = RequestUtil.sendGet(geoUrl);
        JsonNode geoJson = mapper.readTree(geoResponse);
        JsonNode results = geoJson.get("results");
        if (results == null || !results.isArray() || results.isEmpty()) {
            throw new RuntimeException("Город не найден");
        }
        double latitude = results.get(0).get("latitude").asDouble();
        double longitude = results.get(0).get("longitude").asDouble();

        // 2. Получаем прогноз погоды
        String weatherUrl = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&hourly=temperature_2m",
                latitude, longitude
        );
        String weatherResponse = RequestUtil.sendGet(weatherUrl);
        JsonNode weatherJson = mapper.readTree(weatherResponse);

        JsonNode hourly = weatherJson.get("hourly");
        JsonNode times = hourly.get("time");
        JsonNode temperatures = hourly.get("temperature_2m");

        List<Date> timeList = new ArrayList<>();
        List<Double> temperatureList = new ArrayList<>();
        OffsetDateTime now = OffsetDateTime.now();
        for (int i = 0; i < temperatures.size(); i++) {
            OffsetDateTime parse = LocalDateTime
                    .parse(times.get(i).asText(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
                    .atOffset(ZoneOffset.UTC);
            if (parse.isBefore(now) || parse.isAfter(now.plusDays(1))) {
                continue;
            }
            timeList.add(Date.from(parse.toInstant()));
            temperatureList.add(temperatures.get(i).asDouble());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("city", city);
        result.put("times", timeList);
        result.put("temperatures", temperatureList);

        // 3. Сохраняем в Redis
        redisService.setCached(city, mapper.writeValueAsString(result));

        return result;
    }
}
