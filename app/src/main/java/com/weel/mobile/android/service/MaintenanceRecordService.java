package com.weel.mobile.android.service;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import com.weel.mobile.android.model.ServiceAttachment;
import com.weel.mobile.android.model.ServiceRecord;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jeremy.beckman on 2016-02-09.
 */
public class MaintenanceRecordService extends WeeLService {

    public ServiceRecord addServiceRecord(String url, Long vehicleId, String description, String attachment, String authToken) {
        return requestAddServiceRecord(url, vehicleId, authToken, description, attachment);
    }

    public ArrayList<ServiceRecord> getServiceRecords(String url, Long id, String authToken) throws IOException {
        return getMaintenanceService(url, id, authToken);
    }

    private ArrayList<ServiceRecord> getMaintenanceService(String url, Long id, String authToken) throws IOException {
        ArrayList<ServiceRecord> serviceRecordList = new ArrayList<ServiceRecord>();
        url = buildMaintenanceServiceRequestUrl(url, id, authToken);
        JsonReader jsonReader = getUserMaintenanceData(url);
        serviceRecordList = readMaintenanceServiceJson(jsonReader);

        return serviceRecordList;
    }

    private String buildMaintenanceServiceRequestUrl(String url, Long id, String token) {
        String segment = String.valueOf(id);
        url = String.format(url, segment);
        StringBuilder builder = new StringBuilder(url);
        builder.append("?session_hash=");
        builder.append(token);
        return builder.toString();
    }

    private JsonReader getUserMaintenanceData(String path) throws IOException {
        JsonReader jsonReader = null;
        URL url = new URL(path);
        URLConnection connection = url.openConnection();
        InputStream inputStream = connection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        jsonReader = new JsonReader(inputStreamReader);
        jsonReader.setLenient(true);
        return jsonReader;
    }

