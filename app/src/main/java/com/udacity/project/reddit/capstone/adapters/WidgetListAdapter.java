package com.udacity.project.reddit.capstone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.activity.ReadyItWidgetActivity;
import com.udacity.project.reddit.capstone.model.SubscribeRedditsViewModel;

import java.util.List;

/**
 * Created by Neha on 23-10-2017.
 */

public class WidgetListAdapter extends BaseAdapter {
    private Context context;
    private List<SubscribeRedditsViewModel> listData;

    public WidgetListAdapter(ReadyItWidgetActivity readyItWidgetActivity, List<SubscribeRedditsViewModel> subredditDataList) {
        this.context = readyItWidgetActivity;
        this.listData = subredditDataList;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        int layoutId = R.layout.layout_widget_item;
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (convertView == null) {
            convertView = inflater.inflate(layoutId, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.txt_subreddit_title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(listData.get(position).title);

        return convertView;
    }

    class ViewHolder {
        TextView title;
    }
}
