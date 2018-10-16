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

package org.md2k.mcerebrum.jinsmeme.datakit;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.mcerebrum.jinsmeme.MyApplication;
import org.md2k.mcerebrum.jinsmeme.configuration.ConfigurationManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manages the connection between this application and <code>DataKitAPI</code>.
 */
public class DataKitManager {
    private DataKitAPI dataKitAPI;
    private HashMap<String, DataSourceClient> hashMap;

    /**
     * Connects <code>DataKitAPI</code>.
     * @return An <code>Observable</code>
     */
    public void connect(OnConnectionListener onConnectionListener){
        hashMap = new HashMap<>();
            dataKitAPI=DataKitAPI.getInstance(MyApplication.getContext());
        try {
            dataKitAPI.connect(new OnConnectionListener() {
                @Override
                public void onConnected() {
                    ArrayList<DataSource> d = ConfigurationManager.read();
                    for(int i = 0;i<d.size();i++){
                        try {
                            hashMap.put(d.get(i).getType(), dataKitAPI.register(new DataSourceBuilder(d.get(i))));
                        } catch (DataKitException e) {
                            e.printStackTrace();
                        }
                    }
                    onConnectionListener.onConnected();
                }
            });
        } catch (DataKitException e) {
            e.printStackTrace();
        }
    }
    public void insert(String type, DataTypeDoubleArray dataTypeDoubleArray) {
        try {
            dataKitAPI.insertHighFrequency(hashMap.get(type), dataTypeDoubleArray);
        } catch (DataKitException e) {
            e.printStackTrace();
        }
    }

    /**
     * Disconnects <code>DataKitAPI</code>.
     */
    public void disconnect() {
        dataKitAPI.disconnect();
    }
}
