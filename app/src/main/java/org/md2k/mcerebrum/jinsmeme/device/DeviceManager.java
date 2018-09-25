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

package org.md2k.mcerebrum.jinsmeme.device;

import android.content.Context;

import com.orhanobut.logger.Logger;

import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.mcerebrum.jinsmeme.Data;
import org.md2k.mcerebrum.jinsmeme.ReceiveCallback;
import org.md2k.mcerebrum.jinsmeme.device.motionsense.MotionSense;
import org.md2k.mcerebrum.jinsmeme.device.motionsense_hrv.MotionSenseHRV;
import org.md2k.mcerebrum.jinsmeme.device.motionsense_hrv_plus.MotionSenseHRVPlus;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;

/**
 * Manages lists of <code>Device</code> objects.
 */
public class DeviceManager {
    private ArrayList<Device> devices;

    /**
     * Constructor
     */
    public DeviceManager() {
        devices = new ArrayList<>();
    }

    /**
     * Returns an <code>Observable</code> over every <code>Device</code> in the arraylist.
     * @param context Android context.
     * @return
     */
    public Observable<ArrayList<Data>> connect(Context context) {
        Logger.d("DeviceManager: connect()");
        return Observable.create((Subscriber<? super ArrayList<Data>> subscriber) -> {
            for (int i = 0; i < devices.size(); i++)
                devices.get(i).connect(context, new ReceiveCallback() {
                    /**
                     * Passes the received <code>Data</code> to <code>subscriber.onNext()</code>.
                     * @param t <code>Data</code> received.
                     */
                    @Override
                    public void onReceive(ArrayList<Data> t) {
                        subscriber.onNext(t);
                    }
                });
        });
    }

    /**
     * Adds the given sensor to the arraylist of <code>devices</code>.
     * @param sensor <code>Sensor</code> to add.
     */
    public void add(Sensor sensor) {
        Device device = getDevice(sensor.getDeviceId());
        if (device == null) {
            switch (sensor.getDeviceType()) {
                case PlatformType.MOTION_SENSE:
                    device = new MotionSense(sensor.getDeviceId());
                    break;
                case PlatformType.MOTION_SENSE_HRV:
                    device = new MotionSenseHRV(sensor.getDeviceId());
                    break;
                case PlatformType.MOTION_SENSE_HRV_PLUS:
                    device = new MotionSenseHRVPlus(sensor.getDeviceId());
                    break;
                default:
                    break;
            }
            if (device != null)
                devices.add(device);
            else return;
        }
        device.add(sensor);
    }

    /**
     * Disconnects all devices.
     */
    public void disconnect() {
        Logger.d("DeviceManager: disconnect()");
        for (int i = 0; i < devices.size(); i++)
            devices.get(i).disconnect();
    }

    /**
     * Returns a <code>Device</code> with the given id.
     * @param id Id to match.
     * @return A <code>Device</code> with the given id.
     */
    private Device getDevice(String id) {
        for (int i = 0; i < devices.size(); i++)
            if (devices.get(i).getDeviceId().equals(id))
                return devices.get(i);
        return null;
    }

}
