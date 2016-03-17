package com.weel.mobile.android.service;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Pair;

import com.weel.mobile.android.model.IncidentSource;
import com.weel.mobile.android.model.MechanicIncident;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeremy.beckman on 2016-03-01.
 */
public class MechanicService extends WeeLService {
    private static final String TAG = "MechanicService";

    public MechanicIncident addMechanicIncident(String url, long vehicleId, String authToken) {
        MechanicIncident incident = null;
        url = buildMechanicRequestUrl(url, vehicleId, authToken);
        try {
            incident = createIncident(url, vehicleId, authToken);
        } catch(IOException ioe) {

        }
        return incident;
    }

    private String buildMechanicRequestUrl(String url, long vehicleId, String authToken) {
        String segment = String.valueOf(vehicleId);
        url = String.format(url, segment);
        return url;
    }

    private MechanicIncident createIncident(String path, long vehicleId, String token) throws IOException {
        List<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
        params.add(new Pair("session_hash", token));
        params.add(new Pair("user_vehicle_id", String.valueOf(vehicleId)));

        JsonReader reader = createIncidentRequest(path, params);
        MechanicIncident incident = readIncidentJson(reader);
        return incident;
    }

    private JsonReader createIncidentRequest(String url, List<Pair<String, String>> params) {
        JsonReader jsonReader = postData(url, params);
        return jsonReader;
    }

    private MechanicIncident readIncidentJson(JsonReader reader) throws IOException {
        MechanicIncident incident = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                String status = reader.nextString();
                if (status.equals("error")) {
                    readError(reader);
                }
            } else if (name.equals("incident") && reader.peek() == JsonToken.BEGIN_OBJECT) {
                incident = readMechanicIncident(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return incident;
    }

    private MechanicIncident readMechanicIncident(JsonReader reader) throws IOException {
        MechanicIncident incident = new MechanicIncident();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id") && reader.peek() == JsonToken.NUMBER) {
                incident.setId(reader.nextLong());
            } else if (name.equals("call_rating") && reader.peek() == JsonToken.NUMBER) {
                incident.setRating(reader.nextInt());
            } else if (name.equals("call_feedback") && reader.peek() == JsonToken.STRING) {
                incident.setFeedback(reader.nextString());
            } else if (name.equals("weel_live_mechanic") && reader.peek() == JsonToken.BEGIN_OBJECT) {
                incident.setIncidentSource(readIncidentSource(reader));
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
            if (name.equals("program_name") && reader.peek() == JsonToken.STRING) {
                source.setProgram(reader.nextString());
            } else if (name.equals("description") && reader.peek() == JsonToken.STRING) {
                source.setDescription(reader.nextString());
            } else if (name.equals("phone_number") && reader.peek() == JsonToken.STRING) {
                source.setPhone(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return source;
    }
}
