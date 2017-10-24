package com.wxy.vpn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



public class Signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.wxy.vpn.R.layout.activity_signup);

 /*       if (VpnStatus.isVPNActive())
            VpnStarter.stopSilent(this);*/

        Button btnLoginEmail = (Button) findViewById(com.wxy.vpn.R.id.btn_login_email);
        btnLoginEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Signup.this, Login.class));
            }
        });

        Button btnSingInByGoogle = (Button) findViewById(com.wxy.vpn.R.id.btn_sign_in_google);
        btnSingInByGoogle.setEnabled(false);

        Button btnSingInByFacebook = (Button) findViewById(com.wxy.vpn.R.id.btn_sign_in_facebook);
        btnSingInByFacebook.setEnabled(false);

        Button btnSignUpByEmail = (Button) findViewById(com.wxy.vpn.R.id.btn_sign_up_email);
        btnSignUpByEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Signup.this, PhoneInputActivity.class));
             //   startActivity(new Intent(Signup.this, PhoneInputActivity.class));
            }
        });

        TextView textPolicy = (TextView) findViewById(com.wxy.vpn.R.id.policies_and_terms);
        textPolicy.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
