/*
 * Copyright (C) 2017 Pranav Pandey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pranavpandey.android.dynamic.engine.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;

import com.pranavpandey.android.dynamic.engine.utils.DynamicEngineUtils;

/**
 * Broadcast receiver to listen call events. It is added in the manifest and
 * should be registered dynamically at the runtime.
 *
 * <p>Package must be granted {@link android.Manifest.permission_group#PHONE}
 * permission to listen call events on Android M and above devices.</p>
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class DynamicStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(@NonNull final Context context, @Nullable Intent intent) {
        if (intent != null && intent.getAction() != null
                && intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)
                    || state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(
                        new Intent(DynamicEngineUtils.ACTION_ON_CALL));
            } else {
                LocalBroadcastManager.getInstance(context).sendBroadcast(
                        new Intent(DynamicEngineUtils.ACTION_CALL_IDLE));
            }
        }
    }
}