package com.weel.mobile.android.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.weel.mobile.R;
import com.weel.mobile.android.adapter.ServiceRecordPagerAdapter;
import com.weel.mobile.android.model.ServiceRecord;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jeremy.beckman on 2016-02-29.
 */
public class ServiceRecordActivity extends WeeLActivity {
    public static final String EXTRA_SERVICE_RECORD = "com.weel.mobile.extras.SERVICE_RECORD";

    private ServiceRecordPagerAdapter serviceRecordPagerAdapter;
    private ViewPager viewPager;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_service_record);

        addToolbar();

        Bundle extras = getIntent().getExtras();
        ServiceRecord serviceRecord = (ServiceRecord) extras.getSerializable(EXTRA_SERVICE_RECORD);

        loadServiceRecord(serviceRecord);

        serviceRecordPagerAdapter = new ServiceRecordPagerAdapter(getSupportFragmentManager(), serviceRecord.getAttachments());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(serviceRecordPagerAdapter);
    }

    @Override
    protected void setView() {

    }

    @Override
    protected void callRemoteAPI(String remoteUri, Bundle params) {

    }

    @Override
    protected void addToolbar() {
        super.addToolbar();
        toolbar.setTitle(R.string.title_activity_service_record);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.weel_theme));
    }

    private void loadServiceRecord(ServiceRecord serviceRecord) {
        TextView descriptionTextView = (TextView) findViewById(R.id.service_description);
        descriptionTextView.setText(serviceRecord.getDescription());

        Date serviceDate = serviceRecord.getServiceDate();
        if (serviceDate != null) {
            TextView dateTextView = (TextView) findViewById(R.id.service_date);
            DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
            dateTextView.setText(dateFormat.format(serviceDate));
        }

        TextView locationTextView = (TextView) findViewById(R.id.service_location);
        locationTextView.setText(serviceRecord.getLocation());

        TextView costTextView = (TextView) findViewById(R.id.service_cost);

        if (serviceRecord.getCost() != null) {
            String currency = showAsCurrency(serviceRecord.getCost());
            costTextView.setText(currency);
        }
    }

    private String showAsCurrency(Double value) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return currencyFormat.format(value);
    }
}
