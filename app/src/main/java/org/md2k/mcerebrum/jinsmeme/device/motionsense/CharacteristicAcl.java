package org.md2k.mcerebrum.jinsmeme.device.motionsense;
/*
 * Copyright (c) 2016, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
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

import com.orhanobut.logger.Logger;
import com.polidea.rxandroidble.RxBleConnection;

import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.mcerebrum.jinsmeme.Data;
import org.md2k.mcerebrum.jinsmeme.device.Characteristic;
import org.md2k.mcerebrum.jinsmeme.device.Sensor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import rx.BackpressureOverflow;
import rx.Observable;
import rx.functions.Action0;

/**
 * Defines the accelerometer characteristic of the device.
 */
public class CharacteristicAcl extends Characteristic {
    private HashMap<String, Sensor> listSensor;

    /**
     * Constructor
     */
    CharacteristicAcl() {
        super("da39c921-1d81-48e2-9c68-d0ae4bbd351f", "CHARACTERISTIC_ACCELEROMETER", 25.0);
        //TODO: fix frequency
    }

    /**
     * Returns the <code>Observable</code> created in <code>setNotify()</code>.
     * @param rxBleConnection The BLE connection handle
     * @param sensors Arraylist of <code>Sensor</code>s
     * @return The <code>Observable</code> created in <code>setNotify()</code>.
     */
    @Override
    public Observable<ArrayList<Data>> getObservable(RxBleConnection rxBleConnection, ArrayList<Sensor> sensors) {
        prepareList(sensors);
        return setNotify(rxBleConnection);
    }

    /**
     * Prepares a hashmap of <code>Sensor</code>s and their <code>DataSourceType</code>.
     * @param sensors List of <code>Sensor</code>s to add.
     */
    private void prepareList(ArrayList<Sensor> sensors) {
        listSensor = new HashMap<>();
        for (Sensor sensor : sensors) {
            String t = sensor.getDataSourceType();
            if (sensor.getDataSourceId() != null)
                t += sensor.getDataSourceId();
            listSensor.put(t, sensor);
        }
    }

    /**
     * Returns an <code>Observable</code> to create notifications for accelerometer data.
     * @param rxBleConnection The BLE connection handle.
     * @return An <code>Observable</code> to create notifications for accelerometer data.
     */
    private Observable<ArrayList<Data>> setNotify(RxBleConnection rxBleConnection) {
        UUID uuid = UUID.fromString(getId());
        return rxBleConnection.setupNotification(uuid)
                .flatMap(notificationObservable -> notificationObservable)
                .onBackpressureBuffer(100, new Action0() {
                    @Override
                    public void call() {
                        Logger.e("CharactericsicACL...Data Overflow occurs...after buffer... drop oldest packet");
                    }
                }, BackpressureOverflow.ON_OVERFLOW_DROP_OLDEST)

                .map(bytes -> {
                    ArrayList<Data> data = new ArrayList<>();
                    int curSeq = (int) TranslateAcl.getSequenceNumber(bytes)[0];
                    long curTime = correctTimeStamp(curSeq,65636);
                    if (listSensor.containsKey(DataSourceType.ACCELEROMETER)) {
                        DataType d = new DataTypeDoubleArray(curTime, TranslateAcl.getAccelerometer(bytes));
                        data.add(new Data(listSensor.get(DataSourceType.ACCELEROMETER), d));
                    }
                    if (listSensor.containsKey(DataSourceType.GYROSCOPE)) {
                        DataType d = new DataTypeDoubleArray((long) (curTime - 1000.0 / (2.0 * frequency)), TranslateAcl.getGyroscope1(bytes));
                        data.add(new Data(listSensor.get(DataSourceType.GYROSCOPE), d));
                        d = new DataTypeDoubleArray(curTime, TranslateAcl.getGyroscope2(bytes));
                        data.add(new Data(listSensor.get(DataSourceType.GYROSCOPE), d));
                    }
                    if (listSensor.containsKey(DataSourceType.SEQUENCE_NUMBER + getName())) {
                        DataType d = new DataTypeDoubleArray(curTime, TranslateAcl.getSequenceNumber(bytes));
                        data.add(new Data(listSensor.get(DataSourceType.SEQUENCE_NUMBER + getName()), d));
                    }
                    if (listSensor.containsKey(DataSourceType.RAW + getName())) {
                        DataType d = new DataTypeDoubleArray(curTime, TranslateAcl.getRaw(bytes));
                        data.add(new Data(listSensor.get(DataSourceType.RAW + getName()), d));
                    }
                    lastSequence = curSeq;
                    lastTimestamp = curTime;
                    return data;
                });
    }
}
