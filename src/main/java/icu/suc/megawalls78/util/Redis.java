package icu.suc.megawalls78.util;

import icu.suc.megawalls78.MegaWalls78;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Redis {

    private static final JedisPool POOL = new JedisPool(MegaWalls78.getInstance().getConfigManager().host, MegaWalls78.getInstance().getConfigManager().port);

    public static Jedis get() {
        return POOL.getResource();
    }

    public static void close(Jedis jedis) {
        POOL.returnResource(jedis);
    }
}