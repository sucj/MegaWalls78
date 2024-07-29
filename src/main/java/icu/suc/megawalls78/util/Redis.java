package icu.suc.kevin557.mw78lobby.util;

import icu.suc.kevin557.mw78lobby.MW78Lobby;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Redis {

    private static final JedisPool POOL = new JedisPool(MW78Lobby.HOST, MW78Lobby.PORT);

    public static Jedis get() {
        return POOL.getResource();
    }

    public static void close(Jedis jedis) {
        POOL.returnResource(jedis);
    }
}