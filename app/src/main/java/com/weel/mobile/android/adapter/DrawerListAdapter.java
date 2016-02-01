package com.weel.mobile.android.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.weel.mobile.android.R;
import com.weel.mobile.android.model.DrawerItem;
import com.weel.mobile.android.view.properties.NavigationViewProperties;

import java.util.List;

/**
 * Created by jeremy.beckman on 2015-10-17.
 */
public class DrawerListAdapter extends ArrayAdapter<DrawerItem> {

    private LayoutInflater inflater;
    private List<DrawerItem> drawerItemList;

    public DrawerListAdapter(Context context, List<DrawerItem> drawerItemList) {
        super(context, R.layout.activity_drawer_item);

        this.inflater = LayoutInflater.from(context);
        this.drawerItemList = drawerItemList;
    }

    @Override
    public int getCount() {
        return drawerItemList.size();
    }

    @Override
    public DrawerItem getItem(int position) {
        return drawerItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflateIfRequired(view, position, parent);
        bind(drawerItemList.get(position), view);
        return view;
    }

    private View inflateIfRequired(View view, int position, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.activity_drawer_item, parent, false);
            try {
                NavigationViewProperties properties = new NavigationViewProperties(view);
                view.setTag(properties);
            } catch (Exception e) {
            //    Log.d(TAG, e.getMessage());
            }
        }
        return view;

    }

    private void bind(DrawerItem drawerItem, View view) {
        NavigationViewProperties properties = (NavigationViewProperties) view.getTag();
        properties.label.setText(drawerItem.getLabel());
        Drawable drawableLeft = view.getResources().getDrawable(drawerItem.getIcon());
        properties.label.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
    }
}
