package com.klmklm.meeting_attendance.lib;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.persistence.AttributeConverter;
import java.util.HashMap;
import java.util.Map;

public class JsonConverter implements AttributeConverter<Map<String,Object>,String> {
    @Override
    public String convertToDatabaseColumn(Map<String,Object> data) {
        return new Gson().toJson(data);
    }

    @Override
    public Map<String,Object> convertToEntityAttribute(String json) {
        JsonParser jp = new JsonParser();
        JsonObject jo = jp.parse(json).getAsJsonObject();
        Map<String, Object> result = new HashMap<>();
        for (String key : jo.keySet()) {
            result.put(key, jo.get(key).getAsString());
        }
        return result;
    }
}
