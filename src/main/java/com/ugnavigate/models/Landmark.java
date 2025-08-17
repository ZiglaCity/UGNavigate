package com.ugnavigate.models;

import java.util.Map;

public class Landmark {
    private String name;
    private double lat;
    private double lon;
    private long id;
    private String osm_type;
    private Map<String, String> tags;

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public long getId() {
        return id;
    }

    public String getOsm_type() {
        return osm_type;
    }

    public Map<String, String> getTags() {
        return tags;
    }
}
