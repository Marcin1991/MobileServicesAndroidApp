package com.example.marcin.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


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

