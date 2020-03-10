package com.michalgerhat.fimme;

import android.location.Location;

public class LocationObject
{
    double lat;
    double lon;
    double alt;

    public LocationObject(double lat, double lon, double alt)
    {
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
    }

    public LocationObject(Location l)
    {
        this.lat = l.getLatitude();
        this.lon = l.getLongitude();
        this.alt = l.getAltitude();
    }

    public double getDistance(LocationObject other)
    {
        // Haversine method
        // https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude

        final int R = 6371000; // earth radius

        double latDistance = Math.toRadians(this.lat - other.lat);
        double lonDistance = Math.toRadians(this.lon - other.lon);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(other.lat)) * Math.cos(Math.toRadians(this.lat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = Math.sqrt(Math.pow(R * c, 2) + Math.pow(other.alt - this.alt, 2));

        return distance;
    }
}
