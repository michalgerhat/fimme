package com.michalgerhat.fimme;

import android.location.Location;
import java.io.Serializable;

public class LocationObject implements Serializable
{
    private static final long serialVersionUID = 1337;

    String displayName;
    double lat;
    double lon;
    double alt;

    LocationObject(String displayName, double lat, double lon, double alt)
    {
        this.displayName = displayName;
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
    }

    LocationObject(Location l)
    {
        this.displayName = "me";
        this.lat = l.getLatitude();
        this.lon = l.getLongitude();
        this.alt = l.getAltitude();
    }

    int getDistance(LocationObject other)
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

        double distance = Math.round(Math.sqrt(Math.pow(R * c, 2) + Math.pow(other.alt - this.alt, 2)));

        return (int)distance;
    }

    int getBearing(LocationObject other)
    {
        // https://www.igismap.com/formula-to-find-bearing-or-heading-angle-between-two-points-latitude-longitude/

        double deltaLon = Math.abs(this.lon - other.lon);
        double x = Math.cos(other.lat) * Math.sin(deltaLon);
        double y = (Math.cos(this.lat) * Math.sin(other.lat)) - (Math.sin(this.lat) * Math.cos(other.lat) * Math.cos(deltaLon));
        double bearingRad = Math.atan2(x, y);
        return (int)Math.round(Math.toDegrees(bearingRad));
    }

    @Override
    public String toString()
    {
        return this.displayName;
    }
}
