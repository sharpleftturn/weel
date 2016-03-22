package com.weel.mobile.android.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.weel.mobile.R;
import com.weel.mobile.android.model.Deal;
import com.weel.mobile.android.view.properties.DealsViewProperties;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jeremy.beckman on 2016-02-29.
 */
public class DealsListAdapter extends ArrayAdapter<Deal> {
    private static final String TAG = "DealsListAdapter";
    private LayoutInflater inflater;
    private List<Deal> deals;

    public DealsListAdapter(Context context, List<Deal> deals) {
        super(context, R.layout.deals_list_item, deals);
        this.inflater = LayoutInflater.from(context);
        this.deals = deals;
    }

    @Override
    public int getCount() {
        return deals.size();
    }

    @Override
    public Deal getItem(int position) {
        return deals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflateIfRequired(view, position, parent);
        bind(deals.get(position), view);
        return view;
    }

    private View inflateIfRequired(View view, int position, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.deals_list_item, parent, false);
            view.setTag(new DealsViewProperties(view));
        }
        return view;
    }

    private void bind(Deal item, View view) {
        DealsViewProperties properties = (DealsViewProperties) view.getTag();

        AsyncTask<Deal, Void, Bitmap> task = new BitmapWorkerTask().execute(item);

        Bitmap bitmap = null;

        try {
            bitmap = task.get();
        } catch (ExecutionException ee) {

        } catch (InterruptedException ie) {

        }

        properties.logoView.setImageBitmap(bitmap);
        properties.nameView.setText(item.getName());
        properties.descriptionView.setText(item.getDescription());
    }

    private class BitmapWorkerTask extends AsyncTask<Deal, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Deal... params) {
            Deal deal = params[0];
            Bitmap bitmap = null;

            String logoUrl = deal.getLogoUrl();
            if (deal.getLogoUrl() != null && !deal.getLogoUrl().equals("")) {
                try {
                    URL url = new URL(logoUrl);
                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.connect();

                    bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                    deal.setLogoBitmap(bitmap);
                } catch (IOException ioe) {
                    Log.d(TAG, ioe.getMessage());
                }
            }
            return bitmap;
        }
    }
}
