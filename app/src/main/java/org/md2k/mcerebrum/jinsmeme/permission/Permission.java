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

package org.md2k.mcerebrum.jinsmeme.permission;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import com.tbruyelle.rxpermissions.RxPermissions;

import rx.functions.Action1;

/**
 * Handles permission requests and callbacks.
 */
public class Permission{

    /**
     * Determines if the app has the needed permissions.
     * @param context Android context
     * @return Whether the app has the needed permissions.
     */

    public static boolean hasPermission(Context context){
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_PERMISSIONS);
            for (int i = 0; i < info.requestedPermissions.length; i++) {
                if(context.checkCallingOrSelfPermission(info.requestedPermissions[i])!=
                        PackageManager.PERMISSION_GRANTED)
                    return false;
            }
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!mBluetoothAdapter.isEnabled())
                return false;
            final LocationManager manager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );
            if(manager == null)
                return false;
            if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER ))
                return false;
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }

    /**
     * Requests for the needed permissions for the given activity.
     * @param activity Activity requesting permissions.
     * @param permissionCallback Callback interface used for requesting permissions.
     */
    public static void requestPermission(Activity activity, final PermissionCallback permissionCallback) {
        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo(activity.getPackageName(),
                    PackageManager.GET_PERMISSIONS);
            RxPermissions rxPermissions = new RxPermissions(activity);
            rxPermissions.request(info.requestedPermissions).subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean isGranted) {
                    permissionCallback.OnResponse(isGranted);
                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
