package com.example.insulinapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.spec.PBEKeySpec;

public class FrontPage extends AppCompatActivity {
    Cursor mCursor;

    /* The front page is very basic. It only acts as a hub to go between the calculator and the
    historical data. There is a welcome message at the top and two buttons. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);
        Bundle getExtras = getIntent().getExtras();
        String prompt = "Welcome back " + getExtras.getString("userName") + "!";
        TextView welcomePrompt = (TextView) findViewById(R.id.welcomeUser);
        welcomePrompt.setText(prompt);

        Button calculatorButton = (Button) findViewById(R.id.goToCalc);
        calculatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(FrontPage.this, calculatorPage.class);
                myIntent.putExtra("userName", getExtras.getString("userName"));
                startActivity(myIntent);
            }
        });
        Button historyButton = (Button) findViewById(R.id.goToHistory);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(FrontPage.this, historyPage.class);
                myIntent.putExtra("userName", getExtras.getString("userName"));
                startActivity(myIntent);
            }
        });
    }
}