package com.wxy.vpn.api;

import android.content.Context;
import android.content.pm.PackageManager;

import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Locale;

import com.wxy.vpn.UserCredentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by bers on 10.03.17.
 */

public final class ApiK9Server {

    public static final int PASSWORD_LENGTH = 6;

    private static final String API_URL = "http://159.203.97.168/api/";
    private static Retrofit sInstance;

    public static Retrofit getRestAdapter(final Context ctx) {
        if (sInstance == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addNetworkInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain) throws IOException {
                            String agent = "";

                            try {
                                agent = String.format(Locale.getDefault(), "Wifi-K9/%s (%d)",
                                        ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName.trim(),
                                        ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionCode);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }

                            Request.Builder builder = chain.request().newBuilder();
                            builder.header("Accept", "application/json");
                            builder.header("User-Agent", agent);

                            Request request = builder.build();
                            return chain.proceed(request);
                        }
                    }).build();

            sInstance = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return sInstance;
    }

    public static ApiInterface getApiInterface(final Context ctx) {
        return getRestAdapter(ctx).create(ApiInterface.class);
    }

    public static ApiError parseError(Response<?> response) {
        Converter<ResponseBody, ApiError> errorConverter =
                getRestAdapter(null).
                        responseBodyConverter(ApiError.class, new Annotation[0]);

        ApiError error = null;
        try {
            error = errorConverter.convert(response.errorBody());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return error;
    }

    public interface ApiInterface {

        @POST("login")
        Call<TokenData> login(
                @Body LoginCred body
        );

        @POST("sign-up")
        Call<ApiResponse> signUp(
                @Body SignupCred body
        );

        @POST("restore-pass")
        Call<ApiResponse> restorePass(
                @Body EmailCred body
        );

        @POST("get-vpn-list")
        Call<ListWrapper<VpnInfo>> getVpnList(
                @Body BasicCred body
        );

        @POST("get-vpn-config")
        Call<ResponseBody> getVpnConfig(
                @Body VpnConfigCred body
        );

        @POST("get-vpn-certificate")
        Call<ResponseBody> getVpnCertificate(
                @Body VpnConfigCred body
        );

        @POST("get-android-cert")
        Call<ResponseBody> getAndroidCertificate(
                @Body AndroidCertificateCred body
        );

        @POST("get-android-key")
        Call<ResponseBody> getAndroidKey(
                @Body AndroidCertificateCred body
        );

        @POST("create-android")
        Call<ApiResponse> createAndroidCertificate(
                @Body AndroidCreateCertificateCred body
        );

        @POST("remove-android")
        Call<ApiResponse> removeAndroidCertificate(
                @Body AndroidCertificateCred body
        );

        @POST("change-pass")
        Call<ApiResponse> changePassword(
                @Body ChangePasswordCred body
        );

        @POST("get-user-credentials")
        Call<UserCred> getUserCredentials(
                @Body BasicCred body
        );

        @POST("set-user-credentials")
        Call<ApiResponse> setUserCredentials(
                @Body SetCredentialsCred body
        );

        @POST("resend-email-confirmation")
        Call<ApiResponse> resendEmailConfirmation(
                @Body EmailCred email
        );

        @POST("ping")
        Call<ApiResponse> ping(
                @Body BasicCred body
        );
    }

    public static class SignupCred {
        String email;
        String password;
        @SerializedName("device-id")
        String deviceId;
        @SerializedName("phone")
        String phone;
        String fullName;

        public SignupCred(String fullName, String email, String password, String deviceId, String phone) {
            this.fullName = fullName;
            this.email = email;
            this.password = password;
            this.deviceId = deviceId;
            this.phone = phone;
        }

    }

    public static class LoginCred {
        String email;
        String password;
        @SerializedName("device-id")
        String deviceId;
        @SerializedName("phone")
        String phone;

        public LoginCred(String eml, String pass, String device, String phone) {
            email = eml;
            password = pass;
            deviceId = device;
            this.phone = phone;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPassword() {
            return password;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public String getEmail() {
            return email;
        }
    }

    public static class EmailCred {
        String email;

        public EmailCred(String email) {
            this.email = email;
        }
    }

    public static class BasicCred {
        String token;
        String email;

        public BasicCred(String token, String email) {
            this.token = token;
            this.email = email;
        }
    }

    static class AndroidCertificateCred {
        String token;
        @SerializedName("device-id")
        String deviceId;
        String email;

        public AndroidCertificateCred(String token, String deviceId, String email) {
            this.token = token;
            this.deviceId = deviceId;
            this.email = email;
        }
    }

    public static class AndroidCreateCertificateCred {
        String token;
        @SerializedName("device-id")
        String deviceId;
        String email;
        String name;

        public AndroidCreateCertificateCred(String token, String deviceId, String email, String name) {
            this.token = token;
            this.deviceId = deviceId;
            this.email = email;
            this.name = name;
        }

        public String getToken() {
            return token;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public String getEmail() {
            return email;
        }

        public String getName() {
            return name;
        }
    }

    public static class VpnConfigCred {
        String token;
        String identificator;
        String email;
        @SerializedName("device-id")
        String deviceId;

        public VpnConfigCred(String token, String identificator, String email, String deviceId) {
            this.token = token;
            this.identificator = identificator;
            this.email = email;
            this.deviceId = deviceId;
        }
    }

    public static class ChangePasswordCred {
        String token;
        @SerializedName("old-password")
        String oldPass;
        @SerializedName("new-password")
        String newPass;
        String email;

        public ChangePasswordCred(String token, String oldPass, String newPass, String email) {
            this.token = token;
            this.oldPass = oldPass;
            this.newPass = newPass;
            this.email = email;
        }
    }

    static class SetCredentialsCred {
        String token;
        String email;
        UserCredentials data;

        public SetCredentialsCred(String token, String email, UserCredentials data) {
            this.token = token;
            this.email = email;
            this.data = data;
        }
    }


    public static class VpnInfo {
        String name;
        String ip;
        String identificator;

        public String getName() {
            return name;
        }

        public String getIp() {
            return ip;
        }

        public String getIdentificator() {
            return identificator;
        }

        @Override
        public String toString() {
            return name + " " + ip + " " + identificator;
        }
    }

    public static class TokenData {
        Data data;

        public String getToken() {
            return data.token;
        }

        static class Data {
            String token;
        }
    }

    public static class UserCred {
        public UserCredentials data;

        @Override
        public String toString() {
            return String.format(Locale.getDefault(),
                    "Name: %s\nCountry: %s\nState: %s\nCity: %s\nAddress 1: %s\nAddress 2: %s\nZip: %s",
                    data.getFullName(), data.getCountry(), data.getState(), data.getCity(),
                    data.getAddress1(), data.getAddress2(), data.getZipCode()
            );
        }
    }

    public static class ApiResponse {
        Data data;

        public String getMessage() {
            return data.message;
        }

        static class Data {
            Boolean success;
            String message;
        }
    }

    public static class ApiError {
        Errors errors;

        public Errors getErrors() {
            return errors;
        }

        public static class Errors {
            int code;
            String[] messages;

            public int getCode() {
                return code;
            }

            public String[] getMessages() {
                return messages;
            }
        }
    }

    public class ListWrapper<T> {
        List<T> data;

        public List<T> getList() {
            return data;
        }
    }

}
