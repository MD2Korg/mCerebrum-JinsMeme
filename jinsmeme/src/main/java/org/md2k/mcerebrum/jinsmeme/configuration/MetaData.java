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
import org.md2k.mcerebrum.jinsmeme.MyApplication;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Provides methods for accessing metadata.
 */
class MetaData {
    private static final String FILENAME_ASSET_METADATA = "metadata.json";

    /**
     * Returns an arraylist of <code>DataSource</code>s in the metadata file.
     * @return An arraylist of <code>DataSource</code>s in the metadata file.
     */
    private static ArrayList<DataSource> readMetaData() {
        try {
            return Storage.readJsonArrayFromAsset(MyApplication.getContext(), FILENAME_ASSET_METADATA, DataSource.class);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * Returns a <code>DataSource</code> that matches the given parameters.
     * @param dataSourceType Data type of the <code>DataSource</code>.
     * @param dataSourceId Id of the <code>DataSource</code>.
     * @return A <code>DataSource</code> that matches the given parameters.
     */
    static DataSource getDataSource(String dataSourceType, String dataSourceId) {
        ArrayList<DataSource> metaData = readMetaData();
        for(int i = 0; metaData != null && i < metaData.size(); i++){
            if(!metaData.get(i).getType().equals(dataSourceType))
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
     * @return An arraylist of <code>DataSource</code>s that match the given type.
     */
    static ArrayList<DataSource> getDataSources() {
        ArrayList<DataSource> metaData = readMetaData();
        return metaData;
    }
}
