package com.example.insulinapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.ParsedRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.List;

public class importCarb extends AppCompatActivity {
    String username = "";
    Double carbCount = 0.00;
    String sugarCurrent = "";
    String sugarGoal = "";
    String correction = "";
    String carbRatio = "";
    DecimalFormat carbTwoPlaces = new DecimalFormat("0.00");
    /* For our import carb page, the user can type in sentences and/or food into the bar at the
    top of the page and the api will use a natural language system to parse the input for common
    foods and then return the combined carb amount for everything in the input. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_carb);
        AndroidNetworking.initialize(getApplicationContext());
        Bundle getExtras = getIntent().getExtras();
        EditText foodSearch = (EditText) findViewById(R.id.foodInput);
        /* Save all of the relevant data from the form. */
        if (getExtras != null) {
            username = getExtras.getString("userName");
            carbCount = getExtras.getDouble("carbCount");
            sugarCurrent = getExtras.getString("sugarCurrent");
            sugarGoal = getExtras.getString("sugarGoal");
            correction = getExtras.getString("correction");
            carbRatio = getExtras.getString("carbRatio");
        }
        /* Once the user hits GO, then the request will be made. */
        Button searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foodName = foodSearch.getText().toString();
                makeRequest(foodName);
            }
        });
        /* If the user wants to insert data manually, they can do that too with this button. It
        will add to the total amount displayed at the bottom of the screen. */
        Button addToTotal = (Button) findViewById(R.id.enterButton);
        addToTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText addCarb = (EditText) findViewById(R.id.carbInput);
                carbCount += Double.parseDouble(addCarb.getText().toString());
                TextView totalCarb = findViewById(R.id.carbTotal);
                totalCarb.setText(String.valueOf(carbTwoPlaces.format(carbCount)));
            }
        });
        /* Then, when the user is ready to go back this button will send them back and insert these
        values into the form for them. */
        Button goBack = (Button) findViewById(R.id.backToCalculator);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(importCarb.this, calculatorPage.class);
                myIntent.putExtra("userName", username);
                myIntent.putExtra("carbCount", carbCount);
                myIntent.putExtra("sugarCurrent", sugarCurrent);
                myIntent.putExtra("sugarGoal", sugarGoal);
                myIntent.putExtra("correction", correction);
                myIntent.putExtra("carbRatio", carbRatio);
                startActivity(myIntent);
            }
        });

    }
    /* Where the actual api connection is made. */
    private void makeRequest(String name) {
        String TAG = "FOOD_CARB";
        /* We will build our request here. For this api we need too make a POST request with our
        app ids for our api connection into the header part, and we will add a body parameter which
        is the string that was given by the users. We do not need to do any parsing or checking,
        the api will do that for us. */
        ANRequest req = AndroidNetworking.post(
                        "https://trackapi.nutritionix.com/v2/natural/nutrients")
                .addHeaders("x-app-id","xxxxxxxxxxxx")
                .addBodyParameter("query",name)
                .addHeaders("x-app-key","xxxxxxxxxxxxxxxxxxxxxxxxxxxx")
                .addHeaders("x-remote-user-id","xxxxxxxxxxxxxxxxxxxxxx")
                .setPriority(Priority.MEDIUM)
                .build();
        /* The JSON object that is returned has arrays mixed with regular data. We only need the
        JSON array with data in it since that has all of the combined nutritional value
        information. The rest of the object is individual information which we do not need. */
        req.getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                /* We need to catch the error if there is an issue with parsing the JSON array on
                our end. However, we will pull the total carb information and then insert it into
                the total amount part of our activity. */
                try {
                    JSONArray array = response.getJSONArray("foods");
                    JSONObject object = array.getJSONObject(0);
                    String check = object.getString("nf_total_carbohydrate");
                    carbCount += Double.parseDouble(check);
                    TextView totalCarb = findViewById(R.id.carbTotal);
                    totalCarb.setText(String.valueOf(carbTwoPlaces.format(carbCount)));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(importCarb.this,
                            "Something went wrong with finding the array!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(ANError anError) {
                Toast.makeText(importCarb.this,
                        "Something went wrong with retrieving the object!",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
}