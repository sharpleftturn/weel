package com.weel.mobile.android.service;

import android.support.annotation.Nullable;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Pair;

import com.weel.mobile.android.model.Make;
import com.weel.mobile.android.model.Model;
import com.weel.mobile.android.model.ModelYear;
import com.weel.mobile.android.model.Photo;
import com.weel.mobile.android.model.Vehicle;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jeremy.beckman on 2015-10-07.
 */
public class VehicleService extends WeeLService {
    public Vehicle addVehicle(Vehicle vehicle, String token, String url) {
        vehicle = requestAddVehicle(url, vehicle.getModel().getId(), vehicle.getMake().getId(), vehicle.getYearId(), vehicle.getVin(), vehicle.getName(), token);
        return vehicle;
    }

    public Vehicle getVehicleByVin(String vin, String token, String url) {
        return requestVehicleByVin(url, vin, token);
    }

    public ArrayList<Vehicle> getUserVehicles(String url, String token) {
        return requestUserVehicles(url, token);
    }

    public ArrayList<Make> getMakes(String url, String token) {
        return requestAllMakes(url, token);
    }

    public ArrayList<Model> getModels(String url, Long makeId, String token) {
        return requestAllModels(url, makeId, token);
    }

    public ArrayList<ModelYear> getModelYears(String url, Long modelId, String token) {
        return requestAllModelYears(url, modelId, token);
    }

    public Vehicle updateVehicle(String url, Vehicle vehicle, String token) {
        return requestUpdateVehicle(url, vehicle, token);
    }

    private Vehicle requestAddVehicle(String url, long model, long make, long year, @Nullable String vin, @Nullable String name, String authToken) {
        Vehicle vehicle = null;
        try {
            vehicle = addVehicle(url, model, make, year, vin, name, authToken);
        } catch (IOException ioe) {
        }
        return vehicle;
    }

    private Vehicle requestVehicleByVin(String url, String vin, String authToken) {
        Vehicle vehicle = null;
        url = buildVehicleByVinRequest(url, vin, authToken);
        try {
            vehicle = getVehicleByVin(url);
            vehicle.setVin(vin);
        } catch (IOException ioe) {
        }
        return vehicle;
    }

    private ArrayList<Vehicle> requestUserVehicles(String url, String authToken) {
        ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
        url = buildUserVehicleRequest(url, authToken);
        try {
            vehicles = getUserVehicles(url);
        } catch (IOException ioe) {
        }
        return vehicles;
    }

    private Vehicle requestUpdateVehicle(String url, Vehicle vehicle, String authToken) {
        url = buildUpdateVehicleRequest(url, vehicle, authToken);
        try {
            vehicle = updateUserVehicle(url, vehicle, authToken);
        } catch (IOException ioe) {
        }
        return vehicle;
    }

    private ArrayList<Make> requestAllMakes(String url, String authToken) {
        ArrayList<Make> makes = new ArrayList<Make>();
        url = buildMakesRequest(url, authToken);
        try {
            makes = getAllMakes(url);
        } catch (IOException ioe) {
        }
        return makes;
    }

    private ArrayList<Model> requestAllModels(String url, Long makeId, String authToken) {
        ArrayList<Model> models = new ArrayList<Model>();
        url = buildModelsRequest(url, makeId, authToken);
        try {
            models = getAllModels(url);
        } catch (IOException ioe) {
        }
        return models;
    }

    private ArrayList<ModelYear> requestAllModelYears(String url, Long modelId, String authToken) {
        ArrayList<ModelYear> modelYears = new ArrayList<ModelYear>();
        url = buildModelYearsRequest(url, modelId, authToken);
        try {
            modelYears = getAllModelYears(url);
        } catch (IOException ioe) {
        }
        return modelYears;
    }

    private String buildVehicleByVinRequest(String url, String vin, String token) {
        StringBuilder builder = new StringBuilder(url);
        builder.append("/");
        builder.append(vin);
        builder.append("?session_hash=");
        builder.append(token);
        return builder.toString();
    }

    private String buildUserVehicleRequest(String url, String token) {
        StringBuilder builder = new StringBuilder(url);
        builder.append("?session_hash=");
        builder.append(token);
        return builder.toString();
    }

