package com.abercompany.bakonproto.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.abercompany.bakonproto.R;
import com.abercompany.bakonproto.utils.Util;

public class SplasyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splasy);

        Util.runDelay(2000, new Runnable() {
            @Override
            public void run() {
                moveMainAct();
            }
        });
    }

    private void moveMainAct() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
