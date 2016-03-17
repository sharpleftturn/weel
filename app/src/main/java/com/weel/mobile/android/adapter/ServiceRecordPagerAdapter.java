package com.weel.mobile.android.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.weel.mobile.android.fragment.ServiceRecordSlideFragment;
import com.weel.mobile.android.model.ServiceAttachment;

import java.util.List;

/**
 * Created by jeremy.beckman on 2016-02-29.
 */
public class ServiceRecordPagerAdapter extends FragmentPagerAdapter {

    List<ServiceAttachment> attachments;

    public ServiceRecordPagerAdapter(FragmentManager fm, List<ServiceAttachment> attachments) {
        super(fm);
        this.attachments = attachments;
    }

    @Override
    public int getCount() {
        return attachments.size();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new ServiceRecordSlideFragment();
        Bundle args = new Bundle();
        args.putSerializable(ServiceRecordSlideFragment.ARG_OBJECT, attachments.get(position));
        fragment.setArguments(args);
        return fragment;
    }
}
