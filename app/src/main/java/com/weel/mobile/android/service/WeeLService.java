package com.weel.mobile.android.service;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Pair;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jeremy.beckman on 2015-10-07.
 */
public class WeeLService {
    private static final String TAG = "WeeLService";

    protected boolean hasError;
    protected String error;
    protected String message;

    public boolean hasError() {
        return hasError;
    }

    public String getErrorMessage() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public void readError(JsonReader reader) throws IOException {
        String name = reader.nextName();
        if (name.equals("message")) {
            error = reader.nextString();
        } else {
            reader.skipValue();
        }
        this.hasError = true;
    }

    public void readSystemError(String message) {
        error = message;
        this.hasError = true;
    }

    public void readMessage(JsonReader reader) throws IOException {
        String name = reader.nextName();
        if (name.equals("message")) {
            message = reader.nextString();
        } else {
            reader.skipValue();
        }
    }

    protected JsonReader getData(String path) {
        JsonReader jsonReader = null;
        try {
            URL url = new URL(path);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            jsonReader = new JsonReader(inputStreamReader);
            jsonReader.setLenient(true);
        } catch (IOException ioe) {
            readSystemError(ioe.getMessage());
        }
        return jsonReader;
    }

    protected JsonReader postData(String path, List<Pair<String, String>> params) {
        JsonReader jsonReader = null;
        try {
            URL url = new URL(path);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Cache-Control", "no-cache");

            StringBuilder builder = new StringBuilder();
            for (Pair pair : params) {
                builder.append(URLEncoder.encode((String) pair.first, "UTF-8"));
                builder.append("=");
                builder.append(URLEncoder.encode((String) pair.second, "UTF-8"));
                builder.append("&");
            }
            builder.deleteCharAt(builder.length() - 1);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream), true);
            writer.write(builder.toString());
            writer.flush();
            writer.close();

            int status = connection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
                jsonReader = new JsonReader(inputStreamReader);
                jsonReader.setLenient(true);
            }
        } catch (IOException ioe) {
            readSystemError(ioe.getMessage());
        }
        return jsonReader;
    }
}
