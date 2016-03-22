package com.weel.mobile.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.weel.mobile.R;
import com.weel.mobile.android.model.ServiceRecord;
import com.weel.mobile.android.view.properties.ServiceHistoryViewProperties;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by jeremy.beckman on 2016-02-26.
 */
public class ServiceHistoryListAdapter extends ArrayAdapter<ServiceRecord> {

    private LayoutInflater inflater;
    private List<ServiceRecord> serviceRecordList;
    private HashMap<Integer, Boolean> selections;

    public ServiceHistoryListAdapter(Context context, List<ServiceRecord> serviceVisitList) {
        super(context, R.layout.adapter_service_history_list_item, serviceVisitList);
        this.inflater = LayoutInflater.from(context);
        this.serviceRecordList = serviceVisitList;
        selections = new HashMap<Integer, Boolean>();
    }

    @Override
    public int getCount() {
        return serviceRecordList.size();
    }

    @Override
    public ServiceRecord getItem(int position) {
        return serviceRecordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public HashMap<Integer, Boolean> getSelections() {
        return selections;
    }

    public void updateSelection(int position, boolean checked) {
        if (checked) {
            selections.put(position, checked);
        } else {
            selections.remove(position);
        }
        notifyDataSetChanged();
    }

    public void clearSelections() {
        selections.clear();
        notifyDataSetChanged();
    }

    public void addSelections(List<ServiceRecord> list) {
        for(int n = 0; n < list.size(); n++) {
            selections.put(n, true);
        }
        notifyDataSetChanged();
    }

    public int selected() {
        return selections.size();
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflateIfRequired(view, position, parent);
        bind(serviceRecordList.get(position), view);
        return view;
    }

    private View inflateIfRequired(View view, int position, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.adapter_service_history_list_item, parent, false);
            view.setTag(new ServiceHistoryViewProperties(view));
        }
        return view;
    }

    private void bind(ServiceRecord serviceVisit, View view) {
        ServiceHistoryViewProperties properties = (ServiceHistoryViewProperties) view.getTag();
        properties.serviceHistoryName.setText(serviceVisit.getDescription());

        Date serviceDate = serviceVisit.getServiceDate();
        if (serviceDate != null) {
            DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
            properties.serviceHistoryDate.setText(dateFormat.format(serviceDate));
        }

        Double serviceCost = serviceVisit.getCost();
        if (serviceCost != null) {
            String currency = showAsCurrency(serviceCost);
            properties.serviceHistoryCost.setText(currency);
        }

        String serviceLocation = serviceVisit.getLocation();
        if (serviceLocation != null) {
            properties.serviceHistoryLocation.setText(serviceLocation);
        }

        if (!serviceVisit.getProcessed()) {
            String serviceStatus = view.getResources().getString(R.string.service_record_not_processed);
            properties.serviceHistoryStatus.setText(serviceStatus);
        }

        if (serviceVisit.getAttachments().size() > 0) {
            loadAttachmentIcon(view);
        }
    }

    private void loadAttachmentIcon(View view) {
        ImageView attachmentIcon = new ImageView(view.getContext());
        attachmentIcon.setImageResource(R.mipmap.ic_attachment_black_36dp);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, R.id.content_layout);
        layoutParams.setMargins(0, 0, 18, 0);

        attachmentIcon.setLayoutParams(layoutParams);

        ((RelativeLayout) view).addView(attachmentIcon);
    }

    private String showAsCurrency(Double value) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return currencyFormat.format(value);
    }
}
