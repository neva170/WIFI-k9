package com.wxy.vpn;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wxy.vpn.fragments.ChangePasswordDialog;
import com.wxy.vpn.fragments.GuiHelperUtils;
import com.wxy.vpn.utils.SettingsStorage;


public class UserProfile extends AppCompatActivity implements ChangePasswordDialog.OnChangePasswordDialogListener {

    private TextInputLayout mFullNameView;
    private GuiHelperUtils.RequiredFieldValidator mRequiredFieldValidator;
    private DialogFragment mChangeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.wxy.vpn.R.layout.activity_user_profile);

        TextView email = (TextView) findViewById(com.wxy.vpn.R.id.credentialsEmail);
        email.setText(SettingsStorage.User.getEmail(this));

        setupFullnameInput();

        setupButtons();
    }

    private void setupButtons() {
        Button btnChangePassword = (Button) findViewById(com.wxy.vpn.R.id.btn_change_password);
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChangeDialog = ChangePasswordDialog.newInstance(
                        SettingsStorage.User.getEmail(UserProfile.this),
                        SettingsStorage.User.getToken(UserProfile.this)
                );
                mChangeDialog.show(getFragmentManager(), "dialog");
            }
        });

        Button btnLogout = (Button) findViewById(com.wxy.vpn.R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsStorage.User.logoutDialog(UserProfile.this);
            }
        });
    }

    private void setupFullnameInput() {
        mFullNameView = (TextInputLayout) findViewById(com.wxy.vpn.R.id.credentials_full_name_layout);
        mFullNameView.getEditText().setText(SettingsStorage.User.getCredentials(this).getFullName());
        mRequiredFieldValidator = new GuiHelperUtils.RequiredFieldValidator(mFullNameView);
        mFullNameView.getEditText().addTextChangedListener(mRequiredFieldValidator);
    }

    @Override
    public void onChangePasswordResult(boolean success, String message) {
        mChangeDialog.dismiss();
        Snackbar.make(
                findViewById(com.wxy.vpn.R.id.сoordinator_activity_restore_password),
                message, Snackbar.LENGTH_LONG
        ).show();
    }
}
