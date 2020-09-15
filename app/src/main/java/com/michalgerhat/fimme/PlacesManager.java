package com.michalgerhat.fimme;

import android.content.Context;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;

class PlacesManager
{
    private static final String filename = "places.json";
    ArrayList<LocationObject> placesArray;
    private Context context;

    PlacesManager(Context context)
    {
        this.context = context;
        this.placesArray = parsePlaces();
    }

    private String loadPlaces()
    {
        try
        {
            FileInputStream inputStream = context.openFileInput(filename);
            if (inputStream != null)
            {
                InputStreamReader isReader = new InputStreamReader(inputStream);
                BufferedReader bfReader = new BufferedReader(isReader);
                StringBuilder stringBuilder = new StringBuilder();
                String tmp = "";

                while ((tmp = bfReader.readLine()) != null)
                    stringBuilder.append("\n").append(tmp);

                inputStream.close();
                return stringBuilder.toString();
            }
            return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private ArrayList<LocationObject> parsePlaces()
    {
        ArrayList<LocationObject> places = new ArrayList<>();
        String placesJson = this.loadPlaces();
        if (placesJson != null)
        {
            try
            {
                JSONArray jsonArray = new JSONArray(placesJson);
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject place = jsonArray.getJSONObject(i);
                    String name = place.getString("displayName");
                    double lat = place.getDouble("lat");
                    double lon = place.getDouble("lon");
                    double alt = place.getDouble("alt");
                    places.add(new LocationObject(name, lat, lon, alt));
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        return places;
    }

    private String stringifyPlaces()
    {
        try
        {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < placesArray.size(); i++)
            {
                LocationObject place = placesArray.get(i);
                JSONObject jsonPlace = new JSONObject();
                jsonPlace.put("displayName", place.displayName);
                jsonPlace.put("lat", place.lat);
                jsonPlace.put("lon", place.lon);
                jsonPlace.put("alt", place.alt);
                jsonArray.put(i, jsonPlace);
            }
            return jsonArray.toString();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private void savePlaces()
    {
        String placesString = stringifyPlaces();
        if (placesString != null)
        {
            try
            {
                OutputStreamWriter outputStream = new OutputStreamWriter(
                        context.openFileOutput(filename, Context.MODE_PRIVATE));
                outputStream.write(placesString);
                outputStream.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    void addPlace(LocationObject place)
    {
        placesArray.add(place);
        savePlaces();
    }

    void removePlace(LocationObject place)
    {
        Iterator<LocationObject> it = placesArray.iterator();
        while (it.hasNext())
        {
            LocationObject nextPlace = it.next();
            if (place.equals(nextPlace))
                it.remove();
        }
        savePlaces();
    }

    void sharePlace(LocationObject place)
    {
        System.out.println("sharing place");
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "http://fimme/location?name=" + place.displayName +
                "&lat=" + place.lat + "&lon=" + place.lon + "&alt=" + place.alt);
        Intent shareIntent = Intent.createChooser(sendIntent, "Share location");
        context.startActivity(shareIntent);
    }
}
