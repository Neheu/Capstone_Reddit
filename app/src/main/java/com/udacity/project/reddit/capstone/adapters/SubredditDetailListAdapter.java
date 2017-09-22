package com.udacity.project.reddit.capstone.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.activity.SubredditDetailActivity;
import com.udacity.project.reddit.capstone.db.ReadyitProvider;
import com.udacity.project.reddit.capstone.db.RedyItSQLiteOpenHelper;
import com.udacity.project.reddit.capstone.model.SubredditListViewModel;
import com.udacity.project.reddit.capstone.server.ApiInterface;
import com.udacity.project.reddit.capstone.server.GetRefreshedToken;
import com.udacity.project.reddit.capstone.utils.Constants;
import com.udacity.project.reddit.capstone.utils.DatabaseUtils;
import com.udacity.project.reddit.capstone.utils.OnLoadMoreListener;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.udacity.project.reddit.capstone.R.id.imageView;

/**
 * Created by Neha on 03-09-2017.
 */

public class SubredditDetailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements GetRefreshedToken {
    private OnLoadMoreListener mOnLoadMoreListener;
    private List<SubredditListViewModel> resultDataList;
    private Context context;
    private SubredditsAdapters.checkChangeListener checkHandler;
    private onSubredditSelectListener clickHandler;
    RedyItSQLiteOpenHelper dbHelper;
    Activity activity;

    public SubredditDetailListAdapter(Activity act, onSubredditSelectListener handler, final Context actContext, RecyclerView rv, final List<SubredditListViewModel> subredditDataList) {
        this.context = actContext;
        this.resultDataList = subredditDataList;
        dbHelper = new RedyItSQLiteOpenHelper(context);
        this.clickHandler = handler;
        activity = act;
    }

