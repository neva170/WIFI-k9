package com.wxy.vpn;

import android.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.support.v7.app.AppCompatActivity;

import com.wxy.vpn.core.Preferences;
import com.wxy.vpn.utils.SettingsStorage;

import java.util.List;




public class StartUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(com.wxy.vpn.R.style.k9_NoActionBar);
        super.onCreate(savedInstanceState);
            PreferenceManager.setDefaultValues(this, com.wxy.vpn.R.xml.settings, false);
            disableLogWindowWorkaround();

            Intent intent;
            if (SettingsStorage.User.isLoggedIn(this)) {
                intent = new Intent(this, WelcomeTour.class);
            } else {
                intent = new Intent(this, Signup.class);
             //   intent = new Intent(this, MasterActivity.class);
            }

            startActivity(intent);
            finish();

    }

    private void disableLogWindowWorkaround() {
        SharedPreferences.Editor editor = Preferences.getDefaultSharedPreferences(this).edit();
        editor.putBoolean("showlogwindow", false);
        editor.apply();
    }

}
