package com.weel.mobile.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.weel.mobile.android.R;
import com.weel.mobile.android.adapter.ServiceHistoryListAdapter;
import com.weel.mobile.android.model.ServiceRecord;
import com.weel.mobile.android.model.Vehicle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by jeremy.beckman on 2016-02-25.
 */
public class ServiceHistoryListFragment extends ListFragment {
    public static final String TAG = "ServiceHistoryListFragment";
    public static final String ARG_VEHICLEDATA = "com.weel.mobile.fragments.arguments.VEHICLEDATA";
    public static final String ARG_SERVICEDATA = "com.weel.mobile.fragments.arguments.SERVICEDATA";

    private OnFragmentInteractionListener mListener;
    private ArrayList<ServiceRecord> serviceList;
    private ServiceHistoryListAdapter adapter;
    private List<ServiceRecord> selected;

    public static ServiceHistoryListFragment newInstance(Vehicle vehicle, ArrayList<ServiceRecord> list, String token) {
        ServiceHistoryListFragment fragment = new ServiceHistoryListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SERVICEDATA, list);
        fragment.setArguments(args);
        return fragment;
    }

    public ServiceHistoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle extras = getArguments();
        serviceList = (ArrayList<ServiceRecord>) extras.getSerializable(ARG_SERVICEDATA);
        return inflater.inflate(R.layout.fragment_service_history_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListAdapter(serviceList);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        int footerCount = l.getFooterViewsCount();
        int headerCount = l.getHeaderViewsCount();

        if (l.getCount() > (footerCount + headerCount)) {
            int index = position - headerCount;

            if (mListener != null) {
                mListener.onFragmentInteraction(serviceList.get(index));
            }
        }
    }

    public void setListAdapter(ArrayList<ServiceRecord> list) {
        if (serviceList != null && serviceList.size() > 0) {
            adapter = new ServiceHistoryListAdapter(getActivity(), list);
            setListAdapter(adapter);

            addListHeaderView();

            final ListView listView = getListView();
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                public void onItemCheckedStateChanged(ActionMode mode, int position, long l, boolean checked) {
                    int footerCount = listView.getFooterViewsCount();
                    int headerCount = listView.getHeaderViewsCount();

                    if (listView.getCount() > (footerCount + headerCount)) {
                        int index = position - headerCount;
                        adapter.updateSelection(index, checked);
                        StringBuilder title = new StringBuilder(getString(R.string.selected));
                        title.insert(0, " ");
                        title.insert(0, adapter.selected());
                        mode.setTitle(title);
                    }
                }

                public boolean onCreateActionMode(ActionMode am, Menu menu) {
                    MenuInflater inflater = am.getMenuInflater();
                    inflater.inflate(R.menu.context_menu, menu);
                    return true;
                }

                public boolean onPrepareActionMode(ActionMode am, Menu menu) {
                    return false;
                }

                public boolean onActionItemClicked(ActionMode am, MenuItem mi) {
                    switch (mi.getItemId()) {
                        case R.id.action_select_all:
                            selectAll();
                            return true;
                        default:
                            return false;
                    }
                }

                public void onDestroyActionMode(ActionMode am) {
                    selected = new ArrayList<ServiceRecord>();
                    HashMap<Integer, Boolean> selectionMap = adapter.getSelections();
                    Set<Integer> keys = selectionMap.keySet();
                    for (Integer key : keys) {
                        selected.add(serviceList.get(key));
                    }
                    adapter.clearSelections();
                }
            });
        }
    }

    public void addListItem(ServiceRecord serviceRecord) {
        serviceList.add(serviceRecord);
        adapter.notifyDataSetChanged();
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Serializable args);
    }

    private void addListHeaderView() {
        ListView serviceListView = getListView();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.service_history_header, serviceListView, false);

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        serviceListView.addHeaderView(header, null, false);
    }

    private void selectAll() {
        for (int n = 0; n < serviceList.size(); n++) {
            getListView().setItemChecked(n, true);
        }
        adapter.addSelections(serviceList);
    }
}
