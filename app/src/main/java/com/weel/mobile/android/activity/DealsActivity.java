package com.weel.mobile.android.activity;

import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;

import com.weel.mobile.R;
import com.weel.mobile.android.config.Constants;
import com.weel.mobile.android.fragment.DealsListFragment;
import com.weel.mobile.android.model.Deal;
import com.weel.mobile.android.model.Vehicle;
import com.weel.mobile.android.service.DealService;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by jeremy.beckman on 2016-01-14.
 */
public class DealsActivity extends WeeLActivity implements DealsListFragment.OnFragmentInteractionListener {

    private Vehicle vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals);

        addToolbar();

        Intent intent = getIntent();
        vehicle = (Vehicle) intent.getSerializableExtra(Constants.VEHICLE_DATA);
        authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);

        AsyncTask<Void, Void, ArrayList<Deal>> task = new GetDealsWorkerTask().execute();

        try {
            ArrayList<Deal> deals = (ArrayList<Deal>) task.get();
            showDeals(deals);
        } catch (ExecutionException ee) {

        } catch (InterruptedException ie) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_deals, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Object data) {
        Deal deal = (Deal) data;
        showDealInBrowser(deal);
    }

    private void showDeals(ArrayList<Deal> deals) {
        Fragment fragment = DealsListFragment.newInstance(deals);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_layout, fragment, "DealsList")
                .commit();
    }

    private void showDealInBrowser(Deal deal) {

        String vendorLink = deal.getLink();
        if (vendorLink != null && !vendorLink.equals("")) {
            if (!vendorLink.startsWith("http")) {
                vendorLink = getString(R.string.deals_vendor_protocol) + vendorLink;
            }

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(vendorLink));
            startActivity(browserIntent);
            finish();
        }
    }

    private class GetDealsWorkerTask extends AsyncTask<Void, Void, ArrayList<Deal>> {

        @Override
        protected ArrayList<Deal> doInBackground(Void... params) {
            String url = getString(R.string.api_url) + getString(R.string.deals_uri);
            DealService service = new DealService();
            ArrayList<Deal> deals = service.getDeals(url, vehicle.getId(), authToken);
            return deals;
        }

        @Override
        protected void onPostExecute(ArrayList<Deal> deals) {

        }
    }

    @Override
    protected void callRemoteAPI(String remoteUri, Bundle params) {

    }

    @Override
    protected void setView() {

    }

    @Override
    protected void addToolbar() {
        super.addToolbar();
        toolbar.setTitle(R.string.title_activity_deals);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.weel_deals_theme));
    }
}
