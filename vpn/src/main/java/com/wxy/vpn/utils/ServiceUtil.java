package com.wxy.vpn.utils;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.InputType;
import android.widget.EditText;


import retrofit.RestAdapter;

/**
 * Created by alkesh.chimnani on 2/10/2017.
 */

public class ServiceUtil {
    private static final String BASE_URL = "https://api.authy.com/protected/json/phones/verification/";
  public static RestAdapter restAdapter(){
        RestAdapter rest = new RestAdapter.Builder().setEndpoint(BASE_URL).build();
        return rest;
    }
    public static void firstLetterCapitaEditText(EditText editor){
        editor.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    }

    public static String getPathFromURI(Uri contentUri, Activity activity) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }



}


