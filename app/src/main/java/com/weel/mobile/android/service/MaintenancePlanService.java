package com.weel.mobile.android.service;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import com.weel.mobile.android.model.Maintenance;
import com.weel.mobile.android.model.MaintenanceItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeremy.beckman on 2016-02-06.
 */
public class MaintenancePlanService extends WeeLService {
    private static final String TAG = "MaintenancePlanService";

    public ArrayList<Maintenance> getUserMaintenancePlan(String url, Long id, String authToken) {
        return getVehicleMaintenance(url, id, authToken);
    }

    private ArrayList<Maintenance> getVehicleMaintenance(String url, Long id, String authToken) {
        ArrayList<Maintenance> maintenanceList = new ArrayList<Maintenance>();
        url = buildUserMaintenanceRequestUrl(url, id, authToken);
        JsonReader jsonReader = getUserMaintenanceData(url);

        try {
            maintenanceList = readUserMaintenanceJson(jsonReader);
        } catch (IOException ioe) {
            Log.d(TAG, ioe.getMessage());
        }
        return maintenanceList;
    }

    private String buildUserMaintenanceRequestUrl(String url, Long id, String token) {
        String segment = String.valueOf(id);
        url = String.format(url, segment);
        StringBuilder builder = new StringBuilder(url);
        builder.append("?session_hash=");
        builder.append(token);
        return builder.toString();
    }

    private JsonReader getUserMaintenanceData(String path) {
        JsonReader jsonReader = null;
        try {
            URL url = new URL(path);
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            jsonReader = new JsonReader(inputStreamReader);
            jsonReader.setLenient(true);
        } catch (IOException ioe) {
            Log.d(TAG, ioe.getMessage());
        }
        return jsonReader;
    }

    private ArrayList<Maintenance> readUserMaintenanceJson(JsonReader reader) throws IOException {
        ArrayList<Maintenance> maintenanceList = new ArrayList<Maintenance>();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                String status = reader.nextString();
                if (status.equals("error")) {
                    readError(reader);
                }
            } else if (name.equals("maintenance_plan") && reader.peek() == JsonToken.BEGIN_ARRAY) {
                maintenanceList = readMaintenancePlan(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return maintenanceList;
    }

    private ArrayList<Maintenance> readMaintenancePlan(JsonReader reader) throws IOException {
        ArrayList<Maintenance> maintenanceList = new ArrayList<Maintenance>();
        reader.beginArray();
        while (reader.hasNext()) {
            Maintenance maintenance = readMaintenance(reader);
            maintenanceList.add(maintenance);
        }
        reader.endArray();
        return maintenanceList;
    }

    private Maintenance readMaintenance(JsonReader reader) throws IOException {
        Maintenance maintenance = new Maintenance();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("name")) {
                maintenance.setMilestone(reader.nextString());
            } else if (name.equals("items")) {
                maintenance.setItems(readMaintenanceItems(reader));
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return maintenance;
    }

    private List<MaintenanceItem> readMaintenanceItems(JsonReader reader) throws IOException {
        List<MaintenanceItem> items = new ArrayList<MaintenanceItem>();
        reader.beginArray();
        while (reader.hasNext()) {
            items.add(readMaintenanceItem(reader));
        }
        reader.endArray();
        return items;
    }

    private MaintenanceItem readMaintenanceItem(JsonReader reader) throws IOException {
        MaintenanceItem item = new MaintenanceItem();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("name")) {
                item.setName(reader.nextString());
            } else if (name.equals("description") && reader.peek() != JsonToken.NULL) {
                item.setDescription(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return item;
    }
}
