package com.udacity.project.reddit.capstone.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;

import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.utils.Constants;

public class SplashScreenActivity extends AppCompatActivity {
    private TextView txtReddit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtReddit = (TextView) findViewById(R.id.txt_reddit);
        SharedPreferences pref = getSharedPreferences(Constants.PREFRENCE_NAME,MODE_PRIVATE);
        MyThread thread = new MyThread();
        thread.start();
        if(pref!=null && pref.contains(Constants.PREFRENCE_MODHASH))
            startActivity(new Intent(SplashScreenActivity.this,SubRedditsActivity.class));
            else
        startActivity(new Intent(SplashScreenActivity.this,MainActivity.class));
        finish();

    }
    class MyThread extends Thread {
        //used for stopping thread
        boolean flag;
        int startPosition = 0;
        int charGaps = 2;
        int lengthOfString = txtReddit.getText().length();

        int endPosition = charGaps;

        //init flag to true so that method run continuously
        public MyThread() {
            flag = true;
        }

        //set flag false, if want to stop this thread
        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        @Override
        public void run() {
            super.run();
            while (flag) {
                try {
                    Thread.sleep(150);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Spannable spn = new SpannableString(txtReddit
                                    .getText().toString());
                            spn.setSpan(new ForegroundColorSpan(Color.WHITE),
                                    startPosition, endPosition,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            txtReddit.setText(spn);

                            startPosition++;
                            endPosition++;
                            endPosition %= (lengthOfString + charGaps);
                            startPosition %= lengthOfString;

                            if (startPosition == 0) {
                                endPosition = charGaps;
                                startPosition = 0;
                            }

                            if (endPosition > lengthOfString) {
                                endPosition = lengthOfString;
                            }

                            Log.d("Home", "Start : " + startPosition + " End : " + endPosition);

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                flag = false;
            }
        }
    }
}
