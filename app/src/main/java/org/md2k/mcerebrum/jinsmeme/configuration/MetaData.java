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

import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.mcerebrum.commons.storage.Storage;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Provides methods for accessing metadata.
 */
class MetaData {
    private static final String FILENAME_ASSET_METADATA = "metadata.json";

    /**
     * Returns an arraylist of <code>DataSource</code>s in the metadata file.
     * @param context Android context
     * @return An arraylist of <code>DataSource</code>s in the metadata file.
     */
    private static ArrayList<DataSource> readMetaData(Context context) {
        try {
            return Storage.readJsonArrayFromAsset(context, FILENAME_ASSET_METADATA, DataSource.class);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * Returns a <code>DataSource</code> that matches the given parameters.
     * @param context Android context
     * @param dataSourceType Data type of the <code>DataSource</code>.
     * @param dataSourceId Id of the <code>DataSource</code>.
     * @param platformType Platform type of the <code>DataSource</code>.
     * @return A <code>DataSource</code> that matches the given parameters.
     */
    static DataSource getDataSource(Context context, String dataSourceType, String dataSourceId, String platformType) {
        ArrayList<DataSource> metaData = readMetaData(context);
        for(int i = 0; metaData != null && i < metaData.size(); i++){
            if(!metaData.get(i).getType().equals(dataSourceType))
                continue;
            if(!metaData.get(i).getPlatform().getType().equals(platformType))
                continue;
            if(dataSourceId == null && metaData.get(i).getId() == null)
                return metaData.get(i);
            if(dataSourceId != null && metaData.get(i).getId()!= null && dataSourceId.equals(metaData.get(i).getId()))
                return metaData.get(i);
        }
        return null;
    }

    /**
     * Returns an arraylist of <code>DataSource</code>s that match the given type.
     * @param context Android context
     * @param type Data type to look for.
     * @return An arraylist of <code>DataSource</code>s that match the given type.
     */
    static ArrayList<DataSource> getDataSources(Context context, String type) {
        ArrayList<DataSource> metaData = readMetaData(context);
        ArrayList<DataSource> dataSources = new ArrayList<>();
        for(int i = 0; metaData != null && i < metaData.size(); i++){
            if(metaData.get(i).getPlatform().getType().equals(type)){
                dataSources.add(metaData.get(i));
            }
        }
        return dataSources;
    }
}
