package org.md2k.mcerebrum.jinsmeme;

import android.util.Log;

import com.jins_jp.meme.MemeConnectListener;
import com.jins_jp.meme.MemeLib;
import com.jins_jp.meme.MemeRealtimeData;
import com.jins_jp.meme.MemeRealtimeListener;
import com.jins_jp.meme.MemeStatus;

import org.md2k.mcerebrum.jinsmeme.configuration.ConfigurationManager;

/**
 * Created by nusrat on 10/3/2018.
 */

public class Device {
    // TODO : Replace APP_ID and APP_SECRET
    private MemeLib memeLib;

    public Device(){
        memeLib = MemeLib.getInstance();
        memeLib.setMemeConnectListener(memeConnectListener);
    }

    final private MemeConnectListener memeConnectListener = new MemeConnectListener() {
        @Override
        public void memeConnectCallback(boolean b) {
            Log.d("abc","memeConnectCallback b="+b);
            memeLib.startDataReport(memeRealtimeListener);
        }

        @Override
        public void memeDisconnectCallback() {
            Log.d("abc","memeDisConnectCallback");
        }
    };

    public void connect(){
        String deviceId = ConfigurationManager.getDeviceId();
        if(memeLib.isScanning()) memeLib.stopScan();
        memeLib.setMemeConnectListener(memeConnectListener);

        Log.d("abc"," deviceid="+deviceId+" isconnected = "+memeLib.isConnected());
        if (memeLib.isConnected()) {
            memeLib.startDataReport(memeRealtimeListener);
        }else {
            MemeStatus status = memeLib.connect(deviceId);
            Log.d("abc","status="+status+" deviceid="+deviceId+" isconnected = "+memeLib.isConnected());
        }

    }

    public void disconnect(){
        if(memeLib.isConnected()) memeLib.disconnect();
    }


    private final MemeRealtimeListener memeRealtimeListener = new MemeRealtimeListener() {
        @Override
        public void memeRealtimeCallback(final MemeRealtimeData memeRealtimeData) {
            Log.d("abc", "new data"+memeRealtimeData.getAccX()+" "+memeRealtimeData.getAccY()+" "+memeRealtimeData.getAccZ());

//                    updateMemeData(memeRealtimeData);
        }
    };
}
