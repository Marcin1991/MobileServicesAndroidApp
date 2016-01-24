package com.example.marcin.storyroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A login screen that offers login via login/password.
 */
public class LoginActivity extends Activity {

    final Context context = this;

    private Button btnLogin;
    private EditText txtLogin;
    private EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtLogin =  (EditText) findViewById(R.id.login);
        txtPassword =  (EditText) findViewById(R.id.password);
        btnLogin =  (Button) findViewById(R.id.email_sign_in_button);

        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String login = txtLogin.getText().toString();
                String password = txtPassword.getText().toString();

                if( login.length() == 0 || password.length() == 0) {
                    Toast.makeText(context, "Uzupełnij login i hasło", Toast.LENGTH_LONG).show();
                } else {

                    Intent nextScreen = new Intent(context, MainActivity.class);
                    nextScreen.putExtra("login", txtLogin.getText().toString());
                    nextScreen.putExtra("password", txtPassword.getText().toString());
                    startActivity(nextScreen);

                }
            }
        });

    }

}