    private JsonReader postServiceRecord(String url, Long id, String token, String description, String path) {
        JsonReader jsonReader = null;
        List<String> response = new ArrayList<String>();
        File file = new File(path);
        String hyphen = "--";
        String cr = "\r\n";
        String boundary = "========";
        try {
            URL rsUrl = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection) rsUrl.openConnection();
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(hyphen + boundary + cr);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"user_vehicle_id\"" + cr);
            outputStream.writeBytes("Content-Type: text/plain; charset=ISO-8859-1" + cr);
            outputStream.writeBytes(cr);
            outputStream.writeBytes(String.valueOf(id) + cr);
            outputStream.writeBytes(hyphen + boundary + cr);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"session_hash\"" + cr);
            outputStream.writeBytes("Content-Type: text/plain; charset=ISO-8859-1" + cr);
            outputStream.writeBytes(cr);
            outputStream.writeBytes(token + cr);
            outputStream.writeBytes(hyphen + boundary + cr);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"service_description\"" + cr);
            outputStream.writeBytes("Content-Type: text/plain; charset=ISO-8859-1" + cr);
            outputStream.writeBytes(cr);
            outputStream.writeBytes(description + cr);
            outputStream.flush();

            outputStream.writeBytes(hyphen + boundary + cr);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"userfile\"; filename=\"" + file.getName() + "\"");
            outputStream.writeBytes(cr);
            outputStream.writeBytes("Content-Type: " + URLConnection.guessContentTypeFromName(file.getName()));
            outputStream.writeBytes(cr);
            outputStream.writeBytes("Content-Transfer-Encoding: binary");
            outputStream.writeBytes(cr);
            outputStream.writeBytes(cr);

            FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();

            outputStream.writeBytes(cr);

            outputStream.writeBytes(cr);
            outputStream.writeBytes(hyphen + boundary + hyphen + cr);
            outputStream.flush();
            outputStream.close();

            int status = connection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
                jsonReader = new JsonReader(inputStreamReader);
                jsonReader.setLenient(true);
            }
        } catch (IOException ioe) {

        }
        return jsonReader;
    }

    private ServiceRecord requestAddServiceRecord(String url, Long id, String token, String desc, String path) {
        ServiceRecord serviceRecord = null;
        url = String.format(url, id);
        JsonReader jsonReader = postServiceRecord(url, id, token, desc, path);

        try {
            serviceRecord = readServiceRecordJson(jsonReader);
        } catch (IOException ioe) {

        }
        return serviceRecord;
    }

    private ArrayList<ServiceRecord> readMaintenanceServiceJson(JsonReader reader) throws IOException {
        ArrayList<ServiceRecord> recordList = new ArrayList<ServiceRecord>();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                String status = reader.nextString();
                if (status.equals("error")) {
                    readError(reader);
                }
            } else if (name.equals("maintenance_records") && reader.peek() == JsonToken.BEGIN_ARRAY) {
                recordList = readMaintenanceService(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return recordList;
    }

    private ArrayList<ServiceRecord> readMaintenanceService(JsonReader reader) throws IOException {
        ArrayList<ServiceRecord> recordList = new ArrayList<ServiceRecord>();
        reader.beginArray();
        while (reader.hasNext()) {
            ServiceRecord record = readServiceRecord(reader);
            recordList.add(record);
        }
        reader.endArray();
        return recordList;
    }

    private ServiceRecord readServiceRecordJson(JsonReader reader) throws IOException {ServiceRecord serviceRecord = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("maintenance_record")) {
                serviceRecord = readServiceRecord(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return serviceRecord;
    }

    private ServiceRecord readServiceRecord(JsonReader reader) throws IOException {
        ServiceRecord serviceRecord = new ServiceRecord();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id") && reader.peek() != JsonToken.NULL) {
                serviceRecord.setId(reader.nextLong());
            } else if (name.equals("user_vehicle_id")) {
                serviceRecord.setVehicleId(reader.nextLong());
            } else if (name.equals("service_description")) {
                serviceRecord.setDescription(reader.nextString());
            } else if (name.equals("service_cost") && reader.peek() != JsonToken.NULL
                    && reader.peek() == JsonToken.STRING) {
                String str = reader.nextString();
                if (!str.equals("")) {
                    serviceRecord.setCost(Double.valueOf(str));
                }
            } else if (name.equals("service_cost") && reader.peek() != JsonToken.NULL
                    && reader.peek() == JsonToken.NUMBER) {
                serviceRecord.setCost(reader.nextDouble());
            } else if (name.equals("service_location")) {
                serviceRecord.setLocation(reader.nextString());
            } else if (name.equals("service_date")) {
                try {
                    serviceRecord.setServiceDate(new SimpleDateFormat("yyyy-MM-dd").parse(reader.nextString()));
                } catch (ParseException pe) {

                }
            } else if (name.equals("processed")) {
                serviceRecord.setProcessed(reader.nextBoolean());
            } else if (name.equals("created")) {
                try {
                    serviceRecord.setCreated(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(reader.nextString()));
                } catch (ParseException pe) {

                }
            } else if (name.equals("last_updated")) {
                try {
                    serviceRecord.setLastUpdated(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(reader.nextString()));
                } catch (ParseException pe) {

                }
            } else if (name.equals("attachments")) {
                serviceRecord.setAttachments(readServiceAttachments(reader));
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return serviceRecord;
    }

    private List<ServiceAttachment> readServiceAttachments(JsonReader reader) throws IOException {
        List<ServiceAttachment> attachments = new ArrayList<ServiceAttachment>();
        reader.beginArray();
        while (reader.hasNext()) {
            attachments.add(readServiceAttachment(reader));
        }
        reader.endArray();
        return attachments;
    }

    private ServiceAttachment readServiceAttachment(JsonReader reader) throws IOException {
        ServiceAttachment attachment = new ServiceAttachment();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                attachment.setId(reader.nextLong());
            } else if (name.equals("user_id")) {
                attachment.setUserId(reader.nextLong());
            } else if (name.equals("file_name")) {
                attachment.setName(reader.nextString());
            } else if (name.equals("file_extension")) {
                attachment.setExtension(reader.nextString());
            } else if (name.equals("file_size")) {
                attachment.setSize(reader.nextLong());
            } else if (name.equals("file_full_path")) {
                attachment.setPath(reader.nextString());
            } else if (name.equals("attachment_type")) {
                attachment.setType(reader.nextString());
            } else if (name.equals("item_id")) {
                attachment.setItemId(reader.nextLong());
            } else if (name.equals("created")) {
                try {
                    attachment.setCreated(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(reader.nextString()));
                } catch (ParseException pe) {

                }
            } else if (name.equals("last_updated")) {
                try {
                    attachment.setLastUpdated(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(reader.nextString()));
                } catch (ParseException pe) {

                }
            } else if (name.equals("full_url")) {
                attachment.setFullUrl(reader.nextString());
            } else if (name.equals("thumbnail_url")) {
                attachment.setThumbnailUrl(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return attachment;
    }
}
