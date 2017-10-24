package com.wxy.vpn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wxy.vpn.api.APIService;
import com.wxy.vpn.api.TwillioBasicResponse;
import com.wxy.vpn.fragments.GuiHelperUtils;
import com.wxy.vpn.utils.ServiceUtil;
import com.wxy.vpn.utils.SnackBarUtils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by WASEEM AKRAM on 29-Sep-17.
 */

public class ConfirmCodeActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    EditText tvNo1,tvNo2,tvNo3,tvNo4;
    Button btnSubmit;
    private GuiHelperUtils.RequiredFieldValidator mRequiredFieldValidator;
    private ProgressDialog mProgressDialog;
    String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cnfrm_code);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        initViews();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    private void initViews() {

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvNo1 = (EditText) findViewById(R.id.tvNo1);
                tvNo2 = (EditText) findViewById(R.id.tvNo2);
                tvNo3 = (EditText) findViewById(R.id.tvNo3);
                tvNo4 = (EditText) findViewById(R.id.tvNo4);
                if (!tvNo1.getText().toString().isEmpty()
                        && !tvNo2.getText().toString().isEmpty()
                        && !tvNo3.getText().toString().isEmpty()
                        && !tvNo4.getText().toString().isEmpty()) {
                    SnackBarUtils.hideSoftKeyboard(ConfirmCodeActivity.this);
                    String code = tvNo1.getText().toString() + tvNo2.getText().toString() + tvNo3.getText().toString() + tvNo4.getText().toString();
                    if (null != TwillioBasicResponse.mCountryCode && null != TwillioBasicResponse.mPhoneNumber) {
                        phone = TwillioBasicResponse.mPhoneNumber;
                        attemptConfirmNumberSend(TwillioBasicResponse.mPhoneNumber,TwillioBasicResponse.mCountryCode, code);
                    }
                }
                else {
                    Toast.makeText(ConfirmCodeActivity.this, R.string.invalid_passcode, Toast.LENGTH_LONG).show();

                }
            }


        });
    }
    private void attemptConfirmNumberSend(String number, String countryCode,String code) {
        mProgressDialog.setMessage("Loding…");
        mProgressDialog.show();

        try{
            APIService api = ServiceUtil.restAdapter().create(APIService.class);

            api.verificationConfirm("n8U9fumVsL70iZcRwSKRFy2urXSRDQcS",number,countryCode,code, new Callback<TwillioBasicResponse>() {
                @Override
                public void success(TwillioBasicResponse basicResponse, Response response) {


                    if(basicResponse.getSuccess()){
                        mProgressDialog.hide();
                        Intent intent = new Intent(ConfirmCodeActivity.this, SignupByEmail.class);
                        intent.putExtra("phone",phone);
                        startActivity(intent);
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

    }

}
