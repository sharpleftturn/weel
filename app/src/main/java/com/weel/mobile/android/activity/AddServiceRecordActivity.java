package com.weel.mobile.android.activity;

import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weel.mobile.R;
import com.weel.mobile.android.model.ServiceRecord;
import com.weel.mobile.android.model.Vehicle;
import com.weel.mobile.android.service.MaintenanceRecordService;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by jeremy.beckman on 2016-02-09.
 */
public class AddServiceRecordActivity extends WeeLActivity {
    public static final String EXTRA_VEHICLEDATA = "com.weel.mobile.extras.VEHICLEDATA";
    public static final String EXTRA_IMAGE_UPLOAD = "extraImageUpload";
    public static final String EXTRA_SERVICE_RECORD = "com.weel.mobile.android.extras.SERVICE_RECORD";

    private static final String EXTRA_SERVICE_RECORD_DESCRIPTION = "com.weel.mobile.android.extras.SERVICE_DESCRIPTION";
    private static final String EXTRA_SERVICE_RECORD_ATTACHMENT = "com.weel.mobile.android.extras.SERVICE_ATTACHMENT";

    private Vehicle vehicle;
    private String previewImagePath;
    private RelativeLayout contentLayout;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service_record);

        addToolbar();

        Intent intent = getIntent();
        vehicle = (Vehicle) intent.getSerializableExtra(EXTRA_VEHICLEDATA);
        previewImagePath = intent.getStringExtra(EXTRA_IMAGE_UPLOAD);
        authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);

        Uri previewUri = Uri.fromFile(new File(previewImagePath));

        loadPreviewImage(previewUri);

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveServiceRecord(view);
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel(view);
            }
        });
    }

    @Override
    protected void addToolbar() {
        super.addToolbar();
        toolbar.setTitle(R.string.title_activity_add_service_record);
    }


    @Override
    protected void callRemoteAPI(String remoteUri, Bundle params) {

    }

    @Override
    protected void setView() {

    }

    private void saveServiceRecord(View view) {
        String description = ((TextView) findViewById(R.id.description_value)).getText().toString();

        Bundle params = new Bundle();
        params.putString(EXTRA_SERVICE_RECORD_DESCRIPTION, description);
        params.putString(EXTRA_SERVICE_RECORD_ATTACHMENT, previewImagePath);

        AsyncTask<Bundle, Void, ServiceRecord> maintenanceServiceTask = new MaintenanceServiceWorker(AddServiceRecordActivity.this).execute(params);

        Bundle data = new Bundle();

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private void cancel(View view) {
        finish();
    }

    private void loadPreviewImage(Uri uri) {
        ImageView previewImageView = (ImageView) findViewById(R.id.preview_image);
        AsyncTask<Uri, Void, Bitmap> asyncTask = new BitmapWorkerTask().execute(uri);

        try {
            Bitmap bitmap = (Bitmap) asyncTask.get();

            if (bitmap != null) {
                previewImageView.setImageBitmap(bitmap);
            }
        } catch (ExecutionException ee) {

        } catch (InterruptedException ie) {

        }
    }

    private class BitmapWorkerTask extends AsyncTask<Uri, Void, Bitmap> {
        private final String TAG = "BitmapWorkerTask";

        public BitmapWorkerTask() {

        }

        @Override
        protected Bitmap doInBackground(Uri... params) {
            Bitmap bitmap = null;
            Uri uri = params[0];
            try {
                bitmap = loadBitmap(uri);
            } catch (IOException ioe) {
                Log.d(TAG, ioe.getMessage());
            }
            return bitmap;
        }

        private Bitmap loadBitmap(Uri uri) throws IOException {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath(), options);
            return bitmap;
        }
    }

    private class MaintenanceServiceWorker extends AsyncTask<Bundle, Void, ServiceRecord> {

        private Context context;
        private MaintenanceRecordService service;
        private Notification notification;
        private static final int NOTIFICATION_ID = 124;

        public MaintenanceServiceWorker(Context context) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            createNotification(getString(R.string.notification_upload_started), getString(R.string.notification_upload_started_detail), android.R.drawable.stat_sys_upload);
        }

        @Override
        protected ServiceRecord doInBackground(Bundle... params) {
            Bundle data = params[0];
            String description = data.getString(EXTRA_SERVICE_RECORD_DESCRIPTION);
            String attachment = data.getString(EXTRA_SERVICE_RECORD_ATTACHMENT);

            String url = getString(R.string.api_url) + getString(R.string.service_records_uri);

            service = new MaintenanceRecordService();
            ServiceRecord serviceRecord = service.addServiceRecord(url, vehicle.getId(), description, attachment, authToken);
            return serviceRecord;
        }

        @Override
        protected void onPostExecute(ServiceRecord record) {
            if (service.hasError()) {
                createNotification(getString(R.string.notification_upload_failed), service.getErrorMessage(), android.R.drawable.stat_sys_warning);
            } else {
                createNotification(getString(R.string.notification_upload_complete), getString(R.string.notification_upload_complete_detail), android.R.drawable.stat_sys_upload_done);
            }
        }

        private void createNotification(String title, @Nullable String text, int resId) {
            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(resId);
            builder.setAutoCancel(true);
            builder.setContentTitle(title);

            if (text != null) {
                builder.setContentText(text);
            }

            notification = builder.build();

            notificationManager.notify(NOTIFICATION_ID, notification);

        }
    }
}
