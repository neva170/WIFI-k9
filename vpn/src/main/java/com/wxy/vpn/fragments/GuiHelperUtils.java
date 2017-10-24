package com.wxy.vpn.fragments;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.widget.CheckBox;

/**
 * Created by bers on 13.03.17.
 */

public final class GuiHelperUtils {

    public static boolean isEmailFormatValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public static boolean isPhoneFormatValid(String phone) {
        return Patterns.PHONE.matcher(phone).matches();
    }

    private static void setErrorAndFocus(TextInputLayout layout, String errorMessage) {
        if (TextUtils.isEmpty(errorMessage)) {
            layout.setError(null);
            layout.setErrorEnabled(false);
        } else {
            layout.setErrorEnabled(true);
            layout.setError(errorMessage);
            layout.requestFocus();
        }
    }

    public static boolean isTermsAccepted(CheckBox termsChkBox) {
        if (!termsChkBox.isChecked()) {
            termsChkBox.setError("Please read the Terms and Conditions and check the box to accept.");
            termsChkBox.requestFocus();
            return false;
        }
        return true;
    }

    public static boolean isPrivacyAccepted(CheckBox privacyChkBox) {
        if (!privacyChkBox.isChecked()) {
            privacyChkBox.setError("Please read the Privacy Policy and check the box to accept.");
            privacyChkBox.requestFocus();
            return false;
        }
        return true;
    }

    public static abstract class TextValidator implements TextWatcher, InputFilter {
        private final TextInputLayout mTextInputLayout;

        protected OnValidInputListener mOnValidInputListener;

        public TextValidator(TextInputLayout textInputLayout) {
            this.mTextInputLayout = textInputLayout;
        }

        public void setOnValidInputListener(OnValidInputListener listener) {
            mOnValidInputListener = listener;
        }

        public abstract boolean validate(String text);

        protected TextInputLayout getTextInputLayout() {
            return mTextInputLayout;
        }

        protected void dispatchCallbacks(boolean valid) {
            if (mOnValidInputListener != null)
                if (valid)
                    mOnValidInputListener.onValid();
                else
                    mOnValidInputListener.onInvalid();
        }

        @Override
        final public void afterTextChanged(Editable s) {
            String text = "";
            try {
                text = mTextInputLayout.getEditText().getText().toString();
            } catch (NullPointerException e) {
                Log.e(this.getClass().getSimpleName(), e.getMessage());
            }
            validate(text);
        }

        @Override
        final public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        final public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (Character.isSpaceChar(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }

        public interface OnValidInputListener {
            void onValid();

            void onInvalid();
        }
    }

    public static class EmailValidator extends TextValidator {

        public EmailValidator(TextInputLayout textInputLayout) {
            super(textInputLayout);
        }

        @Override
        public boolean validate(String text) {
            final String email = text.trim();

            final boolean empty = TextUtils.isEmpty(email);
            final boolean valid = !empty && isEmailFormatValid(email);

            if (empty) {
                setErrorAndFocus(getTextInputLayout(), "Please enter your email.");
            } else if (!valid) {
                setErrorAndFocus(getTextInputLayout(), "Email format is invalid.");
            } else {
                setErrorAndFocus(getTextInputLayout(), null);
            }

            dispatchCallbacks(valid);

            return valid;
        }
    }

    public static class PhoneValidator extends TextValidator {

        public PhoneValidator(TextInputLayout textInputLayout) {
            super(textInputLayout);
        }

        @Override
        public boolean validate(String text) {
            final String phone = text.trim();

            final boolean empty = TextUtils.isEmpty(phone);
            final boolean valid = !empty && isPhoneFormatValid(phone);

            if (empty) {
                setErrorAndFocus(getTextInputLayout(), "Please enter your phone number.");
            } else if (!valid) {
                setErrorAndFocus(getTextInputLayout(), "Phone number is invalid.");
            } else {
                setErrorAndFocus(getTextInputLayout(), null);
            }

            dispatchCallbacks(valid);

            return valid;
        }
    }



    public static class PasswordValidator extends TextValidator {

        private TextInputLayout mPasswordRepeatLayout = null;
        private int mPasswordLength = 0;

        public PasswordValidator(TextInputLayout passwordLayout, int passwordLength) {
            super(passwordLayout);
            mPasswordLength = passwordLength;
        }

        public PasswordValidator(TextInputLayout passwordRepeatLayout, TextInputLayout passwordLayout) {
            super(passwordLayout);
            mPasswordRepeatLayout = passwordRepeatLayout;
        }

        @Override
        public boolean validate(String text) {
            final boolean valid;
            final String errorMessage;

            if (mPasswordRepeatLayout == null) {
                valid = text.trim().length() >= mPasswordLength;
                errorMessage = "Use at least " + mPasswordLength + " characters.";
            } else {
                valid = getTextInputLayout().getEditText().getText().toString().contentEquals(
                        mPasswordRepeatLayout.getEditText().getText().toString()
                );
                errorMessage = "Passwords must match.";
            }

            if (!valid) {
                setErrorAndFocus(getTextInputLayout(), errorMessage);
            } else {
                setErrorAndFocus(getTextInputLayout(), null);
            }

            dispatchCallbacks(valid);

            return valid;
        }
    }

    public static class RequiredFieldValidator extends TextValidator {

        public RequiredFieldValidator(TextInputLayout textInputLayout) {
            super(textInputLayout);
        }

        @Override
        public boolean validate(String text) {
            final boolean valid = !TextUtils.isEmpty(text.trim());

            if (!valid) {
                setErrorAndFocus(getTextInputLayout(), "This field is required.");
            } else {
                setErrorAndFocus(getTextInputLayout(), null);
            }

            return valid;
        }
    }

}
