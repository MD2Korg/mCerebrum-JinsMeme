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

package org.md2k.mcerebrum.jinsmeme;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.orhanobut.logger.Logger;
import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.RxBleDevice;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Handles the pairing and unpairing of Bluetooth low energy devices.
 */
public class BLEPair {
    /**
     * Pairs the given device.
     * @param context Android context
     * @param device Bluetooth device
     */
    public static void pairDevice(Context context, BluetoothDevice device) {
        try {
            if(isPaired(context, device.getAddress()))
                return;
            Method m = device.getClass().getMethod("createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception ignored) {
            Logger.e("BLEPair pair device error: e="+ignored.toString(), ignored);
        }
    }

    /**
     * Unpairs the given device.
     * @param context Android context
     * @param device Bluetooth device
     */
    static void unpairDevice(Context context, BluetoothDevice device) {
        try {
            if(!isPaired(context, device.getAddress()))
                return;
            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception ignored) {
            Logger.e("BLEPair unpair device error: e="+ignored.toString(), ignored);
        }
    }

    /**
     * Returns whether the device is paired or not.
     * @param context Android context
     * @param macAddress MAC address of the device
     * @return Whether the device is paired or not.
     */
    private static boolean isPaired(Context context, String macAddress){
        RxBleClient rxBleClient = MyApplication.getRxBleClient(context);
        Set<RxBleDevice> rxBleDeviceSet = rxBleClient.getBondedDevices();
        for (RxBleDevice rxBleDevice : rxBleDeviceSet) {
            if (rxBleDevice.getMacAddress().equals(macAddress))
                return true;
        }
        return false;
    }
}
