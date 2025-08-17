package com.ugnavigate.models;

import com.ugnavigate.utils.LandmarkLoader;

import java.util.HashMap;
import java.util.Map;

public class Landmark {
    private final String name;
    private final double lat;
    private final double lon;
    private final long id;
    private final String osm_type;
    private final Map<String, String> tags;

    public Landmark(String name, double lat, double lon, long id, String osm_type, Map<String, String> tags){
        this.name = name;
        this.lat = lat;
        this.lon = lon;
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

    public Map<String, String> getTags() {
        return tags;
    }
}
