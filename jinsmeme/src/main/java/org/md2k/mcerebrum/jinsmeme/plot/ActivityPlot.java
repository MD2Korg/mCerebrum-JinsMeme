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

package org.md2k.mcerebrum.jinsmeme.plot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeDouble;
import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.datatype.DataTypeFloat;
import org.md2k.datakitapi.datatype.DataTypeFloatArray;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.mcerebrum.commons.plot.RealtimeLineChartActivity;
import org.md2k.mcerebrum.jinsmeme.ServiceJinsMeme;

/**
 * This class is for the plotting activity.
 */
public class ActivityPlot extends RealtimeLineChartActivity {
    DataSource dataSource;

    /**
     * Fetches the data source for the plot.
     *
     * @param savedInstanceState This activity's previous state, is null if this activity has never
     *                           existed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            dataSource = getIntent().getExtras().getParcelable(DataSource.class.getSimpleName());
        }catch (Exception e){
            finish();
        }
    }

    /**
     * Unregisters <code>mMessageReceiver</code> when this activity is paused.
     */
    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(ServiceJinsMeme.INTENT_DATA));

        super.onResume();
    }

    /**
     * Creates a new broadcast receiver that updates the plot when it receives new data.
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DataSource ds = intent.getParcelableExtra(DataSource.class.getSimpleName());
            if(!ds.getType().equals(dataSource.getType()))
                return;
            if(ds.getId()!= null && dataSource.getId()!= null && !ds.getId().equals(dataSource.getId()))
                return;
            if(ds.getId() == null && dataSource.getId()!= null)
                return;
            if(ds.getId()!=null && dataSource.getId() == null)
                return;
            if(!ds.getPlatform().getId().equals(dataSource.getPlatform().getId()))
                return;
            updatePlot(intent, ds.getType());
        }
    };

    /**
     * Registers <code>mMessageReceiver</code> when the activity is resumed.
     */
    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    /**
     * Updates the plot with data.
     *
     * @param intent Android intent
     */
    void updatePlot(Intent intent, String ds) {
        float[] sample = new float[1];
        String[] legends;

        getmChart().getDescription().setText(dataSource.getType());
        getmChart().getDescription().setPosition(1f, 1f);
        getmChart().getDescription().setEnabled(true);
        getmChart().getDescription().setTextColor(Color.WHITE);
        switch (ds) {
            case DataSourceType.LED:
                legends = new String[]{"LED 1", "LED 2", "LED 3"};
                break;
            case DataSourceType.ACCELEROMETER:
                legends = new String[]{"Accelerometer X", "Accelerometer Y", "Accelerometer Z"};
                break;
            case DataSourceType.GYROSCOPE:
                legends = new String[]{"Gyroscope X", "Gyroscope Y", "Gyroscope Z"};
                break;
            case DataSourceType.MAGNETOMETER:
                legends = new String[]{"Magnetometer X", "Magnetometer Y", "Magnetometer Z"};
                break;
            case DataSourceType.QUATERNION:
                legends = new String[]{"Quaternion X", "Quaternion Y", "Quaternion Z"};
                break;
            case DataSourceType.MAGNETOMETER_SENSITIVITY:
                legends = new String[]{"Sensitivity X", "Sensitivity Y", "Sensitivity Z"};
                break;
            default:
                legends = new String[]{ds};
                break;
        }
        DataType[] datas = (DataType[]) intent.getParcelableArrayExtra(DataType.class.getSimpleName());
        for(int ii = 0;ii<datas.length;ii++) {
            DataType data = datas[ii];
            if (data instanceof DataTypeFloat) {
                sample = new float[]{((DataTypeFloat) data).getSample()};
            } else if (data instanceof DataTypeFloatArray) {
                sample = ((DataTypeFloatArray) data).getSample();
            } else if (data instanceof DataTypeDoubleArray) {
                double[] samples = ((DataTypeDoubleArray) data).getSample();
                sample = new float[samples.length];
                for (int i = 0; i < samples.length; i++) {
                    sample[i] = (float) samples[i];
                }
            } else if (data instanceof DataTypeDouble) {
                double samples = ((DataTypeDouble) data).getSample();
                sample = new float[]{(float) samples};
            }
            addEntry(sample, legends, 600);
        }
    }

}
