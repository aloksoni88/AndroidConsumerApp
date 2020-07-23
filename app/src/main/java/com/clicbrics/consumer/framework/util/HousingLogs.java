package com.clicbrics.consumer.framework.util;

import android.content.Context;
import android.util.Log;

import com.clicbrics.consumer.BuildConfig;
import com.clicbrics.consumer.HousingApplication;

import javax.security.auth.x500.X500Principal;

/**
 * Created by Alok on 05-09-2016.
 */
public class HousingLogs {
    public static boolean isDebugLogOn = true;
    public static boolean isInfoLogOn = true;
    public static boolean isWarningLogOn = true;
    public static boolean isVerboseLogOn = true;
    static{
        if(isDebuggable(HousingApplication.getInstance())){
            HousingLogs.isDebugLogOn=true;
            HousingLogs.isInfoLogOn =true;
            HousingLogs.isVerboseLogOn =true;
        }else{
            HousingLogs.isDebugLogOn=false;
            HousingLogs.isInfoLogOn =false;
            HousingLogs.isVerboseLogOn =false;
        }
    }
    private static boolean isDebuggable(HousingApplication housingApplication)
    {
        final X500Principal DEBUG_DN = new X500Principal("CN=Android Debug,O=Android,C=US");
        boolean debuggable = false;
        if(BuildConfig.DEBUG){
            debuggable = true;
        }
        /*try
        {
            PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature signatures[] = pinfo.signatures;

            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            for ( int i = 0; i < signatures.length;i++)
            {
                ByteArrayInputStream stream = new ByteArrayInputStream(signatures[i].toByteArray());
                X509Certificate cert = (X509Certificate) cf.generateCertificate(stream);
                debuggable = cert.getSubjectX500Principal().equals(DEBUG_DN);
                if (debuggable)
                    break;
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            //debuggable variable will remain false
        }
        catch (CertificateException e)
        {
            //debuggable variable will remain false
        }*/
        return debuggable;
    }

    public static void d(String tag, String msg) {
        if (isDebugLogOn) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Context context){
        if (isDebugLogOn) {
            Log.d(tag, msg);
        }
        TrackAnalytics.trackEvent(tag,msg,context);
    }

    public static void e(String Tag, String msg) {
        Log.e(Tag, msg);
    }

    public static void e(String Tag, String msg, Throwable exception) {
        Log.e(Tag, msg, exception);
    }

    public static void i(String Tag, String msg) {
        if (isInfoLogOn) {
            Log.i(Tag, msg);
        }
    }

    public static void w(String Tag, String msg) {
        if (isWarningLogOn) {
            Log.w(Tag, msg);
        }
    }

    public static void v(String Tag, String msg) {
        if (isVerboseLogOn){
            Log.v(Tag, msg);
        }
    }
}
