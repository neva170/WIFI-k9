package com.wxy.vpn.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.wxy.vpn.StartUp;
import com.wxy.vpn.UserCredentials;

/**
 * Created by bers on 19.03.17.
 */

public class SettingsStorage {
    final static private String SYSTEM_UNIQUE_ID = "SYSTEM_UNIQUE_ID";

    final static private String SETTINGS_STORAGE = "SETTINGS_STORAGE";

    static private SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(SETTINGS_STORAGE, Context.MODE_PRIVATE);
    }

    static private SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    static private void setString(Context context, String key, String value) {
        final SharedPreferences.Editor editor = getEditor(context);
        editor.putString(key, value);
        editor.commit();
    }


    static public class User {
        final static private String FULLNAME = "USER_FULLNAME";
        final static private String COUNTRY = "USER_COUNTRY";
        final static private String STATE = "USER_STATE";
        final static private String CITY = "USER_CITY";
        final static private String ADDRESS1 = "USER_ADDRESS1";
        final static private String ADDRESS2 = "USER_ADDRESS2";
        final static private String ZIP = "USER_ZIP";
        final static private String EMAIL = "USER_EMAIL";
        final static private String TOKEN = "USER_TOKEN";
        final static private String PASS = "USER_PASS";

        static public String login(Context context, String email, String password, String token) {
            setString(context, TOKEN, token);
            setString(context, EMAIL, email);
            setString(context, PASS, password);
            return token;
        }

        static public boolean isLoggedIn(Context context) {
            return !TextUtils.isEmpty(getPreferences(context).getString(TOKEN, null));
        }

        static public void logoutDialog(final Context context) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

            DialogInterface.OnClickListener onLogout = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        logout(context);

                        final Intent intent = new Intent(context, StartUp.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);

                        ((Activity) context).finish();
                    }
                }
            };

            alertBuilder.setMessage(
                    "Are you sure you want to logout?"
            ).setPositiveButton("Logout", onLogout).setNegativeButton("Keep logged", onLogout).show();
        }

        static void logout(Context context) {
            final SharedPreferences.Editor editor = getPreferences(context).edit();
            editor.remove(FULLNAME);
            editor.remove(COUNTRY);
            editor.remove(STATE);
            editor.remove(CITY);
            editor.remove(ADDRESS1);
            editor.remove(ADDRESS2);
            editor.remove(ZIP);
            editor.remove(EMAIL);
            editor.remove(TOKEN);
            editor.remove(PASS);
            editor.apply();
        }

        static public String getToken(Context context) {
            return getPreferences(context).getString(TOKEN, null);
        }

        static public void setPass(Context context, String pass) {
            setString(context, PASS, pass);
        }

        static public String getPass(Context context) {
            return getPreferences(context).getString(PASS, null);
        }

        static public void setCredentials(Context context, UserCredentials credentials) {
            setString(context, FULLNAME, credentials.getFullName());
            setString(context, COUNTRY, credentials.getCountry());
            setString(context, STATE, credentials.getState());
            setString(context, CITY, credentials.getCity());
            setString(context, ADDRESS1, credentials.getAddress1());
            setString(context, ADDRESS2, credentials.getAddress2());
            setString(context, ZIP, credentials.getZipCode());
        }

        static public UserCredentials getCredentials(Context context) {
            return new UserCredentials(
                    getPreferences(context).getString(FULLNAME, null),
                    getPreferences(context).getString(COUNTRY, null),
                    getPreferences(context).getString(STATE, null),
                    getPreferences(context).getString(CITY, null),
                    getPreferences(context).getString(ADDRESS1, null),
                    getPreferences(context).getString(ADDRESS2, null),
                    getPreferences(context).getString(ZIP, null)
            );
        }

        static public void setEmail(Context context, String email) {
            setString(context, EMAIL, email);
        }

        static public String getEmail(Context context) {
            return getPreferences(context).getString(EMAIL, null);
        }
    }

    static public class Vpn {
        final static private String ID = "VPN_ID";
        final static private String CERT_NAME = "VPN_CERT_NAME";
        final static private String CERT_VPN_UUID = "VPN_CERT_UUID";

        static public void setId(Context context, String id) {
            setString(context, ID, id);
        }

        static public String getId(Context context) {
            return getPreferences(context).getString(ID, null);
        }

        static public void setCertName(Context context, String name) {
            setString(context, CERT_NAME, name);
        }

        static public String getCertName(Context context) {
            return getPreferences(context).getString(CERT_NAME, null);
        }

        static public void setCertVpnUuid(Context context, String uuid) {
            setString(context, CERT_VPN_UUID, uuid);
        }

        static public String getCertVpnUuid(Context context) {
            return getPreferences(context).getString(CERT_VPN_UUID, null);
        }
    }

    static public class Device {
        final static private String ID = "DEVICE_ID";

        static private void storeId(Context context, String id) {
            setString(context, ID, id);
        }

        static public String getId(Context context) {
            String deviceId = getPreferences(context).getString(ID, null);

            if (deviceId == null) {
                deviceId = DeviceUuidFactory.getDeviceUuid(context);
                storeId(context, deviceId);
            }

            return deviceId;
        }
    }


    static public class Notification {

        private final static String NOTIFICATION_TAG = "NOTIFICATION";
        private final static String NOTIFICATION_TIMEOUT_MS = NOTIFICATION_TAG + "_TIMEOUT_";
        private final static String NOTIFICATION_START_MS = NOTIFICATION_TAG + "_START_";

        public static boolean isTimeoutExpired(Context context, String ssidName) {
            final long startingPoint = getStartingPoint(context, ssidName);
            return startingPoint == 0 || System.currentTimeMillis() > (startingPoint + getTimeout(context, ssidName));
        }

        public static void setTimeout(Context context, String ssidName, long minutes) {
            SharedPreferences.Editor editor = context.getSharedPreferences(NOTIFICATION_TAG, Context.MODE_PRIVATE).edit();
            editor
                    .putLong(NOTIFICATION_TIMEOUT_MS + ssidName, TimeUnit.MINUTES.toMillis(minutes))
                    .putLong(NOTIFICATION_START_MS + ssidName, System.currentTimeMillis())
                    .apply();
        }

        private static long getTimeout(Context context, String ssidName) {
            SharedPreferences prefs = context.getSharedPreferences(NOTIFICATION_TAG, Context.MODE_PRIVATE);

            return prefs.getLong(NOTIFICATION_TIMEOUT_MS + ssidName, 0);
        }

        private static long getStartingPoint(Context context, String ssidName) {
            SharedPreferences prefs = context.getSharedPreferences(NOTIFICATION_TAG, Context.MODE_PRIVATE);

            return prefs.getLong(NOTIFICATION_START_MS + ssidName, 0);
        }

    }

    static public class Ssid {

        public final static String LIST_TYPE_TAG = "SSID_LIST_TYPE";
        public final static String LIST_TYPE_HISTORY = "SSID_LIST_HISTORY";
        public final static String LIST_TYPE_SAFE_LIST = "SSID_LIST_SAFE_LIST";

        public static void add(Context context, String listType, Ssid.Item historyItem) {
            SharedPreferences.Editor editor = context.getSharedPreferences(listType, Context.MODE_PRIVATE).edit();
            editor
                    .putLong(historyItem.ssidName, historyItem.lastConnected)
                    .apply();
        }

        public static void update(Context context, String listType, String ssidName) {
            SharedPreferences.Editor editor = context.getSharedPreferences(listType, Context.MODE_PRIVATE).edit();
            editor
                    .putLong(ssidName, System.currentTimeMillis())
                    .apply();
        }

        public static void remove(Context context, String listType, String ssidName) {
            SharedPreferences.Editor editor = context.getSharedPreferences(listType, Context.MODE_PRIVATE).edit();
            editor
                    .remove(ssidName)
                    .apply();
        }

        public static boolean exists(Context context, String listType, String ssidName) {
            SharedPreferences prefs = context.getSharedPreferences(listType, Context.MODE_PRIVATE);

            return prefs.contains(ssidName);
        }

        public static ArrayList<Item> getList(Context context, String listType) {
            SharedPreferences prefs = context.getSharedPreferences(listType, Context.MODE_PRIVATE);
            final ArrayList<Item> items = new ArrayList<>();

            for (Map.Entry<String, ?> entry : prefs.getAll().entrySet()) {
                items.add(new Ssid.Item(entry.getKey(), (Long) entry.getValue()));
            }

            return items;
        }

        public static class Item {
            String ssidName;
            Long lastConnected;

            public Item(String ssidName, Long lastConnected) {
                this.ssidName = ssidName;
                this.lastConnected = lastConnected;
            }

            public String getSsidName() {
                return ssidName;
            }

            public Long getLastConnected() {
                return lastConnected;
            }
        }
    }

}
