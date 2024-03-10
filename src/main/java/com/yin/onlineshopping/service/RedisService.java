package com.yin.onlineshopping.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Collections;

@Service
@Slf4j
public class RedisService {
    @Resource
    private JedisPool jedisPool;

    public void setValue(String key, String value) {
        Jedis resource = jedisPool.getResource();
        resource.set(key, value);
        resource.close();
    }

    public void setValue(String key, Long value) {
        Jedis resource = jedisPool.getResource();
        resource.set(key, value.toString());
        resource.close();
    }
    public String getValue(String key) {
        Jedis resource = jedisPool.getResource();
        String value = resource.get(key);
        resource.close();
        return value;
    }

    public long stockDeduct(String redisKey) {
        Jedis resource = jedisPool.getResource();
        String script =
                "if redis.call('exists', KEYS[1]) == 1 then\n" +
                        "    local stock = tonumber(redis.call('get', KEYS[1]))\n" +
                        "    if (stock<=0) then\n" +
                        "        return -1\n" +
                        "    end\n" +
                        "\n" +
                        "    redis.call('decr', KEYS[1]);\n" +
                        "    return stock - 1;\n" +
                        "end\n" +
                        "\n" +
                        "return -1;";
        Long stock = (Long)resource.eval(script, Collections.singletonList(redisKey), Collections.emptyList());
        resource.close();
        if (stock < 0) {
            log.info("There is no stock available ");
            return -1;
        } else {
            return stock;
        }
    }

    public boolean tryGetDistributedLock(String lockKey, String requestId, int expireTime) {
        Jedis resource = jedisPool.getResource();
        String result = resource.set(lockKey, requestId, "NX", "PX", expireTime);
        resource.close();
        if ("OK".equals(result)) {
            return true;
        }
        return false;
    }

    public boolean releaseDistributedLock(String lockKey, String requestId) {
        Jedis resource = jedisPool.getResource();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1]" +
                " then return redis.call('del', KEYS[1])" +
                " else return 0 end";
        Long result = (Long) resource.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
        resource.close();
        if(result == 1L) {
            return true;
        }
        return false;
    }

    public void revertStock(String key) {
        Jedis resource = jedisPool.getResource();
        resource.incr(key);
        resource.close();
    }

    public boolean isInDenyList(Long userId, Long commodityId) {
        Jedis jedisClient = jedisPool.getResource();
        Boolean isInDenyList = jedisClient.sismember("denyList:" + userId,
                String.valueOf(commodityId));
        jedisClient.close();
        log.info("userId: {} , commodityId {} is InDenyList result: {}", userId, commodityId,
                isInDenyList);
        return isInDenyList;
    }

    public void addToDenyList(Long userId, Long commodityId) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.sadd("denyList:" + userId,
                String.valueOf(commodityId));
        jedisClient.close();
        log.info("Add userId: {} into denyList for commodityId: {}", userId, commodityId);
    }

    public void removeFromDenyList(Long userId, Long commodityId) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.srem("denyList:" + userId,
                String.valueOf(commodityId));
        jedisClient.close();
        log.info("Remove userId: {} into denyList for commodityId: {}", userId, commodityId);
    }

}
