package com.udacity.project.reddit.capstone.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.db.RedyItSQLiteOpenHelper;
import com.udacity.project.reddit.capstone.utils.Constants;
import com.udacity.project.reddit.capstone.utils.DatabaseUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashScreenActivity extends AppCompatActivity {
    @BindView(R.id.linearLayout)
    LinearLayout rootLayout;
    @BindView(R.id.txt_reddit)
    TextView txtReddit;
    SQLiteDatabase db;
    RedyItSQLiteOpenHelper dbHelper;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtReddit = (TextView) findViewById(R.id.txt_reddit);
        dbHelper = new RedyItSQLiteOpenHelper(this);
        db = dbHelper.getReadableDatabase();
        StartAnimations();
        SplashRestThread thread = new SplashRestThread();
        thread.start();


    }

    class SplashRestThread extends Thread {
        //used for stopping thread
        boolean flag;
        int startPosition = 0;
        int charGaps = 2;
        int lengthOfString = txtReddit.getText().length();

        int endPosition = charGaps;

        //init flag to true so that method run continuously
        public SplashRestThread() {
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
                    Thread.sleep(3500);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences pref = getSharedPreferences(Constants.PREFRENCE_NAME, MODE_PRIVATE);
                            if (pref != null && pref.contains(Constants.PREFRENCE_TOKEN))
//                                if (dbHelper.isTableNotEmpty(DatabaseUtils.TABLE_SUBS_SUBREDDIT, db)) {
//
//                                } else
                                    startActivity(new Intent(SplashScreenActivity.this, MineSubredditsActivity.class));
                            else

                                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                            finish();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
                flag = false;
            }
        }

    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.splash_alpha);
        anim.reset();
        rootLayout.clearAnimation();
        rootLayout.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.splash_transition);
        anim.reset();

        txtReddit.clearAnimation();
        txtReddit.startAnimation(anim);

    }
}
