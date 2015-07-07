package gs.zenodotus.back;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import gs.zenodotus.R;
import gs.zenodotus.front.SplashScreenActivity;

//import java.util.Calendar;

/**
 * Stores global attributes and methods for application.
 * There is no instance of this class, all methods are static.
 */
public class GlobalDataProvider {
    /** Instance of one data factory in all app. */
    private static OnlineDataFactory factory;

//    private static Calendar lastCapabilitiesActualizationTime;

    /**
     * Creates new factory if it doesn't exist.
     */
    public static void setFactory() {
        if (factory == null) {
            factory = new OnlineDataFactory();
        }
    }

    /**
     * Returns existing factory instance.
     *
     * @return all app one factory instance
     */
    public static DataFactory getFactory() {
        setFactory();
        return factory;
    }

    public static boolean areCapabillitiesActual(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.global_app_preference_key),
                Context.MODE_PRIVATE);
        long storedTimePlain = sharedPreferences.getLong(
                context.getString(R.string.preference_last_actualization_key),
                0);
        Date storedTimeDate = new Date(storedTimePlain);
        Log.v("Zenodot", "" + storedTimePlain);
        Calendar yearAgo = Calendar.getInstance();
        yearAgo.add(Calendar.YEAR, -1);
        Date yearAgoTimeDate = yearAgo.getTime();
        return yearAgoTimeDate.before(storedTimeDate);
    }

    public static void setCapabilitiesActual(Context context) {
        Calendar now = Calendar.getInstance();
        long nowPlain = now.getTimeInMillis();
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.global_app_preference_key),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(
                context.getString(R.string.preference_last_actualization_key),
                nowPlain);
        editor.commit();
    }
}
