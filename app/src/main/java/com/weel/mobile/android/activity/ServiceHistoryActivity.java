package com.weel.mobile.android.activity;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import com.weel.mobile.android.R;
import com.weel.mobile.android.config.Constants;
import com.weel.mobile.android.fragment.ServiceHistoryEmptyFragment;
import com.weel.mobile.android.fragment.ServiceHistoryListFragment;
import com.weel.mobile.android.model.ServiceRecord;
import com.weel.mobile.android.model.User;
import com.weel.mobile.android.model.Vehicle;
import com.weel.mobile.android.service.MaintenanceRecordService;

/**
 * Created by jeremy.beckman on 2016-02-08.
 */
public class ServiceHistoryActivity extends WeeLActivity implements ServiceHistoryListFragment.OnFragmentInteractionListener {

    private Vehicle vehicle = null;
    private String imageCapturePath;
    private ArrayList<ServiceRecord> serviceRecordList;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    startAddServiceActivity();
                }
                return;
            case Constants.REQUEST_ADD_SERVICE_RECORD:
//                ServiceRecord serviceRecord = (ServiceRecord) data.getSerializableExtra(AddServiceRecordActivity.EXTRA_SERVICE_RECORD);
//                updateServiceRecordList(serviceRecord);
                return;
            default:
                return;
        }
    }

    @Override
    public void onFragmentInteraction(Serializable args) {
        ServiceRecord serviceRecord = (ServiceRecord) args;
    //    if (serviceRecord.getProcessed()) {
            Intent intent = new Intent(ServiceHistoryActivity.this, ServiceRecordActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(ServiceRecordActivity.EXTRA_SERVICE_RECORD, serviceRecord);
            startActivityForResult(intent, Constants.REQUEST_SERVICE_RECORD);
//        } else {
//            Toast.makeText(this, R.string.service_record_not_processed, Toast.LENGTH_SHORT).show();
//        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_history);

        addToolbar();

        Intent intent = getIntent();

        vehicle = (Vehicle) intent.getSerializableExtra(Constants.VEHICLE_DATA);
        authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
        user = (User) intent.getSerializableExtra(Constants.USER_DATA);

        ImageButton actionButton = (ImageButton) findViewById(R.id.fab_photo);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addServiceRecord(view);
            }
        });

        loadServiceRecords();
    }

    @Override
    protected void addToolbar() {
        super.addToolbar();
        toolbar.setTitle(R.string.service_toolbar_label);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void callRemoteAPI(String remoteUri, Bundle params) {

    }

    @Override
    protected void setView() {

    }

    private void loadServiceRecords() {
        try {
            Fragment fragment;
            FragmentManager fragmentManager = getSupportFragmentManager();

            AsyncTask<Void, Void, ArrayList<ServiceRecord>> asyncTask = new GetServiceRecordsWorkerTask().execute();
            serviceRecordList = asyncTask.get();

            if (serviceRecordList == null || serviceRecordList.size() == 0) {
                fragment = fragmentManager.findFragmentByTag(Constants.EMPTY);
                if (fragment == null) {
                    fragment = new ServiceHistoryEmptyFragment();
                    fragmentManager.beginTransaction()
                            .add(R.id.service_content_layout, fragment, Constants.EMPTY)
                            .commit();
                }
            } else {
                fragment = fragmentManager.findFragmentByTag(ServiceHistoryListFragment.TAG);
                if (fragment == null) {
                    fragment = ServiceHistoryListFragment.newInstance(vehicle, serviceRecordList, authToken);
                    fragmentManager.beginTransaction()
                            .add(R.id.service_content_layout, fragment, ServiceHistoryListFragment.TAG)
                            .commit();
                }
            }
        } catch (ExecutionException ee) {

        } catch (InterruptedException ie) {

        }
    }

    private void updateServiceRecordList(ServiceRecord record) {
        serviceRecordList.add(record);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(ServiceHistoryListFragment.TAG);
        ((ServiceHistoryListFragment) fragment).addListItem(record);
    }

    private void startAddServiceActivity() {
        Intent intent = new Intent(ServiceHistoryActivity.this, AddServiceRecordActivity.class);
        intent.putExtra(AddServiceRecordActivity.EXTRA_VEHICLEDATA, vehicle);
        intent.putExtra(AddServiceRecordActivity.EXTRA_IMAGE_UPLOAD, imageCapturePath);
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
        startActivityForResult(intent, Constants.REQUEST_ADD_SERVICE_RECORD);
    }

    private void addServiceRecord(View view) {
        final CharSequence[] options = {getString(R.string.action_image_photo), getString(R.string.action_image_gallery)};
        openOptions(options, view);
    }

    private void openOptions(final CharSequence[] options, View view) {
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.capture_option_item, options);

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), AlertDialog.THEME_HOLO_LIGHT); // THEME_HOLO_LIGHT deprecated in API level 23
        builder.setTitle(getString(R.string.add_invoice_label));
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (options[i].equals(getString(R.string.action_image_photo))) {
                    dispatchImageCapture();
                } else if (options[i].equals(getString(R.string.action_image_gallery))) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, Constants.REQUEST_IMAGE_GALLERY);
                }
            }
        });
        builder.show();
    }

    private File createImageCapture() throws IOException {
        StringBuffer buffer = new StringBuffer(getString(R.string.app_capture_file_prefix));

        buffer.append("_");
        buffer.append(new SimpleDateFormat(getString(R.string.app_timestamp_format)).format(new Date()));
        String imageFileName = buffer.toString();

        File imageCaptureDir = getExternalFilesDir(null);
        File imageCapture = File.createTempFile(imageFileName, ".jpg", imageCaptureDir);

        imageCapturePath = imageCapture.getAbsolutePath();
        return imageCapture;
    }

    private void dispatchImageCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File imageFile = null;
            try {
                imageFile = createImageCapture();
            } catch (IOException ioe) {
                Toast.makeText(this, getString(R.string.image_capture_error), Toast.LENGTH_LONG);
            }

            if (imageFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                startActivityForResult(intent, Constants.REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private class GetServiceRecordsWorkerTask extends AsyncTask<Void, Void, ArrayList<ServiceRecord>> {

        @Override
        protected ArrayList<ServiceRecord> doInBackground(Void... params) {
            ArrayList<ServiceRecord> records = null;

            MaintenanceRecordService service = new MaintenanceRecordService();

            try {
                String url = getString(R.string.api_url) + getString(R.string.service_records_uri);
                records = service.getServiceRecords(url, vehicle.getId(), authToken);
            } catch (IOException ioe) {
                service = null;
            }
            return records;
        }
    }
}
