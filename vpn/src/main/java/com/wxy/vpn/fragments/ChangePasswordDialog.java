package com.wxy.vpn.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.wxy.vpn.api.ApiK9Server;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChangePasswordDialog extends DialogFragment implements Callback<ApiK9Server.ApiResponse> {
    private static final String ARG_EMAIL = "restore_pass_email";
    private static final String ARG_TOKEN = "restore_pass_token";
    private final String TAG = getClass().getSimpleName();

    private String mEmail;
    private String mToken;

    private OnChangePasswordDialogListener mListener;
    private TextInputLayout mOldPassLayout;
    private TextInputLayout mNewPassLayout;
    private TextInputLayout mRepeatPassLayout;

    private GuiHelperUtils.PasswordValidator mOldPasswordValidator;
    private GuiHelperUtils.PasswordValidator mNewPasswordValidator;
    private GuiHelperUtils.PasswordValidator mPasswordRepeatValidator;
    private View mView;

    public ChangePasswordDialog() {
        // Required empty public constructor
    }

    public static ChangePasswordDialog newInstance(String email, String token) {
        ChangePasswordDialog fragment = new ChangePasswordDialog();

        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        args.putString(ARG_TOKEN, token);
        fragment.setArguments(args);

        return fragment;
    }

    private void setupOldPasswordInput() {
        mOldPassLayout = (TextInputLayout) mView.findViewById(com.wxy.vpn.R.id.restore_old_password_layout);
        mOldPasswordValidator = new GuiHelperUtils.PasswordValidator(mOldPassLayout, ApiK9Server.PASSWORD_LENGTH);
        mOldPassLayout.getEditText().addTextChangedListener(mOldPasswordValidator);
        mOldPassLayout.getEditText().setFilters(new InputFilter[]{mOldPasswordValidator});
    }

    private void setupNewPasswordInput() {
        mNewPassLayout = (TextInputLayout) mView.findViewById(com.wxy.vpn.R.id.restore_new_password_layout);
        mNewPasswordValidator = new GuiHelperUtils.PasswordValidator(mNewPassLayout, ApiK9Server.PASSWORD_LENGTH);
        mNewPassLayout.getEditText().addTextChangedListener(mNewPasswordValidator);
        mNewPassLayout.getEditText().setFilters(new InputFilter[]{mNewPasswordValidator});
    }

    private void setupPasswordRepeatInput() {
        mRepeatPassLayout = (TextInputLayout) mView.findViewById(com.wxy.vpn.R.id.restore_repeat_password_layout);
        mPasswordRepeatValidator = new GuiHelperUtils.PasswordValidator(mNewPassLayout, mRepeatPassLayout);
        mRepeatPassLayout.getEditText().addTextChangedListener(mPasswordRepeatValidator);
        mRepeatPassLayout.getEditText().setFilters(new InputFilter[]{mPasswordRepeatValidator});
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEmail = getArguments().getString(ARG_EMAIL);
            mToken = getArguments().getString(ARG_TOKEN);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mView = View.inflate(getActivity(), com.wxy.vpn.R.layout.fragment_change_password_dialog, null);

        setupOldPasswordInput();
        setupNewPasswordInput();
        setupPasswordRepeatInput();

        setupOnResultListener();

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(mView)
                .setTitle("Change your password")
                .setPositiveButton("Change", null)
                .setNegativeButton("Leave", null)
                .create();
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button change = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                change.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        attemptChangePassword();
                    }
                });
            }
        });

        return alertDialog;
    }

    private void setupOnResultListener() {
        if (getActivity() instanceof OnChangePasswordDialogListener) {
            mListener = (OnChangePasswordDialogListener) getActivity();
        } else {
            throw new RuntimeException(getActivity().toString()
                    + " must implement OnChangePasswordDialogListener");
        }
    }

    private void attemptChangePassword() {
        final String oldPass = mOldPassLayout.getEditText().getText().toString().trim();
        final String newPass = mNewPassLayout.getEditText().getText().toString().trim();
        final String repeatNewPass = mRepeatPassLayout.getEditText().getText().toString().trim();

        if (mOldPasswordValidator.validate(oldPass)
                && mNewPasswordValidator.validate(newPass)
                && mPasswordRepeatValidator.validate(repeatNewPass)) {
            Call<ApiK9Server.ApiResponse> changePass = ApiK9Server.getApiInterface(getActivity().getApplicationContext()).changePassword(
                    new ApiK9Server.ChangePasswordCred(
                            mToken, oldPass, newPass, mEmail
                    )
            );
            changePass.enqueue(this);
        }
    }

    @Override
    public void onResponse(Call<ApiK9Server.ApiResponse> call, Response<ApiK9Server.ApiResponse> response) {
        if (!response.isSuccessful() && response.errorBody() != null) {
            ApiK9Server.ApiError error = ApiK9Server.parseError(response);
            onApiResult(false, error.getErrors().getMessages()[0]);
            return;
        }

        ApiK9Server.ApiResponse decodedResponse = response.body();
        if (decodedResponse != null) {
            onApiResult(true, decodedResponse.getMessage());
        }
    }

    @Override
    public void onFailure(Call<ApiK9Server.ApiResponse> call, Throwable throwable) {
        Log.e(TAG, "Error while change password", throwable);
    }


    public void onApiResult(boolean success, String message) {
        if (mListener != null) {
            mListener.onChangePasswordResult(success, message);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mListener = null;
    }

    public interface OnChangePasswordDialogListener {
        void onChangePasswordResult(boolean success, String message);
    }
}
