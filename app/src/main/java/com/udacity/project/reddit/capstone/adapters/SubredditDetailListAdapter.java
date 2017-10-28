package com.udacity.project.reddit.capstone.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.activity.ReplyActivity;
import com.udacity.project.reddit.capstone.activity.SubredditDetailActivity;
import com.udacity.project.reddit.capstone.db.ReadyitProvider;
import com.udacity.project.reddit.capstone.db.RedyItSQLiteOpenHelper;
import com.udacity.project.reddit.capstone.model.GetCommentsModel;
import com.udacity.project.reddit.capstone.model.GetDetailedSubredditListModel;
import com.udacity.project.reddit.capstone.model.GetSubredditsModel;
import com.udacity.project.reddit.capstone.model.SubredditListViewModel;
import com.udacity.project.reddit.capstone.server.ApiClient;
import com.udacity.project.reddit.capstone.server.ApiInterface;
import com.udacity.project.reddit.capstone.server.GetRefreshedToken;
import com.udacity.project.reddit.capstone.utils.Constants;
import com.udacity.project.reddit.capstone.utils.DatabaseUtils;
import com.udacity.project.reddit.capstone.utils.OnLoadMoreListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    int currentItemPosition;
    private String calledApi = "";

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
            currentItemPosition = position;
            final SubredditListViewModel data = resultDataList.get(position);
            vHoler.title.setText(data.subreddit_name);
            vHoler.vote_count.setText(String.valueOf(data.up));
            vHoler.comment_count.setText(String.valueOf(data.comment_count));
            vHoler.detail.setText(data.subreddit_title);
            if (data.likes == 1) {
                vHoler.img_vote_up.setBackgroundResource(R.drawable.ic_up_vote_active);
                vHoler.img_vote_down.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_arrow_down));
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
                    share.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
                    share.putExtra(Intent.EXTRA_TEXT, data.share_url);

                    context.startActivity(Intent.createChooser(share, context.getString(R.string.share_to)));
                }
            });
            vHoler.comment_count.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, ReplyActivity.class).putExtra(Constants.SUB_ID, selectedData.id).putExtra(Constants.SUB_NAME,selectedData.subreddit_name));
                }
            });
            vHoler.img_vote_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedData = resultDataList.get(position);
                    id = selectedData.name;
                    //First time voting up
                    if (selectedData.likes == -1) {
                        dir = 1;
                        selectedData.up = selectedData.up + 1;
                        vHoler.updatevote(dir, selectedData);
                        selectedData.likes = 1;
                    }
                    //If Already downvoted and voting up
                    else if (selectedData.likes == 0) {
                        dir = -1;
                        selectedData.up = selectedData.up + 1;
                        vHoler.updatevote(dir, selectedData);
                        selectedData.likes = -1;

                    }
                    calledApi = context.getString(R.string.voting);
                    new RefreshToken().execute();

                }
            });
            vHoler.img_vote_down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedData = resultDataList.get(position);
                    id = selectedData.name;
                    if (selectedData.likes == -1) {
                        dir = 0;
                        selectedData.up = selectedData.up - 1;
                        vHoler.updatevote(dir, selectedData);
                        selectedData.likes = 0;
                    }  //If Already upvoted and voting down
                    else if (selectedData.likes == 1) {
                        dir = -1;
                        selectedData.up = selectedData.up - 1;
                        vHoler.updatevote(-1, selectedData);
                        selectedData.likes = -1;
                    }
                    calledApi = context.getString(R.string.voting);
                    new RefreshToken().execute();
                }
            });
            vHoler.comment_count.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    calledApi = context.getString(R.string.comments);
                    selectedData = resultDataList.get(position);
                   context.startActivity(new Intent(context, ReplyActivity.class).putExtra(Constants.SUB_NAME,selectedData.subreddit_name)
                   .putExtra(Constants.SUB_ID,selectedData.id));
                }
            });
        }
    }

//    private void getCommentsList(String token, String subreddit_url) {
//        OkHttpClient client = new OkHttpClient();
//
//        Request request = new Request.Builder()
//                .addHeader("Authorization", "bearer " + token)
//                .url(Constants.API_OAUTH_BASE_URL + subreddit_url + "/.json")
//                .build();
//
//        client.newCall(request).enqueue(new okhttp3.Callback() {
//            @Override
//            public void onFailure(okhttp3.Call call, IOException e) {
//                Log.e(">> ", "ERROR: " + e);
//            }
//
//            @Override
//            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
//                String json = response.body().string();
//                int code = response.code();
//
//                if (code == 200) {
//                    HashMap<String, List<GetCommentsModel.Child>> commentsList = new HashMap<>();
//                    List<GetCommentsModel> allDataList = (List<GetCommentsModel>) response.body();
//                    List<GetCommentsModel.Child> commentsOnly = allDataList.get(1).data.children;
//                    for (int i = 0; i < commentsOnly.size(); i++) {
//                        GetCommentsModel.Data_ data = commentsOnly.get(i).data;
//                        commentsList.put(data.parentId, data.mReplyData.data.children);
//
//                    }
//
//                }
//
//            }
//        });
//    }

    private class RefreshToken extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //If vote up ,  like is 1+ and upvote color changes
        }

        @Override
        protected Void doInBackground(Void... params) {
            Constants.refreshAccessToken(context, SubredditDetailListAdapter.this, calledApi);
            return null;
        }
    }

    private int dir;
    private String id;
    private ApiInterface apiInterface;

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
                    db.updateLikeCount(selectedData);
                    resultDataList.set(currentItemPosition, selectedData);

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
    public void onTokenRefreshed(String token, String apitag) {
        new token(token, apitag).execute();
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

        }

        void updatevote(int type, SubredditListViewModel data) {
            final int VOTE_UP = 1, VOTE_DOWN = 0, VOTE_CANCEL = -1;
            switch (type) {
                case VOTE_UP:
                    if (data.likes < 1) {
                        Glide.with(context).load("").placeholder(R.drawable.ic_up_vote_active).into(img_vote_up);
                        Glide.with(context).load("").placeholder(R.drawable.ic_arrow_down).into(img_vote_down);
                        vote_count.setText(String.valueOf(data.up));
                        notifyDataSetChanged();

                    }
                    break;
                case VOTE_DOWN:
                    if (data.likes < 1) {
                        Glide.with(context).load("").placeholder(R.drawable.ic_arrow_upward).into(img_vote_up);
                        Glide.with(context).load("").placeholder(R.drawable.ic_vote_down_active).into(img_vote_down);
                        vote_count.setText(String.valueOf(data.up));
                        notifyDataSetChanged();
                    }
                    break;
                case VOTE_CANCEL:
                    Glide.with(context).load("").placeholder(R.drawable.ic_arrow_upward).into(img_vote_up);
                    Glide.with(context).load("").placeholder(R.drawable.ic_arrow_down).into(img_vote_down);
                    notifyDataSetChanged();


            }
        }

        @Override
        public void onClick(View v) {
//            SubredditListViewModel moviesDataHolder = resultDataList.get(getAdapterPosition());
//            clickHandler.onClick(moviesDataHolder);
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
        String apiTag;

        public token(String t, String tag) {
            token = t;
            apiTag = tag;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (apiTag.equals("voting"))
                postVoteCount(token);

            return null;
        }
    }




}