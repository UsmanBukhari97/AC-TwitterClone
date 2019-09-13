package com.example.ac_twitterclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtEmail, edtUsername, edtPassword;
    private Button btnSignUp, btnLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Sign Up");

        // Save the current Installation to Back4App
        ParseInstallation.getCurrentInstallation().saveInBackground();

            edtEmail = findViewById(R.id.edtEnterEmail);
            edtPassword = findViewById(R.id.edtEnterPassword);

            //Pressing enter when signing up
            edtPassword.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int keyCode, KeyEvent event) {

                    if (keyCode == KeyEvent.KEYCODE_ENTER &&
                            event.getAction() == KeyEvent.ACTION_DOWN) {

                        onClick(btnSignUp);

                    }
                    return false;
                }
            });

            edtUsername = findViewById(R.id.edtUsername);
            btnSignUp = findViewById(R.id.btnSignUp);
            btnLogIn = findViewById(R.id.btnLogIn);

            btnSignUp.setOnClickListener(this);
            btnLogIn.setOnClickListener(this);

            if (ParseUser.getCurrentUser() != null) {
                //ParseUser.getCurrentUser().logOut();
                transitionToSocialMediaActivity();
            }


        }

        private void transitionToSocialMediaActivity () {

            Intent intent = new Intent(MainActivity.this, TwitterUsers.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onClick (View view){

            switch (view.getId()) {

                case R.id.btnSignUp:

                    if (edtEmail.getText().toString().equals("") ||
                            edtUsername.getText().toString().equals("") ||
                            edtPassword.getText().toString().equals("")) {

                        FancyToast.makeText(MainActivity.this,
                                "Email, Username, Password is required!",
                                Toast.LENGTH_SHORT, FancyToast.INFO,
                                false).show();

                    } else {
                        final ParseUser appUser = new ParseUser();
                        appUser.setEmail(edtEmail.getText().toString());
                        appUser.setUsername(edtUsername.getText().toString());
                        appUser.setPassword(edtPassword.getText().toString());

                        final ProgressDialog progressDialog = new ProgressDialog(this);
                        progressDialog.setMessage("Signing up " + edtUsername.getText().toString());
                        progressDialog.show();
                        appUser.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    FancyToast.makeText(MainActivity.this,
                                            appUser.getUsername() + " is signed up",
                                            Toast.LENGTH_SHORT, FancyToast.SUCCESS,
                                            true).show();

                                    transitionToSocialMediaActivity();

                                } else {

                                    FancyToast.makeText(MainActivity.this,
                                            "There was an error: " + e.getMessage(),
                                            Toast.LENGTH_LONG, FancyToast.ERROR,
                                            true).show();

                                }


                                //after siging up dismissing progress dialog
                                progressDialog.dismiss();
                            }
                        });

                    }
                    break;

                case R.id.btnLogIn:

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    break;
            }
        }

        public void rootLayoutTapped (View view){

        //by touching anywhere on screen making sure keyboard disappears
            try {

                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            } catch (Exception e) {

                e.printStackTrace();
            }

        }

    }
