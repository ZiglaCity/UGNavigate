package com.ugnavigate.models;

import com.ugnavigate.utils.LandmarkLoader;

import java.util.Map;

public class Landmark {
    private String name;
    private double lat;
    private double lon;
    private long id;
    private String osm_type;
    private Map<String, Map<String, String>> tags;

    public Landmark(String name, double latitude, double longitude, long id, String osm_type, Map<String, Map<String, String>> tags){
        this.name = name;
        lat = latitude;
        lon = longitude;
        this.id = id;
        this.osm_type = osm_type;
        this.tags = tags;
    }

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

    public Map<String, Map<String, String>> getTags() {
        return tags;
    }
}
