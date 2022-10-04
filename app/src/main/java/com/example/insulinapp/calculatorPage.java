package com.example.insulinapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.time.LocalDate;

import java.time.LocalTime;
import java.util.Random;
/* This will be the page that handles the calculator implementation. The users will put in data
into a form and then this will run the computations which will be explained below. */
public class calculatorPage extends AppCompatActivity {
    String username = "";
    Double carbCount = 0.00;
    DecimalFormat carbTwoPlaces = new DecimalFormat("0.00");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_page);
        AndroidNetworking.initialize(getApplicationContext());
        Bundle getExtras = getIntent().getExtras();
        /* If there is no bundle then we don't need to worry about saving data but if there is we
        need to save the incoming data and clear the form. */
        if(getExtras != null) {
            EditText sugarCurrent = (EditText) findViewById(R.id.currentSugarInput);
            EditText sugarGoal = (EditText) findViewById(R.id.sugarGoalInput);
            EditText correction = (EditText) findViewById(R.id.correctionInput);
            EditText carbRatio = (EditText) findViewById(R.id.ratioInput);

            username = getExtras.getString("userName");
            carbCount = getExtras.getDouble("carbCount");
            sugarCurrent.setText(getExtras.getString("sugarCurrent"));
            sugarGoal.setText(getExtras.getString("sugarGoal"));
            correction.setText(getExtras.getString("correction"));
            carbRatio.setText(getExtras.getString("carbRatio"));

        }
        /* This will place 0.0 into the carb input part of the form unless the user comes back from
        the import carb page, in which case it will place the value they made from the other
        page. */
        EditText carbIntakeTemp = (EditText) findViewById(R.id.consumedInput);
        carbIntakeTemp.setText(String.valueOf(carbCount));

        Button homeButton = (Button) findViewById(R.id.homeButton);

        /* This will allow the user to go to the home page. */
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(calculatorPage.this, FrontPage.class);
                myIntent.putExtra("userName", username);
                startActivity(myIntent);
            }
        });

        Button calculateButton = (Button) findViewById(R.id.calculate);
        /* Once the calculate button is pressed then we will run the computation. */
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText sugarCurrent = (EditText) findViewById(R.id.currentSugarInput);
                EditText sugarGoal = (EditText) findViewById(R.id.sugarGoalInput);
                EditText correction = (EditText) findViewById(R.id.correctionInput);
                EditText carbIntake = (EditText) findViewById(R.id.consumedInput);
                EditText carbRatio = (EditText) findViewById(R.id.ratioInput);
                /* Make sure the form is filled properly. */
                if(sugarCurrent.getText().toString().equals("") ||
                        sugarGoal.getText().toString().equals("") ||
                        correction.getText().toString().equals("") ||
                        carbIntake.getText().toString().equals("") ||
                        carbRatio.getText().toString().equals("")) {
                    Toast.makeText(calculatorPage.this, "Part of the form cannot be empty!",
                            Toast.LENGTH_SHORT).show();
                    ((TextView)findViewById(R.id.calculatorView)).setTextColor(Color.RED);
                }

                else {
                /* The equation to calculate the units of insulin needed is as follows:

                (totalCarb / carbRatio) + (bloodSugar - targetBloodSugar)/correctionFactor.

                Correction factor and carbRatio are going to be data that are given from the doctor
                since they will be the ratios of insulin to carb, and insulin to blood sugar. From
                there, the rest of the data is from personal measurements. */
                    Float totalUnits = ((Float.valueOf(sugarCurrent.getText().toString()) -
                            Float.valueOf(sugarGoal.getText().toString())) /
                            Float.valueOf(correction.getText().toString())) +
                            (Float.valueOf(carbIntake.getText().toString()) /
                                    Float.valueOf(carbRatio.getText().toString()));

                    TextView totalView = (TextView) findViewById(R.id.unitsNeeded);
                    totalView.setText(String.valueOf(carbTwoPlaces.format(totalUnits)));
                    Random rand = new Random();

                /* Now we can insert the value into the database for the historical data page and
                also insert the date and time. */
                    ContentValues values = new ContentValues();
                    values.put(dbProvider.ID_COLUMN, rand.nextInt(10000));
                    values.put(dbProvider.USERNAME_COLUMN, username);
                    values.put(dbProvider.DATA_COLUMN, totalUnits);
                    LocalDate local = LocalDate.now();
                    LocalTime local2 = LocalTime.now();
                    values.put(dbProvider.TIME_COLUMN, String.valueOf(local2));
                    values.put(dbProvider.DATE_COLUMN, String.valueOf(local));
                /* Let the user know that the data was successfully inserted into the database and
                then clear the form data. */
                    Uri uri = getContentResolver().insert(dbProvider.HISTORICAL_URI, values);
                    Toast.makeText(calculatorPage.this, "Added to the database!",
                            Toast.LENGTH_SHORT).show();
                    sugarCurrent.setText("");
                    sugarGoal.setText("");
                    correction.setText("");
                    carbRatio.setText("");
                    carbIntake.setText("0.00");
                    ((TextView)findViewById(R.id.calculatorView)).setTextColor(Color.BLACK);
                }
            }
        });

        Button importButton = (Button) findViewById(R.id.importButton);
        /* If the user wants to import carb data using our Nutritionix API connection then this
        button will handle saving any form data they might have entered and send them over to that
        activity. */
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(calculatorPage.this, importCarb.class);
                EditText sugarCurrent = (EditText) findViewById(R.id.currentSugarInput);
                EditText sugarGoal = (EditText) findViewById(R.id.sugarGoalInput);
                EditText correction = (EditText) findViewById(R.id.correctionInput);
                EditText carbIntake = (EditText) findViewById(R.id.consumedInput);
                EditText carbRatio = (EditText) findViewById(R.id.ratioInput);
                String sugarC = sugarCurrent.getText().toString();
                String sugarG = sugarGoal.getText().toString();
                String correct = correction.getText().toString();
                String carbR = carbRatio.getText().toString();
                myIntent.putExtra("userName", username);
                myIntent.putExtra("carbCount", carbCount +
                        Double.parseDouble(carbIntake.getText().toString()));

                myIntent.putExtra("sugarCurrent", sugarC);
                myIntent.putExtra("sugarGoal",sugarG );
                myIntent.putExtra("correction", correct);
                myIntent.putExtra("carbRatio", carbR);

                startActivity(myIntent);
            }
        });

    }
}