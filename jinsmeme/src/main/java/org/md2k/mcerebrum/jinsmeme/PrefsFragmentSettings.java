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

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.jins_jp.meme.MemeConnectListener;
import com.jins_jp.meme.MemeLib;
import com.jins_jp.meme.MemeScanListener;
import com.jins_jp.meme.MemeStatus;

import org.md2k.datakitapi.source.METADATA;
import org.md2k.datakitapi.source.platform.Platform;
import org.md2k.mcerebrum.commons.dialog.Dialog;
import org.md2k.mcerebrum.jinsmeme.configuration.ConfigurationManager;

import java.util.ArrayList;

/**
 * Preferences Fragment for this application's settings.
 */
public class PrefsFragmentSettings extends PreferenceFragment {
    private MemeLib memeLib;
    private int REQUEST_CODE_ACCESS_COARSE_LOCATION = 0x01;
    private boolean locationPermissionGranted = false;
    Handler h;
    /**
     * Creates a new <code>devices</code> hashmap.
     *
     * @param savedInstanceState This activity's previous state, is null if this activity has never
     *                           existed.
     */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        memeLib = MemeLib.getInstance(); //MemeLib is singleton
        memeLib.setVerbose(true); // for debug
        addPreferencesFromResource(R.xml.pref_settings);
        setPreferenceScreenConfigured();
        checkPermissions();
        h=new Handler();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == REQUEST_CODE_ACCESS_COARSE_LOCATION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                showPermissionError();
            } else {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void checkPermissions() {
        // for Android 6
        // Check ACCESS_COARSE_LOCATION permission is granted
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 「Never ask again」Checkbox
            Log.d("abc", "location permission=false");
            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Keep preference for the first time false return
                SharedPreferences preferences = getActivity().getSharedPreferences("LIVE_VIEW", Context.MODE_PRIVATE);
                if (preferences.getBoolean("FIRST_LOCATION_PERMISSION_ACCESS", true)) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("FIRST_LOCATION_PERMISSION_ACCESS", false);
                    editor.apply();
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ACCESS_COARSE_LOCATION);
                } else {
                    showPermissionError();
                }
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ACCESS_COARSE_LOCATION);
            }
        } else {
            Log.d("abc", "location permission=true");
            locationPermissionGranted = true;
        }

        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            new AlertDialog.Builder(getActivity())
                    .setMessage("Turn on Bluetooth to allow app to connect to JINS MEME")
                    .setPositiveButton(android.R.string.yes, null)
                    .show();
        }

    }

    private void showPermissionError() {
        new AlertDialog.Builder(getActivity())
                .setMessage("Turn on Location Service to allow to scan JINS MEME")
                .setPositiveButton(android.R.string.yes, null)
                .show();
    }

    /**
     * Calls <code>scan()</code> when resumed.
     */
    @Override
    public void onResume() {
        h.postDelayed(r,1000);
        startScan();
        super.onResume();
    }
    Runnable r = new Runnable() {
        @Override
        public void run() {
            startScan();
        }
    };


    /**
     * Scans for available devices.
     */
/*
    final private MemeConnectListener memeConnectListener = new MemeConnectListener() {
        @Override
        public void memeConnectCallback(boolean b) {
            Log.d("abc","memeConnectCallback b="+b);
            //describe actions after connection with JINS MEME
        }

        @Override
        public void memeDisconnectCallback() {
            Log.d("abc","memeDisConnectCallback");
            //describe actions after disconnection from JINS MEME
        }
    };
*/


    private void startScan() {
        if (!locationPermissionGranted) {
            Log.d("abc", "permission not granted");
        }
        Log.d("abc", "start scan");
        //Sets MemeConnectListener to get connection result.

        //starts scanning JINS MEME
//        memeLib.setMemeConnectListener(memeConnectListener);
        MemeStatus status = memeLib.startScan(new MemeScanListener() {
            @Override
            public void memeFoundCallback(String address) {
                Log.d("abc", address);
//                memeLib.stopScan();
//                memeLib.connect(address);
                addToPreferenceScreenAvailable(address);
            }
        });
        if(status==MemeStatus.MEME_CMD_INVALID)
            h.postDelayed(r, 1000);
        Log.d("abc", "status = " + status.toString());

    }

    private void stopScan() {
        //   invalidateOptionsMenu();

        //stop scanning JINS MEME
        Log.d("abc", "stop scan");
        if (memeLib.isScanning())
            memeLib.stopScan();
    }


    /**
     * Sets the settings category of configured devices.
     */
    void setPreferenceScreenConfigured() {
        PreferenceCategory category = (PreferenceCategory) findPreference("key_device_configured");
        category.removeAll();
        String deviceId = ConfigurationManager.getDeviceId();
        if (deviceId != null) {
            Preference preference = new Preference(getActivity());
            preference.setKey(deviceId);
            preference.setTitle(deviceId);
            preference.setIcon(R.drawable.ic_glass);
            preference.setOnPreferenceClickListener(preferenceListenerConfigured());
            category.addPreference(preference);
        }
    }

    /**
     * Adds the given device to the list of available devices.
     *
     * @param deviceId Id of device.
     */
    void addToPreferenceScreenAvailable(String deviceId) {
        final PreferenceCategory category = (PreferenceCategory) findPreference("key_device_available");
        String c = ConfigurationManager.getDeviceId();
        if (c != null && deviceId.equals(c)) return;
        for (int i = 0; i < category.getPreferenceCount(); i++)
            if (category.getPreference(i).getKey().equals(deviceId))
                return;
        Preference listPreference = new Preference(getActivity());

        listPreference.setKey(deviceId);
        listPreference.setTitle(deviceId);
        listPreference.setIcon(R.drawable.ic_glass);
        listPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (ConfigurationManager.isConfigured())
                    Toast.makeText(getActivity(), "Device: " + preference.getKey() + " already configured", Toast.LENGTH_LONG).show();
                else {
                    ConfigurationManager.addDevice(deviceId);
                    setPreferenceScreenConfigured();
                    category.removePreference(preference);
                }
                return false;
            }
        });

        category.addPreference(listPreference);
    }

    /**
     * Creates a click listener for configured devices.
     *
     * @return An <code>OnPreferenceClickListener</code> for configured devices.
     */
    private Preference.OnPreferenceClickListener preferenceListenerConfigured() {
        return preference -> {
            final String deviceId = preference.getKey();
            Dialog.simple(getActivity(), "Delete Eyeglass", "Delete Eyeglass (" +
                    preference.getTitle() + ")?", "Delete", "Cancel", value -> {
                if ("Delete".equals(value)) {
                    ConfigurationManager.deleteDevice();
                    setPreferenceScreenConfigured();
                }
            }).show();
            return true;
        };
    }

    /**
     * Creates the settings view
     *
     * @param inflater           Android LayoutInflater
     * @param container          Android ViewGroup
     * @param savedInstanceState This activity's previous state, is null if this activity has never
     *                           existed.
     * @return The view this method created.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        assert v != null;
        ListView lv = (ListView) v.findViewById(android.R.id.list);
        lv.setPadding(0, 0, 0, 0);
        return v;
    }

    /**
     * Handles menu item selection.
     *
     * @param item Android MenuItem
     * @return true when the menu item selection actions are successful.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Unsubscribes the <code>scanSubscription</code> when the activity is paused.
     */
    @Override
    public void onPause() {
        stopScan();
        h.removeCallbacks(r);
        super.onPause();
    }
}
