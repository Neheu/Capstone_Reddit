package com.udacity.project.reddit.capstone.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.db.ReadyitProvider;
import com.udacity.project.reddit.capstone.server.ApiClient;
import com.udacity.project.reddit.capstone.server.ApiInterface;
import com.udacity.project.reddit.capstone.server.GetRefreshedToken;
import com.udacity.project.reddit.capstone.utils.Constants;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.udacity.project.reddit.capstone.utils.DatabaseUtils.TABLE_SUBS_SUBREDDIT;

public class PostReplyActivity extends AppCompatActivity implements GetRefreshedToken {
    @BindView(R.id.btn_post)
    Button post;
    @BindView(R.id.txt_post_comment)
    EditText postComment;
    Intent intent;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_reply);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        intent = getIntent();
        name = intent.getStringExtra("name");
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RefreshToken().execute();
            }
        });
    }

    @Override
    public void onTokenRefreshed(String token, String tag) {
        postComment(token);
    }

    private class RefreshToken extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

        @Override
        protected Void doInBackground(Void... params) {
            Constants.refreshAccessToken(PostReplyActivity.this, PostReplyActivity.this, "post_reply_api");
            return null;
        }

    }

    private void postComment(String token) {
        String replyText = postComment.getText().toString();
        if (replyText.equals("")) {
            Toast.makeText(this, R.string.write_comment, Toast.LENGTH_SHORT).show();
        } else {
            ApiInterface mApiInterface = ApiClient.getClient().create(ApiInterface.class);
            mApiInterface.doPostReply("bearer " + token, "json", replyText, name)
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.d(">>", "Success responseCode:" + response.code());
                            if (response.code() == 200) {
                                postComment.setText("");
                                // ReadyitProvider.tableToProcess(TABLE_SUBS_SUBREDDIT);
//                            dbHelper.updateSubscribeReddits(id,"0");
//                            updateList();
                                Toast.makeText(PostReplyActivity.this, R.string.posted_successfully, Toast.LENGTH_SHORT).show();

                            } else
                                Toast.makeText(PostReplyActivity.this, R.string.error_occurred, Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d("<<", "fail:" + t.getMessage());
                        }
                    });
        }

    }

}
