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


public class LginActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<JSONObject> {
    private TextView txtReddit;

    @BindView(R.id.ed_user_name)
    TextInputEditText editTextUserName;
    @BindView(R.id.ed_user_password)
    TextInputEditText editTextPassword;
    @BindView(R.id.text_input_layout_username)
    TextInputLayout textInputUserName;
    @BindView(R.id.text_input_layout_password)
    TextInputLayout textInputPassword;
    private static ProgressDialog progressDialog;

    @BindView(R.id.btn_user_login)
    Button btnLoggin;
    private static String userName = "";
    private static String userPasswd = "";
    private SharedPreferences prefs = null;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        new LoginAsync().execute();
        ButterKnife.bind(this);
        prefs = getSharedPreferences(Constants.PREFRENCE_NAME,MODE_PRIVATE);

        btnLoggin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = editTextUserName.getText().toString();
                userPasswd = editTextPassword.getText().toString();

//                if ("".equals(userName)) {
//                    textInputPassword.setError(null);
//                    textInputUserName.setError(getString(R.string.error_empty_username));
//                }
//                    else if ("".equals(userPasswd)) {
//                    textInputUserName.setError(null);
//                    textInputPassword.setError(getString(R.string.error_empty_password));
//                }
//                else {
//                    textInputUserName.setError(null);
//                    textInputPassword.setError(null);
//                    getLoaderManager().initLoader(0, null, LginActivity.this);
//                }
                String url = String.format(Constants.AUTH_URL, Constants.CLIENT_ID, STATE, REDIRECT_URI);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        if(getIntent()!=null && getIntent().getAction()!=null)
            if(getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            Uri uri = getIntent().getData();
            if(uri.getQueryParameter("error") != null) {
                String error = uri.getQueryParameter("error");
                Log.e("LOGIN. ", "An error has occurred : " + error);
            } else {
                String state = uri.getQueryParameter("state");
                if(state.equals(STATE)) {
                    String code = uri.getQueryParameter("code");
                    getAccessToken(code);
                }
            }
        }
    }
    @Override
    public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
        try {
            return (new RedditLogin(this));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void getAccessToken(String code) {
        OkHttpClient client = new OkHttpClient();
        String authString = Constants.CLIENT_ID + ":";
        String encodedAuthString = Base64.encodeToString(authString.getBytes(),
                Base64.NO_WRAP);
        Request request = new Request.Builder()
                .addHeader("User-Agent", "Sample App")
                .addHeader("Authorization", "Basic " + encodedAuthString)
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

                    Log.d(">> ", "Access Token = " + accessToken);
                    Log.d(">> ", "Refresh Token = " + refreshToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    public void onLoadFinished(Loader<JSONObject> loader, JSONObject data) {
        progressDialog.dismiss();
        userName=""; userPasswd="";

        if(data!=null)
         if (data.has(("modhash"))) {
            try {
                String modhash = data.getString("modhash");
                editor = prefs.edit();
                editor.putString(Constants.PREFRENCE_MODHASH, modhash);
                editor.apply();
                Toast.makeText(LginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this,SubRedditsActivity.class));
                finish();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (data.has("errors")) {
             editTextUserName.setText(userName);
             editTextPassword.setText(userPasswd);
            Toast.makeText(LginActivity.this, getString(R.string.incorrect_credentials), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<JSONObject> loader) {

    }



    private static String login(URL url, String user, String pw) throws IOException {

        String data = "api_type=json&user=" + user + "&passwd=" + pw;
        HttpURLConnection ycConnection = null;
        ycConnection = (HttpURLConnection) url.openConnection();
        ycConnection.setRequestMethod("POST");
        ycConnection.setDoOutput(true);
        ycConnection.setUseCaches(false);
        ycConnection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded; charset=UTF-8");
        ycConnection.setRequestProperty("Content-Length", String.valueOf(data.length()));

        DataOutputStream wr = new DataOutputStream(
                ycConnection.getOutputStream());
        wr.writeBytes(data);
        wr.flush();
        wr.close();
        InputStream is = ycConnection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer response = new StringBuffer();
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }

        rd.close();
        return response.toString();
    }

    private static class RedditLogin extends AsyncTaskLoader<JSONObject> {
        private String response;
        private JSONObject obj;

        public RedditLogin(Context context) throws MalformedURLException {
            super(context);
            progressDialog = ProgressDialog.show(context,context.getString(R.string.reddit_connect),context.getString(R.string.reddit_connect));
        }


        @Override
        public JSONObject loadInBackground() {

            try {
                URL url = new URL("https://ssl.reddit.com/api/login/");
                response = login(url, userName, userPasswd);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                JSONObject responseObj = new JSONObject(response);
                if (responseObj.has("json")) {
                    JSONObject jsonObj = responseObj.getJSONObject("json");
                    if (jsonObj.has("data")) {
                        obj = jsonObj.getJSONObject("data");

                    } else if (jsonObj.has("errors")) {
                        obj = jsonObj;

                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return (obj);
        }


        @Override
        protected void onStartLoading() {
            if (obj != null) {
                deliverResult(obj);
            }

            if (takeContentChanged() || obj == null) {
                forceLoad();
            }
        }
    }
}

