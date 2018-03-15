package com.example.luca.stockcharts_volley;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    // TODO: 15/03/2018 customise keyboard action button

    // apiKey is still demo
    String urlBase = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=MSFT&apikey=";

    String apiKey = "";

    EditText apiKeyEditText;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // instantiates and assign sharedPreferences
        sharedPreferences = this.getSharedPreferences("com.example.luca.stockcharts_volley", Context.MODE_PRIVATE);

        // gets apiKey. If null, defaults to "demo"
        apiKey = sharedPreferences.getString("apiKey", "demo");

        // assign apiKeyEditText // TODO: 15/03/2018  do you really need this line? I mean, once the key has been entered the text edit is no longer visible
        apiKeyEditText = findViewById(R.id.apiKey_editText);

        // if key is not demo, unlock full features
        if (apiKey != "demo") {
            unlockFullFeatures();
        }

    }



    private void unlockFullFeatures() {
        // TODO: 11/12/2017 this will unlock all features
        // remove the apiKey_editText and the checkApiKeyButton

        // show "Instruments" dropdown

        // show "Timeframes" dropdown

        return;
    }



    public void getChartButtonClick(View view) {

        Intent i = new Intent(this, GetChart.class);
        i.putExtra("url", urlBase + apiKey);
        startActivity(i);

    }



    public void checkAPIkeyButton(View view) {

        // if isAPIkeyValid() is successful it will unlockFullFeatures() from there
        String userKey = apiKeyEditText.getText().toString();
        isAPIkeyValid(userKey);

    }



    private void isAPIkeyValid(final String key) {

        // TODO: 11/12/2017 check if key is valid
        // https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=MSFT&apikey=demo
        Log.i("key", key);
        String testURL = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=AAPL&apikey=" + key;



        // TODO: 12/12/2017 call the query above and check whether it returns "Metadata" or "Information"
        // here it is:

        // instantiate and initialize volley requestQueue
        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        // instantiate jsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, testURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("Check API key response", response.toString() );
                boolean hasMetadata = response.has("Meta Data");
                Log.i("response.has(Meta Data)", String.valueOf(hasMetadata));
                // TODO: 15/03/2018 if response has "Meta Data", call the unlock full features


                if (hasMetadata) {
                    // assign the entered key to apiKey
                    apiKey = key;
                    // save it on sharedPreferences
                    sharedPreferences.edit().putString("apiKey", apiKey);
                    // unlock full features
                    Toast.makeText(getApplicationContext(), "Full features unlocked", Toast.LENGTH_SHORT ).show();

                    unlockFullFeatures();
                } else {
                    // notify user the key is not valid
                    Toast.makeText(getApplicationContext(), "Invalid API key", Toast.LENGTH_SHORT ).show();
                }



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


    private void getData(String url) {

        //chart.setNoDataText("Fetching data");


        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        // instantiate jsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

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


}

