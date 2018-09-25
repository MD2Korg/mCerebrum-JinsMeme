package org.md2k.mcerebrum.jinsmeme.device.data_quality;
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

import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.datatype.DataTypeInt;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.mcerebrum.jinsmeme.Data;
import org.md2k.mcerebrum.jinsmeme.device.Sensor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.functions.Func1;

/**
 * Manages a hashmap for <code>DataQuality</code> objects and a hashmap for <code>Sensor</code> objects.
 */
public class DataQualityManager {
    private HashMap<String, DataQuality> dataQualityHashMap;
    private HashMap<String, Sensor> sensorHashMap;

    /**
     * Constructor
     */
    public DataQualityManager() {
        dataQualityHashMap = new HashMap<>();
        sensorHashMap = new HashMap<>();
    }

    /**
     * Returns the <code>Observable</code>s for the <code>DataQualityManager</code>.
     * @return The <code>Observable</code>s for the <code>DataQualityManager</code>.
     */
    public Observable<ArrayList<Data>> getObservable() {
        ArrayList<Observable<Data>> observables = new ArrayList<>();
        for (Map.Entry<String, DataQuality> entry : dataQualityHashMap.entrySet()) {
            String key = entry.getKey();
            observables.add(entry.getValue().start(sensorHashMap.get(key)));
        }
        return Observable.merge(observables).map(new Func1<Data, ArrayList<Data>>() {
            @Override
            public ArrayList<Data> call(Data data) {
                ArrayList<Data> a = new ArrayList<>();
                a.add(data);
                return a;
            }
        });
    }

    /**
     * Adds the given <code>Sensor</code> to the <code>sensorHashMap</code>.
     * @param sensor <code>Sensor</code> to add.
     */
    public void addSensor(Sensor sensor) {
        if (!isValidSensor(sensor))
            return;
        String name = sensor.getDeviceType() + sensor.getDeviceId() + sensor.getDataSourceId();
        if (!dataQualityHashMap.containsKey(name)) {
            dataQualityHashMap.put(name, getDataQuality(sensor));
            sensorHashMap.put(name, sensor);
        }
    }

    /**
     * Adds the given <code>Data</code> to the <code>dataQualityHashMap</code>.
     * @param data <code>Data</code> to add.
     */
    public void addData(Data data) {
        String name = data.getSensor().getDeviceType() + data.getSensor().getDeviceId() +
                data.getSensor().getDataSourceType();
        if (!dataQualityHashMap.containsKey(name))
            return;
        dataQualityHashMap.get(name).add((DataTypeDoubleArray) data.getDataType());
    }

    /**
     * Returns the appropriate <code>DataQuality</code> object for the given <code>Sensor</code>.
     * @param sensor <code>Sensor</code> to get the <code>DataQuality</code> for.
     * @return The appropriate <code>DataQuality</code> object for the given <code>Sensor</code>.
     */
    private DataQuality getDataQuality(Sensor sensor) {
        switch (sensor.getDataSourceId()) {
            case DataSourceType.ACCELEROMETER:
                return new DataQualityAccelerometer();
            case DataSourceType.LED:
                return new DataQualityLed();
            default:
                return null;
        }
    }

    /**
     * Returns whether the given <code>Sensor</code> is valid.
     * @param sensor <code>Sensor</code> to validate.
     * @return Whether the given <code>Sensor</code> is valid.
     */
    private boolean isValidSensor(Sensor sensor) {
        if (sensor.getDataSourceId() == null || sensor.getDataSourceType() == null ||
                sensor.getDeviceType() == null || sensor.getDeviceId() == null)
            return false;
        if (!sensor.getDataSourceType().equals(DataSourceType.DATA_QUALITY))
            return false;
        return !(!sensor.getDataSourceId().equals(DataSourceType.ACCELEROMETER) &&
                !sensor.getDataSourceId().equals(DataSourceType.LED));
    }

    /**
     * Returns a summary of the given data.
     * @param data Data to process.
     * @return A summary of the given data.
     */
    public DataType getSummary(Data data) {
        if (!isValidSensor(data.getSensor()))
            return null;
        String name = data.getSensor().getDeviceType() + data.getSensor().getDeviceId() +
                data.getSensor().getDataSourceId();
        if (!dataQualityHashMap.containsKey(name))
            return null;
        return dataQualityHashMap.get(name).getSummary((DataTypeInt) data.getDataType());

    }
}
