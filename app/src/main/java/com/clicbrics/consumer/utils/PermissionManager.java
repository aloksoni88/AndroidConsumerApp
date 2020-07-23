package com.clicbrics.consumer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import static com.facebook.login.widget.ProfilePictureView.TAG;

/**
 * Created by root on 12/12/16.
 */
public class PermissionManager {

    public static boolean shouldAskPermission() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "permission denied->" + permission);
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean checkIfAlreadyhasPermission(Context context, String[] permissions) {
        for(int i=0;i<permissions.length;i++) {
            int result = ContextCompat.checkSelfPermission(context, permissions[i]);
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    public static void requestForSpecificPermission(Context context, String[] permissions, int permission_code) {
        ActivityCompat.requestPermissions((Activity)context , permissions, permission_code);
    }

}
