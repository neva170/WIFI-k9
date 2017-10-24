package com.wxy.vpn;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class InviteFriends extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.wxy.vpn.R.layout.activity_invite_friends);

        findViewById(com.wxy.vpn.R.id.invite_via_sms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteViaSms();
            }
        });

        findViewById(com.wxy.vpn.R.id.invite_via_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteViaEmail();
            }
        });

        findViewById(com.wxy.vpn.R.id.invite_via_google_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteViaGooglePlus();
            }
        });

        findViewById(com.wxy.vpn.R.id.invite_via_whatsapp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteViaWhatsApp();
            }
        });
    }

    private void sendMessageToPackage(String packageName, String message) throws PackageManager.NameNotFoundException {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, message);
        i.setType("text/plain");
        // check if package installed
        getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA);
        i.setPackage(packageName);
        startActivity(i);
    }

    private void inviteViaGooglePlus() {
        try {
            sendMessageToPackage("com.google.android.apps.plus", getString(com.wxy.vpn.R.string.invite_text));
        } catch (PackageManager.NameNotFoundException e) {
            Snackbar.make(findViewById(
                    android.R.id.content),
                    "Google+ is not installed",
                    Snackbar.LENGTH_LONG
            ).show();
        }
    }

    private void inviteViaWhatsApp() {
        try {
            sendMessageToPackage("com.whatsapp", getString(com.wxy.vpn.R.string.invite_text));
        } catch (PackageManager.NameNotFoundException e) {
            Snackbar.make(findViewById(
                    android.R.id.content),
                    "WhatsApp is not installed",
                    Snackbar.LENGTH_LONG
            ).show();
        }
    }

    private void inviteViaSms() {
        String text = getString(com.wxy.vpn.R.string.invite_text);
        Intent sendIntent = new Intent();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this);

            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);

            if (defaultSmsPackageName != null) {
                sendIntent.setPackage(defaultSmsPackageName);
            }
        } else {
            sendIntent.setAction(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("smsto:"));
            sendIntent.putExtra("sms_body", text);
        }

        startActivity(sendIntent);
    }

    private void inviteViaEmail() {
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setData(Uri.parse("mailto:"));
        i.putExtra(Intent.EXTRA_TEXT, getString(com.wxy.vpn.R.string.invite_text));
        i.putExtra(Intent.EXTRA_SUBJECT, getString(com.wxy.vpn.R.string.invite_subject));

        ComponentName emailApp = i.resolveActivity(getPackageManager());
        ComponentName unsupportedAction = ComponentName.unflattenFromString("com.android.fallback/.Fallback");
        if (emailApp != null && !emailApp.equals(unsupportedAction))
            try {
                startActivity(Intent.createChooser(i, "Choose an email client"));
            } catch (ActivityNotFoundException e) {
                Snackbar.make(findViewById(
                        android.R.id.content),
                        "There are no email clients installed.",
                        Snackbar.LENGTH_SHORT
                ).show();
            }
    }
}
