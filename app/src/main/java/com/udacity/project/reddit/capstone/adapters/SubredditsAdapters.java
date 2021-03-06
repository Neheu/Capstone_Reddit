package com.udacity.project.reddit.capstone.adapters;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.activity.SubRedditsActivity;
import com.udacity.project.reddit.capstone.model.GetSubredditsModel;
import com.udacity.project.reddit.capstone.model.SubredditListViewModel;
import com.udacity.project.reddit.capstone.model.SubscribeRedditsViewModel;
import com.udacity.project.reddit.capstone.utils.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Neha on 11-08-2017.
 */

public class SubredditsAdapters extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private List<SubscribeRedditsViewModel> resultDataList;
    private Context context;
    private int count = 0;
    public static HashMap<String, SubscribeRedditsViewModel> checkedList = new HashMap<>();
    private checkChangeListener checkHandler;
    private boolean isLoading;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    onSubredditSelectListener clickHandler;
    private boolean isToBeSubs;

    public SubredditsAdapters(final boolean toSubs, final Context actContext, RecyclerView rv, final List<SubscribeRedditsViewModel> subredditDataList, checkChangeListener listener, onSubredditSelectListener handler) {
        this.context = actContext;
        this.resultDataList = subredditDataList;
        this.checkHandler = listener;
        checkedList.clear();
        this.clickHandler = handler;
        isToBeSubs = toSubs;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rv.getLayoutManager();
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public SubredditsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        int layoutId = 0;
        if (viewType == VIEW_TYPE_ITEM) {
            layoutId = R.layout.list_select_subreddit_item;

        } else if (viewType == VIEW_TYPE_LOADING) {
            layoutId = R.layout.layout_list_footer_progress;
        }
        View layoutView = layoutInflater.inflate(layoutId, parent, shouldAttachToParentImmediately);


        return new SubredditsViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof SubredditsViewHolder) {
            final SubredditsViewHolder vHoler = (SubredditsViewHolder) holder;
            if (resultDataList.size() != 0) {
                final SubscribeRedditsViewModel data = resultDataList.get(position);
                vHoler.title.setText(data.title);
                if (data.isSubscribed) {
                    vHoler.switchCompat.setChecked(true);
                }
                if (isToBeSubs)
                    vHoler.switchCompat.setText(R.string.subscribe);
                else
                    vHoler.switchCompat.setText(R.string.unsubscribe);
                vHoler.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            count++;
                            data.isSelected(true);
                            data.isSubscribed = true;
                            checkedList.put(data.title, resultDataList.get(position));
                            vHoler.switchCompat.setId(resultDataList.size());
                            checkHandler.checkCount(count, checkedList);
                        } else {
                            count--;
                            data.isSelected(false);
                            checkHandler.checkCount(count, checkedList);
                            for (String key : checkedList.keySet()) {
                                if (key.equals(data.title)) {
                                    checkedList.remove(key);
                                    break;
                                }
                            }
                        }

                    }
                });
                vHoler.switchCompat.setChecked(data.isSubscribed);
            }

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder viewHolder = (LoadingViewHolder) holder;
            viewHolder.progressBar.setIndeterminate(true);


        }
    }


    public interface checkChangeListener {
        void checkCount(int count, HashMap<String, SubscribeRedditsViewModel> data);
    }

    public interface onSubredditSelectListener {
        void onClick(SubscribeRedditsViewModel dataHolder);

    }

    @Override
    public int getItemViewType(int position) {
        return resultDataList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return resultDataList == null ? 0 : resultDataList.size();
    }

    class SubredditsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        SwitchCompat switchCompat;


        public SubredditsViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.txt_subreddit_title);
            switchCompat = (SwitchCompat) itemView.findViewById(R.id.btn_switch);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            SubscribeRedditsViewModel moviesDataHolder = resultDataList.get(getAdapterPosition());
            clickHandler.onClick(moviesDataHolder);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }

}
