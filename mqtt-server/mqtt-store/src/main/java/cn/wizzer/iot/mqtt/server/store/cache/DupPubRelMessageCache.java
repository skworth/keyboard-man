package cn.wizzer.iot.mqtt.server.store.cache;

import cn.wizzer.iot.mqtt.server.common.message.DupPubRelMessageStore;
import com.alibaba.fastjson.JSONObject;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@IocBean
public class DupPubRelMessageCache {
    private final static String CACHE_PRE = "mqttwk:pubrel:";
    @Inject
    private RedisService redisService;
    @Inject
    private PropertiesProxy conf;

    public DupPubRelMessageStore put(String clientId, Integer messageId, DupPubRelMessageStore dupPubRelMessageStore) {
        redisService.hset(CACHE_PRE + clientId, String.valueOf(messageId), JSONObject.toJSONString(dupPubRelMessageStore));
        return dupPubRelMessageStore;
    }

    public ConcurrentHashMap<Integer, DupPubRelMessageStore> get(String clientId) {
        ConcurrentHashMap<Integer, DupPubRelMessageStore> map = new ConcurrentHashMap<>();
        Map<String, String> map1 = redisService.hgetAll(CACHE_PRE + clientId);
        if (map1 != null && !map1.isEmpty()) {
            map1.forEach((k, v) -> {
                map.put(Integer.valueOf(k), JSONObject.parseObject(v, DupPubRelMessageStore.class));
            });
        }
        return map;
    }

    public boolean containsKey(String clientId) {
        return redisService.exists(CACHE_PRE + clientId);
    }

    @Async
    public void remove(String clientId, Integer messageId) {
        redisService.hdel(CACHE_PRE + clientId, String.valueOf(messageId));
    }

    @Async
    public void remove(String clientId) {
        redisService.del(CACHE_PRE + clientId);
    }
}
