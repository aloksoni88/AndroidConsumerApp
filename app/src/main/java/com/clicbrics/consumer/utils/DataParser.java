package com.clicbrics.consumer.utils;

/**
 * Created by root on 13/10/16.
 */

import android.util.Log;

import com.clicbrics.consumer.framework.util.HousingLogs;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    private static final String TAG = DataParser.class.getSimpleName();

    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<List<HashMap<String,String>>> getRouteCoordinates(JSONObject jObject){

        List<List<HashMap<String, String>>> routes = new ArrayList<>() ;
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;

        try {

            jRoutes = jObject.getJSONArray("routes");

            // Traversing all routes
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<>();

                // Traversing all legs
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    // Traversing all steps
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        // Traversing all points
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString((list.get(l)).latitude) );
                            hm.put("lng", Double.toString((list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }

        return routes;
    }

    //Method to decode polyline points
    public List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = null;
        try {
            poly = new ArrayList<>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return poly;
    }

    public List<HashMap<String, String>> parse(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject((String) jsonData);
            jsonArray = jsonObject.getJSONArray("results");
            for(int i=0;i<jsonArray.length();i++)
                Log.d("PLACES","jsonArray->"+jsonArray.get(i));
        } catch (Exception e) {
            Log.d("Places", "parse error");
            e.printStackTrace();
            TrackAnalytics.trackException(TAG, "Error in parse\n"+e.getMessage(), e);
        }
        return getPlaces(jsonArray);
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {
        List<HashMap<String, String>> placesList = null;
        try {
            int placesCount = jsonArray.length();
            placesList = new ArrayList<>();
            HashMap<String, String> placeMap = null;
            Log.d("Places", "getPlaces");

            for (int i = 0; i < placesCount; i++) {
                try {
                    placeMap = getPlace((JSONObject) jsonArray.get(i));
                    placesList.add(placeMap);
                    Log.d("Places", "Adding places");

                } catch (JSONException e) {
                    Log.d("Places", "Error in Adding places");
                    e.printStackTrace();
                    TrackAnalytics.trackException(TAG, "Error in Adding Places\n"+e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return placesList;
    }

    private HashMap<String, String> getPlace(JSONObject googlePlaceJson) {
        HashMap<String, String> googlePlaceMap = new HashMap<String, String>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";

        Log.d("getPlace", "Entered");

        try {
            if (!googlePlaceJson.isNull("name")) {
                placeName = googlePlaceJson.getString("name");
            }
            if (!googlePlaceJson.isNull("vicinity")) {
                vicinity = googlePlaceJson.getString("vicinity");
            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJson.getString("reference");
            googlePlaceMap.put("place_name", placeName);
            googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("reference", reference);
            Log.d("getPlace", "Putting Places");
        } catch (JSONException e) {
            Log.d("getPlace", "Error");
            e.printStackTrace();
            TrackAnalytics.trackException(TAG,"Error in getPlaces\n" + e.getMessage(), e);
        }
        return googlePlaceMap;
    }

    public static String getDirectionsURL (double sourcelat, double sourcelog, double destlat, double destlog, String PLACES_KEY){

        StringBuilder urlString = null;
        try {
            urlString = new StringBuilder();
            urlString.append("https://maps.googleapis.com/maps/api/directions/json");
            urlString.append("?origin=");// from
            urlString.append(Double.toString(sourcelat));
            urlString.append(",");
            urlString
                    .append(Double.toString( sourcelog));
            urlString.append("&destination=");// to
            urlString
                    .append(Double.toString( destlat));
            urlString.append(",");
            urlString.append(Double.toString( destlog));
            urlString.append("&sensor=false&mode=driving&alternatives=true");
            urlString.append("&key="+PLACES_KEY);
            Log.d(TAG,"URL->"+urlString.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlString.toString();
    }

    public static String getNearbyUrl(double latitude, double longitude, String nearbyPlace, int radius, String PLACES_KEY) {

        StringBuilder googlePlacesUrl = null;
        try {
            googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            googlePlacesUrl.append("location=" + latitude + "," + longitude);
            googlePlacesUrl.append("&radius=" + radius);
            if(nearbyPlace.equals("train_station")){
                //append subway_station for metro
                googlePlacesUrl.append("&type=" + nearbyPlace+"|subway_station");
            }else {
                googlePlacesUrl.append("&type=" + nearbyPlace);
            }//googlePlacesUrl.append("&sensor=true");
            if (nearbyPlace.equals("airport")) {
                googlePlacesUrl.append("&keyword=airport");
            } else if (nearbyPlace.equals("train_station")) {
                googlePlacesUrl.append("&keyword=station");
            }
            if (nearbyPlace.equals("petrol_pump")) {
                googlePlacesUrl.append("&keyword=petrol");

            }
            googlePlacesUrl.append("&hasNextPage=" + true);
            googlePlacesUrl.append("&nextPage()=" + true);
            googlePlacesUrl.append("&key=" + PLACES_KEY); //getResources().getString(R.string.google_maps_key));

            Log.d("getUrl", googlePlacesUrl.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //return "https://maps.googleapis.com/maps/api/place/search/xml?location=28.45835389,77.07133339&radius=50000&types=country%7Cairport%7Camusement_park%7Cbank%7Cbook_store%7Cbus_station%7Ccafe%7Ccar_rental%7Ccar_repair%7Cchurch%7Cdoctor%7Cfire_station%7Cfood%7Chindu_temple%7Chospital%7Clawyer%7Clibrary%7Cmosque%7Cmuseum%7Cpark%7Cparking%7Cpharmacy%7Cpolice%7Cpost_office%7Crestaurant%7Cschool%7Ctrain_station%7Czoo&sensor=true&key="
        //        +"AIzaSyCSa6I2sv9siUv9oP5B_uPQ8XF3oe_Kmv4";
        return (googlePlacesUrl.toString());
    }

    //static List<String> datList = new ArrayList<>();
    public static String getData(URL url) {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        HousingLogs.d(TAG, "URL->" + url);
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            inputStream = httpURLConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer buffer = new StringBuffer();

            String line = "";
            while (reader != null && (line = reader.readLine()) != null) {
                buffer.append(line);
            }
            data = buffer.toString();
            HousingLogs.d(TAG, "data->" + data);
            //datList.add(data);
            reader.close();
            /*try {
                JSONObject jsonObject = new JSONObject(data);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            httpURLConnection.disconnect();
        }

        return data;
    }
}

