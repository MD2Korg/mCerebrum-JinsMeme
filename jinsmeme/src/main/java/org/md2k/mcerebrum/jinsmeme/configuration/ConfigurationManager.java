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

package org.md2k.mcerebrum.jinsmeme.configuration;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.md2k.datakitapi.source.METADATA;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.platform.Platform;
import org.md2k.datakitapi.source.platform.PlatformBuilder;
import org.md2k.mcerebrum.commons.storage.Storage;

import java.util.ArrayList;

/**
 *
 */
public class ConfigurationManager {
    private static final String CONFIG_DIRECTORY = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/mCerebrum/org.md2k.mcerebrum.jinsmeme/";
    private static final String CONFIG_FILENAME = "config.json";
    public static ArrayList<DataSource> read() {
        try {
            return Storage.readJsonArrayList(CONFIG_DIRECTORY + CONFIG_FILENAME, DataSource.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    private static void write(ArrayList<DataSource> dataSources) {
        try {
            Storage.writeJsonArray(CONFIG_DIRECTORY + CONFIG_FILENAME, dataSources);
        } catch (Exception e) {
            Log.e("abc","write error");
        }
    }
    public static String getDeviceId(){
        ArrayList<DataSource> d = read();
        if(d.size()==0) return null;
        return d.get(0).getPlatform().getMetadata().get(METADATA.DEVICE_ID);
    }

    /**
     * Deletes the given device from the configuration.
     */
    public static void deleteDevice() {
        write(new ArrayList<>());
    }

    /**
     * Adds the given platform to the configuration file.
     * @param deviceId Id of the device
     */
    public static void addDevice(String deviceId) {
        ArrayList<DataSource> d = MetaData.getDataSources();
        for(int i = 0;i<d.size();i++){
            d.get(i).getPlatform().getMetadata().put(METADATA.DEVICE_ID, deviceId);
        }
       write(d);
    }

    /**
     * Returns whether any <code>DataSource</code>s have been configured.
     * @return Whether any <code>DataSource</code>s have been configured.
     */
    public static boolean isConfigured() {
        if(read().size()==0) return false;
        return true;
    }
}
