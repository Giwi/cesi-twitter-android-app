package giwi.org.cesitwitter.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xavier on 16/12/15.
 */
public class PreferenceHelper {

    private static String PREFS = "prefs";

    public static String LOGIN = "login";
    public static String TOKEN = "token";

    public static void setValue(final Context context, String key, String value){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getValue(final Context context, String key){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }
}
