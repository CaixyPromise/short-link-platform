package com.caixy.shortlink.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.internal.LinkedTreeMap;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * GsonRedis构造器
 *
 * @author CAIXYPROMISE
 * @name com.caixy.shortlink.config.GsonRedisSerializer
 * @since 2024-06-16 11:07
 **/
public class GsonRedisSerializer<T> implements RedisSerializer<T>
{
    private final Gson gson;
    private final Type type;

    public GsonRedisSerializer(Type type)
    {
        this.type = type;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Long.class, (JsonSerializer<Long>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
                .registerTypeAdapter(Long.TYPE, (JsonSerializer<Long>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
                .create();
    }

    @Override
    public byte[] serialize(T t)
    {
        return gson.toJson(t).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public T deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        String json = new String(bytes, StandardCharsets.UTF_8);

        // 通过Gson反序列化成LinkedTreeMap
        Object obj = gson.fromJson(json, Object.class);

        // 处理LinkedTreeMap对象
        if (obj instanceof LinkedTreeMap) {
            obj = convertLinkedTreeMap((LinkedTreeMap<?, ?>) obj);
        }

        return gson.fromJson(gson.toJson(obj), type); // 转换为目标类型
    }

    /**
     * 递归转换LinkedTreeMap为目标类型的对象
     *
     * @param linkedTreeMap LinkedTreeMap实例
     * @return 转换后的目标对象
     */
    private Object convertLinkedTreeMap(LinkedTreeMap<?, ?> linkedTreeMap) {
        LinkedTreeMap<String, Object> result = new LinkedTreeMap<>();
        for (Map.Entry<?, ?> entry : linkedTreeMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof LinkedTreeMap) {
                result.put(entry.getKey().toString(), convertLinkedTreeMap((LinkedTreeMap<?, ?>) value));
            } else {
                result.put(entry.getKey().toString(), value);
            }
        }
        return result;
    }
}

