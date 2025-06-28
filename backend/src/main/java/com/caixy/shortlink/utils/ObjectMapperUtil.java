package com.caixy.shortlink.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import org.apache.poi.ss.formula.functions.T;

import java.util.Map;


/**
 * 对象Mapper工具类
 *
 * @Author CAIXYPROMISE
 * @name com.caixy.onlineJudge.common.jackson.ObjectUtil
 * @since 2024/8/28 下午4:48
 */
public class ObjectMapperUtil
{
    @FunctionalInterface
    public interface CheckedSupplier<T>
    {
        T get() throws Exception;
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static <T> T baseHandler(CheckedSupplier<T> supplier)
    {
        T result = null;
        try
        {
            result = supplier.get();
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException("JSON processing error: " + e.getMessage(), e);
        }
        catch (Exception e)
        {
            throw new RuntimeException("General error: " + e.getMessage(), e);
        }
        return result;
    }

    public static <T> T readValue(Object source, Class<T> valueType)
    {
        return baseHandler(() -> objectMapper.readValue(source.toString(), valueType));
    }

    public static <T> T readValue(byte[] source, Class<T> valueType)
    {
        return baseHandler(() -> objectMapper.readValue(source, valueType));
    }

    public static <T> T convertValue(Object source, Class<T> valueType)
    {
        return baseHandler(() -> objectMapper.convertValue(source, valueType));
    }

    public static <T> T convertValue(Object source, MapType valueType)
    {
        return baseHandler(() -> objectMapper.convertValue(source, valueType));
    }

    /**
     * 将对象转换为 Map<K,V> 结构
     *
     * @param source     源对象
     * @param keyType    Map Key 类型
     * @param valueType  Map Value 类型
     * @param <K>        Key 泛型
     * @param <V>        Value 泛型
     * @return Map<K,V> 结果
     */
    public static <K, V> Map<K, V> convertToMap(Object source, Class<K> keyType, Class<V> valueType)
    {
        return baseHandler(() ->
        {
            JavaType mapType = objectMapper.getTypeFactory().constructMapType(Map.class, keyType, valueType);
            return objectMapper.convertValue(source, mapType);
        });
    }
    /**
     * 将 Map<K,V> 结构转换为指定类型的 Java 对象
     *
     * @param map        源 Map
     * @param targetType 目标类型
     * @param <T>        目标对象泛型
     * @return 目标对象
     */
    public static <T> T convertFromMap(Map<?, ?> map, Class<T> targetType)
    {
        return baseHandler(() -> objectMapper.convertValue(map, targetType));
    }
}