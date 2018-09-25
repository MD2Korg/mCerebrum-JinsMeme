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

import android.os.Parcel;
import android.os.Parcelable;

import org.md2k.datakitapi.time.DateTime;

/**
 *
 */
public class Summary implements Parcelable {
    private long startTimestamp;
    private int count;

    /**
     * Constructor
     */
    Summary(){
        startTimestamp = DateTime.getDateTime();
        count = 0;
    }

    /**
     * Constructor
     * @param in Parcelable input
     */
    protected Summary(Parcel in) {
        startTimestamp = in.readLong();
        count = in.readInt();
    }

    public static final Creator<Summary> CREATOR = new Creator<Summary>() {
        /**
         * Creates a <code>Summary</code> from a parcel.
         * @param in Parcelable input.
         * @return Returns the <code>Summary</code>.
         */
        @Override
        public Summary createFromParcel(Parcel in) {
            return new Summary(in);
        }

        /**
         * Creates a new array of <code>Summary</code> objects.
         * @param size Size of the array.
         * @return A new array of <code>Summary</code> objects.
         */
        @Override
        public Summary[] newArray(int size) {
            return new Summary[size];
        }
    };

    /**
     * Increments <code>count</code>.
     */
    public void set(){
        count++;
    }

    /**
     * Returns the starting timestamp.
     * @return The starting timestamp.
     */
    public long getStartTimestamp() {
        return startTimestamp;
    }

    /**
     * Returns the count.
     * @return The count.
     */
    public int getCount() {
        return count;
    }

    /**
     * Returns the frequency.
     * @return The frequency.
     */
    public double getFrequency(){
        return (double)count/ ((DateTime.getDateTime()-startTimestamp)/1000.0);
    }

    /**
     * Returns 0 because this <code>Parcelable</code> does not contain a <code>FileDescriptor</code>.
     * @return Always returns 0.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes the calling object to a parcel.
     * @param dest Parcel to write to.
     * @param flags Contextual flags as per <code>Parcelable.writeToParcel()</code>.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(startTimestamp);
        dest.writeInt(count);
    }
}
