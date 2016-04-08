package com.weel.mobile.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.weel.mobile.R;
import com.weel.mobile.android.model.Deal;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jeremy.beckman on 16-04-02.
 */
public class DealsRecyclerViewAdapter extends RecyclerView.Adapter<DealsRecyclerViewAdapter.ViewHolder> {

    private List<Deal> sourceList;
    private Context context;

    public DealsRecyclerViewAdapter(Context context, List sourceList) {
        this.sourceList = sourceList;
        this.context = context;
    }

    @Override
    public DealsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(context).inflate(R.layout.deals_card_item, null);
        DealsRecyclerViewAdapter.ViewHolder viewHolder = new DealsRecyclerViewAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DealsRecyclerViewAdapter.ViewHolder viewHolder, int i) {
        final Deal deal = sourceList.get(i);

        viewHolder.setOnClickListener(new ViewHolder.DealsClickListener() {
            @Override
            public void onDealClick(View caller) {
                showDealInBrowser(caller, deal);
            }
        });

        AsyncTask<Deal, Void, Bitmap> task = new BitmapWorkerTask().execute(deal);

        try {
            Bitmap bitmap = task.get();

            if (bitmap != null) {
                viewHolder.logoImageView.setImageBitmap(bitmap);
            }
        } catch (ExecutionException ee) {

        } catch (InterruptedException ie) {

        }

        viewHolder.nameTextView.setText(deal.getName());
        viewHolder.descriptionTextView.setText(deal.getDescription());
    }

    @Override
    public int getItemCount() {
        int count = -1;

        if (sourceList != null) {
            count = sourceList.size();
        }

        return count;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener {
        public ImageView logoImageView;
        public TextView nameTextView;
        public TextView descriptionTextView;
        public Button actionWebButton;

        private DealsClickListener listener;

        public ViewHolder(View view) {
            super(view);
            logoImageView = (ImageView) view.findViewById(R.id.logo);
            nameTextView = (TextView) view.findViewById(R.id.deal_name);
            descriptionTextView = (TextView) view.findViewById(R.id.deal_description);
            actionWebButton = (Button) view.findViewById(R.id.web_button);
            actionWebButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onDealClick(v);
        }

        public void setOnClickListener(DealsClickListener listener) {
            this.listener = listener;
        }

        public static interface DealsClickListener {
            public void onDealClick(View caller);
        }
    }

    private void showDealInBrowser(View view, Deal deal) {
        String vendorLink = deal.getLink();
        if (vendorLink != null && !vendorLink.equals("")) {
            if (!vendorLink.startsWith("http")) {
                vendorLink = view.getResources().getString(R.string.deals_vendor_protocol) + vendorLink;
            }

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(vendorLink));
            context.startActivity(browserIntent);
            ((Activity) context).finish();
        }
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

                }
            }
            return bitmap;
        }
    }
}
