package com.udacity.project.reddit.capstone.activity;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.udacity.project.reddit.capstone.utils.Constants.ACCESS_TOKEN_URL;
import static com.udacity.project.reddit.capstone.utils.Constants.REDIRECT_URI;
import static com.udacity.project.reddit.capstone.utils.Constants.STATE;


public class LoginActivity extends AppCompatActivity {

    private static ProgressDialog progressDialog;

    @BindView(R.id.btn_user_login)
    Button btnLoggin;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        new LoginAsync().execute();
        ButterKnife.bind(this);
        prefs = getSharedPreferences(Constants.PREFRENCE_NAME, MODE_PRIVATE);
        editor = prefs.edit();

        btnLoggin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = String.format(Constants.AUTH_URL, Constants.CLIENT_ID, STATE, REDIRECT_URI);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)).putExtra("for_my_subreddits", false);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getIntent() != null && getIntent().getAction() != null)
            if (getIntent().getAction().equals(Intent.ACTION_VIEW)) {
                Uri uri = getIntent().getData();
                if (uri.getQueryParameter("error") != null) {
                    String error = uri.getQueryParameter("error");
                    Log.e("LOGIN. ", "An error has occurred : " + error);
                } else {
                    String state = uri.getQueryParameter("state");
                    if (state.equals(STATE)) {
                        String code = uri.getQueryParameter("code");
                        getAccessToken(code);
                    }
                }
            }
    }

    private void getAccessToken(String code) {
        OkHttpClient client = new OkHttpClient();
        String authString = Constants.CLIENT_ID + ":";
        String encodedAuthString = Base64.encodeToString(authString.getBytes(),
                Base64.NO_WRAP);
        Request request = new Request.Builder()
                .addHeader(Constants.USER_AGENT_STRING, "Sample App")
                .addHeader(Constants.AUTHORIZATION, "Basic " + encodedAuthString)
                .url(ACCESS_TOKEN_URL)
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                        "grant_type=authorization_code&code=" + code +
                                "&redirect_uri=" + REDIRECT_URI))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(">> ", "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                JSONObject data = null;
                try {
                    data = new JSONObject(json);
                    String accessToken = data.optString("access_token");
                    String refreshToken = data.optString("refresh_token");
                    editor.putString(Constants.PREFRENCE_TOKEN, accessToken);
                    editor.putString(Constants.PREFRENCE_REFRESH_TOKEN, refreshToken);
                    Date date = new Date(System.currentTimeMillis());

                    long millis = date.getTime();
                    editor.putLong(Constants.PRERENCES_TOKEN_REFRESH_TIME, millis);
                    editor.apply();
                    startActivity(new Intent(LoginActivity.this, SubRedditsActivity.class));
                    finish();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}

