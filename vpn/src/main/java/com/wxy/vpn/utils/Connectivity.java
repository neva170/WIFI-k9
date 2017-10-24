package com.wxy.vpn.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.wxy.vpn.MasterActivity;
import com.wxy.vpn.api.ApiK9Server;
import com.wxy.vpn.core.Preferences;

import de.blinkt.openvpn.BuildConfig;
import de.blinkt.openvpn.core.VpnStatus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by bers on 17.04.17.
 */

public class Connectivity {
    /**
     * Get the network info
     */
    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * Get Wi-Fi SSID name
     */
    public static String getNetworkName(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        return info.getSSID();
    }

    /**
     * Check if there is any connectivity to a Wifi network
     */
    public static boolean isConnectedWifi(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * Check if there is any connectivity to a mobile network
     */
    public static boolean isConnectedMobile(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }


    public interface OnNetworkChanged {
        void onWifi(boolean connected);

        void onMobile(boolean connected);

        void onNotAvailable(String message);
    }

    public static class NetworkStateReceiver extends BroadcastReceiver implements Callback<ApiK9Server.ApiResponse>, OnNetworkChanged {

        private final static int NOTIFICATION_ID = 1;
        private final static String NOTIFICATION_ID_TAG = "notificationId";
        private final static String ACTION_YES = BuildConfig.APPLICATION_ID + ".ACTION_YES";
        private final static String ACTION_NO = BuildConfig.APPLICATION_ID + ".ACTION_NO";
        private final static String ACTION_NEVER = BuildConfig.APPLICATION_ID + ".ACTION_NEVER";
        private final String TAG = getClass().getSimpleName();

        private int mConnectedType;
        private Context mContext;

        @Override
        public void onReceive(Context context, Intent intent) {
            // handle intents from network types dispatching
            mContext = context;

            switch (intent.getAction()) {
                case ACTION_YES:
                    startVpnAndMaximize();
                    NotificationManagerCompat.from(context).cancel(intent.getIntExtra(NOTIFICATION_ID_TAG, -1));
                    break;
                case ACTION_NO:
                    SettingsStorage.Notification.setTimeout(context, getNetworkName(context), 60);
                    NotificationManagerCompat.from(context).cancel(intent.getIntExtra(NOTIFICATION_ID_TAG, -1));
                    break;
                case ACTION_NEVER:
                    SettingsStorage.Ssid.add(
                            context,
                            SettingsStorage.Ssid.LIST_TYPE_SAFE_LIST,
                            new SettingsStorage.Ssid.Item(getNetworkName(context), 0L)
                    );
                    NotificationManagerCompat.from(context).cancel(intent.getIntExtra(NOTIFICATION_ID_TAG, -1));
                    break;
            }

            // dispatch network types
            NetworkInfo currentNetwork = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (currentNetwork != null) {
                if (currentNetwork.getState() == NetworkInfo.State.CONNECTED) {
                    mConnectedType = currentNetwork.getType();

                    // ensure there is a real internet connection available
                    ApiK9Server.getApiInterface(context).ping(
                            new ApiK9Server.BasicCred(
                                    SettingsStorage.User.getToken(context),
                                    SettingsStorage.User.getEmail(context)
                            )
                    ).enqueue(this);
                } else {
                    switch (currentNetwork.getType()) {
                        case ConnectivityManager.TYPE_WIFI:
                            onWifi(false);
                            break;
                        case ConnectivityManager.TYPE_MOBILE:
                            onMobile(false);
                            break;
                    }
                }
            }
        }

        // on ping server
        @Override
        public void onResponse(Call<ApiK9Server.ApiResponse> call, Response<ApiK9Server.ApiResponse> response) {
            if (!response.isSuccessful() && response.errorBody() != null) {
                ApiK9Server.ApiError error = ApiK9Server.parseError(response);
                for (String errorMessage : error.getErrors().getMessages()) {
                    Log.e(TAG + ": ping k9 server", errorMessage);
                    FirebaseCrash.log(TAG + ": ping k9 server" + errorMessage);
                }
            }

            ApiK9Server.ApiResponse decodedResponse = response.body();
            if (decodedResponse != null) {
                if (mConnectedType == ConnectivityManager.TYPE_WIFI)
                    onWifi(true);
                else if (mConnectedType == ConnectivityManager.TYPE_MOBILE)
                    onMobile(true);
            }
        }

        // on ping server request failure
        @Override
        public void onFailure(Call<ApiK9Server.ApiResponse> call, Throwable throwable) {
            onNotAvailable(throwable.getMessage());
        }

        @Override
        public void onWifi(boolean connected) {
            Log.d(TAG, "Wifi " + (connected ? "connected" : "disconnected"));

            if (!connected) {
                NotificationManagerCompat.from(mContext).cancelAll();

                if (VpnStatus.isVPNActive())


                return;
            }

            if (SettingsStorage.Ssid.exists(mContext, SettingsStorage.Ssid.LIST_TYPE_SAFE_LIST, getNetworkName(mContext))
                    || !SettingsStorage.Notification.isTimeoutExpired(mContext, getNetworkName(mContext)))
                return;

            final String onWifiAutoConnect = Preferences.getDefaultSharedPreferences(mContext)
                    .getString(
                            mContext.getString(com.wxy.vpn.R.string.pref_wifi_on_connected),
                            mContext.getString(com.wxy.vpn.R.string.pref_wifi_autoconnect_action_default)
                    );

            if (onWifiAutoConnect.contentEquals(mContext.getString(com.wxy.vpn.R.string.pref_wifi_autoconnect_action_do_connect))) {
                startVpnAndMaximize();
            } else if (onWifiAutoConnect.contentEquals(mContext.getString(com.wxy.vpn.R.string.pref_wifi_autoconnect_action_do_notify))) {
                notifyWifiConnected();
            }
        }

        private void startVpnAndMaximize() {
         /*   final Intent i = new Intent(mContext, MasterActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);*/
          //  VpnStarter.start(mContext);
        }

        @Override
        public void onMobile(boolean connected) {
            Log.d(TAG, "Mobile " + (connected ? "connected" : "disconnected"));
        }

        @Override
        public void onNotAvailable(String message) {
            Log.d(TAG, "Network not available: " + message);
        }

        private PendingIntent getPendingIntentForAction(String action) {
            Intent intent = new Intent(mContext, this.getClass());
            intent.setAction(action);
            intent.putExtra(NOTIFICATION_ID_TAG, NOTIFICATION_ID);
            return PendingIntent.getBroadcast(mContext, 0, intent, 0);
        }

        private void notifyWifiConnected() {
            final String message = "You just connected to " + getNetworkName(mContext) + ". Should WiFi-K9 protect your Wi-Fi signal?";

            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
            builder
                    .setSmallIcon(com.wxy.vpn.R.drawable.logo_dog_shield)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), com.wxy.vpn.R.drawable.logo_gradient))
                    .setContentTitle("WiFi-K9")
                    .setContentText(message)
                    .setTicker(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .addAction(com.wxy.vpn.R.drawable.icon_yes, mContext.getString(android.R.string.yes), getPendingIntentForAction(ACTION_YES))
                    .addAction(com.wxy.vpn.R.drawable.icon_no, mContext.getString(android.R.string.no), getPendingIntentForAction(ACTION_NO))
                    .addAction(com.wxy.vpn.R.drawable.icon_never, "Never", getPendingIntentForAction(ACTION_NEVER))
                    .setDefaults(NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_LIGHTS);

            NotificationManagerCompat.from(mContext).notify(NOTIFICATION_ID, builder.build());
        }
    }

}
