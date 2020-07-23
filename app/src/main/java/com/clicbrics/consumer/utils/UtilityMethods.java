package com.clicbrics.consumer.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.PermissionChecker;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.BlobEntity;
import com.buy.housing.backend.userEndPoint.model.CommonResponse;
import com.buy.housing.backend.userEndPoint.model.SearchCriteria;
import com.buy.housing.backend.userEndPoint.model.UserLoginResponse;
import com.clicbrics.consumer.BuildConfig;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.customview.CircularImageView;
import com.clicbrics.consumer.customview.CustomWebDialog;
import com.clicbrics.consumer.framework.customview.CustomDialog;
import com.clicbrics.consumer.framework.util.HousingLogs;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.model.Project;
import com.clicbrics.consumer.wrapper.ProjectBuilderWrapper;
import com.clicbrics.consumer.wrapper.ProjectWrapper;
import com.clicbrics.consumer.wrapper.SaveSearchWrapper;
import com.clicbrics.consumer.wrapper.TopLocalityModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.http.HttpHeaders;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tapadoo.alerter.Alerter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * this class contains application's common methods
 */
@SuppressWarnings("deprecation")
public class UtilityMethods {

    private static final String TAG = UtilityMethods.class.getSimpleName();

    public static Set<String> favoriteIDs=new HashSet<>();
    public static List<String> savedSearchList = new ArrayList<>();