    private String buildUpdateVehicleRequest(String url, Vehicle vehicle, String token) {
        StringBuilder builder = new StringBuilder(url);
        builder.append("/");
        builder.append(vehicle.getId());
        return builder.toString();
    }

    private String buildMakesRequest(String url, String token) {
        StringBuilder builder = new StringBuilder(url);
        builder.append("?session_hash=");
        builder.append(token);
        return builder.toString();
    }

    private String buildModelsRequest(String url, Long id, String token) {
        String segment = String.valueOf(id);
        url = String.format(url, segment);
        StringBuilder builder = new StringBuilder(url);
        builder.append("?session_hash=");
        builder.append(token);
        return builder.toString();
    }

    private String buildModelYearsRequest(String url, Long id, String token) {
        String segment = String.valueOf(id);
        url = String.format(url, segment);
        StringBuilder builder = new StringBuilder(url);
        builder.append("?session_hash=");
        builder.append(token);
        return builder.toString();
    }

    private Vehicle addVehicle(String url, long model, long make, long year, @Nullable String vin, @Nullable String name, String token) throws IOException {
        List<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
        params.add(new Pair("session_hash", token));
        params.add(new Pair("model_id", String.valueOf(model)));
        params.add(new Pair("make_id", String.valueOf(make)));
        params.add(new Pair("model_year_id", String.valueOf(year)));

        if (vin != null) {
            params.add(new Pair("vin", vin));
        }

        if (name != null) {
            params.add(new Pair("name", name));
        }

        JsonReader jsonReader = postData(url, params);
        return readAddVehicleJson(jsonReader);
    }

    private ArrayList<Vehicle> getUserVehicles(String url) throws IOException {
        JsonReader jsonReader = getData(url);
        return readUserVehiclesJson(jsonReader);
    }

    private Vehicle getVehicleByVin(String url) throws IOException {
        JsonReader jsonReader = getData(url);
        return readVehicleByVinJson(jsonReader);
    }

    private Vehicle updateUserVehicle(String url, Vehicle vehicle, String token) throws IOException {
        List<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
        params.add(new Pair(" user_vehicle_id", String.valueOf(vehicle.getId())));
        params.add(new Pair("session_hash", token));
        params.add(new Pair("user_vehicle[name]", vehicle.getName()));
        params.add(new Pair("user_vehicle[vin]", vehicle.getVin()));
        params.add(new Pair("user_vehicle[odometer_value]", String.valueOf(vehicle.getOdometer())));
        params.add(new Pair("user_vehicle[annual_mileage]", String.valueOf(vehicle.getAnnualDistance())));
        params.add(new Pair("user_vehicle[status_when_purchased]", vehicle.getOwnership()));

        Date inServiceDate = vehicle.getInServiceDate();
        if (inServiceDate != null) {
            params.add(new Pair("user_vehicle[in_service_date]", inServiceDate.toString()));
        }
        JsonReader jsonReader = postData(url, params);
        return readUpdateVehicleJson(jsonReader);
    }

    private ArrayList<Make> getAllMakes(String url) throws IOException {
        JsonReader jsonReader = getData(url);
        return readAllMakesJson(jsonReader);
    }

    private ArrayList<Model> getAllModels(String url) throws IOException {
        JsonReader jsonReader = getData(url);
        return readAllModelsJson(jsonReader);
    }

    private ArrayList<ModelYear> getAllModelYears(String url) throws IOException {
        JsonReader jsonReader = getData(url);
        return readAllModelYearsJson(jsonReader);
    }

