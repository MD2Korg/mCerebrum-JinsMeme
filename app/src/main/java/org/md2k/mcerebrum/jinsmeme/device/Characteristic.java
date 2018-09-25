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

import com.polidea.rxandroidble.RxBleConnection;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.mcerebrum.jinsmeme.Data;

import java.util.ArrayList;

import rx.Observable;

/**
 * Base class for defining sensor characteristics.
 */
public abstract class Characteristic {
    protected long lastTimestamp;
    protected int lastSequence;
    private String id;
    protected double frequency;
    private String name;

    /**
     * Constructor
     * @param id Characteristic id.
     * @param name Name of the characteristic.
     * @param frequency Sampling frequency.
     */
    public Characteristic(String id, String name, double frequency) {
        this.id = id;
        this.frequency = frequency;
        this.name = name;
    }

    /**
     * Returns the frequency.
     * @return The frequency.
     */
    public double getFrequency() {
        return frequency;
    }

    /**
     * Returns the name.
     * @return The name.
     */
    public String getName() {
        return name;
    }


    /**
     * Returns an <code>Observable</code> over the data for this <code>Characteristic</code>.
     * @param rxBleConnection The BLE connection handle
     * @param sensors Arraylist of <code>Sensor</code>s
     * @return An <code>Observable</code> over the data for this <code>Characteristic</code>.
     */
    abstract public Observable<ArrayList<Data>> getObservable(RxBleConnection rxBleConnection, ArrayList<Sensor> sensors);


    /**
     * Returns the id.
     * @return The id.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the correct timestamp.
     * @param curSequence
     * @param maxLimit
     * @return The correct timestamp.
     */
    protected long correctTimeStamp(int curSequence, int maxLimit) {
        long time;
        long curTime = DateTime.getDateTime();
        int diff = (curSequence - lastSequence + maxLimit) % maxLimit;
        time = (long) (lastTimestamp + (1000.0 * diff) / frequency);
        if (curTime < time || curTime - time > 5000)
            time = curTime;
        return time;
    }

}
