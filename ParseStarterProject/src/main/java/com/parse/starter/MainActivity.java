/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;

import static com.google.android.gms.analytics.internal.zzy.b;
import static com.google.android.gms.analytics.internal.zzy.e;
import static com.google.android.gms.analytics.internal.zzy.l;
import static com.google.android.gms.analytics.internal.zzy.n;


//View.OnClickListener allows you to make the text for "or, login" interactive
public class MainActivity extends AppCompatActivity implements  View.OnClickListener, View.OnKeyListener {

  Boolean signUpModeActive = true;
  TextView changeSignupModeTextView;
  EditText passwordEditText;

  public void showUserList()
  {
    Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
    startActivity(intent);


  }
  public void signUp(View view)
  {
    EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);


    if(usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches(""))
    {
      Toast.makeText(this, "A username and password are required", Toast.LENGTH_SHORT).show();

    } else {

      //user is signing up
      if(signUpModeActive) {

        ParseUser user = new ParseUser();

        user.setUsername(usernameEditText.getText().toString());
        user.setPassword(passwordEditText.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
          @Override
          public void done(ParseException e) {
            if (e == null) {
              Log.i("Sign up", "Successful");

              //goes to userList after user signed up
              showUserList();
            } else {
              //e.getmessage throws up error message
              Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });
      } else { //user is logging in
        //puts new user in the data base
        ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
          @Override
          public void done(ParseUser user, ParseException e) {
            if(user != null)
            {
              Log.i("Sign up", "Login successful");

              //goes to userList activity after user logged in
              showUserList();
            } else {
              Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });
      }
    }

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setTitle("FInstagram");

    changeSignupModeTextView = (TextView) findViewById(R.id.changeSignupModeTextView);
    changeSignupModeTextView.setOnClickListener(this);
    passwordEditText = (EditText) findViewById(R.id.passwordEditText);
    RelativeLayout backgroundRelativeLayout = (RelativeLayout) findViewById(R.id.backgroundRelativeLayout);

    ImageView logoImageView = (ImageView) findViewById(R.id.logoimageView);
    //sets off the onClick method
    backgroundRelativeLayout.setOnClickListener(this);

    logoImageView.setOnClickListener(this);

    //this method will be called everytime the key is pressed on keyboard
    passwordEditText.setOnKeyListener(this);

    if(ParseUser.getCurrentUser() != null) //user is already logged in
    {
      showUserList();
    }

    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

  @Override
  public void onClick(View view) {
    if(view.getId() == R.id.changeSignupModeTextView)
    {
      Button signUpButton = (Button) findViewById(R.id.signupButton);
      if(signUpModeActive)
      {
        signUpModeActive =false;
        signUpButton.setText("Log in");
        changeSignupModeTextView.setText("or, Sign up");


      } else {
        signUpModeActive =true;
        signUpButton.setText("Sign up");
        changeSignupModeTextView.setText("or, Login");
      }
      //hides the keyboard if the background screen is clicked
    } else  if (view.getId() == R.id.backgroundRelativeLayout || view.getId() == R.id.logoimageView)
    {
      //these two lines shutdown the keyboard if it is up

      //this allows us to manage the method of input, gets the keyboard
      InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

      //getCurrentFocus gets the current window, no need for special token, 0 will do
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

    }

  }

  @Override
  public boolean onKey(View view, int i, KeyEvent keyEvent) {
    //method is called every time a key is pressed on keyboard for passwordEditText

    //tests to see if enter key has been pressed
    //makes sure the enter key is being pressed down rather than released
    if(i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN)
    {
      signUp(view);
    }


    return false;
  }
}