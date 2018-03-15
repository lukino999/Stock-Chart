package com.example.luca.stockcharts_volley;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class GetChart extends AppCompatActivity {


    ArrayList<CandleEntry> candleEntries = new ArrayList<>();
    private ArrayList<String> labels = new ArrayList<>();
    private CandleStickChart chart;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_get_chart);

        url = getIntent().getExtras().getString("url");
        Log.i("url", url);

        // initialize chart
        chart = findViewById(R.id.chart);

        getData(url);

    }



    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // get fullscreen
        hide();
    }



    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // sets chart view to fullscreen
        findViewById(R.id.chart).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }



    private void getData(String url) {

        chart.setNoDataText("Fetching data");

        // instantiate and initialize volley requestQueue
        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        // instantiate jsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                // extract timeSeries part of the JSON
                JSONObject timeSeries = getTimeSeries(response);

                // create chart
                showChart(timeSeries);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("getData", "Something went wrong");
                Toast.makeText(getApplicationContext(), "Unable to perform request", Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonObjectRequest);

    }



    private JSONObject getTimeSeries(JSONObject jsonObject) {

        String keys[] = new String[2];


        // list available keys
        Iterator<?> iterator = jsonObject.keys();
        int i = 0;
        while (iterator.hasNext()) {
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

        //showChart(timeSeries);
        return timeSeries;

    }



    private void showChart(JSONObject timeSeries) {

        // get all the keys
        Iterator<String> iterator = timeSeries.keys();

        // iterate through all the keys and store them in the temp ArrayList
        ArrayList<String> temp = new ArrayList<>();

        while (iterator.hasNext()) {
            String key = iterator.next();
            temp.add(key);
        }

        // reverse array order
        for (int i = temp.size() - 1; i >= 0; i--) {
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


        // instantiate CandleData passing dataSet
        CandleData candleData = new CandleData(dataSet);

        // set labels
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(90f);


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

        chart.setDescription(null);

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

        candleEntries.add(i, new CandleEntry((float) i, high, low, open, close));

    }



}
