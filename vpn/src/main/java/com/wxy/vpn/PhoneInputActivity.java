package com.wxy.vpn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lamudi.phonefield.PhoneEditText;
import com.lamudi.phonefield.PhoneInputLayout;
import com.wxy.vpn.api.APIService;
import com.wxy.vpn.utils.ServiceUtil;
import com.wxy.vpn.api.TwillioBasicResponse;
import com.wxy.vpn.utils.SnackBarUtils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class PhoneInputActivity extends AppCompatActivity {
  private ProgressDialog mProgressDialog;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.phone_input_activity);

    final PhoneInputLayout phoneInputLayout =
        (PhoneInputLayout) findViewById(R.id.phone_input_layout);
    final PhoneEditText phoneEditText = (PhoneEditText) findViewById(R.id.edit_text);

    mProgressDialog = new ProgressDialog(this);
    mProgressDialog.setIndeterminate(true);
/*    CustomPhoneInputLayout customPhoneInputLayout = new CustomPhoneInputLayout(this, "EG");

    final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
        .findViewById(android.R.id.content)).getChildAt(0);

    viewGroup.addView(customPhoneInputLayout, 2);*/


    final Button button = (Button) findViewById(R.id.submit_button);

    assert phoneInputLayout != null;
    assert phoneEditText != null;
    assert button != null;

    phoneInputLayout.setHint(R.string.phone_hint);
    phoneInputLayout.setDefaultCountry("PK");

  /*  phoneEditText.setHint(R.string.phone_hint);
    phoneEditText.setDefaultCountry("FR");*/

    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        SnackBarUtils.hideSoftKeyboard(PhoneInputActivity.this);
        boolean valid = true;
        if (phoneInputLayout.isValid()) {
          phoneInputLayout.setError(null);
        } else {
          phoneInputLayout.setError(getString(R.string.invalid_phone_number));
          valid = false;
        }

       /* if (phoneEditText.isValid()) {
          phoneEditText.setError(null);
        } else {
          phoneEditText.setError(getString(R.string.invalid_phone_number));
          valid = false;
        }*/

        if (valid) {

         attemptConfirmNumber(phoneInputLayout.getPhoneNumber(),String.valueOf((phoneInputLayout).mCountry.mDialCode));
          //   Toast.makeText(PhoneInputActivity.this, R.string.valid_phone_number, Toast.LENGTH_LONG).show();
        } else {
        //  Toast.makeText(PhoneInputActivity.this, R.string.invalid_phone_number, Toast.LENGTH_LONG).show();
        }
      }
    });
  }

  private void attemptConfirmNumber(final String number, final String countryCode) {
    mProgressDialog.setMessage("Loding…");
    mProgressDialog.show();

    try{
      APIService api = ServiceUtil.restAdapter().create(APIService.class);

      api.verificationRequest("n8U9fumVsL70iZcRwSKRFy2urXSRDQcS","sms",number,countryCode, new Callback<TwillioBasicResponse>() {
        @Override
        public void success(TwillioBasicResponse basicResponse, Response response) {


          if(basicResponse.getSuccess()){
            TwillioBasicResponse.mCountryCode=countryCode;
            TwillioBasicResponse.mPhoneNumber=number;
            mProgressDialog.hide();
            Toast.makeText(PhoneInputActivity.this, ""+basicResponse.getMessage(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PhoneInputActivity.this, ConfirmCodeActivity.class));
          }
          else{
            mProgressDialog.hide();
           // SnackBarUtils.showSnackBar(findViewById(android.R.id.content),basicResponse.getMessage(), Snackbar.LENGTH_SHORT);

          }

        }

        @Override
        public void failure(RetrofitError error) {
          Log.e("", "failure: ",error );
          SnackBarUtils.showSnackBar(findViewById(android.R.id.content), getResources().getString(R.string.error)+error, Snackbar.LENGTH_SHORT);
          mProgressDialog.hide();
        }
      });
    }
    catch (Exception Exp){
      Exp.printStackTrace();
    }

  }

}
