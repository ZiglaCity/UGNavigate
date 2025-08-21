package com.ugnavigate.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.ugnavigate.models.Landmark;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LandmarkLoader {
    /**
     * Loads landmarks.json which historically was a map keyed by name -> Landmark.
     * Newer format may be an array of objects with fields {id,name,x,y,tags}.
     * This method accepts both: it first tries to parse a Map; if that fails it
     * will parse the array and convert entries into Landmark instances keyed by name.
     */
    public static Map<String, Landmark> loadLandmarks(String filePath) {
        try {
            InputStream inputStream = LandmarkLoader.class.getClassLoader()
                    .getResourceAsStream(filePath);

            if (inputStream == null) {
                throw new RuntimeException("File not found in resources: " + filePath);
            }

            byte[] bytes = inputStream.readAllBytes();
            String json = new String(bytes, StandardCharsets.UTF_8);
            Gson gson = new Gson();

            // Try old map format first
            try {
                Type type = new TypeToken<Map<String, Landmark>>() {}.getType();
                Map<String, Landmark> map = gson.fromJson(json, type);
                if (map != null && !map.isEmpty()) return map;
            } catch (JsonSyntaxException ignored) {
                // fallthrough to try array format
            }

            // Try array format: list of simplified landmark objects
            try {
                Type arrType = new TypeToken<List<Map<String, Object>>>(){}.getType();
                List<Map<String, Object>> arr = gson.fromJson(json, arrType);
                Map<String, Landmark> out = new HashMap<>();
                if (arr != null) {
                    long nextId = 1;
                    for (Map<String, Object> item : arr) {
                        Landmark lm = new Landmark();
                        // name
                        Object nameObj = item.get("name");
                        String name = nameObj != null ? nameObj.toString() : null;
                        lm.setName(name != null ? name : (item.get("id") != null ? item.get("id").toString() : "unknown"));

                        // if x/y provided, store them in lon/lat respectively so existing code using getLat/getLon still works
                        Object xObj = item.get("x");
                        Object yObj = item.get("y");
                        if (xObj != null && yObj != null) {
                            try {
                                double x = Double.parseDouble(xObj.toString());
                                double y = Double.parseDouble(yObj.toString());
                                // store pixel coordinates as x/y on the landmark
                                lm.setX(x);
                                lm.setY(y);
                                // Also set lon/lat for backward compatibility (some code expects lat/lon)
                                lm.setLon(x);
                                lm.setLat(y);
                            } catch (NumberFormatException nfe) {
                                // ignore
                            }
                        }

                        // id - try to set numeric id if provided, otherwise keep numeric nextId
                        Object idObj = item.get("id");
                        String idStr = null;
                        if (idObj != null) {
                            idStr = idObj.toString();
                            try {
                                lm.setId(Long.parseLong(idStr));
                            } catch (NumberFormatException ignored) {
                                // leave numeric id assigned below
                            }
                        }
                        if (lm.getId() == 0) {
                            lm.setId(nextId++);
                        }

                        // tags array -> put first tag as type in tags map for compatibility
                        Object tagsObj = item.get("tags");
                        if (tagsObj instanceof List) {
                            List<?> tagsList = (List<?>) tagsObj;
                            Map<String, String> tagMap = new HashMap<>();
                            if (!tagsList.isEmpty()) tagMap.put("type", tagsList.get(0).toString());
                            lm.setTags(tagMap);
                        }

                        // key the map by several keys to maximize compatibility with adjacency keys:
                        // - human-readable name (e.g. "Balme Library")
                        // - provided id string (e.g. "jqb") and its uppercase form (e.g. "JQB")
                        // - normalized variants: trimmed, upper-case, and alphanumeric-only tokens
                        if (lm.getName() != null) {
                            putNormalizedKeys(out, lm.getName(), lm);
                        }
                        if (idStr != null && !idStr.isBlank()) {
                            putNormalizedKeys(out, idStr, lm);
                        }
                    }
                }
                return out;
            } catch (JsonSyntaxException ex) {
                throw new RuntimeException("landmarks.json has an unexpected structure: " + ex.getMessage(), ex);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading landmarks: " + filePath, e);
        }
    }

    // Register multiple normalized key variants for a given landmark into the output map.
    private static void putNormalizedKeys(Map<String, Landmark> out, String key, Landmark lm) {
        if (key == null) return;
        String trimmed = key.trim();
        out.putIfAbsent(trimmed, lm);
        out.putIfAbsent(trimmed.toUpperCase(), lm);

        // alphanumeric-only (remove punctuation/spaces)
        String alnum = trimmed.replaceAll("[^A-Za-z0-9]", "");
        if (!alnum.isBlank()) out.putIfAbsent(alnum, lm);

        // if the name contains a parenthesized token like "Jones Quartey Building (JQB)", register the token
        int open = trimmed.indexOf('(');
        int close = trimmed.indexOf(')');
        if (open >= 0 && close > open) {
            String token = trimmed.substring(open + 1, close).trim();
            if (!token.isBlank()) {
                out.putIfAbsent(token, lm);
                out.putIfAbsent(token.toUpperCase(), lm);
            }
        }
    }
}
