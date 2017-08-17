package com.udacity.project.reddit.capstone.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.model.GetSubredditsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neha on 11-08-2017.
 */

public class SubredditsAdapters extends RecyclerView.Adapter<SubredditsAdapters.SubredditsViewHolder> {

    private List<GetSubredditsModel.Data_> resultDataList;
    private Context context;
    public static List<GetSubredditsModel.Data_> checkedList = new ArrayList<>();
    // private onMovieThumbClickHandler clickHandler;

    public SubredditsAdapters(final Context actContext, final List<GetSubredditsModel.Data_> subredditDataList) {
        this.context = actContext;
        this.resultDataList = subredditDataList;
        //this.clickHandler = handler;
    }

    @Override
    public SubredditsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.list_select_subreddit_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View layoutView = layoutInflater.inflate(layoutId, parent, shouldAttachToParentImmediately);
        int height = parent.getMeasuredHeight() / 4;
        layoutView.setMinimumHeight(height);

        return new SubredditsViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final SubredditsViewHolder holder, final int position) {
        if (resultDataList.size() != 0) {
            final GetSubredditsModel.Data_ data = resultDataList.get(position);
            holder.title.setText(data.title);
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        data.isSelected(true);
                        holder.checkBox.setId(position);
                        checkedList.add(resultDataList.get(position));
                    } else {
                        data.isSelected(false);
                        if (holder.checkBox.getId() == position)
                            checkedList.remove(position);
                    }
                }
            });
            holder.checkBox.setChecked(data.hasChecked);
        }
    }


    @Override
    public int getItemCount() {
        return resultDataList.size();
    }

    class SubredditsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        CheckBox checkBox;


        public SubredditsViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.txt_subreddit_title);
            checkBox = (CheckBox) itemView.findViewById(R.id.cb_subreddit);
            itemView.setOnClickListener(this);
//            checkBox.setClickable(false);

        }

        @Override
        public void onClick(View v) {

        }
    }

}
