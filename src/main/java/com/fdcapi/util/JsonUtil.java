package com.fdcapi.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fdcapi.exception.ApiException;
import com.fdcapi.exception.ErrorCodes;

public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.INDENT_OUTPUT, true);

    private JsonUtil() {
        // Utility class
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new ApiException(ErrorCodes.INTERNAL_SERVER_ERROR, 
                "Error parsing JSON: " + e.getMessage(), e);
        }
    }

    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new ApiException(ErrorCodes.INTERNAL_SERVER_ERROR, 
                "Error generating JSON: " + e.getMessage(), e);
        }
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
