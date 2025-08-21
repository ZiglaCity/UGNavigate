package com.ugnavigate.models;

import java.util.Map;

/**
 * Landmark model. Made mutable and with a no-arg constructor so Gson can deserialize reliably.
 */
public class Landmark {
    private String name;
    private double lat;
    private double lon;
    private double x;
    private double y;
    private long id;
    private String osm_type;
    private Map<String, String> tags;

    // No-arg constructor required by Gson
    public Landmark() {
    }

    public Landmark(String name, double lat, double lon, long id, String osm_type, Map<String, String> tags) {
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

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOsm_type() {
        return osm_type;
    }

    public void setOsm_type(String osm_type) {
        this.osm_type = osm_type;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }
}
