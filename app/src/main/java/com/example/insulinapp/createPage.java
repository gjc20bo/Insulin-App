package com.example.insulinapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import at.favre.lib.crypto.bcrypt.BCrypt;
/* This class allows the user to create a new account for their app. */
public class createPage extends AppCompatActivity {
    Cursor mCursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_page);


        EditText userName = (EditText) findViewById(R.id.userNameCreate);
        EditText password = (EditText) findViewById(R.id.passwordCreate);

        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        /* If the user changes their mind, they can return to the main screen without issue. */
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(createPage.this, MainActivity.class);
                startActivity(myIntent);
            }
        });

        Button createButton = (Button) findViewById(R.id.createButton);
        /* When the user clicks on create, then the text they typed in will be pulled */
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    String[] mProjection = {dbProvider.USERNAME_COLUMN};
                    String mSelection = dbProvider.USERNAME_COLUMN+" = ?";
                    String[] mSelectionArgs = {userName.getText().toString()};
                    mCursor = getContentResolver().query(dbProvider.USER_URI,mProjection,
                            mSelection, mSelectionArgs,null);
                    /* If the cursor returns results, then there was an entry with the same
                    username as the one given. Usernames need to be unique in order to keep the
                    historical data tied to the right people. */
                    if(mCursor != null && mCursor.moveToFirst()) {
                        Toast.makeText(createPage.this, "That username already exists",
                                Toast.LENGTH_LONG).show();
                    }
                    /* If nothing comes back, then the username is free and we hash the password
                    given and store both values into our table. The user is alerted that it was
                    successful and they are returned to the main page. */
                    else {
                        ContentValues values = new ContentValues();
                        values.put(dbProvider.USERNAME_COLUMN, userName.getText().toString());
                        values.put(dbProvider.PASSWORD_COLUMN, BCrypt.withDefaults().
                                hashToString(4,password.getText().toString().toCharArray()));
                        Uri uri = getContentResolver().insert(dbProvider.USER_URI, values);
                        Toast.makeText(createPage.this,
                                "Account created successfully!!", Toast.LENGTH_LONG).show();
                        Intent myIntent = new Intent(createPage.this,
                                MainActivity.class);
                        startActivity(myIntent);
                    }


            }
        });

    }
}