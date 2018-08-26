package com.project.natsu_dragneel.people_tracker_android_java.classes;

import com.google.android.gms.maps.model.LatLng;

public class Places {
    private String title;
    private String snippet;
    private LatLng coordinates;
    private float fenceRadius;
    private int iconResourceId;
    private float defaultZoomLevel;

    public Places(String title, String snippet, LatLng coordinates, float fenceRadius, int iconResourceId, float defaultZoomLevel) {
        this.title = title;
        this.snippet = snippet;
        this.coordinates = coordinates;
        this.fenceRadius = fenceRadius;
        this.iconResourceId = iconResourceId;
        this.defaultZoomLevel = defaultZoomLevel;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public float getFenceRadius() {
        return fenceRadius;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public float getDefaultZoomLevel() {
        return defaultZoomLevel;
    }
}