    @Override
    public SubredditDetailListAdapter.SubredditsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        int layoutId = R.layout.subreddits_detail_list_item;
        View layoutView = layoutInflater.inflate(layoutId, parent, shouldAttachToParentImmediately);
        return new SubredditDetailListAdapter.SubredditsViewHolder(layoutView);
    }

    SubredditDetailListAdapter.SubredditsViewHolder vHoler;
    SubredditListViewModel selectedData;

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        vHoler = (SubredditDetailListAdapter.SubredditsViewHolder) holder;
        if (resultDataList.size() != 0) {
            SubredditListViewModel data = resultDataList.get(position);
            vHoler.title.setText(data.subreddit_name);
            vHoler.vote_count.setText(String.valueOf(data.up));
            vHoler.comment_count.setText(String.valueOf(data.comment_count));
            vHoler.detail.setText(data.subreddit_title);
            if (data.likes == 1) {
                vHoler.img_vote_up.setImageResource(R.drawable.ic_up_vote_active);
                vHoler.img_vote_down.setImageResource(R.drawable.ic_arrow_down);

            } else if (data.likes == 0) {
                vHoler.img_vote_down.setImageResource(R.drawable.ic_vote_down_active);
                vHoler.img_vote_up.setImageResource(R.drawable.ic_arrow_upward);
            } else {
                vHoler.img_vote_up.setImageResource(R.drawable.ic_arrow_upward);
                vHoler.img_vote_down.setImageResource(R.drawable.ic_arrow_down);
            }
            Glide.with(context)
                    .load(data.thumb_url)
                    .placeholder(R.mipmap.redit_icon)
                    .into(vHoler.thum_img);

            vHoler.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
                    share.setType("text/plain");
                    selectedData = resultDataList.get(position);
                    share.putExtra(Intent.EXTRA_SUBJECT, "ReadyIt");
                    share.putExtra(Intent.EXTRA_TEXT, selectedData.share_url);

                    context.startActivity(Intent.createChooser(share, "Share text to."));
                }
            });
            vHoler.comment_count.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, SubredditDetailActivity.class).putExtra(Constants.INTENT_SUBREDDIT_DETAIL_DATA, selectedData));
                }
            });
            vHoler.img_vote_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedData = resultDataList.get(position);
                    id = selectedData.name;
                    if (selectedData.likes < 1) {
//                        dir = 1;
//                        selectedData.likes = 1;
//                        selectedData.up = selectedData.up - 1;
                        vHoler.img_vote_down.setImageResource(R.drawable.ic_arrow_down);
//                        vHoler.img_vote_up.setImageResource(R.drawable.ic_up_vote_active);
//                        vHoler.vote_count.setText(String.valueOf(selectedData.up));

                    } else if (selectedData.likes == 1) {
                        dir = 0;
                        selectedData.up = selectedData.up - 1;
                        vHoler.img_vote_down.setImageResource(R.drawable.ic_vote_down_active);
                        vHoler.img_vote_up.setImageResource(R.drawable.ic_arrow_upward);
                        vHoler.vote_count.setText(String.valueOf(selectedData.up));
                    }
                    // new RefreshToken().execute();

                }
            });
            vHoler.img_vote_down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    selectedData = resultDataList.get(position);

                    id = selectedData.name;
                    if (selectedData.likes != 0) {
                        dir = -1;
                        selectedData.likes = 0;
                        selectedData.up = selectedData.up + 1;
                        vHoler.img_vote_up.setImageResource(R.drawable.ic_up_vote_active);
                        vHoler.img_vote_down.setImageResource(R.drawable.ic_arrow_down);
                        vHoler.vote_count.setText(String.valueOf(selectedData.up));


                    } else if (selectedData.likes == 0) {
                        dir = 0;
                        selectedData.up = selectedData.up + 1;
                        vHoler.img_vote_down.setImageResource(R.drawable.ic_arrow_down);
                        vHoler.img_vote_up.setImageResource(R.drawable.ic_arrow_upward);
                        vHoler.vote_count.setText(String.valueOf(selectedData.up));
                    }
                    // new RefreshToken().execute();
                }
            });
        }
    }

    private class RefreshToken extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //If vote up ,  like is 1+ and upvote color changes


        }

        @Override
        protected Void doInBackground(Void... params) {
            Constants.refreshAccessToken(context, SubredditDetailListAdapter.this);
            return null;
        }
    }

    private int dir;
    private String id;
    private ApiInterface apiInterface;
    private Retrofit retrofit;

    private void postVoteCount(String token) {
        OkHttpClient client = new OkHttpClient();
        MediaType MEDIA_TYPE =
                MediaType.parse("application/x-www-form-urlencoded");
        String requestString = "dir=" + String.valueOf(dir)
                + "&id=" + id + "&rank=" + String.valueOf(2);
        RequestBody body = RequestBody.create(MEDIA_TYPE,
                requestString);
        Request request = new Request.Builder()
                .addHeader("User-Agent", "Sample App")
                .addHeader("Authorization", "bearer " + token)
                .url(Constants.API_OAUTH_BASE_URL + Constants.API_VOTE)
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e("Error ", e.toString());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.code() == 200) {
                    RedyItSQLiteOpenHelper db = new RedyItSQLiteOpenHelper(context);
                    ReadyitProvider.tableToProcess(DatabaseUtils.TABLE_DETAIL_SUBREDDIT);
                    int i = db.updateLikeCount(selectedData);
                } else {
                    vHoler.img_vote_up.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_upward));
                    vHoler.img_vote_down.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_down));
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return resultDataList == null ? 0 : resultDataList.size();
    }

    @Override
    public void onTokenRefreshed(String token) {
        new token(token).execute();
    }


    class SubredditsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, detail, comment_count, vote_count, share;
        ImageView thum_img, img_vote_up, img_vote_down;

        public SubredditsViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            detail = (TextView) itemView.findViewById(R.id.tv_detail);
            comment_count = (TextView) itemView.findViewById(R.id.tv_comments);
            vote_count = (TextView) itemView.findViewById(R.id.tv_vote_count);
            thum_img = (ImageView) itemView.findViewById(R.id.thumb_img);
            share = (TextView) itemView.findViewById(R.id.tv_share);
            img_vote_down = (ImageView) itemView.findViewById(R.id.image_down_vote);
            img_vote_up = (ImageView) itemView.findViewById(R.id.image_up_vote);

            itemView.setOnClickListener(this);
            img_vote_down.setOnClickListener(this);
            img_vote_up.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            SubredditListViewModel moviesDataHolder = resultDataList.get(getAdapterPosition());
            clickHandler.onClick(moviesDataHolder);
        }
    }

    public void updateList(List<SubredditListViewModel> list) {
        resultDataList = list;
    }

    public interface onSubredditSelectListener {
        void onClick(SubredditListViewModel dataHolder);

    }

    public class token extends AsyncTask<Void, Void, Void> {
        String token;

        public token(String t) {
            token = t;
        }

        @Override
        protected Void doInBackground(Void... params) {
            postVoteCount(token);
            return null;
        }
    }

}
