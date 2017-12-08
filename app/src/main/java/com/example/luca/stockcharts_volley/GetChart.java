package com.example.luca.stockcharts_volley;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GetChart extends AppCompatActivity {

    // https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=MSFT&apikey=demo
    String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=MSFT&apikey=M131GCLA5V2D33ZH";

    //String url = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=MSFT&interval=1min&apikey=M131GCLA5V2D33ZH";

    JSONObject data;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_chart);

        getData(url);


    }

    private void getData(String url) {

        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                data = response;
                logJSONobject(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("getData", "Somehting went wrong");
            }
        });

        requestQueue.add(jsonObjectRequest);

    }

    String keys[] = new String[2];

    private void logJSONobject(JSONObject jsonObject) {


        // list available keys
        Iterator<?> iterator = jsonObject.keys();
        int i = 0;
        while (iterator.hasNext()){
            String key = (String) iterator.next();
            keys[i] = key.toString();
            Log.i("iterator", key);
            i++;
        }



        // extract Meta Data from jsonObject
        String metadata = "";
        try {
            metadata = jsonObject.getString("Meta Data").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("Meta Data", metadata);

        JSONObject timeSeries = null;
        try {
            timeSeries = jsonObject.getJSONObject((keys[1]));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("timeSeries", timeSeries.toString());

    }
}
