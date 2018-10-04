package com.abercompany.bakonproto.utils;

import android.os.Handler;

public class Util {

    /**
     * 메서드의 기능설명은 한 두줄로 간결하게..
     *
     * @param long time 밀리세컨드 단위의 딜레이 시간
     * @return void
     */
    public static void runDelay(long time, Runnable runnable)
    {
        final Handler handler = new Handler();
        handler.postDelayed(runnable, time);

    }
}
