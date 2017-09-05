package com.udacity.project.reddit.capstone.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.activity.SubredditDetailActivity;
import com.udacity.project.reddit.capstone.db.RedyItSQLiteOpenHelper;
import com.udacity.project.reddit.capstone.model.SubredditListViewModel;
import com.udacity.project.reddit.capstone.utils.Constants;
import com.udacity.project.reddit.capstone.utils.OnLoadMoreListener;

import java.util.List;

/**
 * Created by Neha on 03-09-2017.
 */

public class SubredditDetailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private OnLoadMoreListener mOnLoadMoreListener;
    private List<SubredditListViewModel> resultDataList;
    private Context context;
    private int count = 0;
    private SubredditsAdapters.checkChangeListener checkHandler;
    private boolean isLoading;
    private onSubredditSelectListener clickHandler;
    RedyItSQLiteOpenHelper dbHelper;

    public SubredditDetailListAdapter(onSubredditSelectListener handler, final Context actContext, RecyclerView rv, final List<SubredditListViewModel> subredditDataList) {
        this.context = actContext;
        this.resultDataList = subredditDataList;
        dbHelper = new RedyItSQLiteOpenHelper(context);
        this.clickHandler = handler;

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

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final SubredditDetailListAdapter.SubredditsViewHolder vHoler = (SubredditDetailListAdapter.SubredditsViewHolder) holder;
        if (resultDataList.size() != 0) {
            final SubredditListViewModel data = resultDataList.get(position);
            vHoler.title.setText(data.subreddit_name);
            vHoler.vote_count.setText(String.valueOf(data.up));
            vHoler.comment_count.setText(String.valueOf(data.comment_count));
            vHoler.detail.setText(data.subreddit_title);
            Picasso.with(context).load(data.thumb_url).error(R.mipmap.redit_icon).into(vHoler.thum_img);
            vHoler.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
                    share.setType("text/plain");

                    share.putExtra(Intent.EXTRA_SUBJECT, "ReadyIt");
                    share.putExtra(Intent.EXTRA_TEXT, data.share_url);

                    context.startActivity(Intent.createChooser(share, "Share text to."));
                }
            });
            vHoler.comment_count.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, SubredditDetailActivity.class).putExtra(Constants.INTENT_SUBREDDIT_DETAIL_DATA,data));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return resultDataList == null ? 0 : resultDataList.size();
    }

    class SubredditsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, detail, comment_count, vote_count, share;
        ImageView thum_img;

        public SubredditsViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            detail = (TextView) itemView.findViewById(R.id.tv_detail);
            comment_count = (TextView) itemView.findViewById(R.id.tv_comments);
            vote_count = (TextView) itemView.findViewById(R.id.tv_vote_count);
            thum_img = (ImageView) itemView.findViewById(R.id.thumb_img);
            share = (TextView) itemView.findViewById(R.id.tv_share);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            SubredditListViewModel moviesDataHolder = resultDataList.get(getAdapterPosition());
            clickHandler.onClick(moviesDataHolder);
        }
    }

    public interface onSubredditSelectListener {
        void onClick(SubredditListViewModel dataHolder);

    }
}