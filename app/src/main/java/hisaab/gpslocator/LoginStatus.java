package hisaab.gpslocator;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Saurabh on 08-09-2015.
 */
public class LoginStatus {
    public static void loggedIn(Context context,boolean status){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("LOGGED_IN",status).apply();
    }
    public static boolean isLoggedIn(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("LOGGED_IN",false);
    }

}