    private Vehicle readAddVehicleJson(JsonReader reader) throws IOException {
        Vehicle vehicle = new Vehicle();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                String status = reader.nextString();
                if (status.equals("error")) {
                    readError(reader);
                }
            } else if (name.equals("user_vehicle") && reader.peek() == JsonToken.BEGIN_OBJECT) {
                vehicle = readVehicle(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return vehicle;
    }

    private ArrayList<Vehicle> readUserVehiclesJson(JsonReader reader) throws IOException {
        ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                String status = reader.nextString();
                if (status.equals("error")) {
                    readError(reader);
                }
            } else if (name.equals("user_vehicles") && reader.peek() == JsonToken.BEGIN_ARRAY) {
                vehicles = readUserVehicles(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return vehicles;
    }

    private Vehicle readVehicleByVinJson(JsonReader reader) throws IOException {
        Vehicle vehicle = new Vehicle();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                String status = reader.nextString();
                if (!status.equals("success")) {
                    return null;
                }
            } else if (name.equals("vin_results")) {
                reader.beginObject();
                while (reader.hasNext()) {
                    name = reader.nextName();
                    if (name.equals("make")) {
                        Make make = readMake(reader);
                        vehicle.setMake(make);
                    } else if (name.equals("model")) {
                        Model model = readModel(reader);
                        vehicle.setModel(model);
                    } else if (name.equals("model_year")) {
                        vehicle = readModelYear(reader, vehicle);
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return vehicle;
    }

    private Vehicle readUpdateVehicleJson(JsonReader reader) throws IOException {
        Vehicle vehicle = new Vehicle();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                String status = reader.nextString();
                if (status.equals("error")) {
                    readError(reader);
                }
            } else if (name.equals("user_vehicle") && reader.peek() == JsonToken.BEGIN_OBJECT) {
                vehicle = readUserVehicle(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return vehicle;
    }

    private ArrayList<Make> readAllMakesJson(JsonReader reader) throws IOException {
        ArrayList<Make> makes = new ArrayList<Make>();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                String status = reader.nextString();
                if (status.equals("error")) {
                    readError(reader);
                }
            } else if (name.equals("makes") && reader.peek() == JsonToken.BEGIN_ARRAY) {
                makes = readMakes(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return makes;
    }

    private ArrayList<Model> readAllModelsJson(JsonReader reader) throws IOException {
        ArrayList<Model> models = new ArrayList<Model>();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                String status = reader.nextString();
                if (status.equals("error")) {
                    readError(reader);
                }
            } else if (name.equals("models") && reader.peek() == JsonToken.BEGIN_ARRAY) {
                models = readModels(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return models;
    }

    private ArrayList<ModelYear> readAllModelYearsJson(JsonReader reader) throws IOException {
        ArrayList<ModelYear> modelYears = new ArrayList<ModelYear>();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                String status = reader.nextString();
                if (status.equals("error")) {
                    readError(reader);
                }
            } else if (name.equals("model_years") && reader.peek() == JsonToken.BEGIN_ARRAY) {
                modelYears = readModelYears(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return modelYears;
    }

    private ArrayList<Vehicle> readUserVehicles(JsonReader reader) throws IOException {
        ArrayList<Vehicle> list = new ArrayList<Vehicle>();
        reader.beginArray();
        while (reader.hasNext()) {
            list.add(readVehicle(reader));
        }
        reader.endArray();
        return list;
    }

    private Vehicle readVehicle(JsonReader reader) throws IOException {
        Vehicle vehicle = new Vehicle();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                vehicle.setId(reader.nextLong());
            } else if (name.equals("name")) {
                vehicle.setName(reader.nextString());
            } else if (name.equals("vin")) {
                vehicle.setVin(reader.nextString());
            } else if (name.equals("odometer_value") && reader.peek() == JsonToken.NUMBER) {
                vehicle.setOdometer(reader.nextInt());
            } else if (name.equals("annual_mileage") && reader.peek() == JsonToken.NUMBER) {
                vehicle.setAnnualDistance(reader.nextInt());
            } else if (name.equals("status_when_purchased") && reader.peek() == JsonToken.STRING) {
                vehicle.setOwnership(reader.nextString());
            } else if (name.equals("photo") && reader.peek() == JsonToken.BEGIN_OBJECT) {
                Photo photo = readPhoto(reader);
                vehicle.setPhoto(photo);
            } else if (name.equals("active")) {
                vehicle.setActive(reader.nextBoolean());
            } else if (name.equals("created")) {
                try {
                    vehicle.setCreated(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(reader.nextString()));
                } catch (ParseException pe) {
                }
            } else if (name.equals("last_updated")) {
                try {
                    vehicle.setLastUpdated(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(reader.nextString()));
                } catch (ParseException pe) {
                }
            } else if (name.equals("model_year")) {
                vehicle = readModelYear(reader, vehicle);
            } else if (name.equals("model")) {
                Model model = readModel(reader);
                vehicle.setModel(model);
            } else if (name.equals("make")) {
                Make make = readMake(reader);
                vehicle.setMake(make);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return vehicle;
    }

    private ArrayList<Make> readMakes(JsonReader reader) throws IOException {
        ArrayList<Make> makes = new ArrayList<Make>();
        reader.beginArray();
        while (reader.hasNext()) {
            makes.add(readMake(reader));
        }
        reader.endArray();
        return makes;
    }

    private Make readMake(JsonReader reader) throws IOException {
        Make make = new Make();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                make.setId(reader.nextLong());
            } else if (name.equals("nice_name")) {
                make.setName(reader.nextString());
            } else if (name.equals("name")) {
                make.setDisplayName(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return make;
    }

    private ArrayList<Model> readModels(JsonReader reader) throws IOException {
        ArrayList<Model> models = new ArrayList<Model>();
        reader.beginArray();
        while (reader.hasNext()) {
            models.add(readModel(reader));
        }
        reader.endArray();
        return models;
    }

    private Model readModel(JsonReader reader) throws IOException {
        Model model = new Model();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                model.setId(reader.nextLong());
            } else if (name.equals("nice_name")) {
                model.setName(reader.nextString());
            } else if (name.equals("name")) {
                model.setDisplayName(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return model;
    }

    private Vehicle readModelYear(JsonReader reader, Vehicle vehicle) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("year")) {
                vehicle.setYear(reader.nextInt());
            } else if (name.equals("id")) {
                vehicle.setYearId(reader.nextLong());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return vehicle;
    }

    private ModelYear readModelYear(JsonReader reader) throws IOException {
        ModelYear modelYear = new ModelYear();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("year")) {
                modelYear.setModelYear(reader.nextInt());
            } else if (name.equals("id")) {
                modelYear.setId(reader.nextLong());
            } else if (name.equals("model_id")) {
                modelYear.setModelId(reader.nextLong());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return modelYear;
    }

    private ArrayList<ModelYear> readModelYears(JsonReader reader) throws IOException {
        ArrayList<ModelYear> modelYears = new ArrayList<ModelYear>();
        reader.beginArray();
        while (reader.hasNext()) {
            modelYears.add(readModelYear(reader));
        }
        reader.endArray();
        return modelYears;
    }

    private Vehicle readUserVehicle(JsonReader reader) throws IOException {
        Vehicle vehicle = new Vehicle();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                vehicle.setId(reader.nextLong());
            } else if (name.equals("name")) {
                vehicle.setName(reader.nextString());
            } else if (name.equals("make_id")) {
                vehicle.getMake().setId(reader.nextLong());
            } else if (name.equals("model_id")) {
                vehicle.getModel().setId(reader.nextLong());
            } else if (name.equals("model_year")) {
                reader.beginObject();
                while (reader.hasNext()) {
                    name = reader.nextName();
                    if (name.equals("year")) {
                        vehicle.setYear(reader.nextInt());
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
            } else if (name.equals("vin")) {
                vehicle.setVin(reader.nextString());

            } else if (name.equals("odometer_value")) {
                vehicle.setOdometer(reader.nextInt());
            } else if (name.equals("annual_mileage")) {
                vehicle.setAnnualDistance(reader.nextInt());
            } else if (name.equals("status_when_purchased")) {
                vehicle.setOwnership(reader.nextString());
            } else if (name.equals("photo") && reader.peek() == JsonToken.BEGIN_OBJECT) {
                Photo photo = readPhoto(reader);
                vehicle.setPhoto(photo);
            } else if (name.equals("active")) {
                vehicle.setActive(reader.nextBoolean());
            } else if (name.equals("created")) {
                try {
                    vehicle.setCreated(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(reader.nextString()));
                } catch (ParseException pe) {
                }
            } else if (name.equals("last_updated")) {
                try {
                    vehicle.setLastUpdated(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(reader.nextString()));
                } catch (ParseException pe) {
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return vehicle;
    }

    private Photo readPhoto(JsonReader reader) throws IOException {
        Photo photo = new Photo();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                photo.setId(reader.nextLong());
            } else if (name.equals("thumbnail_url")) {
                photo.setThumbnailUrl(reader.nextString());
            } else if (name.equals("full_url")) {
                photo.setFullUrl(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return photo;
    }
}
