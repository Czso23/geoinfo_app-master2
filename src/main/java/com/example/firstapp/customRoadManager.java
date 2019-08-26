package com.example.firstapp;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.bonuspack.utils.BonusPackHelper;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;


public class customRoadManager extends RoadManager {

    private static final String SERVICE = "https://api.openrouteservice.org/v2/directions/driving-car";
    private Context mContext;

    public customRoadManager(Context context){
        super();
        mContext = context;
       // mServiceUrl = SERVICE;
       // mUserAgent = BonusPackHelper.DEFAULT_USER_AGENT;
    }



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
            JSONObject jProb =jNull.getJSONObject("properties");
            JSONArray jArr = jProb.getJSONArray("segments");
            JSONObject jSeg = jArr.getJSONObject(0);
            road.mLength = jSeg.getDouble("distance")/1000;
            road.mDuration = jSeg.getDouble("duration");
            JSONArray jStep = jSeg.getJSONArray("steps");
            int jLength = jKoordinaten.length();
            road.mRouteHigh = new ArrayList<>(jLength);

            for (int i= 0; 1< jLength; i++){
                JSONArray punkte = jKoordinaten.getJSONArray (i);
                double lon = punkte.getDouble(1);
                double lat = punkte.getDouble(0);
                GeoPoint p = new GeoPoint(lon,lat);
                road.mRouteHigh.add(p);
            }

            for (int o = 0; o < jStep.length(); o++){
                RoadNode knoten = new RoadNode();
                JSONObject schritt = jStep.getJSONObject(o);
                JSONArray knotenpunkt = schritt.getJSONArray("way_points");
                knoten.mLength = schritt.getDouble("distance")/1000;
                knoten.mDuration = schritt.getDouble("duration");
                road.mNodes.add(knoten);

            }

        } catch (JSONException e) {
            e.printStackTrace();
            return new Road (waypoints);
        }

        return road;
    }

    @Override
    public Road[] getRoads(ArrayList<GeoPoint> waypoints) {
        Road road = getRoad(waypoints);
        Road []roads = new Road[1];
        roads [0]= road;
        return roads;
    }
}
