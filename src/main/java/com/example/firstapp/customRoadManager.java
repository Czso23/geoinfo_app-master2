package com.example.firstapp;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.utils.BonusPackHelper;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;


public class customRoadManager extends RoadManager {

    public customRoadManager(Context context){
        super(context);
       // mContext = context;
        mServiceUrl = SERVICE;
        mUserAgent = BonusPackHelper.DEFAULT_USER_AGENT;
    }

    private static final String SERVICE = "https://api.openrouteservice.org/v2/directions/driving-car";


    private String getApiString (ArrayList<GeoPoint> waypoints) {
        StringBuilder string = new StringBuilder(SERVICE);
        string.append("?api_key=5b3ce3597851110001cf6248daf1be4a1e8b4ba1b5f3a680d51485a2");
        string.append("&start=");
        GeoPoint start = waypoints.get(0);
        string.append(start);
        string.append("&end=");
        GeoPoint end = waypoints.get(1);
        string.append(end);

        return string.toString();
    }


    @Override
    public Road getRoad(ArrayList<GeoPoint> waypoints) {
        String url = getApiString(waypoints);
        String result = BonusPackHelper.requestStringFromUrl(url);
        if (url == null){
            return new Road (waypoints);
        }
        Road road = new Road();
        try {
            JSONObject jRoot = new JSONObject(result);
            JSONArray jFeatures = jRoot.getJSONArray("features");
            JSONObject jNull = jFeatures.getJSONObject(0);
            JSONObject jGeometrie = jNull.getJSONObject("geometry");
            JSONArray jKoordinaten = jGeometrie.getJSONArray("coordinates");
            int jLength = jKoordinaten.length();
            road.mRouteHigh = new ArrayList<>(jLength);

            for (int i= 0; 1< jLength; i++){
                JSONArray punkte = jKoordinaten.getJSONArray (i);
                double lon = punkte.getDouble(1);
                double lat = punkte.getDouble(0);
                GeoPoint p = new GeoPoint(lon,lat);
                road.mRouteHigh.add(p);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return new Road (waypoints);
        }

        return road;
    }

    @Override
    public Road[] getRoads(ArrayList<GeoPoint> waypoints) {
        return new Road[0];
    }
}
