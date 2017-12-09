package com.example.luca.stockcharts_volley;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class GetChart extends AppCompatActivity {

    private ArrayList<String> labels = new ArrayList<>();

    private CandleStickChart chart;

    ArrayList<CandleEntry> candleEntries = new ArrayList<>();

    // https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=MSFT&apikey=demo
    String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=MSFT&apikey=demo";



    JSONObject data;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_chart);

        chart = findViewById(R.id.chart);

        getData(url);

    }


    private void getData(String url) {

        chart.setNoDataText("Fetching data");

        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                data = response;

                // extract timeseries part of the JSON
                JSONObject timeSeries = parseJSON(response);

                // create dataset for the chart API
                parseTimeseries(timeSeries);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("getData", "Something went wrong");
            }
        });

        requestQueue.add(jsonObjectRequest);

    }


    private JSONObject parseJSON(JSONObject jsonObject) {

        String keys[] = new String[2];


        // list available keys
        Iterator<?> iterator = jsonObject.keys();
        int i = 0;
        while (iterator.hasNext()){
            String key = (String) iterator.next();
            keys[i] = key;
            i++;
        }

        // extract Meta Data from jsonObject
        String metadata = "";
        try {
            metadata = jsonObject.getString("Meta Data");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject timeSeries = null;
        try {
            timeSeries = jsonObject.getJSONObject((keys[1]));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //parseTimeseries(timeSeries);
        return timeSeries;

    }


    private void parseTimeseries(JSONObject timeSeries) {

        // get all the keys
        Iterator<String> iterator = timeSeries.keys();

        // iterate through all the keys and store them in the temp ArrayList
        ArrayList<String> temp = new ArrayList<>();

        while (iterator.hasNext()) {
            String key = iterator.next();
            temp.add(key);
        }

        // reverse array order
        for (int i = temp.size() - 1; i >= 0; i--){
            labels.add(temp.get(i));
        }

        // populate candleEntries
        int i = 0;
        for (String label : labels) {
            try {
                JSONObject entryJSON = timeSeries.getJSONObject(label);
                addEntry(entryJSON, i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("entered ", String.valueOf(i));
            i++;
        }

        // initialize candleDataSet and pass candleEntries
        CandleDataSet dataSet = new CandleDataSet(candleEntries, "stock");

        // instantiate CandleData passing dataSet and labels
        CandleData candleData = new CandleData(dataSet);

        // set colors
        dataSet.setDrawIcons(false);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setShadowColor(Color.DKGRAY);
        dataSet.setShadowWidth(0.7f);
        dataSet.setDecreasingColor(Color.RED);
        dataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        dataSet.setIncreasingColor(Color.rgb(122, 242, 84));
        dataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        dataSet.setNeutralColor(Color.BLUE);

        chart.setData(candleData);
        chart.invalidate();





    }

    private void addEntry(JSONObject entryJSON, int i) {
        // get open
        float open = 0;
        try {
            open = Float.valueOf(entryJSON.getString("1. open"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // get high
        float high = 0;
        try {
            high = Float.valueOf(entryJSON.getString("2. high"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // get low
        float low = 0;
        try {
            low = Float.valueOf(entryJSON.getString("3. low"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // get close
        float close = 0;
        try {
            close = Float.valueOf(entryJSON.getString("4. close"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        candleEntries.add(i, new CandleEntry((float) i, high, low, open, close ));

    }
}
