package com.weel.mobile.android.fragment;

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

import com.weel.mobile.R;
import com.weel.mobile.android.adapter.MaintenanceListAdapter;
import com.weel.mobile.android.model.Maintenance;
import com.weel.mobile.android.model.MaintenanceItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by jeremy.beckman on 2016-02-06.
 */
public class MaintenanceListFragment extends ListFragment {
    private Maintenance maintenance;
    private List<MaintenanceItem> itemList;
    private MaintenanceListAdapter adapter;
    private List<MaintenanceItem> selected;

    public static MaintenanceListFragment newInstance(int num) {
        MaintenanceListFragment f = new MaintenanceListFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maintenance_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        itemList = maintenance.getItems();
        adapter = new MaintenanceListAdapter(getActivity(), itemList);
        setListAdapter(adapter);

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
                inflater.inflate(R.menu.actionbar_menu, menu);
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
                selected = new ArrayList<MaintenanceItem>();
                HashMap<Integer, Boolean> selectionMap = adapter.getSelections();
                Set<Integer> keys = selectionMap.keySet();
                for (Integer key : keys) {
                    selected.add(itemList.get(key));
                }
                adapter.clearSelections();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void setData(Maintenance maintenance) {
        this.maintenance = maintenance;
    }

    private void selectAll() {
        for (int n = 0; n < itemList.size(); n++) {
            getListView().setItemChecked(n, true);
        }
        adapter.addSelections(itemList);
    }

}
