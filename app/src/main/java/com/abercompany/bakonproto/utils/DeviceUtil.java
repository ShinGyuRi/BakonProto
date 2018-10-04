package com.abercompany.bakonproto.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_DISABLED;
import static android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED;
import static android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_WHITELISTED;

public class DeviceUtil {

    public static String TAG = DeviceUtil.class.getSimpleName();

    public static String getDevicePhoneNumber(Context context) {
        TelephonyManager telManager = null;
        String mPhoneNum = null;
        try {
            telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            mPhoneNum = telManager.getLine1Number();
//			mPhoneNum = PhoneNumberUtils.formatNumber(mPhoneNum);
            mPhoneNum = mPhoneNum.replace("+820", "0");
            mPhoneNum = mPhoneNum.replace("+82", "0");
            if (mPhoneNum.subSequence(0, 2).equals("01") == false) {
                mPhoneNum = "";
            }

        } catch (Exception err) {
            mPhoneNum = null;
            Log.e(TAG, err.toString());
        } finally {
            if (telManager != null)
                telManager = null;
        }

        return mPhoneNum;
    }

    /**
     * 디바이스 모델명 가져오기
     */
    public static String getDeviceModel() {
        String model_name = null;
        model_name = Build.MODEL;
        model_name = model_name.replaceAll("\\p{Space}", "");
        return model_name;
    }

    public static String getDate(String format) {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String getDate = sdf.format(date);
        Log.e(TAG, "getDate    ::  " + getDate);

        return getDate;
    }

    public static boolean checkBackgroundDataRestricted(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        switch (connMgr.getRestrictBackgroundStatus()) {
            case RESTRICT_BACKGROUND_STATUS_ENABLED:
                // Background data usage and push notifications are blocked for this app
                return true;

            case RESTRICT_BACKGROUND_STATUS_WHITELISTED:
            case RESTRICT_BACKGROUND_STATUS_DISABLED:
                // Data Saver is disabled or the app is whitelisted
                return false;
        }

        return false;
    }
}
