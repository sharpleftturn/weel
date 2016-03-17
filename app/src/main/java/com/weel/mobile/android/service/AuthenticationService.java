package com.weel.mobile.android.service;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Pair;

import com.weel.mobile.android.model.User;
import com.weel.mobile.android.model.UserSession;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeremy.beckman on 2015-10-07.
 */
public class AuthenticationService extends WeeLService {
    public static final String EXTRA_USER_DATA = "com.weel.mobile.android.extras.auth.USER_DATA";

    public AuthenticationService() {
        super();
    }

    public User addAccount(String url, String name, String username, String password, String version, @Nullable String code) throws IOException {
        User user = requestAdd(url, name, username, password, version, code);
        return user;
    }

    public User authenticate(String url, String username, String password) throws IOException {
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(new Pair("email", username));
        params.add(new Pair("password", password));

        User user = requestAuthentication(url, params);
        return user;
    }

    public Boolean logout(String url, String authToken) throws IOException {
        Boolean result = requestLogout(url, authToken);
        return result;
    }

    public String getErrorMessage() {
        return error;
    }

    private User requestAdd(String requestUrl, String name, String email, String pwd, String version, @Nullable String code) throws IOException {
        User user = null;
        List<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
        Pair emailPair = new Pair("email", email);
        params.add(emailPair);

        Pair passwordPair = new Pair("password", pwd);
        params.add(passwordPair);

        Pair namePair = new Pair("name", name);
        params.add(namePair);

        if (code != null) {
            Pair couponPair = new Pair("coupon_code", code);
            params.add(couponPair);
        }

        if (version != null) {
            Pair sourcePair = new Pair("signup_source", version);
            params.add(sourcePair);
        }

        JsonReader jsonReader = postData(requestUrl, params);
        user = readAddUserJson(jsonReader);

        return user;
    }

    private User requestAuthentication(String path, List<Pair<String, String>> params) throws IOException {
        User user = null;
        JsonReader jsonReader = postData(path, params);
        user = readAuthenticateJson(jsonReader);
        return user;
    }

    private Boolean requestLogout(String requestUrl, String authToken) throws IOException {
        Boolean result = false;

        List<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
        params.add(new Pair("session_hash", authToken));

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
