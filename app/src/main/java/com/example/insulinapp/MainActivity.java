package com.example.insulinapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

/* Our main activity will be where the user logs into the app or can go to the create account page
 to add a new person to their database.*/
public class MainActivity extends AppCompatActivity {
    Cursor mCursor;
    Integer error = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText userName = (EditText) findViewById(R.id.usernameInput);
        EditText password = (EditText) findViewById(R.id.passwordInput);


        /* If the user hits login, then their input is pulled and then checked to see if it exists
        in the database. If it does, then they are let into the app but if not, then they are
        alerted. */
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error=0;

                String[] mProjection = {dbProvider.USERNAME_COLUMN, dbProvider.PASSWORD_COLUMN};
                String mSelectionClause = dbProvider.USERNAME_COLUMN +" = ?";
                String[] mSelectionArgs={userName.getText().toString()};
                mCursor = getContentResolver().query(dbProvider.USER_URI, mProjection,
                            mSelectionClause, mSelectionArgs, null);
                /* We need to make several checks here. The first one is to make sure that at least
                 a single entry came back. If not, then we can skip everything else. Next, we check
                 if the username matches. If it does, then finally, we hash their password input
                 and check it against the password that is stored in our database (also hashed). */
                if(mCursor != null && mCursor.moveToFirst()) {
                    if(mCursor.getCount() > 0) {
                        if (mCursor.getString(0).equals(
                                userName.getText().toString())) {
                            BCrypt.Result result = BCrypt.verifyer().verify(
                                    password.getText().toString().toCharArray(),
                                    mCursor.getString(1).toCharArray());
                            if (result.verified) {
                                ((TextView) findViewById(R.id.promptView)).setTextColor
                                        (Color.BLACK);
                                Intent myIntent = new Intent(MainActivity.this,
                                        FrontPage.class);
                                myIntent.putExtra("userName",
                                        userName.getText().toString());
                                startActivity(myIntent);
                            } else {
                                error = 1;
                            }
                        } else {
                            error = 1;
                        }
                    }

                }
                else {
                    error = 1;
                }
                /* If any of the above checks fail, then error is set to 1 and we then alert the
                user that it failed. */
                if (error == 1) {
                    String test = "Login credentials are not valid";
                    ((TextView) findViewById(R.id.promptView)).setTextColor(Color.RED);
                    Toast.makeText(MainActivity.this, test,
                            Toast.LENGTH_LONG).show();
                    error = 0;
                }



            }
        });
        /* Then there is a button to go to the create account page for the user. */
        Button newAccount = (Button) findViewById(R.id.createAccount);
        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, createPage.class);
                startActivity(myIntent);
            }
        });
    }
}