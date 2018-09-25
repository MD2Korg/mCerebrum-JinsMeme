/*
 * Copyright (c) 2018, The University of Memphis, MD2K Center of Excellence
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.md2k.mcerebrum.jinsmeme.device.motionsense_hrv_plus;

import com.polidea.rxandroidble.RxBleConnection;

import org.md2k.mcerebrum.jinsmeme.Data;
import org.md2k.mcerebrum.jinsmeme.device.Characteristic;
import org.md2k.mcerebrum.jinsmeme.device.Device;
import org.md2k.mcerebrum.jinsmeme.device.Sensor;

import java.util.ArrayList;

import rx.Observable;

public class MotionSenseHRVPlus extends Device {
    private static final String DEVICE_NAME = "MotionSenseHRV+";

    /**
     * Constructor
     * @param deviceId Id of the device.
     */
    public MotionSenseHRVPlus(String deviceId) {
        super(deviceId);
    }

    /**
     * Returns whether the given device is the expected device.
     * @param name Name of the device.
     * @param serviceId Service id of the device.
     * @return Whether the given device is the expected device.
     */
    public static boolean is(String name, String serviceId){
        return DEVICE_NAME.equals(name) && UUID.equals(serviceId);
    }

    /**
     * Returns an <code>Observable</code> over the <code>Characteristic</code>s of the device.
     * @param rxBleConnection The BLE connection handle.
     * @return An <code>Observable</code> over the <code>Characteristic</code>s of the device.
     */
    @Override
    protected Observable<ArrayList<Data>> getCharacteristicsObservable(RxBleConnection rxBleConnection) {
        ArrayList<Observable<ArrayList<Data>>> list=new ArrayList<>();
        Characteristic cLed = new CharacteristicLed();
        Characteristic cMag = new CharacteristicMag();
        Characteristic cBat = new CharacteristicBattery();
        ArrayList<Sensor> sensorLed = getSensors(cLed, sensors);
        ArrayList<Sensor> sensorMag = getSensors(cMag, sensors);
        ArrayList<Sensor> sensorBat = getSensors(cBat, sensors);

        if(sensorLed != null)
            list.add(cLed.getObservable(rxBleConnection, sensorLed));
        if(sensorMag != null)
            list.add(cMag.getObservable(rxBleConnection, sensorMag));
        if(sensorBat != null)
            list.add(cBat.getObservable(rxBleConnection, sensorBat));
        return Observable.merge(list);
    }
}
