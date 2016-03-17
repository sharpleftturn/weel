package com.weel.mobile.android.service;

import android.util.JsonReader;
import android.util.JsonToken;

import com.weel.mobile.android.model.Deal;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jeremy.beckman on 2016-02-29.
 */
public class DealService extends WeeLService {
    public ArrayList<Deal> getDeals(String url, long vehicleId, String authToken) {
        ArrayList<Deal> deals = null;
        url = buildGetDealsRequestUrl(url, vehicleId, authToken);
        try {
            JsonReader reader = requestDeals(url);
            deals = readDealsRequest(reader);
        } catch (IOException ioe) {

        }
        return deals;
    }

    private String buildGetDealsRequestUrl(String url, long vehicleId, String authToken) {
        String segment = String.valueOf(vehicleId);
        url = String.format(url, segment);
        StringBuilder builder = new StringBuilder(url);
        builder.append("?session_hash=");
        builder.append(authToken);
        return builder.toString();
    }

    private JsonReader requestDeals(String path) throws IOException {
        JsonReader jsonReader = null;
        URL url = new URL(path);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        int status = connection.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
            jsonReader = new JsonReader(inputStreamReader);
            jsonReader.setLenient(true);
        }
        return jsonReader;
    }

    private ArrayList<Deal> readDealsRequest(JsonReader reader) throws IOException {
        ArrayList<Deal> deals = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                String status = reader.nextString();
                if (status.equals("error")) {
                    readError(reader);
                }
            } else if (name.equals("deals") && reader.peek() == JsonToken.BEGIN_ARRAY) {
                deals = readDeals(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return deals;
    }

    private ArrayList<Deal> readDeals(JsonReader reader) throws IOException {
        ArrayList<Deal> deals = new ArrayList<>();
        reader.beginArray();
        while(reader.hasNext()) {
            deals.add(readDeal(reader));
        }
        reader.endArray();
        return deals;
    }

    private Deal readDeal(JsonReader reader) throws IOException {
        Deal deal = new Deal();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                deal.setId(reader.nextLong());
            } else if (name.equals("name")) {
                deal.setName(reader.nextString());
            } else if (name.equals("description")) {
                deal.setDescription(reader.nextString());
            } else if (name.equals("vendor")) {
                deal.setVendor(reader.nextString());
            } else if (name.equals("vendor_link")) {
                deal.setLink(reader.nextString());
            } else if (name.equals("vendor_logo")) {
                deal.setLogoUrl(reader.nextString());
            } else if (name.equals("category")) {
                deal.setCategory(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return deal;
    }
}
