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

package org.md2k.mcerebrum.jinsmeme.device.motionsense_hrv;

import com.orhanobut.logger.Logger;
import com.polidea.rxandroidble.RxBleConnection;

import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.mcerebrum.jinsmeme.Data;
import org.md2k.mcerebrum.jinsmeme.device.Characteristic;
import org.md2k.mcerebrum.jinsmeme.device.Sensor;

import java.util.ArrayList;
import java.util.UUID;

import rx.BackpressureOverflow;
import rx.Observable;
import rx.functions.Action0;

/**
 * Defines the battery characteristic of the device.
 */
public class CharacteristicBattery extends Characteristic {

    /**
     * Constructor
     */
    CharacteristicBattery() {
        super("00002A19-0000-1000-8000-00805f9b34fb", "CHARACTERISTIC_BATTERY", 25.0);
        //TODO fix frequency

    }

    /**
     * Returns an <code>Observable</code> over the data for this <code>Characteristic</code>.
     * @param rxBleConnection The BLE connection handle
     * @param sensors Arraylist of <code>Sensor</code>s
     * @return An <code>Observable</code> over the data for this <code>Characteristic</code>.
     */
    @Override
    public Observable<ArrayList<Data>> getObservable(RxBleConnection rxBleConnection, ArrayList<Sensor> sensors) {
        UUID uuid = UUID.fromString(getId());
        return rxBleConnection.setupNotification(uuid)
                .flatMap(notificationObservable -> notificationObservable)
                .onBackpressureBuffer(100, new Action0() {
                    @Override
                    public void call() {
                        Logger.e("CharacteristicBattery...Data Overflow occurs...after buffer... drop oldest packet");
                    }
                }, BackpressureOverflow.ON_OVERFLOW_DROP_OLDEST)
                .map(bytes -> {
                    DataTypeDoubleArray battery = new DataTypeDoubleArray(DateTime.getDateTime(), TranslateBattery.getBattery(bytes));
                    ArrayList<Data> arrayList = new ArrayList<>();
                    arrayList.add(new Data(sensors.get(0), battery));
                    return arrayList;
                });
    }
}
