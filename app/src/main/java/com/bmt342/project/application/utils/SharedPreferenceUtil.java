package com.bmt342.project.application.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceUtil {

    private static Context context;

    public SharedPreferenceUtil(Context context) {
        this.context = context;
    }

    public static boolean getLoginStatus() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("isLogin", false);
    }

    public static void deleteLoginStatus() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("isLogin");
        editor.apply();
    }

    public static void saveLoginStatus() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLogin", true);
        editor.apply();
    }


    public static boolean login(String username, String password){
        if (username.equals("admin")&&password.equals("admin")){
            return true;
        }
        return false;
    }
}
