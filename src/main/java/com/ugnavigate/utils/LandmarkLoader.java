package com.ugnavigate.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ugnavigate.models.Landmark;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;

public class LandmarkLoader {
    public static Map<String, Landmark> loadLandmarks(String filePath) {
        try {
            InputStream inputStream = LandmarkLoader.class.getClassLoader()
                    .getResourceAsStream(filePath);

            if (inputStream == null) {
                throw new RuntimeException("File not found in resources: " + filePath);
            }

            Reader reader = new InputStreamReader(inputStream);            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Landmark>>() {}.getType();
            return gson.fromJson(reader, type);
        } catch (Exception e) {
            throw new RuntimeException("Error loading landmarks: " + filePath, e);
        }
    }
}
