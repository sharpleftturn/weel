package com.weel.mobile.android.service;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.JsonReader;
import android.util.JsonToken;

import com.weel.mobile.android.model.User;
import com.weel.mobile.android.model.UserSession;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jeremy.beckman on 2015-10-07.
 */
public class AuthenticationService extends WeeLService {
    public static final String EXTRA_USER_DATA = "com.weel.mobile.android.extras.auth.USER_DATA";

    public AuthenticationService() {

    }

    public User addAccount(String url, String name, String username, String password, String version, @Nullable String code) throws IOException {
        Bundle data = new Bundle();
        StringBuilder builder = new StringBuilder(url);
        builder.append("/users");

        User user = requestAdd(builder.toString(), name, username, password, version, code);

        return user;
    }

    public User authenticate(String url, String username, String password) throws IOException {
        StringBuilder builder = new StringBuilder(url);
        builder.append("/users/authenticate");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", username));
        params.add(new BasicNameValuePair("password", password));

        User user = requestAuthentication(builder.toString(), params);
        return user;
    }

    public Boolean logout(String url, String authToken) throws IOException {
        StringBuilder builder = new StringBuilder(url);
        builder.append("/users/session/destroy");
        Boolean result = requestLogout(builder.toString(), authToken);
        return result;
    }

    public String getErrorMessage() {
        return error;
    }

    private User requestAdd(String requestUrl, String name, String email, String pwd, String version, @Nullable String code) throws IOException {
        User user = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        BasicNameValuePair emailPair = new BasicNameValuePair("email", email);
        params.add(emailPair);

        BasicNameValuePair passwordPair = new BasicNameValuePair("password", pwd);
        params.add(passwordPair);

        BasicNameValuePair namePair = new BasicNameValuePair("name", name);
        params.add(namePair);

        if (code != null) {
            BasicNameValuePair couponPair = new BasicNameValuePair("coupon_code", code);
            params.add(couponPair);
        }

        if (version != null) {
            BasicNameValuePair sourcePair = new BasicNameValuePair("signup_source", version);
            params.add(sourcePair);
        }

        JsonReader jsonReader = postData(requestUrl, params);
        user = readAddUserJson(jsonReader);

        return user;
    }

    private User requestAuthentication(String path, List<NameValuePair> params) throws IOException {
        User user = null;
        JsonReader jsonReader = postData(path, params);
        user = readAuthenticateJson(jsonReader);
        return user;
    }

    private Boolean requestLogout(String requestUrl, String authToken) throws IOException {
        Boolean result = false;

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("session_hash", authToken));

        JsonReader jsonReader = postData(requestUrl, params);

        result = readLogoutJson(jsonReader);

        return result;
    }

    private User readAddUserJson(JsonReader reader) throws IOException {
        User user = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                String status = reader.nextString();
                if (status.equals("error")) {
                    readError(reader);
                }
            } else if (name.equals("user") && reader.peek() == JsonToken.BEGIN_OBJECT) {
                user = readUser(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return user;
    }

    private User readAuthenticateJson(JsonReader reader) throws IOException {
        User user = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                String status = reader.nextString();
                if (status.equals("error")) {
                    readError(reader);
                }
            } else if (name.equals("user") && reader.peek() == JsonToken.BEGIN_OBJECT) {
                user = readUser(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return user;
    }

    private boolean readLogoutJson(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                String status = reader.nextString();
                if (status.equals("success")) {
                    return true;
                } else if (status.equals("error")) {
                    reader.skipValue();
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return false;
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
