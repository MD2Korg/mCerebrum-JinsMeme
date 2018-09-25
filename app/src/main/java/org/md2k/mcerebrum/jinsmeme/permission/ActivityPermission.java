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

package org.md2k.mcerebrum.jinsmeme.permission;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.md2k.mcerebrum.core.access.MCerebrum;

import es.dmoral.toasty.Toasty;

/**
 * Activity for getting permissions
 */
public class ActivityPermission extends AppCompatActivity {
    private static final int REQUEST_ENABLE_GPS = 1121;
    private static final int REQUEST_ENABLE_BT = 1122;
    BluetoothAdapter bluetoothAdapter;
    private static final int ERROR_PERMISSION = 1;
    private static final int ERROR_BLUETOOTH = 2;
    private static final int ERROR_GPS = 3;
    private static final int SUCCESS = 0;

    /**
     * Checks for permissions.
     * @param savedInstanceState This activity's previous state, is null if this activity has never
     *                           existed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Permission.requestPermission(this, new PermissionCallback() {
            /**
             * Calls <code>enableBluetooth()</code> if permissions are granted.
             * @param isGranted
             */
            @Override
            public void OnResponse(boolean isGranted) {
                if (!isGranted) {
                    setStatus(ERROR_PERMISSION);
                } else
                    enableBluetooth();
            }
        });
    }

    /**
     * Sets the status of the activity and displays an error message if appropriate.
     * @param status Status of the activity.
     */
    void setStatus(int status){
        switch(status){
            case ERROR_BLUETOOTH:
                Toasty.error(getApplicationContext(),
                        "MotionSense - !!! Bluetooth OFF !!! Could not continue...",
                        Toast.LENGTH_SHORT).show();
                MCerebrum.setPermission(ActivityPermission.this, false);
                setResult(Activity.RESULT_CANCELED);
                break;
            case ERROR_GPS:
                Toasty.error(getApplicationContext(),
                        "MotionSense - !!! GPS OFF !!! Could not continue...",
                        Toast.LENGTH_SHORT).show();
                MCerebrum.setPermission(ActivityPermission.this, false);
                setResult(Activity.RESULT_CANCELED);
                break;
            case ERROR_PERMISSION:
                Toasty.error(getApplicationContext(),
                        "MotionSense - !!! PERMISSION DENIED !!! Could not continue...",
                        Toast.LENGTH_SHORT).show();
                MCerebrum.setPermission(ActivityPermission.this, false);
                setResult(Activity.RESULT_CANCELED);
                break;
            case SUCCESS:
                MCerebrum.setPermission(ActivityPermission.this, true);
                Intent intent = getIntent();
                intent.putExtra("result",true);
                setResult(Activity.RESULT_OK,intent);
            default:
        }
        finish();
    }

    /**
     * If the <code>bluetoothAdapter</code> is already enabled, <code>enableGPS()</code> is called.
     * Otherwise, an <code>intent</code> is created to enable bluetooth.
     */
    private void enableBluetooth(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()) {
            enableGPS();
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    /**
     * Enables the corresponding sensor (bluetooth or GPS) if <code>resultCode</code> is affirmative.
     * @param requestCode Code for enable requests.
     * @param resultCode Code returned with the result.
     * @param data Intent sent to this activity.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK)
                    enableGPS();
                else
                    setStatus(ERROR_BLUETOOTH);
                break;
            case REQUEST_ENABLE_GPS:
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
                if(manager == null) {
                    setStatus(ERROR_GPS);
                    return;
                }
                boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if(!statusOfGPS)
                    setStatus(ERROR_GPS);
                else setStatus(SUCCESS);
        }
    }

    /**
     * Enables GPS.
     */
    public void enableGPS() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        if(manager == null) {
            setStatus(ERROR_GPS);
            return;
        }
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!statusOfGPS){
            Intent gpsOptionsIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(gpsOptionsIntent, REQUEST_ENABLE_GPS);
        }else
            setStatus(SUCCESS);
    }
}
