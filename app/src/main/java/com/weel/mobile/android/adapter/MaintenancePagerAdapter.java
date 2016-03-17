package com.weel.mobile.android.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.weel.mobile.android.fragment.MaintenanceListFragment;
import com.weel.mobile.android.model.Maintenance;

import java.util.List;

/**
 * Created by jeremy.beckman on 2016-02-06.
 */
public class MaintenancePagerAdapter extends FragmentStatePagerAdapter {
    private Context context;
    private List<Maintenance> list;

    public MaintenancePagerAdapter(FragmentManager fm, Context context, List<Maintenance> list) {
        super(fm);
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Fragment getItem(int position) {
        Maintenance maintenance = list.get(position);
        Fragment fragment = MaintenanceListFragment.newInstance(position);
        ((MaintenanceListFragment)fragment).setData(maintenance);
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Maintenance maintenance = list.get(position);
        return maintenance.getMilestone();
    }
}
