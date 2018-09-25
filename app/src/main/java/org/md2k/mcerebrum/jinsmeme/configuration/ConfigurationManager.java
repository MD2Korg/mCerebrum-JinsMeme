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

import org.md2k.datakitapi.source.METADATA;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.platform.Platform;
import org.md2k.datakitapi.source.platform.PlatformBuilder;

import java.util.ArrayList;

/**
 *
 */
public class ConfigurationManager {

    /**
     * Returns whether the current configuration is equivalent to the default configuration.
     * @return Whether the current configuration is equivalent to the default configuration.
     */
    public static boolean isEqualDefault() {
        boolean flag;
        ArrayList<Platform> platforms = Config.getPlatforms();
        DefaultConfig defaultConfig = DefaultConfig.read();
        if(defaultConfig == null)
            return true;
        if(defaultConfig.required != 0 && defaultConfig.required != platforms.size())
            return false;
        for(int i = 0; i < defaultConfig.devices.size(); i++){
            if(defaultConfig.devices.get(i).use_as.equalsIgnoreCase("OPTIONAL"))
                continue;
            flag = false;
            for(int j = 0; j < platforms.size(); j++){
                if(defaultConfig.devices.get(i).platform_type.equals(platforms.get(j).getType()) &&
                        defaultConfig.devices.get(i).platform_id.equals(platforms.get(j).getId())) {
                    flag = true;
                    break;
                }
            }
            if(!flag) return false;
        }
        return true;
    }

    /**
     * Returns an arraylist of <code>Platform</code>s from the configuration.
     * @return An arraylist of <code>Platform</code>s from the configuration.
     */
    public static ArrayList<Platform> getPlatforms() {
        return Config.getPlatforms();
    }

    /**
     * Returns an arraylist of <code>DataSource</code>s from the the given <code>Platform</code>.
     * @param dataSources List of <code>DataSource</code>s to search through.
     * @param platform <code>Platform</code> to search for.
     * @return An arraylist of <code>DataSource</code>s from the the given <code>Platform</code>.
     */
    public static ArrayList<DataSource> getDataSources(ArrayList<DataSource> dataSources, Platform platform){
        ArrayList<DataSource> selected = new ArrayList<>();
        for(int i = 0; i < dataSources.size(); i++){
            if(dataSources.get(i).getPlatform().getType().equals(platform.getType()) &&
                    dataSources.get(i).getPlatform().getId().equals(platform.getId()))
                selected.add(dataSources.get(i));
        }
        return selected;
    }

    /**
     * Returns the platform id from the default configuration.
     * @return The platform id from the default configuration.
     */
    public static String[] getPlatformIdFromDefault() {
        return DefaultConfig.getPlatformId();
    }
    public static boolean isForegroundApp(){
        return DefaultConfig.isForegroundApp();
    }

    /**
     * Returns whether there is a default configuration.
     * @return Whether there is a default configuration.
     */
    public static boolean hasDefault() {
        return DefaultConfig.hasDefault();
    }

    /**
     * Deletes the given device from the configuration.
     * @param deviceId Device to delete.
     */
    public static void deleteDevice(String deviceId) {
        Config.deleteDevice(deviceId);
    }

    /**
     * Returns whether the given platform and/or device is configured.
     * @param platformId Platform to check.
     * @param deviceId Device to check.
     * @return Whether the given platform and/or device is configured.
     */
    public static boolean isConfigured(String platformId, String deviceId) {
        return Config.isConfigured(platformId, deviceId);
    }

    /**
     * Returns whether the given device is configured.
     * @param deviceId Device to check.
     * @return Whether the given device is configured.
     */
    public static boolean isConfigured(String deviceId) {
        return Config.isConfigured(deviceId);
    }

    /**
     * Adds the given platform to the configuration file.
     * @param context Android context
     * @param platformType Type of platform
     * @param platformId Id of the platform
     * @param deviceId Id of the device
     */
    public static void addPlatform(Context context, String platformType, String platformId, String deviceId) {
        ArrayList<DataSource> res = getDataSources(context, platformType, platformId, deviceId);
       ArrayList<DataSource> d = Config.read();
       if(d == null || d.size() == 0)
           d = res;
       else d.addAll(res);
       Config.write(d);
    }

    /**
     * Returns whether any <code>DataSource</code>s have been configured.
     * @return Whether any <code>DataSource</code>s have been configured.
     */
    public static boolean isConfigured() {
        return Config.isConfigured();
    }

    /**
     * Returns an arraylist of <code>DataSources</code> that match the given parameters.
     * @param context Android context
     * @param platformType Type of platform
     * @param platformId Id of the platform
     * @param deviceId Id of the device
     * @return An arraylist of <code>DataSources</code> that match the given parameters.
     */
    private static ArrayList<DataSource> getDataSources(Context context, String platformType, String platformId, String deviceId){
        ArrayList<DefaultConfig.Sensor> sensors = DefaultConfig.getSensors(platformType, platformId);
        ArrayList<DataSource> res = new ArrayList<>();
        ArrayList<DataSource> dataSources = new ArrayList<>();
        if(sensors == null || sensors.size() == 0){
            dataSources = MetaData.getDataSources(context, platformType);
        }else{
            for(int i = 0; i < sensors.size(); i++){
                DataSource dataSource = MetaData.getDataSource(context, sensors.get(i).type, sensors.get(i).id, platformType);
                if(dataSource != null)
                    dataSources.add(dataSource);
            }
        }
        for(int i = 0; i < dataSources.size(); i++){
            PlatformBuilder platformBuilder = new PlatformBuilder(dataSources.get(i).getPlatform());
            platformBuilder = platformBuilder.setType(platformType).setId(platformId).setMetadata(METADATA.DEVICE_ID, deviceId);

            DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(dataSources.get(i));
            dataSourceBuilder = dataSourceBuilder.setPlatform(platformBuilder.build());
            res.add(dataSourceBuilder.build());
        }
        return res;
    }

    /**
     * Returns an arraylist of <code>DataSource</code>s
     * @param context Android context
     * @return An arraylist of <code>DataSource</code>s
     */
    public static ArrayList<DataSource> read(Context context) {
        ArrayList<DataSource> res = new ArrayList<>();
        ArrayList<Platform> platforms = Config.getPlatforms();
        for(int i = 0; i < platforms.size(); i++){
            ArrayList<DataSource> temp = getDataSources(context, platforms.get(i).getType(),
                    platforms.get(i).getId(), platforms.get(i).getMetadata().get(METADATA.DEVICE_ID));
            if(temp != null && temp.size() != 0)
                res.addAll(temp);
        }
        Config.write(res);
        return res;
    }
}
