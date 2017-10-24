package com.wxy.vpn;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wxy.vpn.api.APIService;
import com.wxy.vpn.api.ApiK9Server;
import com.wxy.vpn.api.TwillioBasicResponse;
import com.wxy.vpn.fragments.GuiHelperUtils;
import com.wxy.vpn.utils.Connectivity;
import com.wxy.vpn.utils.ServiceUtil;
import com.wxy.vpn.utils.SettingsStorage;
import com.wxy.vpn.utils.SnackBarUtils;

import java.io.IOException;

import de.blinkt.openvpn.core.VpnStatus;
import okio.Buffer;
import okio.BufferedSink;
import retrofit.RetrofitError;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Login extends AppCompatActivity implements
        View.OnClickListener,
        Callback<ApiK9Server.TokenData>,
        DialogInterface.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private TextInputLayout mEmailLayout;
    private TextInputLayout mPhoneLayout;
    private TextInputLayout mPasswordLayout;

    private ProgressDialog mProgressDialog;

    private ApiK9Server.ApiInterface api;
    private GuiHelperUtils.PasswordValidator mPasswordValidator;
    private GuiHelperUtils.EmailValidator mEmailValidator;
    private GuiHelperUtils.PhoneValidator mPhoneValidator;
    private Button mBtnRegister;
    private Button mBtnForgotPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.wxy.vpn.R.layout.activity_login);

        setupButtons();
        setupPasswordInput();
        //setupEmailInput();
        setupPhoneInput();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);

        api = ApiK9Server.getApiInterface(getApplicationContext());

    }

    private void setupButtons() {
        mBtnRegister = (Button) findViewById(com.wxy.vpn.R.id.btn_show_login);
        mBtnRegister.setOnClickListener(this);

        mBtnForgotPass = (Button) findViewById(com.wxy.vpn.R.id.btn_login_forgot_pass);
        mBtnForgotPass.setOnClickListener(this);
    }

    private void setupPasswordInput() {
        mPasswordLayout = (TextInputLayout) findViewById(com.wxy.vpn.R.id.login_password);
        mPasswordValidator = new GuiHelperUtils.PasswordValidator(
                mPasswordLayout, ApiK9Server.PASSWORD_LENGTH
        );

        final EditText editTextPassword = mPasswordLayout.getEditText();

        if (editTextPassword != null) {

            editTextPassword.addTextChangedListener(mPasswordValidator);
            editTextPassword.setFilters(new InputFilter[]{mPasswordValidator});
        }
    }

    private void setupEmailInput() {
        mEmailLayout = (TextInputLayout) findViewById(com.wxy.vpn.R.id.login_email);
        mEmailValidator = new GuiHelperUtils.EmailValidator(mEmailLayout);

        final EditText editTextEmail = mEmailLayout.getEditText();
        if (editTextEmail != null) {

            editTextEmail.addTextChangedListener(mEmailValidator);
            editTextEmail.setFilters(new InputFilter[]{mEmailValidator});

            String email = getIntent().getStringExtra(SignupByEmail.EMAIL_FOR_LOGIN);
            if (!TextUtils.isEmpty(email)) {

                editTextEmail.setText(email);
                mPasswordLayout.requestFocus();
            }
        }
    }
    private void setupPhoneInput() {
        mPhoneLayout = (TextInputLayout) findViewById(com.wxy.vpn.R.id.login_email);
        mPhoneValidator = new GuiHelperUtils.PhoneValidator(mPhoneLayout);

        final EditText editTextPhone = mPhoneLayout.getEditText();
        if (editTextPhone != null) {

            editTextPhone.addTextChangedListener(mPhoneValidator);
            editTextPhone.setFilters(new InputFilter[]{mPhoneValidator});

            String email = getIntent().getStringExtra(SignupByEmail.EMAIL_FOR_LOGIN);
            if (!TextUtils.isEmpty(email)) {

                editTextPhone.setText(email);
                mPasswordLayout.requestFocus();
            }
        }
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case com.wxy.vpn.R.id.btn_show_login:
                if (Connectivity.isConnectedWifi(this) || (Connectivity.isConnectedMobile(this))) {
                attemptLogin();
                break;
            } else {
                Toast.makeText(this, "Network Error!",
                        Toast.LENGTH_LONG).show();
            }
              break;
            case com.wxy.vpn.R.id.btn_login_forgot_pass:
            case com.wxy.vpn.R.id.snackbar_action:
                attemptRestorePassword();
                break;
        }
    }

    private void attemptRestorePassword() {
        // we don't need password to restore it
        mPasswordLayout.setError(null);

        final String email = mEmailLayout.getEditText().getText().toString();

        if (mEmailValidator.validate(email)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setMessage(
                    "Do you want to restore password for email " + email + "?"
            ).setPositiveButton("Yes", this).setNegativeButton("No", this).show();
        }
    }

    private void attemptLogin() {
        final String password = mPasswordLayout.getEditText().getText().toString();
   //     final String email = mEmailLayout.getEditText().getText().toString();
        final String deviceId = SettingsStorage.Device.getId(this);
         final String phone =  mPasswordLayout.getEditText().getText().toString();
        if (mPasswordValidator.validate(phone) && mPasswordValidator.validate(password)) {
        //    networklogin( email,  password, deviceId, phone);
            Call<ApiK9Server.TokenData> login = api.login(
                    new ApiK9Server.LoginCred(
                            "",
                            password,
                            deviceId,
                            phone

                    )
            );

            mProgressDialog.setMessage("Logging in…");
            mProgressDialog.show();

            login.enqueue(this);
        }
    }

  /*  private void networklogin(String email, String password,String deviceId,String phone) {
        mProgressDialog.setMessage("Loding…");
        mProgressDialog.show();

        try{
            APIService api = ServiceUtil.restAdapter().create(APIService.class);

            api.login(deviceId,email,phone,password, new retrofit.Callback<TwillioBasicResponse>() {
                @Override
                public void success(TwillioBasicResponse basicResponse, retrofit.client.Response response) {


                    if(basicResponse.getSuccess()){
                        mProgressDialog.hide();

                    }
                    else{
                        mProgressDialog.hide();
                        // SnackBarUtils.showSnackBar(findViewById(android.R.id.content),basicResponse.getMessage(), Snackbar.LENGTH_SHORT);

                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("", "failure: ",error );
                    SnackBarUtils.showSnackBar(findViewById(android.R.id.content), getResources().getString(R.string.error)+"Invalid verification code!", Snackbar.LENGTH_SHORT);
                    mProgressDialog.hide();
                }
            });
        }
        catch (Exception Exp){
            Exp.printStackTrace();
        }

    }*/


    // On login
    @Override
    public void onResponse(Call<ApiK9Server.TokenData> call, Response<ApiK9Server.TokenData> response) {
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();

        if (!response.isSuccessful() && response.errorBody() != null) {
            ApiK9Server.ApiError error = ApiK9Server.parseError(response);
            for (String errorMessage : error.getErrors().getMessages()) {
                final Snackbar snackbar = Snackbar.make(
                        findViewById(com.wxy.vpn.R.id.сoordinator_activity_login),
                        errorMessage,
                        Snackbar.LENGTH_LONG
                );
                if (response.code() == 403)
                    snackbar.setAction("Forgot?", this);
                snackbar.show();
            }
        }

        ApiK9Server.TokenData decodedResponse = response.body();
        if (decodedResponse != null) {
            try {
                BufferedSink bf = new Buffer();
                call.request().body().writeTo(bf);
                Gson gson = new Gson();
                ApiK9Server.LoginCred cred = gson.fromJson(bf.buffer().readUtf8(), ApiK9Server.LoginCred.class);

                SettingsStorage.User.login(this,
                        cred.getEmail(), cred.getPassword(), decodedResponse.getToken());

                if (SettingsStorage.User.isLoggedIn(this)) {
                    startActivity(new Intent(this, WelcomeTour.class));
                    finish();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // On login
    @Override
    public void onFailure(Call<ApiK9Server.TokenData> call, Throwable t) {
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();

        Log.e(TAG, "Error while login", t);
    }

    // On restore password Yes/No dialog click
    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which) {

            case DialogInterface.BUTTON_POSITIVE:
                Call<ApiK9Server.ApiResponse> restorePass = api.restorePass(
                        new ApiK9Server.EmailCred(mEmailLayout.getEditText().getText().toString())
                );

                mProgressDialog.setMessage("Restoring your password…");
                mProgressDialog.show();

                restorePass.enqueue(new Callback<ApiK9Server.ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiK9Server.ApiResponse> call, Response<ApiK9Server.ApiResponse> response) {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();

                        if (!response.isSuccessful() && response.errorBody() != null) {
                            ApiK9Server.ApiError error = ApiK9Server.parseError(response);
                            for (String errorMessage : error.getErrors().getMessages()) {
                                Snackbar.make(
                                        findViewById(com.wxy.vpn.R.id.сoordinator_activity_login),
                                        errorMessage,
                                        Snackbar.LENGTH_LONG
                                ).show();
                            }
                        }

                        ApiK9Server.ApiResponse decodedResponse = response.body();
                        if (decodedResponse != null) {
                            Snackbar.make(
                                    findViewById(com.wxy.vpn.R.id.сoordinator_activity_login),
                                    decodedResponse.getMessage(),
                                    Snackbar.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiK9Server.ApiResponse> call, Throwable t) {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();

                        Log.e(TAG, "Error while restoring password", t);
                    }
                });
        }
    }
}
