package com.huitong.coolchat.service;

import com.huitong.coolchat.redis.RedisClient;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
@Slf4j
public class ChatHistoryService {

    private RedisClient redisClient = new RedisClient();
    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHMM");
    public void recordMessage(String msg) {
        redisClient.getRedisClient().lpush("chatMsg-" + format.format(new Date()), msg);
    }

    public void extractAllMessage() {
        Jedis jedis = redisClient.getRedisClient();
        Set<String> keys = jedis.keys("chatMsg-*");
        keys.stream().forEach(key ->{
            log.info("----");
            log.info(key);
            List<String> msgHistory = jedis.lrange(key, 0, jedis.llen(key));
            msgHistory.forEach(msg -> {
                log.info(msg);
            });
            log.info("----");
        });
    }
}
