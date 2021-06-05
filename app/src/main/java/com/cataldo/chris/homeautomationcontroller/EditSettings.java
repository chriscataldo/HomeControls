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
        String currentDomain = preferences.getString("domain", null);
        String currentAuthcode = preferences.getString("authcode", null);

        if(!TextUtils.isEmpty(currentDomain)) {
            EditText domainField = (EditText) findViewById(R.id.domain);
            domainField.setText(currentDomain);
        }

        if(!TextUtils.isEmpty(currentAuthcode)) {
            EditText authcodeField = (EditText) findViewById(R.id.authcode);
            authcodeField.setText(currentAuthcode);
        }
    }

    public void saveSettings(View view) {
        EditText domainField = (EditText) findViewById(R.id.domain);
        EditText authcodeField = (EditText) findViewById(R.id.authcode);
        String newDomain = domainField.getText().toString();
        String newAuthcode = authcodeField.getText().toString();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("domain", newDomain);
        editor.putString("authcode", newAuthcode);
        editor.apply();

        Intent newIntent = new Intent(this, SplashScreen.class);
        startActivity(newIntent);
        finish();
    }


}
