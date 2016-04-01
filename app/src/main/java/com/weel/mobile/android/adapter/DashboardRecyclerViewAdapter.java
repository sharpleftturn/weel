package com.weel.mobile.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weel.mobile.R;

import java.util.List;

/**
 * Created by jeremy.beckman on 16-03-27.
 */
public class DashboardRecyclerViewAdapter extends RecyclerView.Adapter<DashboardRecyclerViewAdapter.DashboardPanelViewHolder> {

    private List panelList;
    private Context context;

    public DashboardRecyclerViewAdapter(Context context, List panelList) {
        this.panelList = panelList;
        this.context = context;
    }

    @Override
    public DashboardPanelViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dashboard_grid_item, null);
        DashboardPanelViewHolder viewHolder = new DashboardPanelViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DashboardPanelViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {

        return -1;
    }

    public static class DashboardPanelViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener {

        private DashboardPanelClickListener listener;

        public DashboardPanelViewHolder(View view) {
            super(view);

        }

        @Override
        public void onClick(View v) {
            listener.onPanelClick(v);
        }

        public static interface DashboardPanelClickListener {
            public void onPanelClick(View caller);
        }
    }
}
