package com.weel.mobile.android.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.weel.mobile.R;
import com.weel.mobile.android.model.ServiceAttachment;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by jeremy.beckman on 2016-02-29.
 */
public class ServiceRecordSlideFragment extends Fragment {

    public static final String ARG_OBJECT = "object";
    private static final String TAG = "ServiceRecordFragment";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.slide_service_record_view, container, false);

        Bundle args = getArguments();
        ServiceAttachment attachment = (ServiceAttachment) args.getSerializable(ARG_OBJECT);
        String thumbnailUrl = attachment.getThumbnailUrl();

        loadAttachmentAsBitmap(rootView, thumbnailUrl);

        return rootView;
    }

    private void loadAttachmentAsBitmap(View view, String path) {
        try {
            ImageView imageView = (ImageView) view.findViewById(R.id.slide);

            URL url = new URL(path);
            AsyncTask<URL, Void, Bitmap> asyncTask = new BitmapWorkerTask(imageView).execute(url);
            Bitmap bitmap = asyncTask.get();

            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        } catch (MalformedURLException mue) {
            Log.d(TAG, mue.getMessage());
        } catch (ExecutionException ee) {
            Log.d(TAG, ee.getMessage());
        } catch (InterruptedException ie) {
            Log.d(TAG, ie.getMessage());
        }
    }

    private class BitmapWorkerTask extends AsyncTask<URL, Void, Bitmap> {
        private final String TAG = "BitmapWorkerTask";
        private final WeakReference<ImageView> imageViewWeakReference;

        public BitmapWorkerTask(ImageView imageView) {
            imageViewWeakReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(URL... params) {
            Bitmap bitmap = null;
            URL url = params[0];
            try {
                bitmap = loadBitmap(url);
            } catch (IOException ioe) {

            }
            return bitmap;
        }

        private Bitmap loadBitmap(URL url) throws IOException {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
            return bitmap;
        }

    }
}
