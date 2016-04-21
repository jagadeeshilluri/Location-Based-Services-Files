package com.example.jagadeesh.weather;

import android.app.Activity;
import android.os.Bundle;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class convertToZip {

    double lon, lat;
    String zip;
    static String API_KEY = "AIzaSyBdyNq9gkvd-Swfy49yVc9PN4aMHWS81Cw";

    public convertToZip(double latitude, double longitude)
    {
        setLonLat(latitude,longitude);
        String URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + getLon() + "," + getLat() + "&key=" + API_KEY;
        try {
            setZip(new AsynTaskJson().execute(URL).get());
        } catch (InterruptedException e) {
        e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void setZip(String zip)
    {
        this.zip = zip;
    }

    public String getZip()
    {
        return this.zip;
    }

    public void setLonLat (double latitude, double longitude)
    {
        this.lon = longitude;
        this.lat = latitude;
    }

    public double getLon ()
    {
        return this.lon;
    }

    public double getLat()
    {
        return this.lat;
    }

    public class AsynTaskJson extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            String jsonData, zipCode;
            boolean errorFlag;

            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(params[0]);
                HttpResponse response = client.execute(get);
                HttpEntity entity = response.getEntity();
                jsonData = EntityUtils.toString(entity);

                try
                {
                    JSONObject jsonObj = new JSONObject(jsonData);
                    String status = jsonObj.getString("status");
                    if (!status.equals("OK")) {
                        errorFlag = true;
                        return null;
                    }
                    //get zipcode
                    JSONObject results = jsonObj.getJSONArray("results").getJSONObject(0);
                    JSONArray address_components = results.getJSONArray("address_components");
                    JSONArray types;
                    int i =0;
                    for (i = 0; i < address_components.length(); i++)
                    {
                        types = (JSONArray) address_components.getJSONObject(i).get("types");
                        if(types.toString().equals("[\"postal_code\"]"))
                            break;
                    }
                    return address_components.getJSONObject(i).getString("long_name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
