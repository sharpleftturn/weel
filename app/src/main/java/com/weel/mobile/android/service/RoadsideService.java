package com.weel.mobile.android.service;

import android.support.annotation.Nullable;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import com.weel.mobile.android.model.IncidentSource;
import com.weel.mobile.android.model.RoadsideIncident;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeremy.beckman on 2016-01-28.
 */
public class RoadsideService extends WeeLService {
    private static final String TAG = "RoadsideService";

    public RoadsideIncident getRoadsideIncident(String url, long vehicleId, String authToken) {

        return null;
    }

    public RoadsideIncident addRoadsideIncident(String url, long vehicleId, String authToken, @Nullable Double latitude, @Nullable Double longitude, String type) {
        RoadsideIncident incident = null;
        url = buildAddRoadsideRequestUrl(url, vehicleId);
        try {
            incident = createRoadsideIncident(url, vehicleId, authToken, latitude, longitude, type);
        } catch (IOException ioe) {
            Log.d(TAG, ioe.getMessage());
        }
        return incident;
    }

    private String buildAddRoadsideRequestUrl(String url, long vehicleId) {
        String segment = String.valueOf(vehicleId);
        url = String.format(url, segment);
        return url;
    }

    private RoadsideIncident createRoadsideIncident(String url, long vehicleId, String token, @Nullable Double latitude, @Nullable Double longitude, String type) throws IOException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_vehicle_id", String.valueOf(vehicleId)));
        params.add(new BasicNameValuePair("session_hash", token));
        params.add(new BasicNameValuePair("type", type));

        if (latitude != null) {
            params.add(new BasicNameValuePair("latitude", String.valueOf(latitude)));
        }

        if (longitude != null) {
            params.add(new BasicNameValuePair("longitude", String.valueOf(longitude)));
        }

        JsonReader jsonReader = postData(url, params);
        return readAddRoadsideIncident(jsonReader);
    }


    private RoadsideIncident readAddRoadsideIncident(JsonReader reader) throws IOException {
        RoadsideIncident incident = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                String status = reader.nextString();
                if (status.equals("error")) {
                    readError(reader);
                }
            } else if (name.equals("incident")) {
                incident = readRoadsideIncident(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return incident;
    }

    private RoadsideIncident readRoadsideIncident(JsonReader reader) throws IOException {
        RoadsideIncident incident = new RoadsideIncident();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                incident.setId(reader.nextLong());
            } else if (name.equals("address")) {
                incident.setAddress(reader.nextString());
            } else if (name.equals("latitude") && reader.peek() != JsonToken.NULL) {
                incident.setLatitude(reader.nextDouble());
            } else if (name.equals("longitude") && reader.peek() != JsonToken.NULL) {
                incident.setLongitude(reader.nextDouble());
            } else if (name.equals("type")) {
                incident.setType(reader.nextString());
            } else if (name.equals("routing") && reader.peek() != JsonToken.NULL) {
                incident.setRouting(reader.nextString());
            } else if (name.equals("call_rating") && reader.peek() != JsonToken.NULL) {
                incident.setRating(reader.nextInt());
            } else if (name.equals("call_feedback") && reader.peek() != JsonToken.NULL) {
                incident.setFeedback(reader.nextString());
            } else if (name.equals("weel_roadside") && reader.peek() != JsonToken.NULL) {
                incident.getSource().add(readIncidentSource(reader));
            } else if (name.equals("oem_roadside") && reader.peek() != JsonToken.NULL) {
                incident.getSource().add(readIncidentSource(reader));
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return incident;
    }

    private IncidentSource readIncidentSource(JsonReader reader) throws IOException {
        IncidentSource source = new IncidentSource();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("program_name")) {
                source.setProgram(reader.nextString());
            } else if (name.equals("description")) {
                source.setDescription(reader.nextString());
            } else if (name.equals("phone_number")) {
                source.setPhone(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return source;
    }
}
