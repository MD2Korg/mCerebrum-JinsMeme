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

package org.md2k.mcerebrum.jinsmeme.device.data_quality;

import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.mcerebrum.core.data_format.DATA_QUALITY;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Determines what the quality of the data from the LED is.
 */
public class DataQualityLed extends DataQuality {

    /**
     * Returns whether the last three seconds of data are good.
     * @param values Data to check.
     * @return Whether the last three seconds of data are good.
     */
    private boolean[] isGood3Sec(ArrayList<DataTypeDoubleArray> values) {
        double[] sum = new double[]{0, 0, 0};
        boolean[] res = new boolean[6];
        int[] count = new int[3];
        for (int i = 0; i < values.size(); i++) {
            sum[0] += values.get(i).getSample()[0];
            sum[1] += values.get(i).getSample()[1];
            sum[2] += values.get(i).getSample()[2];
            if (values.get(i).getSample()[0] < 30000 || values.get(i).getSample()[0] > 170000) {
                count[0]++;
            }
            if (values.get(i).getSample()[1] < 140000 || values.get(i).getSample()[1] > 230000) {
                count[1]++;
            }
            if (values.get(i).getSample()[2] < 3000 || values.get(i).getSample()[2] > 20000) {
                count[2]++;
            }
        }
        res[0] = count[0] < (int) (.34 * values.size());
        res[1] = count[1] < (int) (.34 * values.size());
        res[2] = count[2] < (int) (.34 * values.size());
        return res;
    }

    /**
     * Returns the mean of the given data.
     * @param values Data to check.
     * @return The mean of the given data.
     */
    private int[] getMean(ArrayList<DataTypeDoubleArray> values) {
        int[] sum = new int[3];
        for (int i = 0; i < values.size(); i++) {
            sum[0] += values.get(i).getSample()[0];
            sum[1] += values.get(i).getSample()[1];
            sum[2] += values.get(i).getSample()[2];
        }
        for (int i = 0; i < 3; i++) {
            sum[i] = sum[i] / values.size();
        }
        return sum;
    }

    /**
     * Returns the last three seconds of data collected.
     * @return The last three seconds of data collected.
     */
    private ArrayList<DataTypeDoubleArray> getLast3Sec() {
        long curTime = DateTime.getDateTime();
        ArrayList<DataTypeDoubleArray> l = new ArrayList<>();
        for (int i = 0; i < samples.size(); i++) {
            if (curTime - samples.get(i).getDateTime() <= 3000)
                l.add(samples.get(i));
        }
        return l;
    }

    /**
     * Returns the data sample stored at the given index.
     * @param index Index of the sample to get.
     * @return The data sample stored at the given index.
     */
    private double[] getSample(int index) {
        double[] d = new double[samples.size()];
        for (int i = 0; i < samples.size(); i++) {
            d[i] = samples.get(i).getSample()[index];
        }
        return d;
    }

    /**
     * Returns the data quality status of the LED.
     * @return The data quality status of the LED.
     */
    @Override
    public synchronized int getStatus() {
        try {
            long curTime = DateTime.getDateTime();
            Iterator<DataTypeDoubleArray> i = samples.iterator();
            while (i.hasNext()) {
                if (curTime - i.next().getDateTime() >= 8000)
                    i.remove();
            }
            ArrayList<DataTypeDoubleArray> last3Sec = getLast3Sec();
            if (last3Sec.size() == 0)
                return DATA_QUALITY.BAND_OFF;
            boolean[] sec3mean = isGood3Sec(samples);
            if (!sec3mean[0] && !sec3mean[1] && !sec3mean[2])
                return DATA_QUALITY.NOT_WORN;
            int[] mean = getMean(samples);
            if (mean[0] < 5000 && mean[1] < 5000 && mean[2] < 5000)
                return DATA_QUALITY.NOT_WORN;
            boolean check = mean[0] > mean[2] && mean[1] > mean[0] && mean[1] > mean[2];
            if (!check)
                return DATA_QUALITY.BAND_LOOSE;
            int diff;
            if (mean[0] > 140000) {
                diff = 15000;
            } else {
                diff = 50000;
            }
            boolean check1 = mean[0] - mean[2] > 50000 && mean[1] - mean[0] > diff;
            if (!check1)
                return DATA_QUALITY.BAND_LOOSE;
            if (sec3mean[0] && new Bandpass(getSample(0)).getResult()) {
                return DATA_QUALITY.GOOD;
            }
            if (sec3mean[1] && new Bandpass(getSample(1)).getResult()) {
                return DATA_QUALITY.GOOD;
            }
            if (sec3mean[2] && new Bandpass(getSample(2)).getResult()) {

                return DATA_QUALITY.GOOD;
            }
            return DATA_QUALITY.NOT_WORN;
        } catch (Exception e) {
            return DATA_QUALITY.GOOD;
        }
    }
}