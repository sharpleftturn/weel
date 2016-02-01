package com.weel.mobile.android.service;

import android.util.JsonReader;
import android.util.JsonToken;

import com.weel.mobile.android.model.User;
import com.weel.mobile.android.model.UserSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jeremy.beckman on 2015-10-21.
 */
public class AccountService extends WeeLService {

    public User getUser(String url, String token) {
        User user = null;
        StringBuilder builder = new StringBuilder(url);
        builder.append("?");
        builder.append("session_hash=");
        builder.append(token);
        try {
            user = requestUser(builder.toString());
        } catch (IOException ioe) {

        }
        return user;
    }

    private User requestUser(String requestUrl) throws IOException {
        User user = null;
        URL url = new URL(requestUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            JsonReader jsonReader = new JsonReader(inputStreamReader);
            jsonReader.setLenient(true);
            user = readUserJson(jsonReader);
        }
        return user;
    }

    private User readUserJson(JsonReader reader) throws IOException {
        User user = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("user")) {
                user = readUser(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return user;
    }

    private User readUser(JsonReader reader) throws IOException {
        User user = new User();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                user.setId(reader.nextLong());
            } else if (name.equals("name")) {
                user.setFullname(reader.nextString());
            } else if (name.equals("session")) {
                user.setSession(readUserSession(reader));
            } else if (name.equals("first_name")) {
                user.setFirstname(reader.nextString());
            } else if (name.equals("created")) {
                try {
                    user.setCreated(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(reader.nextString()));
                } catch (ParseException pe) {

                }
            } else if (name.equals("postal_code") && reader.peek() != JsonToken.NULL) {
                user.setPostalCode(reader.nextString());
            } else if (name.equals("email")) {
                user.setEmail(reader.nextString());
            } else if (name.equals("unit_of_measure")) {
                user.setUom(reader.nextString());
            } else if (name.equals("verified")) {
                user.setActive(reader.nextBoolean());
            } else {
                reader.skipValue();
            }
        }
        return user;
    }

    private UserSession readUserSession(JsonReader reader) throws IOException {
        UserSession session = new UserSession();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("session_hash")) {
                session.setSession(reader.nextString());
            } else if (name.equals("expires")) {
                try {
                    session.setExpires(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(reader.nextString()));
                } catch (ParseException pe) {
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return session;
    }
}
