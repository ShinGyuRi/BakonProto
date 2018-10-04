package com.abercompany.bakonproto.ndk;

import android.util.Log;

public class NDK {

    static {

        try{
            System.loadLibrary("ffmpeg");
        }catch (Exception e){
            e.printStackTrace();
            Log.e("NDK","Exception      ee::        "   +e);
        }
    }
    //  public native int run_ffmpeg();
    public native int run_ffmpeg(String inputUrl, String outputUrl);
}
