package com.techmata.transcomfy.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.techmata.transcomfy.app.database.TransDbHelper;

public class PreferenceHelper {

    private static SharedPreferences mSharedPreferences;

    public static boolean setAccessToken(String token, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("access_token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("access_token", token);
        return editor.commit();
    }

    public static String getAccessToken(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("access_token", Context.MODE_PRIVATE);
        return mSharedPreferences.getString("access_token", null);
    }

    public static boolean setRefreshToken(String token, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("refresh_token", token);
        return editor.commit();
    }

    public static String getRefreshToken(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return mSharedPreferences.getString("refresh_token", null);
    }

    public static boolean setEmail(String email, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("email", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("email", email);
        return editor.commit();
    }
    public static String getEmail(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("email", Context.MODE_PRIVATE);
        return mSharedPreferences.getString("email", "");
    }
    public static boolean setType(int ID, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("type", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("type", ID);
        return editor.commit();
    }

    public static int getType(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("type", 0);
        return mSharedPreferences.getInt("type", 0);
    }

    public static int getHasPage(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("page", 0);
        return mSharedPreferences.getInt("page", 0);
    }

    public static void logOut(Context mContext) {
        setAccessToken(null, mContext);
        setRefreshToken(null, mContext);
        mContext.deleteDatabase(TransDbHelper.DATABASE_NAME );
    }


    public static int getHasInterests(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("interests", 0);
        return mSharedPreferences.getInt("interests", 0);
    }
}
