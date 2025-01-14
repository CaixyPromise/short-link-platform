package com.caixy.shortlink.config.gson.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Date;

/**
 * 时间戳gson适配器
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/13 18:02
 */
public class TimestampDateAdapter extends TypeAdapter<Date>
{
    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.getTime()); // 输出为时间戳
        }
    }

    @Override
    public Date read(JsonReader in) throws IOException
    {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        long timestamp = in.nextLong();
        return new Date(timestamp); // 从时间戳解析为 Date
    }
}