    public static HttpHeaders getHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("RequestId", "AIzaSyA1u2KqZCb3tcAIzaSyA1u2KqZCb3tcEgTyl5EywIgXhY56c6oVAEgTyl5EywIgXhY56c6oVA");
        httpHeaders.set("AppName", Constants.AppConstants.AppType.Consumer.toString());
        httpHeaders.set("AppVersion", BuildConfig.VERSION_NAME);
        httpHeaders.set("AppPackage", BuildConfig.APPLICATION_ID);
        httpHeaders.set("userToken","sadjfbkjbkjvkjvkvkcvkjxcvbjbvivjd343sdnkn");
        return httpHeaders;
    }

    public static int isUpdate(String newVersion, String oldVersion) {
        // String newVersion = "3.0.0.25"; //smaller = -1, equal =0, greater=1
        // String oldVersion = "3.0.0.23";
        String[] vals1 = newVersion.split("\\.");
        String[] vals2 = oldVersion.split("\\.");
        int i = 0;
        while (i < vals1.length && i < vals2.length
                && vals1[i].equals(vals2[i])) {
            i++;
        }

        if (i < vals1.length && i < vals2.length) {
            int diff = Integer.valueOf(vals1[i]).compareTo(
                    Integer.valueOf(vals2[i]));
            return diff < 0 ? -1 : diff == 0 ? 0 : 1;
        }

        return vals1.length < vals2.length ? -1
                : vals1.length == vals2.length ? 0 : 1;
    }

    private static boolean isConnected;
    public static boolean isInternetConnected(Context mContext) {
        /*try {
            ConnectivityManager connectivity = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                final NetworkInfo activeNetwork = connectivity
                        .getActiveNetworkInfo();

                if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                    return true;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;*/

        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName("google.com");
                    } catch (UnknownHostException e) {
                        return null;
                    } catch (Exception e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(1000, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inetAddress!=null && !inetAddress.equals("");
    }


    public static String getVersionName(Activity activity) {
        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException ex) {
            return "";
        }
    }

    public static int getVersionCode(Activity activity) {
        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {
            return 0;
        }
    }

    public static void removeFromPref(Context context, String key) {
        try {
            SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = mySPrefs.edit();
            editor.remove(key);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method save  a String in Application prefs
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveStringInPref(Context context, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * This method returns the string value stored in Application prefs against a key
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getStringInPref(Context context, String key, String defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defaultValue);

    }

    /**
     * This method save  a Int in Application prefs
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveIntInPref(Context context, String key, int value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * This method returns the int value stored in Application prefs against a key
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getIntInPref(Context context, String key, int defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(key, defaultValue);

    }

    /**
     * This method clears a preference
     *
     * @param context
     * @param key
     */
    public static void clearPreference(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }

    /*public static List<Long> getSetValuesFromPrefs(Context context, String key) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Gson gson = new Gson();
            String json = prefs.getString(Constants.AppConstants.PROJECT_ID_SET, "");
            Type type = new TypeToken<List<Long>>() {
            }.getType();

            ArrayList<Long> list = gson.fromJson(json, type);
            if ((list != null) && (!list.isEmpty())) {
                Collections.reverse(list);
            }
            return list;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void saveSetInPref(Context context, String key, List<Long> set) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(set);
        prefsEditor.putString(Constants.AppConstants.PROJECT_ID_SET, json);
        prefsEditor.apply();
    }*/

    /**
     * This method save  a Long in Application prefs
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveLongInPref(Context context, String key, long value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * This method returns the Long value stored in Application prefs against a key
     *
     * @param context
     * @param key
     * @param defaultValue
     */
    public static long getLongInPref(Context context, String key, long defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong(key, defaultValue);

    }

    public static void saveFloatInPref(Context context, String key, float value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    /**
     * This method returns the Long value stored in Application prefs against a key
     *
     * @param context
     * @param key
     * @param defaultValue
     */
    public static float getFloatInPref(Context context, String key, float defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getFloat(key, defaultValue);

    }



    public static boolean getBooleanInPref(Context context, String key, boolean defaultValue) {
        if(context == null){
            return false;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(key, defaultValue);

    }

    public static void saveBooleanInPref(Context context, String key, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public final static boolean isValidPhoneNumber(String target) {
        if (target == null) {
            return false;
        } else if (target.contains(" ")) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target)
                    .matches();
        }
    }


    public final static boolean isValidEmail(String target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else if (target.contains(" ")) {
            return false;
        } else if (!target.contains(".")) {
            return false;
        } else if (target.contains("@")) {
            int arrayLength = target.split("@").length;
            if (arrayLength == 2 && (target.split("@")[0].length() > 64 || target.split("@")[1].length() > 254)) {
                return false;
            } else if (arrayLength == 1) {
                return false;
            }
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                    .matches();
        }
        return true;
    }

    public static boolean selfPermissionGranted(String[] permission, Context context) {
        // For Android < Android M, self permissions are always granted.
        boolean result = true;
        int targetSdkVersion = android.os.Build.VERSION.SDK_INT;
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                for (String per : permission) {
                    result = context.checkSelfPermission(per)
                            == PackageManager.PERMISSION_GRANTED;
                    if (!result)
                        break;
                }
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                for (String per : permission) {
                    result = PermissionChecker.checkSelfPermission(context, per)
                            == PermissionChecker.PERMISSION_GRANTED;
                    if (!result)
                        break;
                }
            }
        }

        return result;
    }


    public static void showSnackBar(View view, String message, int lengthType) {
        Snackbar snackbar = Snackbar.make(view, message, lengthType);
        snackbar.show();
    }

    public static void showErrorSnackBar(View view, String message, int lengthType) {
        try {
            Snackbar snackbar = Snackbar.make(view, message, lengthType);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.uber_red));
//            snackbar.setActionTextColor(ContextCompat.getColor(view.getContext(),R.color.white));
            snackbar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showNetworkErrorSnackBar(View view, Context context){
        final Snackbar snackbar = Snackbar.make(view, context.getResources().getString(R.string.please_check_network_connection), Snackbar.LENGTH_INDEFINITE);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.uber_red));
        snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.white));
        //final Snackbar finalSnackbar = snackbar;
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }


    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float px2Dp(Context context, float dpValue){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static int getStatusBarHeight(Activity context){
        try {
            int result = 0;
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = context.getResources().getDimensionPixelSize(resourceId);
            }
            return result;
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //Converts  dip into its equivalent px
    public static float convertDpToPx(Resources resources, float value){
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.getDisplayMetrics());
        return px;
    }


    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public static final void setAppFont(ViewGroup mContainer, Typeface mFont, boolean reflect) {
        if (mContainer == null || mFont == null) return;

        final int mCount = mContainer.getChildCount();

        // Loop through all of the children.
        for (int i = 0; i < mCount; ++i) {
            final View mChild = mContainer.getChildAt(i);
            if (mChild instanceof TextView) {
                // Set the font if it is a TextView.
                ((TextView) mChild).setTypeface(mFont);
            } else if (mChild instanceof ViewGroup) {
                // Recursively attempt another ViewGroup.
                setAppFont((ViewGroup) mChild, mFont, false);
            } else if (reflect) {
                try {
                    Method mSetTypeface = mChild.getClass().getMethod("setTypeface", Typeface.class);
                    mSetTypeface.invoke(mChild, mFont);
                } catch (Exception e) { /* Do something... */ }
            }
        }
    }

    /*public static Bitmap setImageFromFile(Context context, ImageView imageView, File file) {
        Bitmap bmp = null;
        if(file.exists()){
            bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            if(bmp != null){
                imageView.setImageBitmap(bmp);
            }else{
                bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_icon);
                imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_icon));
            }
        }
        return bmp;
    }*/

    public static Boolean isOnline() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal == 0);
            return reachable;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Display alert dialog.
     *
     * @param ctx
     * @param title
     * @param message
     * @param isCancelable
     * @param positiveText
     * @param positiveListener
     * @param negativeText
     * @param negativeListener
     * @return void
     * @author Rjha
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static CustomDialog showAlert(Context ctx, int title,
                                         String message, boolean isCancelable, int positiveText,
                                         View.OnClickListener positiveListener, int negativeText,
                                         View.OnClickListener negativeListener) {
        CustomDialog dialog = new CustomDialog(ctx);
        if (title != -1) {
            dialog.setTitle(title);
        }
        dialog.setMessage(message);
        dialog.setCancelable(isCancelable);
        if(positiveText != -1){
            dialog.setPositiveButton(positiveText, positiveListener);
        }
        if (negativeText != -1) {
            dialog.setNegativeButton(negativeText, negativeListener);
        }

        if (!((Activity) ctx).isDestroyed()) {
            dialog.show();
        }
        return dialog;
    }

    /**
     * Display alert dialog.
     *
     * @param ctx
     * @param title
     * @param message
     * @param isCancelable
     * @param positiveText
     * @param positiveListener
     * @param negativeText
     * @param negativeListener
     * @return void
     * @author Rjha
     */
    public static CustomDialog showAlert(Context ctx, int title,
                                         String message, boolean isCancelable, int positiveText,
                                         View.OnClickListener positiveListener, int negativeText,
                                         View.OnClickListener negativeListener, String webURL) {
        CustomDialog dialog = new CustomDialog(ctx);
        if (title != -1) {
            dialog.setTitle(title);
        }
        if (!TextUtils.isEmpty(webURL)) {
            dialog.setPositiveButton(-1, positiveListener);
//			dialog.setmWebViewURL(webURL);
        } else {
            dialog.setMessage(message);
            dialog.setCancelable(isCancelable);
            dialog.setPositiveButton(positiveText, positiveListener);
            if (negativeText != -1) {
                dialog.setNegativeButton(negativeText, negativeListener);
            }
        }
        if (!((Activity) ctx).isFinishing()) {
            dialog.show();

        }

        return dialog;
    }

    /**
     * Return date in specified format.
     *
     * @param milliSeconds Date in milliseconds
     * @param dateFormat   Date format
     * @return String representing date in specified format
     */
    //dd-MMM-yyyy-HH:mm:ss aaa
    public static String getDate(long milliSeconds, String dateFormat) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            String dateString = formatter.format(new Date(milliSeconds));
            return dateString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }


    public static int isUpdateNeeded(String newVersion, String oldVersion) {
        // String newVersion = "3.0.0.25"; //smaller = -1, equal =0, greater=1
        // String oldVersion = "3.0.0.23";
        int returnValue = 0;
        try {
            String[] vals1 = (!TextUtils.isEmpty(newVersion)) ? newVersion.split("\\.") : new String[0];
            String[] vals2 = oldVersion.split("\\.");
            int i = 0;
            while (i < vals1.length && i < vals2.length
                    && vals1[i].equals(vals2[i])) {
                i++;
            }

            if (i < vals1.length && i < vals2.length) {
                int diff = Integer.valueOf(vals1[i]).compareTo(
                        Integer.valueOf(vals2[i]));
                return diff < 0 ? -1 : diff == 0 ? 0 : 1;
            }
            returnValue = vals1.length < vals2.length ? -1
                    : vals1.length == vals2.length ? 0 : 1;
            return returnValue;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            returnValue = 0;
        } catch (Exception e) {
            e.printStackTrace();
            returnValue = 0;
        }
        return returnValue;
    }


    public static String getPriceRange(long minPrice, long maxPrice) {
        String word = "";
        long cr = 10000000;
        long lacs = 100000;
        long th = 1000;
        if (maxPrice >= cr) {
            String amount =String.format("%.2f", (float) minPrice / (float) cr) + " - "
                                + String.format("%.2f", (float) maxPrice / (float) cr);
            if (amount.contains(".00"))
                amount = amount.replace(".00", "");
            word = "\u20B9" + " " + amount + " " + Constants.AppConstants.CRORE;
        } else if (maxPrice > lacs) {
            String amount = String.format("%.2f", (float) minPrice / (float) lacs) + " - "
                                + String.format("%.1f", (float) maxPrice / (float) lacs);
            word = "\u20B9"+ " " + amount + " " + Constants.AppConstants.LAKHS;
        } else {
            String amount = String.format("%.2f", (float) minPrice / (float) th) + " - "
                                + String.format("%.1f", (float) maxPrice / (float) th);
            word = "\u20B9"+ " " + amount + " " + Constants.AppConstants.THOUSAND;
        }
        return word;
    }

    public static String getPriceRangeFloat(float minPrice, float maxPrice) {
        String word = "";
        long cr = 10000000;
        long lacs = 100000;
        long th = 1000;
        if (maxPrice >= cr) {
            String amount =String.format("%.2f", (float) minPrice / (float) cr) + " - "
                                + String.format("%.2f", (float) maxPrice / (float) cr);
            if (amount.contains(".00"))
                amount = amount.replace(".00", "");
            word = "\u20B9" + " " + amount + " " + Constants.AppConstants.CRORE;
        } else if (maxPrice > lacs) {
            String amount = String.format("%.2f", (float) minPrice / (float) lacs) + " - "
                                + String.format("%.1f", (float) maxPrice / (float) lacs);
            word = "\u20B9"+ " " + amount + " " + Constants.AppConstants.LAKHS;
        } else {
            String amount = String.format("%.2f", (float) minPrice / (float) th) + " - "
                                + String.format("%.1f", (float) maxPrice / (float) th);
            word = "\u20B9"+ " " + amount + " " + Constants.AppConstants.THOUSAND;
        }
        return word;
    }


    public static String getPriceRangeFloatWithdash(float minPrice, float maxPrice) {
        String word = "",maxword="",minword="",amountmax="",amountmin="";
        long cr = 10000000;
        long lacs = 100000;
        long th = 1000;
        if (maxPrice >= cr) {

            amountmax=(String.format("%.2f", (float) maxPrice / (float) cr));
            if (amountmax.contains(".00"))
            {
                amountmax = amountmax.replace(".00", "");
            }
            maxword=amountmax + " " + Constants.AppConstants.CRORE;

            if(minPrice>cr)
            {
                amountmin=(String.format("%.2f", (float) minPrice / (float) cr));
                if (amountmin.contains(".00"))
                {
                    amountmin = amountmin.replace(".00", "");
                }
                minword=amountmin + " " + Constants.AppConstants.CRORE;
            }
            else  if(minPrice>lacs)
            {
                amountmin=(String.format("%.2f", (float) minPrice / (float) lacs));
                minword=amountmin + " " + Constants.AppConstants.LAKHS;
            }

            else  if(minPrice>th)
            {
                amountmin=(String.format("%.2f", (float) minPrice / (float) th));
                minword=amountmin + " " + Constants.AppConstants.THOUSAND;
            }
            return   word = "\u20B9" + " " + minword +" - "+maxword;



         /*   String amount =String.format("%.2f", (float) minPrice / (float) cr) + " - "
                    + String.format("%.2f", (float) maxPrice / (float) cr);
            if (amount.contains(".00"))
                amount = amount.replace(".00", "");
            word = "\u20B9" + " " + amount + " " + Constants.AppConstants.CRORE;*/
        }

        if (maxPrice > lacs) {

            amountmax=(String.format("%.2f", (float) maxPrice / (float) lacs));
            maxword=amountmax + " " + Constants.AppConstants.LAKHS;
             if(minPrice>lacs)
            {
                amountmin=(String.format("%.2f", (float) minPrice / (float) lacs));
                minword=amountmin + " " + Constants.AppConstants.LAKHS;
            }

            else  if(minPrice>th)
            {
                amountmin=(String.format("%.2f", (float) minPrice / (float) th));
                minword=amountmin + " " + Constants.AppConstants.THOUSAND;
            }
            return  word = "\u20B9" + " " + minword +" - "+maxword;
           /* if (amountmax.contains(".00")||amountmin.contains(".00"))
            {
                amountmax = amountmax.replace(".00", "");
                amountmin = amountmin.replace(".00", "");
            }*/

        /*    String amount = String.format("%.2f", (float) minPrice / (float) lacs) + " - "
                    + String.format("%.1f", (float) maxPrice / (float) lacs);
            word = "\u20B9"+ " " + amount + " " + Constants.AppConstants.LAKHS;*/
        } else if(maxPrice>th){

            amountmax=(String.format("%.2f", (float) maxPrice / (float) th));
            maxword=amountmax + " " + Constants.AppConstants.THOUSAND;


                amountmin=(String.format("%.2f", (float) minPrice / (float) th));
                minword=amountmin + " " + Constants.AppConstants.THOUSAND;

            return  word = "\u20B9" + " " + minword +" - "+maxword;

           /* String amount = String.format("%.2f", (float) minPrice / (float) th) + " - "
                    + String.format("%.1f", (float) maxPrice / (float) th);
            word = "\u20B9"+ " " + amount + " " + Constants.AppConstants.THOUSAND;*/
        }
        else
        {
            amountmax=(String.format("%.2f", (float) maxPrice / (float) th));
            maxword=amountmax + " " + Constants.AppConstants.THOUSAND;

            amountmin=(String.format("%.2f", (float) minPrice / (float) th));
            minword=amountmin + " " + Constants.AppConstants.THOUSAND;

            return word = "\u20B9" + " " + minword +" - "+maxword;
          /*  String amount = String.format("%.2f", (float) minPrice / (float) th) + " - "
                    + String.format("%.1f", (float) maxPrice / (float) th);
            word = "\u20B9"+ " " + amount + " " + Constants.AppConstants.THOUSAND;*/
        }
//        return word;
    }

    /*
    * Method to convert price number to work likem
    * 10000000 ->Cr
    * */
    public static String getPriceWord(long price) {
        String word = "";
        long cr = 10000000;
        long lacs = 100000;
        long th = 1000;
        if (price >= cr) {
            String amount = String.format("%.2f", (float) price / (float) cr);
            if (amount.contains(".00"))
                amount = amount.replace(".00", "");
            BigDecimal stripedVal = new BigDecimal(amount).stripTrailingZeros();
            word = "\u20B9"+ " " + stripedVal.toPlainString() + " " + Constants.AppConstants.CRORE;
            //word = "\u20B9"+ " " + amount + " " + Constants.AppConstants.CRORE;
        } else if (price > lacs) {
            String amount = String.format("%.2f", (float) price / (float) lacs);
            BigDecimal stripedVal = new BigDecimal(amount).stripTrailingZeros();
            word = "\u20B9" + " " + stripedVal.toPlainString() + " " + Constants.AppConstants.LAKHS;
            //word = "\u20B9" + " " + amount + " " + Constants.AppConstants.LAKHS;
        } else {
            String amount = String.format("%.2f", (float) price / (float) th);
            BigDecimal stripedVal = new BigDecimal(amount).stripTrailingZeros();
            word = "\u20B9"+ " " + stripedVal.toPlainString() + " " + Constants.AppConstants.THOUSAND;
            //word = "\u20B9"+ " " + amount + " " + Constants.AppConstants.THOUSAND;
        }
        return word;
    }


    public static StyleSpan getPriceWordBold(long price) {
        String word = "";
        long cr = 10000000;
        long lacs = 100000;
        long th = 1000;
        if (price >= cr) {
            String amount = String.format("%.2f", (float) price / (float) cr);
            if (amount.contains(".00"))
                amount = amount.replace(".00", "");
            BigDecimal stripedVal = new BigDecimal(amount).stripTrailingZeros();
            word = "\u20B9"+ " " + stripedVal.toPlainString() + " " + Constants.AppConstants.CRORE;
            //word = "\u20B9"+ " " + amount + " " + Constants.AppConstants.CRORE;
        } else if (price > lacs) {
            String amount = String.format("%.2f", (float) price / (float) lacs);
            BigDecimal stripedVal = new BigDecimal(amount).stripTrailingZeros();
            word = "\u20B9" + " " + stripedVal.toPlainString() + " " + Constants.AppConstants.LAKHS;
            //word = "\u20B9" + " " + amount + " " + Constants.AppConstants.LAKHS;
        } else {
            String amount = String.format("%.2f", (float) price / (float) th);
            BigDecimal stripedVal = new BigDecimal(amount).stripTrailingZeros();
            word = "\u20B9"+ " " + stripedVal.toPlainString() + " " + Constants.AppConstants.THOUSAND;
            //word = "\u20B9"+ " " + amount + " " + Constants.AppConstants.THOUSAND;
        }

        SpannableString txtSpannable= new SpannableString(word);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
//        txtSpannable.setSpan(new RelativeSizeSpan(0.5f), 0,1, 0);
        txtSpannable.setSpan(boldSpan, 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return boldSpan;
    }
    /*
      * Method to convert price number to work likem
      * 10000000 ->Cr
      * */
    public static String getPriceWordWithoutSymbol(long price) {
        String word = "";
        long cr = 10000000;
        long lakhs = 100000;
        if (price >= cr) {
            String amount = String.format("%.2f", (float) price / (float) cr);
            if (amount.contains(".00")) {
                amount = amount.replace(".00", "");
            }
            BigDecimal stripedVal = new BigDecimal(amount).stripTrailingZeros();
            word = stripedVal.toPlainString() + Constants.AppConstants.CRORE;
            //word = amount + Constants.AppConstants.CRORE;
        } else {
            String amount = String.format("%.2f", (float) price / (float) lakhs);
            BigDecimal stripedVal = new BigDecimal(amount).stripTrailingZeros();
            word = stripedVal.toPlainString() + Constants.AppConstants.LAKHS;
            //word = amount + Constants.AppConstants.LAKHS;
        }
        return word;
    }

    /*
     * Method to convert price number to work likem
     * 10000000 ->Cr
     * */
    public static SpannableString getPriceWordWithoutSymbolofDifferentSize(long price) {
        String word = "";
        long cr = 10000000;
        long lakhs = 100000;
        if (price >= cr) {
            String amount = String.format("%.2f", (float) price / (float) cr);
            if (amount.contains(".00")) {
                amount = amount.replace(".00", "");
            }
            BigDecimal stripedVal = new BigDecimal(amount).stripTrailingZeros();

            word = stripedVal.toPlainString() + Constants.AppConstants.CRORE;
//            String s= "Hello Everyone";

            //word = amount + Constants.AppConstants.CRORE;
        } else {
            String amount = String.format("%.2f", (float) price / (float) lakhs);
            BigDecimal stripedVal = new BigDecimal(amount).stripTrailingZeros();
            word = stripedVal.toPlainString() + Constants.AppConstants.LAKHS;
            //word = amount + Constants.AppConstants.LAKHS;
        }
        SpannableString ss1=  new SpannableString(word);
        if(word.contains("Cr"))
        {
            ss1.setSpan(new RelativeSizeSpan(0.5f), word.length()-2,word.length(), 0);
        }
        else
        {
            ss1.setSpan(new RelativeSizeSpan(0.5f), word.length()-1,word.length(), 0);
        }

        // set size
        return ss1;
    }
    /*
* Method to convert price number to work likem
* 10000000 ->Cr
* */
    public static String getPriceWordTillThousand(double price) {
        String word = "";
        long cr = 10000000;
        long lakhs = 100000;
        long kValue = 1000;
        Log.d(TAG, "price->" + price);
        if (price >= cr) {
            String amount = String.format("%.2f", (float) price / (float) cr);
            if (amount.contains(".00"))
                amount = amount.replace(".00", "");
            BigDecimal stripedVal = new BigDecimal(amount).stripTrailingZeros();
            word = "\u20B9" + " " + stripedVal.toPlainString() + Constants.AppConstants.CRORE;
            //word = "\u20B9" + " " + amount + Constants.AppConstants.CRORE;
        } else if (price >= lakhs) {
            String amount = String.format("%.2f", (float) price / (float) lakhs);
            BigDecimal stripedVal = new BigDecimal(amount).stripTrailingZeros();
            word = "\u20B9"+ " " + stripedVal.toPlainString() + Constants.AppConstants.LAKHS;
            //word = "\u20B9"+ " " + amount + Constants.AppConstants.LAKHS;
        } else {
            String amount = String.format("%.2f", (float) price / (float) kValue);
            BigDecimal stripedVal = new BigDecimal(amount).stripTrailingZeros();
            word = "\u20B9"+ " " + stripedVal.toPlainString() + Constants.AppConstants.THOUSAND;
            //word = "\u20B9"+ " " + amount + Constants.AppConstants.THOUSAND;
        }
        return word;
    }


    public static String getPriceWithLACWord(long price) {
        String word = "";
        long cr = 10000000;
        long lakhs = 100000;
        if (price >= cr) {
            String amount = String.format("%.2f", (float) price / (float) cr);
            if (amount.contains(".00"))
                amount = amount.replace(".00", "");
            BigDecimal stripedVal = new BigDecimal(amount).stripTrailingZeros();
            word = "\u20B9"+ " " + stripedVal.toPlainString() + Constants.AppConstants.CRORE;
            //word = "\u20B9"+ " " + amount + Constants.AppConstants.CRORE;
        } else {
            String amount = String.format("%.1f", (float) price / (float) lakhs);
            BigDecimal stripedVal = new BigDecimal(amount).stripTrailingZeros();
            word = "\u20B9" + " " + stripedVal.toPlainString() + "Lac";
            //word = "\u20B9" + " " + amount + "Lac";
        }
        return word;
    }

    public static Bitmap setImageFromFile(Context context, ImageView imageView, File file) {
        Bitmap bmp = null;
        if (file.exists()) {
            bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bmp != null) {
                imageView.setImageBitmap(bmp);
            }//else{
            //bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_person);
            //imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_person));
            //}
        }
        return bmp;
    }

    public static Bitmap setBlurredImageFromFile(Context context, ImageView imageView, File file) {
        Bitmap bmp = null;
        if (file.exists()) {
            bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bmp != null) {
                bmp = fastblur(bmp);
                imageView.setImageBitmap(bmp);
            }//else{
            //bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_person);
            //imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_person));
            //}
        }
        return bmp;
    }

    public static void  saveUserProfile(Context context, String loginType, String userName,
                                        String email, String countryCode, String mobileNumber,
                                        String image_url, String countryname, String mPassword) {
        UtilityMethods.saveStringInPref(context, Constants.AppConstants.LOGIN_TYPE_PREF_KEY, loginType);
        UtilityMethods.saveStringInPref(context, Constants.AppConstants.USER_NAME_PREF_KEY, userName);
        UtilityMethods.saveStringInPref(context, Constants.AppConstants.EMAIL_PREF_KEY, email);
        UtilityMethods.saveStringInPref(context, Constants.AppConstants.SELECTED_COUNTRY_STD_CODE, countryCode);
        UtilityMethods.saveStringInPref(context, Constants.AppConstants.MOBILE_PREF_KEY, mobileNumber);
        UtilityMethods.saveStringInPref(context, Constants.AppConstants.IMAGE_URL, image_url);
        UtilityMethods.saveStringInPref(context, Constants.AppConstants.PASS_PREF_KEY, mPassword);
        UtilityMethods.saveStringInPref(context, Constants.AppConstants.SELECTED_COUNTRY__NAME, countryname);

    }


    public static void clearUserProfile(Context context) {
        UtilityMethods.removeFromPref(context, Constants.ServerConstants.USER_ID);
        UtilityMethods.removeFromPref(context, Constants.AppConstants.LOGIN_TYPE_PREF_KEY);
        UtilityMethods.removeFromPref(context, Constants.AppConstants.USER_NAME_PREF_KEY);
        UtilityMethods.removeFromPref(context, Constants.AppConstants.EMAIL_PREF_KEY);
        UtilityMethods.removeFromPref(context, Constants.AppConstants.SELECTED_COUNTRY_STD_CODE);
        UtilityMethods.removeFromPref(context, Constants.AppConstants.MOBILE_PREF_KEY);
        UtilityMethods.removeFromPref(context, Constants.AppConstants.IMAGE_URL);
        UtilityMethods.removeFromPref(context, Constants.AppConstants.PROJECT_ID_SET);
        UtilityMethods.removeFromPref(context, Constants.AppConstants.SAVED_SEARCH_COUNT);
        UtilityMethods.removeFromPref(context, Constants.AppConstants.IS_PASSWORD_AVAILABLE_PREF_KEY);
        UtilityMethods.removeFromPref(context, Constants.AppConstants.SAVED_SEARCHES);
        UtilityMethods.removeFromPref(context, Constants.MORE_FRAGMENT_UPDATE);
        File profilePicFile = new File(context.getFilesDir() + File.separator + Constants.AppConstants.PROFILE_PIC);
        if (profilePicFile.exists()) {
            profilePicFile.delete();
        }
    }

    public static String getKey() {
        return "BHBJ78bhbj7878jh";
    }

    public static String getToken(){
        return "sadjfbkjbkjvkjvkvkcvkjxcvbjbvivjd343sdnkn";
    }

    public static void updateImageOnServerInBackground(final Context context, final UserEndPoint mLoginWebService, final int retry, final String image_url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String errorMsg = "";
                try {
                    Bitmap profileImage = Picasso.get().load(image_url).placeholder(R.drawable.ic_person).get();

                    BlobEntity blobEntity = new BlobEntity();

//					InputStream inputStream = new FileInputStream( new File(context.getFilesDir(), Constants.PROFILE_PIC));//You can get an inputStream using any IO API
                    byte[] bytes;
                    byte[] buffer = new byte[8192];
                    int bytesRead;

                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    profileImage.compress(Bitmap.CompressFormat.PNG, 100, output);

                    //try {
                    //	while ((bytesRead = inputStream.read(buffer)) != -1) {
                    //		output.write(buffer, 0, bytesRead);
                    //		}
                    //} catch (IOException e) {
                    //	e.printStackTrace();
                    //}
                    bytes = output.toByteArray();
                    String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
                    blobEntity.setBlob(encodedString);
                    final long rUID = UtilityMethods.getLongInPref(context, Constants.ServerConstants.USER_ID, -1);
                    UserEndPoint.UpdateUserImage updateUsrImage = mLoginWebService.updateUserImage(rUID, blobEntity);
                    UserLoginResponse imageUpdateResponse = updateUsrImage.setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    if (imageUpdateResponse != null && imageUpdateResponse.getStatus()) {
                    } else {
                        errorMsg = "status false in updating image on server";
                        Log.e(TAG, "status false in updating image on server");
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    TrackAnalytics.trackException(TAG, e.getMessage(), e);
                    errorMsg = context.getResources().getString(R.string.network_error_msg);
                    Log.e(TAG, "error in updating image on server");
                }catch (Exception e) {
                    e.printStackTrace();
                    TrackAnalytics.trackException(TAG, e.getMessage(), e);
                    errorMsg = "error in updating image on server";
                    Log.e(TAG, "error in updating image on server");
                }
                if (!TextUtils.isEmpty(errorMsg) && retry < 2) {
                    updateImageOnServerInBackground(context, mLoginWebService, retry + 1, image_url);
                }

            }
        }).start();

    }

    public static Bitmap fastblur(Bitmap sentBitmap) {

        float scale = 0.4f;
        int radius = 6;
        int width = Math.round(sentBitmap.getWidth() * scale);
        int height = Math.round(sentBitmap.getHeight() * scale);
        sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false);

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    public static void loadBlurImage(Context context, final ImageView imageView) {
        String url = UtilityMethods.getStringInPref(context, Constants.AppConstants.IMAGE_URL, "");
        if (!TextUtils.isEmpty(url)) {

            Picasso.get().load(url).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    imageView.setImageBitmap(fastblur(bitmap));
                }

                @Override
                public void onBitmapFailed(Exception e,Drawable errorDrawable) {
                    imageView.setImageResource(R.drawable.splash);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
    }

    public static void loadCircularImageFromPicasso(final Context context, final CircularImageView circularImageView) {

        final String url = UtilityMethods.getStringInPref(context, Constants.AppConstants.IMAGE_URL, "");
        Log.d(TAG, "IMAGE URL->" + url);
        try {
            if (!TextUtils.isEmpty(url)) {
                Picasso.get().load(url).placeholder(R.drawable.ic_person).into(circularImageView);
            }else{
                circularImageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_person));
            }
        } catch (Exception e) {
            e.printStackTrace();
            //circularImageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_person));
        }
    }

    public static float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);

        return -1;
    }


    public static String calculateEmiAmount(double principal, double rate, double time) {

        if(rate == 0){
            return  "\u20B9" + " 0 / month";
        }
        double emi;
        rate = rate / (12 * 100); /*one month interest*/
        time = time * 12; /*one month period*/
        emi = (principal * rate * Math.pow(1 + rate, time)) / (Math.pow(1 + rate, time) - 1);
        if (emi == 0)
            return "Loan not required";
        else
            return getPriceWordTillThousand(emi) + " / month";
    }

    public static int getScreenWidth(Context activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) activity).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;

    }

    public static int getScreenHeight(Context activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) activity).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }

    /*public static void addToFavorites(final View container, final Context context,
                                      final UserEndPoint mUserEndPoint, final long projectID) {

        if (!UtilityMethods.isInternetConnected(context)) {
            Snackbar snackbar = Snackbar.make(container, context.getResources().getString(R.string.network_error_msg), Snackbar.LENGTH_LONG)
                    .setAction(context.getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addToFavorites(container, context, mUserEndPoint, projectID);
                        }
                    });
            snackbar.show();
            return;
        }

        final long userID = UtilityMethods.getLongInPref(context, Constants.ServerConstants.USER_ID, -1);
        if (userID != -1) {
            List<Long> favoriteList = getSetValuesFromPrefs(context, Constants.AppConstants.PROJECT_ID_SET);
            if (favoriteList == null) {
                favoriteList = new ArrayList<>();
            }
            favoriteList.add(projectID);
            clearPreference(context, Constants.AppConstants.PROJECT_ID_SET);
            saveSetInPref(context, Constants.AppConstants.PROJECT_ID_SET, favoriteList);//favoriteIDs);
            new Thread(new Runnable() {
                String errorMsg = "";
                @Override
                public void run() {
                    try {
                        Log.d(TAG, "Add To Favorites " + projectID);
                        final CommonResponse addFavoriteresponse = mUserEndPoint.addFavouriteProject(userID, projectID).setRequestHeaders(getHttpHeaders()).execute();
                    } catch (UnknownHostException e) {
                        errorMsg = context.getResources().getString(R.string.network_error_msg);
                        e.printStackTrace();
                        HousingLogs.d(TAG, e.getMessage());
                    }catch (Exception e) {
                        errorMsg = context.getResources().getString(R.string.something_went_wrong);
                        e.printStackTrace();
                        HousingLogs.d(TAG, e.getMessage());
                    }
                    if(!TextUtils.isEmpty(errorMsg)){
                        *//*if(container != null) {
                            UtilityMethods.showErrorSnackBar(container, errorMsg, Snackbar.LENGTH_LONG);
                        }*//*
                    }
                }
            }).start();
        }

        //Log.d(TAG,"favCount->"+favoriteIDs.size() + " " +
        //        getSetValuesFromPrefs(context, Constants.AppConstants.PROJECT_ID_SET).size());
    }

    public static void removeFromFavorites(final View container, final Context context, final UserEndPoint mUserEndPoint,
                                           final long projectID) {
        if (!UtilityMethods.isInternetConnected(context)) {
            Snackbar snackbar = Snackbar
                    .make(container, context.getResources().getString(R.string.network_error_msg), Snackbar.LENGTH_LONG)
                    .setAction(context.getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            removeFromFavorites(container, context, mUserEndPoint, projectID);
                        }
                    });
            ;

            snackbar.show();
            return;
        }

        final long userID = UtilityMethods.getLongInPref(context, Constants.ServerConstants.USER_ID, -1);
        if (userID != -1) {
            List<Long> favoriteList = getSetValuesFromPrefs(context, Constants.AppConstants.PROJECT_ID_SET);
            if ((favoriteList != null) && (!favoriteList.isEmpty())) {
                int toRemove = favoriteList.indexOf(projectID);
                favoriteList.remove(projectID);
                clearPreference(context, Constants.AppConstants.PROJECT_ID_SET);
                saveSetInPref(context, Constants.AppConstants.PROJECT_ID_SET, favoriteList);
            }
            new Thread(new Runnable() {
                String errorMsg = "";
                @Override
                public void run() {
                    try {
                        Log.d(TAG, "Remove from Favorites " + projectID);
                        final CommonResponse deleteFavouriteProject = mUserEndPoint.deleteFavouriteProject(userID, projectID)
                                .setRequestHeaders(getHttpHeaders()).execute();
                    } catch (UnknownHostException e) {
                        errorMsg = context.getResources().getString(R.string.network_error_msg);
                        e.printStackTrace();
                        HousingLogs.d(TAG, e.getMessage());
                    }catch (Exception e) {
                        e.printStackTrace();
                        errorMsg = context.getResources().getString(R.string.something_went_wrong);
                        HousingLogs.d(TAG, e.getMessage());
                    }
                    if(!TextUtils.isEmpty(errorMsg)){

                    }
                }
            }).start();
        }

        //Log.d(TAG,"favCount->"+favoriteIDs.size() + " " +
        //        getSetValuesFromPrefs(context, Constants.AppConstants.PROJECT_ID_SET).size());

    }*/


    /**
     * Display alert dialog.
     *
     * @param ctx
     * @param title
     * @param message
     * @param isCancelable
     * @param positiveText
     * @param positiveListener
     * @param negativeText
     * @param negativeListener
     * @return void
     * @author Rjha
     */
    public static CustomWebDialog showWebAlert(Context ctx, int title,
                                               String message, boolean isCancelable, int positiveText,
                                               View.OnClickListener positiveListener, int negativeText,
                                               View.OnClickListener negativeListener, String webURL) {
        CustomWebDialog dialog = new CustomWebDialog(ctx);
        if (title != -1) {
            dialog.setTitle(title);
        }
        if (!TextUtils.isEmpty(webURL)) {
            dialog.setPositiveButton(-1, positiveListener);
            dialog.setmWebViewURL(webURL);
        } else {
            dialog.setMessage(message);
            dialog.setCancelable(isCancelable);
            dialog.setPositiveButton(positiveText, positiveListener);
            if (negativeText != -1) {
                dialog.setNegativeButton(negativeText, negativeListener);
            }
        }
        if (!((Activity) ctx).isFinishing()) {
            dialog.show();
        }

        return dialog;
    }

    public static void addToRecentlyViewed(Context context, long projectId, String name, String url,
                                           long price, long cityId,boolean isCommercial, boolean isOffer) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Gson gson = new Gson();
            String json = prefs.getString("recently_viewed", "");
            Type type = new TypeToken<List<ProjectWrapper>>() {
            }.getType();
            List<ProjectWrapper> recentProjects = gson.fromJson(json, type);

            if (recentProjects == null || recentProjects.isEmpty()) {
                recentProjects = new ArrayList<>();
            }else{
                /*if ((recentProjects != null) && (!recentProjects.isEmpty())) {
                    Collections.sort(recentProjects, new Comparator<ProjectWrapper>() {
                        @Override
                        public int compare(ProjectWrapper projectWrapper1, ProjectWrapper projectWrapper2) {
                            return (int) (projectWrapper1.getTimeStamp() - projectWrapper2.getTimeStamp());
                        }
                    });
                }*/
            }

            ProjectWrapper wrapper = new ProjectWrapper(projectId, name, url, price, cityId,
                    System.currentTimeMillis(),isCommercial,isOffer);

            for (int i = 0; i < recentProjects.size(); i++) {
                if (recentProjects.get(i).getProjectId() == projectId) {
                    recentProjects.remove(i);
                }
            }

            recentProjects.add(wrapper);
            if ((recentProjects != null) && (!recentProjects.isEmpty())) {
                Collections.sort(recentProjects, new Comparator<ProjectWrapper>() {
                    @Override
                    public int compare(ProjectWrapper projectWrapper1, ProjectWrapper projectWrapper2) {
                        return (int) (projectWrapper2.getTimeStamp() - projectWrapper1.getTimeStamp());
                    }
                });
            }
            if (recentProjects.size() > 10) {
                //recentProjects.remove(0);
                recentProjects.remove((recentProjects.size()-1));
            }

            SharedPreferences.Editor prefsEditor = prefs.edit();
            gson = new Gson();
            json = gson.toJson(recentProjects);
            prefsEditor.putString("recently_viewed", json);
            prefsEditor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ProjectWrapper> getRecentlyViewed(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString("recently_viewed", "");
        Type type = new TypeToken<List<ProjectWrapper>>() {
        }.getType();
        ArrayList<ProjectWrapper> list = gson.fromJson(json, type);
        if ((list != null) && (!list.isEmpty())) {
            Collections.sort(list, new Comparator<ProjectWrapper>() {
                @Override
                public int compare(ProjectWrapper projectWrapper1, ProjectWrapper projectWrapper2) {
                    return (int) (projectWrapper2.getTimeStamp() - projectWrapper1.getTimeStamp());
                }
            });
        }
        return list;
    }

    public static void updateRecentlyViewList(Context context, ArrayList<ProjectWrapper> list){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditorIds = prefs.edit();
        /*if(list != null && !list.isEmpty()){
            Collections.sort(list, new Comparator<ProjectWrapper>() {
                @Override
                public int compare(ProjectWrapper projectWrapper1, ProjectWrapper projectWrapper2) {
                    return (int)(projectWrapper1.getTimeStamp()-projectWrapper2.getTimeStamp());
                }
            });
        }*/
        Gson gson = new Gson();
        String json = gson.toJson(list);
        prefsEditorIds.putString("recently_viewed", json);
        prefsEditorIds.commit();
    }

    /*public static ArrayList<Long> getRecentlyViewedIds(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString("recently_viewed_ids", "");
        Type type = new TypeToken<List<Long>>() {
        }.getType();
        ArrayList<Long> list = gson.fromJson(json, type);
        if ((list != null) && (!list.isEmpty()))
            Collections.reverse(list);
        return list;
    }*/


    public static void putSaveSearchListInPrefs(Context context, ArrayList<SaveSearchWrapper> saveSearchWrappers){///*Array*/List<SearchCriteria> saveSearchList) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(saveSearchWrappers);//saveSearchList);
        prefsEditor.putString(Constants.AppConstants.SAVED_SEARCHES, json);
        prefsEditor.commit();
    }

    public static void addToSaveSearchList(Context context, SaveSearchWrapper saveSearchWrapper){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(Constants.AppConstants.SAVED_SEARCHES, "");
        Type type = new TypeToken<List<SaveSearchWrapper>>() {}.getType();

        List<SaveSearchWrapper> savedSearchList = gson.fromJson(json, type);

        if (savedSearchList == null || savedSearchList.isEmpty()) {
            savedSearchList = new ArrayList<>();
        }

        savedSearchList.add(saveSearchWrapper);

        SharedPreferences.Editor prefsEditor = prefs.edit();
        gson = new Gson();
        String json2 = gson.toJson(savedSearchList);
        prefsEditor.putString(Constants.AppConstants.SAVED_SEARCHES, json2);
        prefsEditor.apply();
    }

    public static boolean removeSaveSearchItem(Context context, String name){
        Log.d(TAG,"To remove->"+name);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(Constants.AppConstants.SAVED_SEARCHES, "");
        Type type = new TypeToken<List<SaveSearchWrapper>>() {}.getType();

        ArrayList<SaveSearchWrapper> savedSearchList = gson.fromJson(json, type);
        if((savedSearchList==null) || (savedSearchList.isEmpty())) {
            return false;
        }

        boolean removed = false;
        for(int i=0;i<savedSearchList.size();i++){
            if(savedSearchList.get(i).name.equals(name)){
                savedSearchList.remove(i);
                Log.d(TAG,"removed "+name);
                removed = true;
                break;
            }
        }
        SharedPreferences.Editor prefsEditor = prefs.edit();
        gson = new Gson();
        json = gson.toJson(savedSearchList);
        clearPreference(context, Constants.AppConstants.SAVED_SEARCHES);
        prefsEditor.putString(Constants.AppConstants.SAVED_SEARCHES, json);
        prefsEditor.apply();
        //putSaveSearchListInPrefs(context, savedSearchList);

        return removed;
    }

    public static ArrayList<SaveSearchWrapper> getSaveSearchList(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(Constants.AppConstants.SAVED_SEARCHES, "");
        Type type = new TypeToken<ArrayList<SaveSearchWrapper>>() {}.getType();

        ArrayList<SaveSearchWrapper> list = gson.fromJson(json, type);
        if ((list != null) && (!list.isEmpty())) {
            Collections.reverse(list);
        }
        return list;
    }

    public static SaveSearchWrapper getSaveSearchWrapper(SearchCriteria searchCriteria){
        SaveSearchWrapper saveSearchWrapper = new SaveSearchWrapper();

        if (!TextUtils.isEmpty(searchCriteria.getName())) {
            saveSearchWrapper.name = searchCriteria.getName();
        }

        saveSearchWrapper.filterApplied = searchCriteria.getFilterApplied();

        if (searchCriteria.getId() != null) {
            saveSearchWrapper.id = searchCriteria.getId();
        }
        if ((searchCriteria.getLatitude() != null)
                && (searchCriteria.getLongitude() != null)) {
            saveSearchWrapper.latitude = searchCriteria.getLatitude();
            saveSearchWrapper.longitude = searchCriteria.getLongitude();
        }

        if ((searchCriteria.getLatLongList() != null)
                && (!searchCriteria.getLatLongList().isEmpty())) {
            saveSearchWrapper.latLongList = searchCriteria.getLatLongList();
        }

        if(searchCriteria.getCity() != null
                && !searchCriteria.getCity().isEmpty()){
            saveSearchWrapper.cityName = searchCriteria.getCity();
        }
        if(searchCriteria.getCityId() != null){
            saveSearchWrapper.cityId = searchCriteria.getCityId();
        }

        if(searchCriteria.getBuilderId() != null){
            saveSearchWrapper.builderId = searchCriteria.getBuilderId();
        }

        if(searchCriteria.getBuilder() != null
                && !searchCriteria.getBuilder().isEmpty()){
            saveSearchWrapper.builderName = searchCriteria.getBuilder();
        }

        if(searchCriteria.getTime() != null){
            saveSearchWrapper.time = searchCriteria.getTime();
        }

        if (searchCriteria.getFilterApplied()) {
            if ((searchCriteria.getBedList() != null) &&
                    (!searchCriteria.getBedList().isEmpty())) {
                saveSearchWrapper.bedList = searchCriteria.getBedList();
            }
            if (searchCriteria.getMaxCost() != null) {
                saveSearchWrapper.maxCost = searchCriteria.getMaxCost();
            }
            if (searchCriteria.getMinCost() != null) {
                saveSearchWrapper.minCost = searchCriteria.getMinCost();
            }
            if ((searchCriteria.getPropertyTypeEnum() != null)
                    && (!searchCriteria.getPropertyTypeEnum().isEmpty())) {
                saveSearchWrapper.propertyTypeEnum = searchCriteria.getPropertyTypeEnum();
            }
            if ((searchCriteria.getPropertyStatusList() != null)
                    && (!searchCriteria.getPropertyStatusList().isEmpty())) {
                saveSearchWrapper.propertyStatusList = searchCriteria.getPropertyStatusList();
            }
            if ((searchCriteria.getZoomLevel() != null)
                    && (searchCriteria.getZoomLevel() != 0)) {
                saveSearchWrapper.zoomLevel = searchCriteria.getZoomLevel();
            }
        }
        return saveSearchWrapper;
    }

    public static void setDrawableBackground(Context mContext, View view, int drawable){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setBackground(ContextCompat.getDrawable(mContext, drawable));
        }else {
            view.setBackgroundDrawable(mContext.getResources().getDrawable(drawable));
        }
    }

    public static void setProgressBarBackground(Context mContext, ProgressBar progressBar, int drawable){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            progressBar.setProgressDrawable(ContextCompat.getDrawable(mContext, drawable));
        }else {
            progressBar.setProgressDrawable(mContext.getResources().getDrawable(drawable));
        }
    }


    public static void setColorBackground(Context mContext, View view, int color){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setBackgroundColor(ContextCompat.getColor(mContext, color));
        }else {
            view.setBackgroundColor(mContext.getResources().getColor(color));
        }
    }

    public static String getFormatedDay(long date){
        String[] suffixes =
                //    0     1     2     3     4     5     6     7     8     9
                { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
                        //    10    11    12    13    14    15    16    17    18    19
                        "th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
                        //    20    21    22    23    24    25    26    27    28    29
                        "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
                        //    30    31
                        "th", "st" };
        String dayStr = DateFormat.format("dd",date).toString();
        String day = "";
        if(!TextUtils.isEmpty(dayStr)){
            int d = Integer.parseInt(dayStr);
            day  = dayStr + suffixes[d];
        }
        return day;
    }

    public static void setTextViewColor(Context mContext, TextView view, int color){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setTextColor(ContextCompat.getColor(mContext, color));
        }else {
            view.setTextColor(mContext.getResources().getColor(color));
        }
    }

    public static void setButtonTextColor(Context mContext, Button view, int color){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setTextColor(ContextCompat.getColor(mContext, color));
        }else {
            view.setTextColor(mContext.getResources().getColor(color));
        }
    }

    public static void setColorFilter(Context mContext, ImageView imageView, int color, PorterDuff.Mode mode){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            imageView.setColorFilter(ContextCompat.getColor(mContext, color), mode);
        }else {
            imageView.setColorFilter(mContext.getResources().getColor(color),mode);
        }
    }


    public static double distance(double fromLat, double fromLon, double toLat, double toLon) {
        double radius = 6378137;   // approximate Earth radius, *in meters*
        double deltaLat = toLat - fromLat;
        double deltaLon = toLon - fromLon;
        double angle = 2 * Math.asin( Math.sqrt(
                Math.pow(Math.sin(deltaLat/2), 2) +
                        Math.cos(fromLat) * Math.cos(toLat) *
                                Math.pow(Math.sin(deltaLon/2), 2) ) );
        return radius * angle;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static void shareProject(Context context, String projectName, String imageURL, long projectId){
        try {
            TrackAnalytics.trackEvent("ShareProject", Constants.AppConstants.HOLD_TIME ,
                    1, context);
            String cityName = UtilityMethods.getStringInPref(context, Constants.AppConstants.SAVED_CITY, "");
            String url = Constants.AppConstants.SHARE_BASE_URL+projectName.replace(" ","-")
                    +"-"+projectId;
            /*URL imageurl = new URL(imageURL);
            Bitmap image = BitmapFactory.decodeStream(imageurl.openConnection().getInputStream());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + projectName+".jpg");
            boolean isImageCreated = true;
            try {
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
            } catch (Exception e) {
                isImageCreated = false;
                e.printStackTrace();
            }*/
            Intent intent = new Intent(Intent.ACTION_SEND);
            String subject = "", text="";
            String appName = " *clicbrics* ";
            if(!TextUtils.isEmpty(cityName)){
                subject = "Check out "+ projectName+ ", " + cityName +" at"+ appName;
                text = projectName + ", " + cityName +  " - Check out this project at" + appName + url;
            }else{
                subject = "Check out "+ projectName +" at"+ appName;
                text = projectName + " - Check out this project at"+ appName + url;
            }
            intent.setType("text/plain");
            /*if(isImageCreated){
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/" + projectName+".jpg"));
            }else{
                intent.setType("text/plain");
            }*/
            intent.putExtra(Intent.EXTRA_TEXT, text);
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
            context.startActivity(Intent.createChooser(intent, "Share"));
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    public static void shareProjectAppStoreLink(Context context){
        final String appPackageName = context.getPackageName();
        try {
            TrackAnalytics.trackEvent("ShareClicbricsAppstorelink", Constants.AppConstants.HOLD_TIME ,
                    1, context);
            /*Bitmap icon = BitmapFactory.decodeResource(context.getResources(),R.drawable.drawer_logo);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "clicbrics.jpg");
            boolean isImageCreated = true;
            try {
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
            } catch (Exception e) {
                isImageCreated = false;
                e.printStackTrace();
            }

            if(isImageCreated){
                intent.setType("image/text/plain");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/clicbrics.jpg"));
            }else {
                intent.setType("text/plain");
            }*/
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Please Check this out! " + "\nhttps://play.google.com/store/apps/details?id="+ appPackageName);
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "clicbrics");
            context.startActivity(Intent.createChooser(intent, "Share"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void shareRedbricsLink(Context context){
        try {
            TrackAnalytics.trackEvent("ShareClicbricsWithFrnds", Constants.AppConstants.HOLD_TIME ,
                    1, context);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Please Check this out! " + "\nhttps://www.clicbrics.com");
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "clicbrics");
            context.startActivity(Intent.createChooser(intent, "Share"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void putCityListInPrefs(Context context, List<com.buy.housing.backend.userEndPoint.model.City> cityList){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cityList);
        prefsEditor.putString(Constants.AppConstants.CITY_LIST, json);
        prefsEditor.commit();
    }

    public static ArrayList<City> getCityList(Context context) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Gson gson = new Gson();
            String json = prefs.getString(Constants.AppConstants.CITY_LIST, "");
            Type type = new TypeToken<ArrayList<City>>() {
            }.getType();

            ArrayList<City> list = gson.fromJson(json, type);
            if ((list != null) && (!list.isEmpty())) {
                Collections.reverse(list);
            }
            return list;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }




    public static void putEstimateCityListInPrefs(Context context, List<com.buy.housing.backend.userEndPoint.model.City> cityList){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cityList);
        prefsEditor.putString(Constants.AppConstants.ESTIMATE_CITY_LIST, json);
        prefsEditor.commit();
    }

    public static ArrayList<City> getEstimateCityList(Context context) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Gson gson = new Gson();
            String json = prefs.getString(Constants.AppConstants.ESTIMATE_CITY_LIST, "");
            Type type = new TypeToken<ArrayList<City>>() {
            }.getType();

            ArrayList<City> list = gson.fromJson(json, type);
            if ((list != null) && (!list.isEmpty())) {
                Collections.reverse(list);
            }
            return list;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }






    static Bitmap bitmap_image= null;
    public  static Bitmap getBitmapImage(final String imageURL){
        try {
            URL imageurl = new URL(imageURL);
            URI uri = new URI(imageurl.getProtocol(), imageurl.getUserInfo(), imageurl.getHost(), imageurl.getPort(), imageurl.getPath(), imageurl.getQuery(), imageurl.getRef());
            imageurl = uri.toURL();
            final URLConnection connection = new URL(imageurl.toString()).openConnection();
            new Thread(new Runnable() {
                final URLConnection conn = connection;
                @Override
                public void run() {
                    try {
                        bitmap_image = BitmapFactory.decodeStream(conn.getInputStream()) ;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            return bitmap_image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap_image;
    }


    public static long getTimeDiffInSeconds(long currentTime, long oldTime){
        long currentTimeInSec = TimeUnit.MILLISECONDS.toSeconds(currentTime);
        long oldTimeInSec = TimeUnit.MILLISECONDS.toSeconds(oldTime);
        android.util.Log.i("Current Time ", "In Sec: " + currentTimeInSec);
        android.util.Log.i("Old Time ", "In Sec: " + oldTime);
        long timeDiff = currentTimeInSec-oldTimeInSec;
        Log.i("Time Difference ", "In Sec : " + timeDiff);
        if(timeDiff >= 1000000){
            return 0;
        }else {
            return (currentTimeInSec - oldTimeInSec);
        }
    }

    public static String getAreaInYards(long area){
        float sqrd = area/9.0f;
        long reminder = area%9;
        Log.i(TAG, "getAreaInYards: " + sqrd);
        String areaInYards = "";
        if(area == 0 || reminder == 0){
            areaInYards = (int)sqrd+"";
        }else{
            areaInYards = String.format("%.1f",sqrd);
        }
        return areaInYards;
    }

    public static String getBspInYards(long area){
        long bsp = area*9;
        Log.i(TAG, "getBspInYards: " + bsp);
        String bspInYards = bsp+"";
        return bspInYards;
    }


    public static void saveBuilderAndProjectList(Context context, ArrayList<ProjectBuilderWrapper> projectBuilderWrappers){///*Array*/List<SearchCriteria> saveSearchList) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(projectBuilderWrappers);//saveSearchList);
        prefsEditor.putString(Constants.AppConstants.PROJECT_AND_BUILDER_LIST, json);
        prefsEditor.commit();
    }
    public static ArrayList<ProjectBuilderWrapper> getBuilderAndProjectList(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(Constants.AppConstants.PROJECT_AND_BUILDER_LIST, "");
        Type type = new TypeToken<ArrayList<ProjectBuilderWrapper>>() {}.getType();

        ArrayList<ProjectBuilderWrapper> list = gson.fromJson(json, type);
        return list;
    }
    public static void saveTopLocalityList(Context context, ArrayList<TopLocalityModel> projectBuilderWrappers){///*Array*/List<SearchCriteria> saveSearchList) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(projectBuilderWrappers);//saveSearchList);
        prefsEditor.putString(Constants.AppConstants.Top_Locality_LIST, json);
        prefsEditor.commit();
    }
    public static ArrayList<TopLocalityModel> getTopLocalitytList(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(Constants.AppConstants.Top_Locality_LIST, "");
        Type type = new TypeToken<ArrayList<TopLocalityModel>>() {}.getType();

        ArrayList<TopLocalityModel> list = gson.fromJson(json, type);
        return list;
    }

    public static void saveProjectCount(Context context, Map<String,Integer> builderMap){///*Array*/List<SearchCriteria> saveSearchList) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(builderMap);//saveSearchList);
        prefsEditor.putString("ProjectCount", json);
        prefsEditor.commit();
    }

    public static Map<String,Integer> getProjectCount(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString("ProjectCount", "");
        Type type = new TypeToken<Map<String,Integer>>() {}.getType();

        Map<String,Integer> builderMap = gson.fromJson(json, type);
        return builderMap;
    }




    public static void addFavoriteToServer(final View container, final Context context,
                                      final UserEndPoint mUserEndPoint, final long projectID) {

        if (!UtilityMethods.isInternetConnected(context)) {
            Snackbar snackbar = Snackbar.make(container, context.getResources().getString(R.string.network_error_msg), Snackbar.LENGTH_LONG);
                    /*.setAction(context.getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addToFavorites(container, context, mUserEndPoint, projectID);
                        }
                    });*/
            snackbar.show();
            return;
        }
        Set<String> favoriteList = getWishListFromPrefs(context, Constants.AppConstants.PROJECT_ID_SET);
        if (favoriteList == null) {
            favoriteList = new HashSet<>();
        }
        favoriteList.add(projectID + "");
        clearPreference(context, Constants.AppConstants.PROJECT_ID_SET);
        saveWishlistInPref(context, favoriteList);
        final long userID = UtilityMethods.getLongInPref(context, Constants.ServerConstants.USER_ID, -1);
        if (userID != -1) {
            //List<Long> favoriteList = getSetValuesFromPrefs(context, Constants.AppConstants.PROJECT_ID_SET);
            new Thread(new Runnable() {
                String errorMsg = "";
                @Override
                public void run() {
                    try {
                        Log.d(TAG, "Add To Favorites " + projectID);
                        final CommonResponse addFavoriteresponse = mUserEndPoint.addFavouriteProject(userID, projectID).setRequestHeaders(getHttpHeaders()).execute();
                        if(addFavoriteresponse != null && addFavoriteresponse.getStatus()) {

                        }else{
                            if(addFavoriteresponse != null && !TextUtils.isEmpty(addFavoriteresponse.getErrorMessage())) {
                                errorMsg = addFavoriteresponse.getErrorMessage();
                            }else {
                                errorMsg = context.getResources().getString(R.string.something_went_wrong);
                            }
                        }
                    } catch (UnknownHostException e) {
                        errorMsg = context.getResources().getString(R.string.network_error_msg);
                        e.printStackTrace();
                        HousingLogs.d(TAG, e.getMessage());
                    }catch (Exception e) {
                        errorMsg = context.getResources().getString(R.string.something_went_wrong);
                        e.printStackTrace();
                        HousingLogs.d(TAG, e.getMessage());
                    }
                    if(!TextUtils.isEmpty(errorMsg)){
                        /*if(container != null) {
                            UtilityMethods.showErrorSnackBar(container, errorMsg, Snackbar.LENGTH_LONG);
                        }*/
                    }
                }
            }).start();
        }
    }

    public static void removeFavoriteFromServer(final View container, final Context context, final UserEndPoint mUserEndPoint,
                                                final long projectID) {
        if (!UtilityMethods.isInternetConnected(context)) {
            Snackbar snackbar = Snackbar
                    .make(container, context.getResources().getString(R.string.network_error_msg), Snackbar.LENGTH_LONG);
                    /*.setAction(context.getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            removeFromFavorites(container, context, mUserEndPoint, projectID);
                        }
                    });*/

            snackbar.show();
            return;
        }
        Set<String> favoriteList = getWishListFromPrefs(context,Constants.AppConstants.PROJECT_ID_SET);
        if ((favoriteList != null) && (!favoriteList.isEmpty())) {
            if(favoriteList.contains(projectID+"")) {
                favoriteList.remove(projectID+"");
                clearPreference(context, Constants.AppConstants.PROJECT_ID_SET);
                saveWishlistInPref(context,favoriteList);
            }
        }
        final long userID = UtilityMethods.getLongInPref(context, Constants.ServerConstants.USER_ID, -1);
        if (userID != -1) {
            //List<Long> favoriteList = getSetValuesFromPrefs(context, Constants.AppConstants.PROJECT_ID_SET);
            new Thread(new Runnable() {
                String errorMsg = "";
                @Override
                public void run() {
                    try {
                        Log.d(TAG, "Remove from Favorites " + projectID);
                        final CommonResponse deleteFavouriteProject = mUserEndPoint.deleteFavouriteProject(userID, projectID)
                                .setRequestHeaders(getHttpHeaders()).execute();
                        if(deleteFavouriteProject != null && deleteFavouriteProject.getStatus()){

                        }else {
                            if (deleteFavouriteProject != null && !TextUtils.isEmpty(deleteFavouriteProject.getErrorMessage())) {
                                errorMsg = deleteFavouriteProject.getErrorMessage();
                            } else {
                                errorMsg = context.getResources().getString(R.string.something_went_wrong);
                            }
                        }
                    } catch (UnknownHostException e) {
                        errorMsg = context.getResources().getString(R.string.network_error_msg);
                        e.printStackTrace();
                        HousingLogs.d(TAG, e.getMessage());
                    }catch (Exception e) {
                        e.printStackTrace();
                        errorMsg = context.getResources().getString(R.string.something_went_wrong);
                        HousingLogs.d(TAG, e.getMessage());
                    }
                    if(!TextUtils.isEmpty(errorMsg)){

                    }
                }
            }).start();
        }

        //Log.d(TAG,"favCount->"+favoriteIDs.size() + " " +
        //        getSetValuesFromPrefs(context, Constants.AppConstants.PROJECT_ID_SET).size());

    }

    public static Set<String> getWishListFromPrefs(Context context, String key) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Set<String> favListSet =  prefs.getStringSet(Constants.AppConstants.PROJECT_ID_SET,null);
            return favListSet;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void saveWishlistInPref(Context context, Set<String> favListSet) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();

        /*Set<String> favListSet =  prefs.getStringSet(Constants.AppConstants.PROJECT_ID_SET,null);
        if(favListSet != null && favListSet.size() > 0){
            if(!favListSet.contains(projectId+"")){
                favListSet.add(projectId+"");
            }
        }else{
            favListSet.add(projectId+"");
        }*/
        prefsEditor.putStringSet(Constants.AppConstants.PROJECT_ID_SET,favListSet);
        prefsEditor.apply();
    }

    public static void removeFavInPref(Context context, String key, Long projectId) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();

        Set<String> favListSet =  prefs.getStringSet(Constants.AppConstants.PROJECT_ID_SET,null);
        if(favListSet != null && favListSet.size() > 0){
            if(favListSet.contains(projectId+"")){
                favListSet.remove(projectId+"");
            }
        }
        prefsEditor.putStringSet(Constants.AppConstants.PROJECT_ID_SET,favListSet);
        prefsEditor.apply();
    }

    public static void setListViewHeightBasedOnChildren(Context context,ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static Bitmap getBitmap(ImageView imageView){
        Bitmap bitmap = null;
        if(imageView != null) {
            if (imageView.getDrawable() instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            } else {
                Drawable d = imageView.getDrawable();
                bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }
        }
        return bitmap;
    }

    public static boolean isCommercial(String propertyType){
        if(propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.Shop.toString())
                || propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.OfficeSpace.toString())
                || propertyType.equalsIgnoreCase("Office Space")
                || propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.Warehouse.toString())
                || propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.Hotel.toString())
                || propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.GuestHouse.toString())
                || propertyType.equalsIgnoreCase("Guest House")
                || propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.IndustrialBuilding.toString())
                || propertyType.equalsIgnoreCase("Industrial Building")
                || propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.InstitutionLand.toString())
                || propertyType.equalsIgnoreCase("Institution Land")
                || propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.IndustrialLand.toString())
                || propertyType.equalsIgnoreCase("Industrial Land")
                || propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.AgricultureLand.toString())
                || propertyType.equalsIgnoreCase("Agriculture/Farm Land")
                || propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.CommercialLand.toString())
                || propertyType.equalsIgnoreCase("Commercial Land")){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isCommercial(List<String> propertyTypeList){
        if (propertyTypeList.contains(Constants.AppConstants.PropertyType.Shop.toString())
                || propertyTypeList.contains(Constants.AppConstants.PropertyType.OfficeSpace.toString())
                || propertyTypeList.contains(Constants.AppConstants.PropertyType.Warehouse.toString())
                || propertyTypeList.contains(Constants.AppConstants.PropertyType.IndustrialBuilding.toString())
                || propertyTypeList.contains(Constants.AppConstants.PropertyType.Hotel.toString())
                || propertyTypeList.contains(Constants.AppConstants.PropertyType.GuestHouse.toString())
                || propertyTypeList.contains(Constants.AppConstants.PropertyType.CommercialLand.toString())
                || propertyTypeList.contains(Constants.AppConstants.PropertyType.InstitutionLand.toString())
                || propertyTypeList.contains(Constants.AppConstants.PropertyType.IndustrialLand.toString())
                || propertyTypeList.contains(Constants.AppConstants.PropertyType.AgricultureLand.toString())){
            return true;
        }else{
            return false;
        }
    }

    public static String getCommercialTypeName(String propertyType){
        if(propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.Shop.toString())){
            return  "Shop";
        }else if(propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.OfficeSpace.toString())){
            return "Office Space";
        }else if(propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.Warehouse.toString())){
            return "Warehouse";
        }else if(propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.Hotel.toString())){
            return "Hotel";
        }else if(propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.GuestHouse.toString())){
            return "Guest House";
        }else if(propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.IndustrialBuilding.toString())){
            return "Industrial Building";
        }else if(propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.InstitutionLand.toString())){
            return "Institution Land";
        }else if(propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.IndustrialLand.toString())){
            return "Industrial Land";
        }else if(propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.AgricultureLand.toString())){
            return "Agriculture/Farm Land";
        }else if(propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.CommercialLand.toString())){
            return "Commercial Land";
        }else{
            return propertyType;
        }
    }

    public static String getCommercialTypeName(List<String> typeList){
        if(typeList.contains(Constants.AppConstants.PropertyType.Shop.toString())){
            return  "Shop";
        }else if(typeList.contains(Constants.AppConstants.PropertyType.OfficeSpace.toString())){
            return "Office Space";
        }else if(typeList.contains(Constants.AppConstants.PropertyType.Warehouse.toString())){
            return "Warehouse";
        }else if(typeList.contains(Constants.AppConstants.PropertyType.Hotel.toString())){
            return "Hotel";
        }else if(typeList.contains(Constants.AppConstants.PropertyType.GuestHouse.toString())){
            return "Guest House";
        }else if(typeList.contains(Constants.AppConstants.PropertyType.IndustrialBuilding.toString())){
            return "Industrial Building";
        }else if(typeList.contains(Constants.AppConstants.PropertyType.InstitutionLand.toString())){
            return "Institution Land";
        }else if(typeList.contains(Constants.AppConstants.PropertyType.IndustrialLand.toString())){
            return "Industrial Land";
        }else if(typeList.contains(Constants.AppConstants.PropertyType.AgricultureLand.toString())){
            return "Agriculture/Farm Land";
        }else if(typeList.contains(Constants.AppConstants.PropertyType.CommercialLand.toString())){
            return "Commercial Land";
        }else{
            return "Commercial";
        }
    }

    public static boolean isCommercialLand(String propertyType){
        if(propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.InstitutionLand.toString())){
            return true;
        }else if(propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.IndustrialLand.toString())){
            return true;
        }else if(propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.AgricultureLand.toString())){
            return true;
        }else if(propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.CommercialLand.toString())){
            return true;
        }else if(propertyType.toLowerCase().contains("land")){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isOfferExist(long offerStartDate, long offerEndDate){
        if(offerStartDate != 0 && offerEndDate != 0){
            long currentDate = System.currentTimeMillis();
            if (currentDate >= offerStartDate && currentDate <= offerEndDate) {
                return true;
            } else {
                return false;
            }
        }else{
            return false;
        }
    }

    public static boolean hasApartment(String propertyType){
        if(propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.Apartment.toString())
                || propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.Studio.toString())
                || propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.Duplex.toString())
                || propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.Penthouse.toString())){
            return true;
        }else{
            return false;
        }
    }

    public static boolean hasApartment(List<String> propertyTypeList){
        if (propertyTypeList.contains(Constants.AppConstants.PropertyType.Apartment.toString())
                || propertyTypeList.contains(Constants.AppConstants.PropertyType.Studio.toString())
                || propertyTypeList.contains(Constants.AppConstants.PropertyType.Duplex.toString())
                || propertyTypeList.contains(Constants.AppConstants.PropertyType.Penthouse.toString())){
            return true;
        }else{
            return false;
        }
    }

    public static boolean hasVilla(String propertyType){
        if(propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.IndependentHouse.toString())
                || propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.RowHouse.toString())
                || propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.IndependentHouseVilla.toString())
                || propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.Villa.toString())){
            return true;
        }else{
            return false;
        }
    }

    public static boolean hasVilla(List<String> propertyTypeList){
        if (propertyTypeList.contains(Constants.AppConstants.PropertyType.IndependentHouseVilla.toString())
                || propertyTypeList.contains(Constants.AppConstants.PropertyType.IndependentHouse.toString())
                || propertyTypeList.contains(Constants.AppConstants.PropertyType.RowHouse.toString())
                || propertyTypeList.contains(Constants.AppConstants.PropertyType.Villa.toString())){
            return true;
        }else{
            return false;
        }
    }

    public static void sendBroadCast(Context context){
        try {
            Intent intent = new Intent();
            intent.setAction("com.clicbrics.internetconnection");
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getProjectIdFromURL(String url){
        String projectIdStr = "",regExp = "";
        if(url.toString().contains("https://www.clicbrics.com/project/")){
            regExp = "https://www.clicbrics.com/project/";
        }else if(url.toString().contains("http://www.clicbrics.com/project/")){
            regExp = "http://www.clicbrics.com/project/";
        }else if(url.toString().contains("https://clicbrics.com/project/")){
            regExp = "https://clicbrics.com/project/";
        }else if(url.toString().contains("http://clicbrics.com/project/")){
            regExp = "http://clicbrics.com/project/";
        }
        String[] split = url.toString().split(regExp);
        if(split[1].contains("?")){
            projectIdStr = split[1].substring(0,split[1].indexOf("?"));
        }else{
            projectIdStr = split[1];
        }
        return projectIdStr;
    }


    public static boolean appInstalledOrNot(Context context,String uri) {
        try {
            PackageManager pm = context.getPackageManager();
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void showErrorSnackbarOnTop(Activity activity){
        showSnackbarOnTop(activity,"Error",activity.getString(R.string.no_internet_connection),true, Constants.AppConstants.ALERTOR_LENGTH_LONG);
    }

    public static void showSnackbarOnTop(Activity context,String title, String msg,
                                         boolean isError,long duration){
        if(Alerter.isShowing()){
            return;
        }
        if(TextUtils.isEmpty(msg)){
            if(isError){
                Alerter.create(context)
                        .setTitle(title)
                        .setBackgroundColorRes(R.color.uber_red)
                        .setDuration(duration)
                        .show();
            }else {
                Alerter.create(context)
                        .setTitle(title)
                        .setBackgroundColorRes(R.color.colorBlack)
                        .setDuration(duration)
                        .show();
            }
        }else{
            if(isError){
                Alerter.create(context)
                        .setTitle(title)
                        .setText(msg)
                        .setDuration(duration)
                        .setBackgroundColorRes(R.color.uber_red)
                        .show();
            }else {
                Alerter.create(context)
                        .setTitle(title)
                        .setText(msg)
                        .setDuration(duration)
                        .setBackgroundColorRes(R.color.colorBlack)
                        .show();
            }
        }
    }

    public static boolean isPackageInstalled(Context context,String packagename) {
        try {
            context.getPackageManager().getPackageInfo(packagename, 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void addToRecentProjectList(Context context, long projectId,Project recentProject) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Gson gson = new Gson();
            String json = prefs.getString(Constants.SharedPreferConstants.RECENT_PROJECT_LIST, "");
            Type type = new TypeToken<List<Project>>() {
            }.getType();
            List<Project> recentProjects = gson.fromJson(json, type);

            if (recentProjects == null || recentProjects.isEmpty()) {
                recentProjects = new ArrayList<>();
            }

            for (int i = 0; i < recentProjects.size(); i++) {
                if (recentProjects.get(i).getId() == projectId) {
                    recentProjects.remove(i);
                }
            }

            recentProjects.add(recentProject);
            if ((recentProjects != null) && (!recentProjects.isEmpty())) {
                Collections.sort(recentProjects, new Comparator<Project>() {
                    @Override
                    public int compare(Project recentProject1, Project recentProject2) {
                        return (int) (recentProject2.getTimeStamp() - recentProject1.getTimeStamp());
                    }
                });
            }
            if (recentProjects.size() > 10) {
                recentProjects.remove((recentProjects.size()-1));
            }

            SharedPreferences.Editor prefsEditor = prefs.edit();
            gson = new Gson();
            String json2 = gson.toJson(recentProjects);
            prefsEditor.putString(Constants.SharedPreferConstants.RECENT_PROJECT_LIST, json2);
            prefsEditor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Project> getRecentProjectList(Context context) {
        if(context!=null){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Gson gson = new Gson();
            String json = prefs.getString(Constants.SharedPreferConstants.RECENT_PROJECT_LIST, "");
            Type type = new TypeToken<List<Project>>() {
            }.getType();
            ArrayList<Project> list = gson.fromJson(json, type);
            if(list!=null&&(!list.isEmpty())){
                /*for(int i=0;i<list.size();i++){
                    if(list.get(i).getpro()==null||list.get(i).getStatusList().isEmpty()){
                        List<String> statusList=new ArrayList<>();
                        statusList.add(list.get(i).getStatus());
                        list.get(i).setStatusList(statusList);
                    }
                }*/
                Collections.sort(list, new Comparator<Project>() {
                    @Override
                    public int compare(Project recentProject1, Project recentProject2) {
                        return (int) (recentProject2.getTimeStamp() - recentProject1.getTimeStamp());
                    }
                });
            }
            return list;
        }
        return null;

    }

    public static void updateRecentProjectList(Context context, List<Project> list){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditorIds = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        prefsEditorIds.putString(Constants.SharedPreferConstants.RECENT_PROJECT_LIST, json);
        prefsEditorIds.commit();
    }

    /*public static void addPolygonLatlngList(Context context,ArrayList<LatLng> latlngList){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditorIds = prefs.edit();
        if(latlngList == null){
            prefsEditorIds.remove(Constants.SharedPreferConstants.POLYGON_LATLNG_LIST);
            prefsEditorIds.commit();
        }else{
            Gson gson = new Gson();
            String json = gson.toJson(latlngList);
            prefsEditorIds.putString(Constants.SharedPreferConstants.POLYGON_LATLNG_LIST, json);
            prefsEditorIds.commit();
        }
    }

    public static ArrayList<LatLng> getPolygonLatlngList(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(Constants.SharedPreferConstants.POLYGON_LATLNG_LIST, "");
        Type type = new TypeToken<List<LatLng>>() {
        }.getType();
        ArrayList<LatLng> list = gson.fromJson(json, type);
        return list;
    }*/

    public static boolean isFilterApplied(Context context){
        if(context == null){
            return false;
        }
        return UtilityMethods.getBooleanInPref(context,Constants.SharedPreferConstants.PARAM_FILTER_APPLIED,false);
    }

    public static void setStatusBarColor(Activity activity, int color){
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.M){
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(activity.getResources().getColor(color));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public static String getArea(Context context,long area, boolean isLand){
        String selecedUnit = getSelectedUnit(context);
        if(Constants.PropertyUnit.SQFT.toString().equalsIgnoreCase(selecedUnit)){
            Long value = new Long(area);
            if(area == value.intValue()){
                return (int)area + "";
            }else{
                return String.format("%.1f",area);
            }
        }else if(Constants.PropertyUnit.SQM.toString().equalsIgnoreCase(selecedUnit)){
            //One square foot is the equivalent to 0.09290304 square metres.
            Float sqm = area*0.09290304f;
            String areaInMtr = "";
            if(sqm.intValue() == sqm){
                areaInMtr = sqm.intValue() +"";
            }else{
                areaInMtr = String.format("%.1f",sqm.floatValue());
            }
            return areaInMtr;
        }else if(Constants.PropertyUnit.SQYD.toString().equalsIgnoreCase(selecedUnit) || isLand){
            float sqrd = area/9.0f;
            long reminder = area%9;
            String areaInYards = "";
            if(area == 0 || reminder == 0){
                areaInYards = (int)sqrd+"";
            }else{
                if(sqrd == new Float(sqrd).intValue()){
                    areaInYards = (int)sqrd + "";
                }else {
                    areaInYards = String.format("%.1f", sqrd);
                }
            }
            return areaInYards;
        }
        //if no condition match i.e unit is empty so default is sqft
        Long value = new Long(area);
        if(area == value.intValue()){
            return (int)area + "";
        }else{
            return String.format("%.1f",area);
        }
    }


    /**
     * Method used to get the area or area range in different units
     * we get values from server in range in sqft, based on the selected unit we need to change the unit and range values
     * @param context - context
     * @param range - arearange of property
     * @param isLand - boolean which indicate weather property type is land or not
     * @return - area range
     */
    public static String getAreaRange(Context context,String range, boolean isLand){
        String selecedUnit = getSelectedUnit(context);
        if(TextUtils.isEmpty(range)){
            return "";
        }
        String areaRange = "";
        String unitStr = "";
        String digitPattern = "\\d+";
        if(range.contains("Sq.ft")){
            unitStr =  "Sq.ft";
        }else if(range.contains("Sq.yd")){
            unitStr =  "Sq.yd";
        }else{
            return range;
        }
        String sizeRange = range.split(unitStr)[0];
        if(sizeRange != null && !sizeRange.trim().isEmpty()){
            if(sizeRange.trim().matches(digitPattern)){
                areaRange = getAreaInUnit(Float.parseFloat(sizeRange.trim()),selecedUnit,isLand);
            }else if(sizeRange.contains("-")){
                String areaArr[] = sizeRange.split("-");
                String minArea = areaArr[0].trim();
                String maxArea = areaArr[1].trim();
                areaRange = ""+ getAreaInUnit(Float.parseFloat(minArea),selecedUnit,isLand) + " - " + getAreaInUnit(Float.parseFloat(maxArea),selecedUnit,isLand);
            }
            return areaRange;
        }
        return areaRange;
    }

    /**
     * Based on the selected unit, convert the value of area to the selected unit and return
     * @param area - property area on which we need to convert
     * @param selectedUnit -  selected unit
     * @param isLand - boolean flag which indicate weather property is land or not
     * @return - area in selected unit
     */
    private static String getAreaInUnit(float area,String selectedUnit,boolean isLand){
        if(Constants.PropertyUnit.SQFT.toString().equalsIgnoreCase(selectedUnit)){
            Float value = new Float(area);
            if(area == value.intValue()){
                return (int)area + "";
            }else{
                return String.format("%.1f",area);
            }
        }else if(Constants.PropertyUnit.SQM.toString().equalsIgnoreCase(selectedUnit)){
            //One square foot is the equivalent to 0.09290304 square metres.
            Float sqm = area*0.09290304f;
            String areaInMtr = "";
            if(sqm.intValue() == sqm){
                areaInMtr = sqm.intValue() +"";
            }else{
                areaInMtr = String.format("%.1f",sqm);
            }
            return areaInMtr;
        }else if(Constants.PropertyUnit.SQYD.toString().equalsIgnoreCase(selectedUnit) || isLand){
            float sqrd = area/9.0f;
            float reminder = area%9;
            String areaInYards = "";
            if(area == 0 || reminder == 0){
                areaInYards = (int)sqrd+"";
            }else{
                if(sqrd == (new Float(sqrd).intValue())){
                    areaInYards = (int)sqrd + "";
                }else {
                    areaInYards = String.format("%.1f", sqrd);
                }
            }
            return areaInYards;
        }
        //if no condition match i.e unit is empty so default is sqft
        Float value = new Float(area);
        if(area == value.intValue()){
            return (int)area + "";
        }else{
            return String.format("%.1f",area);
        }
    }



    public static float getAreaInSqyd(float area){
            float sqrd = area*9.0f;
            return sqrd;
    }


    public static String getBSP(Context context,long area, boolean isLand){
        String selecedUnit = getSelectedUnit(context);
        if(Constants.PropertyUnit.SQFT.toString().equalsIgnoreCase(selecedUnit)){
            Long value = new Long(area);
            if(area == value.intValue()){
                return (int)area + "";
            }else{
                return String.format("%.1f",area);
            }
        }else if(Constants.PropertyUnit.SQM.toString().equalsIgnoreCase(selecedUnit)){
            //One square foot is the equivalent to 0.09290304 square metres.
            Float sqm = area/0.09290304f;
            String bspInMtr = "";
            if(sqm.intValue() == sqm){
                bspInMtr = sqm.intValue() +"";
            }else{
                bspInMtr = String.format("%.1f",sqm);
            }
            return bspInMtr;
        }else if(Constants.PropertyUnit.SQYD.toString().equalsIgnoreCase(selecedUnit) || isLand){
            Float sqrd = area*9.0f;
            String bspInYards = "";
            if(sqrd.intValue() == sqrd){
                bspInYards = sqrd.intValue()+"";
            }else{
                bspInYards = String.format("%.1f",sqrd.floatValue());
            }
            return bspInYards;
        }
        //if unit is empty i.e by default is sqft
        Long value = new Long(area);
        if(area == value.intValue()){
            return (int)area + "";
        }else{
            return String.format("%.1f",area);
        }
    }

    public static String getUnit(Context context,boolean isLand){
        String selectedUnit = getSelectedUnit(context);
        if(Constants.PropertyUnit.SQFT.toString().equalsIgnoreCase(selectedUnit)){
            return " Sq.ft";
        }else if(Constants.PropertyUnit.SQM.toString().equalsIgnoreCase(selectedUnit)){
            return " Sq.m";
        }else if(Constants.PropertyUnit.SQYD.toString().equalsIgnoreCase(selectedUnit) || isLand){
            return " Sq.yd";
        }else{
            return " Sq.ft";
        }
    }

    public static String getSelectedUnit(Context context){
        return UtilityMethods.getStringInPref(context,Constants.AppConstants.SELECTED_PROPERTY_UNIT,"sqft");
    }

    public static String getPropertyStatus(String propertyStatus){
        return propertyStatus.replace(Constants.AppConstants.PropertyStatus.NotStarted.toString(), "New Launch")
                .replace(Constants.AppConstants.PropertyStatus.InProgress.toString(), "Under Construction")
                .replace(Constants.AppConstants.PropertyStatus.UpComing.toString(), "Upcoming")
                .replace(Constants.AppConstants.PropertyStatus.ReadyToMove.toString(), "Ready To Move");
    }

    public static int getDeviceResolution(Activity activity ){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.densityDpi;
    }

    public static boolean isSortingApplied(Context context){
        return (UtilityMethods.getBooleanInPref(context,Constants.SharedPreferConstants.SORT_TYPE_HIGH_TO_LOW_AREA,false)
                    || UtilityMethods.getBooleanInPref(context,Constants.SharedPreferConstants.SORT_TYPE_LOW_TO_HIGH_AREA,false)
                    || UtilityMethods.getBooleanInPref(context,Constants.SharedPreferConstants.SORT_TYPE_RELEVANCE,false)
                    || UtilityMethods.getBooleanInPref(context,Constants.SharedPreferConstants.SORT_TYPE_HIGH_TO_LOW_PRICE,false)
                    || UtilityMethods.getBooleanInPref(context,Constants.SharedPreferConstants.SORT_TYPE_LOW_TO_HIGH_PRICE,false));
    }

    public static void sendUpdateBroadcast(Context context){
        LocalBroadcastManager changeModeReciever = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(Constants.UPDATE_MORE);
        changeModeReciever.sendBroadcast(intent);
    }

    public static void hideKeyboard(Activity context){
        /*InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindowToken(), 0);*/
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }



    public static int getAppVersionUtility(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException(context.getString(R.string.could_not_get_package_name) + e);
        }
    }

    public static String dateFormat(long date, String format) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));

            String dateStr = dateFormat.format(date);
            return dateStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}