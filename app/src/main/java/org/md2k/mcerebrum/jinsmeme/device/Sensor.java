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

import org.md2k.datakitapi.source.datasource.DataSourceClient;

/**
 * Creates a <code>Sensor</code> object.
 */
public class Sensor {
    private DataSourceClient dataSourceClient;
    private String deviceType;
    private String deviceId;
    private String characteristicName;
    private String dataSourceType;
    private String dataSourceId;

    /**
     * Constructor
     * @param dataSourceClient <code>DataSourceClient</code> for this <code>Sensor</code>.
     * @param deviceType Type of device this <code>Sensor</code> is on.
     * @param deviceId Id of the device this <code>Sensor</code> is on.
     * @param characteristicName Name of the <code>Characteristic</code> for this <code>Sensor</code>.
     * @param dataSourceType Type of <code>DataSource</code> this <code>Sensor</code> produces.
     * @param dataSourceId Id of the <code>DataSource</code> this <code>Sensor</code> produces.
     */
    public Sensor(DataSourceClient dataSourceClient, String deviceType, String deviceId,
                  String characteristicName, String dataSourceType, String dataSourceId) {
        this.deviceId = deviceId;
        this.dataSourceClient = dataSourceClient;
        this.deviceType = deviceType;
        this.characteristicName = characteristicName;
        this.dataSourceType = dataSourceType;
        this.dataSourceId = dataSourceId;
    }

    /**
     * Returns the <code>DataSourceClient</code>.
     * @return The <code>DataSourceClient</code>.
     */
    public DataSourceClient getDataSourceClient() {
        return dataSourceClient;
    }

    /**
     * Returns the type of the device.
     * @return The type of the device.
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * Returns the name of the <code>Characteristic</code>.
     * @return The name of the <code>Characteristic</code>.
     */
    public String getCharacteristicName() {
        return characteristicName;
    }

    /**
     * Returns the <code>DataSource</code> type.
     * @return The <code>DataSource</code> type.
     */
    public String getDataSourceType() {
        return dataSourceType;
    }

    /**
     * Returns the <code>DataSource</code> id.
     * @return The <code>DataSource</code> id.
     */
    public String getDataSourceId() {
        return dataSourceId;
    }

    /**
     * Returns the device id.
     * @return The device id.
     */
    public String getDeviceId() {
        return deviceId;
    }
}
