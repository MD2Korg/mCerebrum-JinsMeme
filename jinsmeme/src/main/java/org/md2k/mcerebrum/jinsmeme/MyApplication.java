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

import android.app.Application;
import android.content.Context;
import android.util.Log;


import com.jins_jp.meme.MemeLib;

import org.md2k.mcerebrum.commons.debug.MyLogger;
import org.md2k.mcerebrum.core.access.MCerebrum;

/**
 * This class connects this application to the <code>MCerebrum</code> core library.
 */
public class MyApplication extends Application {
    private static final String APP_ID = "908074513555095";
    private static final String APP_SECRET = "e4uw89oaz43n6ga0cfkspsye2nrg7xo3";
    private static Context context;

    /**
     * Creates the activity, an <code>RxBleClient</code>, and calls on the <code>MCerebrum</code>
     * library for initialization.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        MCerebrum.init(getApplicationContext(), MyMCerebrumInit.class);
        MyLogger.setLogger(getApplicationContext());
        MemeLib.setAppClientID(this, "908074513555095", "e4uw89oaz43n6ga0cfkspsye2nrg7xo3");
        MemeLib memeLib = MemeLib.getInstance();

    }
    public static Context getContext(){
        return context;
    }
}

