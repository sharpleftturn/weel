package com.weel.mobile.android.service;

import com.weel.mobile.android.model.Make;
import com.weel.mobile.android.model.Model;
import com.weel.mobile.android.model.ModelYear;
import com.weel.mobile.android.model.Vehicle;

import org.junit.Test;

/**
 * Created by jeremy.beckman on 2016-01-29.
 */
public class VehicleServiceTest {

    @Test
    public void addVehicle_ValidVehicle_ReturnsVehicle() {
        Model model = new Model();
        model.setId(677);

        Make make = new Make();
        make.setId(38);

        Vehicle vehicle = new Vehicle();
        vehicle.setModel(model);
        vehicle.setMake(make);
        vehicle.setYearId(4748L);

        VehicleService vehicleService = new VehicleService();

    }

}
