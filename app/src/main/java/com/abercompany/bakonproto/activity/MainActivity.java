package com.abercompany.bakonproto.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.abercompany.bakonproto.R;
import com.abercompany.bakonproto.databinding.ActivityMainBinding;
import com.abercompany.bakonproto.ndk.NDK;
import com.abercompany.bakonproto.utils.DeviceUtil;
import com.abercompany.bakonproto.utils.JSLog;
import com.abercompany.bakonproto.utils.Lib;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;


    private String dateData = "";
    private String timeData = "";
    private String settingUrl = "http://192.168.1.254";
    private String settingCustom = "1";
    private String dateSettingCom = "3005";
    private String timeSettingCom = "3006";
    private String checkingModCom = "3016";
    private String switchingModCom = "3001";
    private String camMod = null;
    private String inputUrl = "";

    private boolean isConnectCameraResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setMain(this);

        checkPermission();


    }

    private void checkPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this servicePlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_recording:


                if (isConnectCamera()) {
                binding.btnStopRecording.setVisibility(View.VISIBLE);
                new FFmpegTask().execute(inputUrl, "/BAKON/" + System.currentTimeMillis() + ".mp4");
//                    String date = DeviceUtil.getDate("yyyy-MM-dd HH:mm:ss");
//                    dateData = date.substring(0, 10);
//                    timeData = date.substring(11, 19);
//                    JSLog.D("Date    ::" + dateData, null);
//                    JSLog.D("Time    ::" + timeData, null);
//                    JSLog.D("------ CameraMode Check", null);
//                    requestCameraStatus(settingUrl, settingCustom, checkingModCom);
//                    JSLog.D("------ 날짜변경 호출전", null);
//                    setUpCamera(settingUrl, settingCustom, dateSettingCom, dateData);
//                    JSLog.D("------ 날짜변경 호출후", null);
//                    JSLog.D("------ 시간변경 호출전", null);
//                    setUpCamera(settingUrl, settingCustom, timeSettingCom, timeData);
//                    JSLog.D("------ 시간변경 호출후", null);
                } else {
                    showConnectCameraDlg();
                }

                break;

            case R.id.btn_stop_recording:
                finish();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    MainActivity.this.finishAffinity();
                } else {
                    System.exit(0);
                }
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
        }
    }

    private void showConnectCameraDlg() {
        Lib.getInstance().showSimpleDialog(this,
                "카메라를 연결해주세요",
                "확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
    }

    private boolean isConnectCamera() {
        isConnectCameraResult = false;

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid = info.getSSID();

        Log.e(TAG, "카메라 연결      ::  " + ssid);
        if ((ssid.contains("SJ4000") ||
                ssid.contains("SportsDV") ||
                ssid.contains("SOOCOOCAM"))) {
//                        binding.content.tvCameraStatus.setText("카메라가 연결되었습니다");
            inputUrl = "rtsp://192.168.1.254/sjcam.mov";

            isConnectCameraResult = true;
        } else {
//                        binding.content.tvCameraStatus.setText("카메라가 연결 되지 않았습니다");
//                        inputUrl = "rtsp://192.168.1.254";

            isConnectCameraResult = false;


        }


        JSLog.D("isConnectCameraResult          :::         " + isConnectCameraResult, null);
        return isConnectCameraResult;

    }

    private void setUpCamera(String uri, String custom, final String cmd, String str) {

        // 날짜변경 - uri : 192.168.1.254 , custom : 1 , cmd : 3005, str : dateData
        // 시간변경 - uri : 192.168.1.254 , custom : 1 , cmd : 3006, str : timeData
        // 카메라모드 변경 - uri : 192.168.1.254 , custom : 1 , cmd : 3001, str : 1
        Log.d(TAG, "=========================================");
        Log.d(TAG, "setUpCamera     setUpCamera  " + custom);
        Log.d(TAG, "setUpCamera     setUpCamera  " + custom);
        Log.d(TAG, "setUpCamera     cmd          " + cmd);
        Log.d(TAG, "setUpCamera     str          " + str);
        Log.d(TAG, "=========================================");


        AsyncHttpClient client = new AsyncHttpClient();
        String url = uri + "/?" + "custom=" + custom + "&cmd=" + cmd + "&str=" + str;
        Log.d(TAG, "url     !!!!  " + url);
        client.get(url, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                Log.e(TAG, "paramsData           ::     " + statusCode);
                Log.e(TAG, "responseString           ::     " + responseString);

                switch (statusCode) {

                    case 200:
                        try {
                            String tagName = "";
                            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                            XmlPullParser parser = factory.newPullParser();
                            parser.setInput(new StringReader(responseString));
                            int eventType = parser.getEventType();
                            boolean isItemTag = false;
                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                if (eventType == XmlPullParser.START_TAG) {
                                    tagName = parser.getName();
                                    if (tagName.equals("Function")) {
                                        isItemTag = true;
                                    }
                                } else if (eventType == XmlPullParser.TEXT && isItemTag) {
                                    if (tagName.equals("Status")) {
                                        String name = parser.getText();
                                        Log.e(TAG, "nameData " + name);
                                        if (name.equals("0")) {
                                            Log.e(TAG, " 성공 ");
//                                            binding.content.tvStreamingStatus.setText("카메라 셋팅완료.");

                                            if (cmd.equals(timeSettingCom)) {
//                                                binding.btnStopRecording.setVisibility(View.VISIBLE);
//                                                new FFmpegTask().execute(inputUrl, "/BAKON/" + System.currentTimeMillis() + ".mp4");
                                            }
                                        } else {
                                            Log.e(TAG, " 실패 ");
                                            showConnectCameraDlg();
//                                            binding.content.tvStreamingStatus.setText("카메라 셋팅실패 재시도해주세요. ");
                                        }
                                    }
                                } else if (eventType == XmlPullParser.END_TAG) {
                                    tagName = parser.getName();
                                    if (tagName.equals("Status")) {
                                        isItemTag = false;
                                    }
                                }
                                eventType = parser.next();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "statusCode           ::     " + statusCode);
                Log.e(TAG, "responseString           ::     " + responseString);
                Toast.makeText(getBaseContext(), "카메라 셋팅 실패하였습니다.다시시도해주세요.", Toast.LENGTH_SHORT).show();

            }
        });


    }

    //Checking CameraMode  0: photo shooting , 1 : video recording , 3: timelapse video recording, 4 : timelapse photo shooting
    private String requestCameraStatus(String uri, String custom, String cmd) {

//        binding.content.tvStreamingStatus.setText("스트리밍 가능 확인중 입니다");

        Log.d(TAG, "requestCameraStatus     !!!!  ");
        AsyncHttpClient client = new AsyncHttpClient();
        String url = uri + "/?" + "custom=" + custom + "&cmd=" + cmd;
        Log.d(TAG, "url     !!!!  " + url);
        client.get(url, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                Log.e(TAG, "paramsData           ::     " + statusCode);
                Log.e(TAG, "responseString           ::     " + responseString);

                switch (statusCode) {

                    case 200:
                        try {
                            String tagName = "";
                            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                            XmlPullParser parser = factory.newPullParser();

                            parser.setInput(new StringReader(responseString));
                            int eventType = parser.getEventType();
                            boolean isItemTag = false;
                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                if (eventType == XmlPullParser.START_TAG) {
                                    tagName = parser.getName();
                                    if (tagName.equals("Function")) {
                                        isItemTag = true;
                                    }
                                } else if (eventType == XmlPullParser.TEXT && isItemTag) {
                                    if (tagName.equals("Status")) {
                                        String name = parser.getText();
                                        Log.e(TAG, "nameData " + name);
                                        if (name.equals("1") && isItemTag) {
//                                            binding.content.tvStreamingStatus.setText("스트리밍 가능합니다");
                                            camMod = name;
                                            Log.e(TAG, "camMod Name 값은   " + camMod);
                                            return;
                                        } else if (!name.equals("1") && isItemTag) {
//                                            binding.content.tvStreamingStatus.setText("카메라 모드를 바꾸는 중입니다.");
                                            camMod = name;
                                            Log.e(TAG, "camMod  Name 값은      " + camMod);
                                            modChangeCamera(settingUrl, settingCustom, switchingModCom, "1");

                                        }
                                    }
                                } else if (eventType == XmlPullParser.END_TAG) {
                                    tagName = parser.getName();
                                    if (tagName.equals("Status")) {
                                        isItemTag = false;
                                    }
                                }
                                eventType = parser.next();
                            }
                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                Log.e(TAG, "statusCode           ::     " + statusCode);
                Log.e(TAG, "responseString           ::     " + responseString);

            }
        });
        Log.e(TAG, "함수끝나기전 camMod " + camMod);
        return camMod;
    }

    private void modChangeCamera(String uri, String custom, String cmd, String str) {

        // 날짜변경 - uri : 192.168.1.254 , custom : 1 , cmd : 3005, str : dateData
        // 시간변경 - uri : 192.168.1.254 , custom : 1 , cmd : 3006, str : timeData
        // 카메라모드 변경 - uri : 192.168.1.254 , custom : 1 , cmd : 3001, str : 1
//        binding.content.tvStreamingStatus.setText("카메라 셋팅중입니다.");
        Log.d(TAG, "=========================================");
        Log.d(TAG, "setUpCamera     setUpCamera  " + custom);
        Log.d(TAG, "setUpCamera     setUpCamera  " + custom);
        Log.d(TAG, "setUpCamera     cmd          " + cmd);
        Log.d(TAG, "setUpCamera     str          " + str);
        Log.d(TAG, "=========================================");


        AsyncHttpClient client = new AsyncHttpClient();
        String url = uri + "/?" + "custom=" + custom + "&cmd=" + cmd + "&par=" + str;
        Log.d(TAG, "url     !!!!  " + url);
        client.get(url, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                Log.e(TAG, "paramsData           ::     " + statusCode);
                Log.e(TAG, "responseString           ::     " + responseString);

                switch (statusCode) {

                    case 200:
                        try {
                            String tagName = "";
                            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                            XmlPullParser parser = factory.newPullParser();
                            parser.setInput(new StringReader(responseString));
                            int eventType = parser.getEventType();
                            boolean isItemTag = false;
                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                if (eventType == XmlPullParser.START_TAG) {
                                    tagName = parser.getName();
                                    if (tagName.equals("Function")) {
                                        isItemTag = true;
                                    }
                                } else if (eventType == XmlPullParser.TEXT && isItemTag) {
                                    if (tagName.equals("Status")) {
                                        String name = parser.getText();
                                        Log.e(TAG, "nameData " + name);
                                        if (name.equals("0")) {
                                            Log.e(TAG, " 성공 ");
//                                            binding.content.tvStreamingStatus.setText("카메라 셋팅완료.");
                                        } else {
                                            Log.e(TAG, " 실패 ");
                                            showConnectCameraDlg();
//                                            binding.content.tvStreamingStatus.setText("카메라 셋팅실패 재시도해주세요. ");
                                        }
                                    }
                                } else if (eventType == XmlPullParser.END_TAG) {
                                    tagName = parser.getName();
                                    if (tagName.equals("Status")) {
                                        isItemTag = false;
                                    }
                                }
                                eventType = parser.next();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "statusCode           ::     " + statusCode);
                Log.e(TAG, "responseString           ::     " + responseString);
                Toast.makeText(getBaseContext(), "카메라 셋팅 실패하였습니다.다시시도해주세요.", Toast.LENGTH_SHORT).show();

            }
        });


    }

    public static class FFmpegTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {

            Log.e(TAG, "ffmpegTask   onPreExecute");
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... strings) {

            if (isCancelled()) {
                Log.d(TAG, "ffmpegTask cancel           !!! ");
            } else if (!isCancelled()) {

                Log.e(TAG, "호출 전  UrlData ::    " + strings[0]);
                new NDK().run_ffmpeg(strings[0], strings[1]);

                Log.e(TAG, "호출 후   ");

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void Void) {
            super.onPostExecute(Void);

        }
    }
}
