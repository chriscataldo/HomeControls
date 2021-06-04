package com.cataldo.chris.homeautomationcontroller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class EditSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_settings);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String domain = preferences.getString("domain", null);
        String authcode = preferences.getString("authcode", null);

        if(!TextUtils.isEmpty(domain)) {
            EditText domainField = (EditText) findViewById(R.id.domain);
            domainField.setText(domain);
        }

        if(!TextUtils.isEmpty(authcode)) {
            EditText authcodeField = (EditText) findViewById(R.id.authcode);
            authcodeField.setText(authcode);
        }
    }

    public void saveSettings(View view) {
        EditText domainField = (EditText) findViewById(R.id.domain);
        EditText authcodeField = (EditText) findViewById(R.id.authcode);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("domain", domainField.getText().toString());
        editor.putString("authcode", authcodeField.getText().toString());
        editor.apply();
        Intent intent = getIntent();
        String initialRun = intent.getStringExtra("initialRun");
        if(initialRun != null) {
            Intent newIntent = new Intent(this, SplashScreen.class);
            startActivity(newIntent);
        } else {
            finish();
        }
    }


}
