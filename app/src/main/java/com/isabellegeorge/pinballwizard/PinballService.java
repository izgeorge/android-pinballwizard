package com.isabellegeorge.pinballwizard;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Epicodus on 4/28/16.
 */
public class PinballService {
    public static void findRegions(Callback callback){
        OkHttpClient client = new OkHttpClient.Builder().build();
        String url = HttpUrl.parse(Constants.REGION_URL).toString();
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void findLocationTypes(int id, Callback callback){
        OkHttpClient client = new OkHttpClient.Builder().build();
        String url = HttpUrl.parse(Constants.LOCATION_TYPES_URL).toString();
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public HashMap<Integer, String> processLocationTypes(Response response){
        HashMap<Integer,String> locationTypes = new HashMap<>();
        try{
            String jsonData = response.body().string();
            if (response.isSuccessful()) {
                JSONObject locationTypeJSON = new JSONObject(jsonData);
                JSONArray locationTypesJSON = locationTypeJSON.getJSONArray("location_types");
                for (int i=0; i<locationTypesJSON.length(); i++){
                    JSONObject detailsJSON = locationTypesJSON.getJSONObject(i);
                    Integer id = detailsJSON.getInt("id");
                    String type = detailsJSON.getString("name");

                    locationTypes.put(id, type);
                }
            }

        } catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e){
            e.printStackTrace();
        }
        return locationTypes;
    }

    public ArrayList<Region> processRegions(Response response){
        ArrayList<Region> regions = new ArrayList<>();
        try {
            String jsonData = response.body().string();
            if(response.isSuccessful()){
                JSONObject regionJSON = new JSONObject(jsonData);
                JSONArray regionsJSON = regionJSON.getJSONArray("regions");
                for (int i = 0; i<regionsJSON.length(); i++){
                    JSONObject detailsJSON = regionsJSON.getJSONObject(i);
                    String name = detailsJSON.getString("name");
                    String city = detailsJSON.getString("full_name");
                    int id = detailsJSON.getInt("id");
                    Region region = new Region(name, city, id);
                    regions.add(region);
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        } catch(JSONException e){
            e.printStackTrace();
        }
        return regions;
    }

    public static void findLocations(String name, Callback callback){
        OkHttpClient client = new OkHttpClient.Builder().build();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.LOCATIONS_URL).newBuilder();
        urlBuilder.addPathSegment(name);
        urlBuilder.addPathSegment("locations");
        String url = urlBuilder.toString();
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public void getLocationType(final Integer id, final Location location){
        findLocationTypes(id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                HashMap<Integer,String> results = processLocationTypes(response);
                for(HashMap.Entry<Integer, String> pairs : results.entrySet()){
                    if(pairs.getKey() == id){
                        location.setLocationType(pairs.getValue());
                    }
                }
            }
        });
     }

    public ArrayList<Location> processLocations(Response response){
        ArrayList<Location> locations = new ArrayList<>();
        try {
            String jsonData = response.body().string();
            if(response.isSuccessful()) {
                JSONObject locationJSON = new JSONObject(jsonData);
                JSONArray locationsJSON = locationJSON.getJSONArray("locations");
                for (int i = 0; i < locationsJSON.length(); i++) {
                    JSONObject detailsJSON = locationsJSON.getJSONObject(i);
                    String name = detailsJSON.getString("name");
                    String address = detailsJSON.getString("street");
                    String city = detailsJSON.getString("city");
                    String state = detailsJSON.getString("state");
                    String zip = detailsJSON.getString("zip");
                    int locationTypeId;
                    if(!detailsJSON.isNull("location_type_id")){
                        locationTypeId = detailsJSON.getInt("location_type_id");
                    } else {
                        locationTypeId = 0;
                    }
                    String phone = detailsJSON.getString("phone");



                    ArrayList<String> machines = new ArrayList<>();
                    ArrayList<String> machineConditions = new ArrayList<>();


                        JSONArray machinesJSON = detailsJSON.getJSONArray("location_machine_xrefs");


                        for(int j=0; j<machinesJSON.length(); j++){
                            JSONObject machineDetailsJSON = machinesJSON.getJSONObject(i);
                            Log.d("HEYHEY", machineDetailsJSON+"");
                            int machineId = machineDetailsJSON.getInt("machine_id");
//                            TODO: get machine name from id

                            JSONArray machineConditionsJSON = machineDetailsJSON.getJSONArray("machine_conditions");
                            for(int k=0; k<machineConditionsJSON.length(); k++){
                                JSONObject commentsJSON =  machineConditionsJSON.getJSONObject(k);
                                String comment = commentsJSON.getString("comment");
                                machineConditions.add(comment);
                            }
                    }

                    Location location = new Location(name, address, city, state, zip, phone,machines,machineConditions);
                    locations.add(location);
                    getLocationType(locationTypeId, location);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e){
            e.printStackTrace();
        }
        return locations;
    }
}
