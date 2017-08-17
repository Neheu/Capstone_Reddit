package com.udacity.project.reddit.capstone.activity;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.utils.Constants;

import org.json.JSONArray;
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


public class MainActivity extends AppCompatActivity implements
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
        setContentView(R.layout.activity_main);
//        new LoginAsync().execute();
        ButterKnife.bind(this);
        prefs = getSharedPreferences(Constants.PREFRENCE_NAME,MODE_PRIVATE);
        btnLoggin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = editTextUserName.getText().toString();
                userPasswd = editTextPassword.getText().toString();

                if ("".equals(userName)) {
                    textInputPassword.setError(null);
                    textInputUserName.setError(getString(R.string.error_empty_username));
                }
                    else if ("".equals(userPasswd)) {
                    textInputUserName.setError(null);
                    textInputPassword.setError(getString(R.string.error_empty_password));
                }
                else {
                    textInputUserName.setError(null);
                    textInputPassword.setError(null);
                    getLoaderManager().initLoader(0, null, MainActivity.this);
                }
            }
        });
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
                Toast.makeText(MainActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this,SubRedditsActivity.class));
                finish();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (data.has("errors")) {
             editTextUserName.setText(userName);
             editTextPassword.setText(userPasswd);
            Toast.makeText(MainActivity.this, getString(R.string.incorrect_credentials), Toast.LENGTH_SHORT).show();
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

