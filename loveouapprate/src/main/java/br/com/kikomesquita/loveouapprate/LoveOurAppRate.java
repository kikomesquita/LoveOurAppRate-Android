package br.com.kikomesquita.loveouapprate;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;


public class LoveOurAppRate {

    private static final String TAG = LoveOurAppRate.class.getSimpleName();

    private static final String PREF_NAME = "LoveOurAppRate";
    private static final String KEY_INSTALL_DATE = "loar_install_date";
    private static final String KEY_LAUNCH_TIMES = "loar_launch_times";
    private static final String KEY_OPT_OUT = "loar_opt_out";
    private static final String KEY_ASK_LATER_DATE = "loar_ask_later_date";

    private static Date mInstallDate = new Date();
    private static int mLaunchTimes = 0;
    private static boolean mOptOut = false;
    private static Date mAskLaterDate = new Date();

    private static Config sConfig = new Config();

    /**
     * Initialize configuration.
     * @param config Configuration object.
     */
    public static void init(Config config) {
        sConfig = config;
    }


    /**
     * Call this API when the launcher activity is launched.<br>
     * It is better to call this API in onCreate() of the launcher activity.
     * @param context Context
     */
    public static void onCreate(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        // If it is the first launch, save the date in shared preference.
        if (pref.getLong(KEY_INSTALL_DATE, 0) == 0L) {
            storeInstallDate(context, editor);
        }
        // Increment launch times
        int launchTimes = pref.getInt(KEY_LAUNCH_TIMES, 0);
        launchTimes++;
        editor.putInt(KEY_LAUNCH_TIMES, launchTimes);
        log("Launch times; " + launchTimes);

        editor.apply();

        mInstallDate = new Date(pref.getLong(KEY_INSTALL_DATE, 0));
        mLaunchTimes = pref.getInt(KEY_LAUNCH_TIMES, 0);
        mOptOut = pref.getBoolean(KEY_OPT_OUT, false);
        mAskLaterDate = new Date(pref.getLong(KEY_ASK_LATER_DATE, 0));

        printStatus(context);
    }



     /**
     * Check whether the rate dialog should be shown or not.
     * Developers may call this method directly if they want to show their own view instead of
     * dialog provided by this library.
     * @return
     */
    public static boolean shouldShowRateRequest() {
        if (mOptOut) {
            return false;
        } else {
            if (mLaunchTimes >= sConfig.mCriteriaLaunchTimes) {
                return true;
            }
            long threshold = TimeUnit.DAYS.toMillis(sConfig.mCriteriaInstallDays);   // msec
            if (new Date().getTime() - mInstallDate.getTime() >= threshold &&
                    new Date().getTime() - mAskLaterDate.getTime() >= threshold) {
                return true;
            }
            return false;
        }
    }


      /**
     * Stop showing the rate request
     * @param context
     */
    public static void stopRateRequest(final Context context){
        setOptOut(context, true);
    }

    /**
     * Get count number of rate request launches
     * @return
     */
    public static int getLaunchCount(final Context context){
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getInt(KEY_LAUNCH_TIMES, 0);
    }


    /**
     * Show the rate dialog if the criteria is satisfied.
     * @param context Context
     * @return true if shown, false otherwise.
     */
    public static boolean showRateRequestIfNeeded(final Context context) {
        if (shouldShowRateRequest()) {
            showRateRequest(context);
            return true;
        } else {
            return false;
        }
    }

    protected static void clearSharedPreferences(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.remove(KEY_INSTALL_DATE);
        editor.remove(KEY_LAUNCH_TIMES);
        editor.apply();
    }

    /**
     * Set opt out flag.
     * If it is true, the rate dialog will never shown unless app data is cleared.
     * This method is called when Yes or No is pressed.
     * @param context
     * @param optOut
     */
    protected static void setOptOut(final Context context, boolean optOut) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putBoolean(KEY_OPT_OUT, optOut);
        editor.apply();
        mOptOut = optOut;
    }

    /**
     * Store install date.
     * Install date is retrieved from package manager if possible.
     * @param context
     * @param editor
     */
    private static void storeInstallDate(final Context context, SharedPreferences.Editor editor) {
        Date installDate = new Date();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            PackageManager packMan = context.getPackageManager();
            try {
                PackageInfo pkgInfo = packMan.getPackageInfo(context.getPackageName(), 0);
                installDate = new Date(pkgInfo.firstInstallTime);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        editor.putLong(KEY_INSTALL_DATE, installDate.getTime());
        log("First install: " + installDate.toString());
    }

    /**
     * Store the date the user asked for being asked again later.
     * @param context
     */
    protected static void storeAskLaterDate(final Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putLong(KEY_ASK_LATER_DATE, System.currentTimeMillis());
        editor.apply();
    }

    /**
     * Print values in SharedPreferences (used for debug)
     * @param context
     */
    private static void printStatus(final Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        log("*** LoveOuAppStatus Status ***");
        log("Install Date: " + new Date(pref.getLong(KEY_INSTALL_DATE, 0)));
        log("Launch Times: " + pref.getInt(KEY_LAUNCH_TIMES, 0));
        log("Opt out: " + pref.getBoolean(KEY_OPT_OUT, false));
    }

    /**
     * Print log
     * @param message
     */
    private static void log(String message) {
            Log.v(TAG, message);
    }

    public static void showRateRequest(Context context){
        Intent i = new Intent(context, LoveOurAppRateActivity.class);
        i.putExtra("appPackageName", context.getPackageName() );
        context.startActivity(i);
    }

      /**
     *  configuration.
     */
    public static class Config {
        public static final int CANCEL_MODE_BACK_KEY_OR_TOUCH_OUTSIDE = 0;
        public static final int CANCEL_MODE_BACK_KEY = 1;
        public static final int CANCEL_MODE_NONE = 2;

        private String mUrl = null;
        private int mCriteriaInstallDays;
        private int mCriteriaLaunchTimes;
        private int mCancelMode = CANCEL_MODE_BACK_KEY_OR_TOUCH_OUTSIDE;

        /**
         * Constructor with default criteria.
         */
        public Config() {
            this(7, 10);
        }

        /**
         * Constructor.
         *
         * @param criteriaInstallDays
         * @param criteriaLaunchTimes
         */
        public Config(int criteriaInstallDays, int criteriaLaunchTimes) {
            this.mCriteriaInstallDays = criteriaInstallDays;
            this.mCriteriaLaunchTimes = criteriaLaunchTimes;
        }

    }
}

