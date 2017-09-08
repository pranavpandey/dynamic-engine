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
import android.support.annotation.RestrictTo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.pranavpandey.android.dynamic.engine.utils.DynamicEngineUtils;

import static android.telephony.PhoneStateListener.LISTEN_CALL_STATE;

/**
 * Broadcast receiver to listen call events. It is added in the manifest and
 * should be registered dynamically at the runtime.
 * <br /><br />
 * Package must be granted {@link android.Manifest.permission_group#PHONE}
 * permission to listen call events on Android M and above devices.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class DynamicStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        final TelephonyManager telephoneManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);

        telephoneManager.listen(new PhoneStateListener() {

            boolean onCall = false;

            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        onCall = false;
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        onCall = true;
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        onCall = true;
                        break;
                }

                context.sendBroadcast(new Intent(onCall
                        ? DynamicEngineUtils.ACTION_ON_CALL
                        : DynamicEngineUtils.ACTION_CALL_IDLE));
            }
        }, LISTEN_CALL_STATE);
    }
}
