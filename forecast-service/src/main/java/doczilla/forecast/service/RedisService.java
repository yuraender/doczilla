package doczilla.forecast.service;

import redis.clients.jedis.Jedis;

public class RedisService {

    private final int cacheTTL;
    private final Jedis jedis;

    public RedisService(String host, int port, int cacheTTL) {
        this.cacheTTL = cacheTTL;
        this.jedis = new Jedis(host, port);
    }

    public String loadCached(String city) {
        return jedis.get(city);
    }

    public void setCached(String city, String data) {
        jedis.setex(city, cacheTTL, data);
    }
}
