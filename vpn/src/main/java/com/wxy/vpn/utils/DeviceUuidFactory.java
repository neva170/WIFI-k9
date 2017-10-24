package com.wxy.vpn.utils;

import android.content.Context;
import android.os.AsyncTask;

import java.util.UUID;

/**
 * Created by bers on 18.03.17.
 */

public class DeviceUuidFactory {
    public static String getDeviceUuid(Context context) {
        String uniqueID = null;

        AsyncTask<Context, Void, String> task = new AsyncTask<Context, Void, String>() {
            @Override
            protected String doInBackground(Context... params) {
                final Context context = params[0];
                String id = UUID.randomUUID().toString();

                // TODO doesn't work for some reason
//                    try {
//                        final AdvertisingIdClient.Info advertisingIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
//                        id = advertisingIdInfo.getId();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (GooglePlayServicesNotAvailableException e) {
//                        e.printStackTrace();
//                    } catch (GooglePlayServicesRepairableException e) {
//                        e.printStackTrace();
//                    }

                return id;
            }
        }.execute(context);

        try {
            uniqueID = task.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return uniqueID;
    }
}
