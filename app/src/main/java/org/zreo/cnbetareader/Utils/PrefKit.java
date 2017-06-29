package org.zreo.cnbetareader.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class PrefKit {

    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context); //PreferenceManager.getDefaultSharedPreferences(Context)是一个静态方法，获取到一个全局的Preference对象
    }

    public static void writeBoolean(Context context, String key, boolean value) {
        SharedPreferences appPrefs = getSharedPreferences(context);
        SharedPreferences.Editor edit = appPrefs.edit();
        edit.putBoolean(key, value);
        edit.apply();
    }


    public static boolean getBoolean(Context context, String key, boolean def) {
        SharedPreferences appPrefs = getSharedPreferences(context);
        return appPrefs.getBoolean(key, def);
    }




    public static boolean getBoolean(Context context, int key, boolean def) {
        SharedPreferences appPrefs = getSharedPreferences(context);
        return appPrefs.getBoolean(context.getString(key), def);
    }

    public static float getLong(Context context, String key, float def) {
        SharedPreferences appPrefs = getSharedPreferences(context);
        return Float.parseFloat(appPrefs.getString(key, Float.toString(def)));
    }



    public static String getString(Context context, String key, String def) {
        SharedPreferences appPrefs = getSharedPreferences(context);
        return appPrefs.getString(key, def);
    }



    public static void writeInt(Context context, String key, int value) {
        SharedPreferences appPrefs = getSharedPreferences(context);
        SharedPreferences.Editor edit = appPrefs.edit();
        edit.putInt(key, value);
        edit.apply();
    }



    public static int getInt(Context context, String key, int def) {
        SharedPreferences appPrefs = getSharedPreferences(context);
        return appPrefs.getInt(key, def);
    }



    public static void delete(Context context, String key) {
        SharedPreferences appPrefs = getSharedPreferences(context);
        SharedPreferences.Editor edit = appPrefs.edit();
        edit.remove(key);
        edit.apply();
    }
}
