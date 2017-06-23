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

package com.pranavpandey.android.dynamic.engine.listener;

import com.pranavpandey.android.dynamic.engine.model.DynamicAppInfo;
import com.pranavpandey.android.dynamic.engine.service.DynamicEngine;

/**
 * Interface to listen various system events with the help of
 * DynamicEngine.
 *
 * @see DynamicEngine
 */
public interface DynamicEventListener {

    /**
     * On initialize the service so that we can get the current
     * charging, headset and dock state.
     *
     * @param isCharging {@code true} if the device is charging or connected
     *                   to a power source.
     * @param isHeadset {@code true} if the device is connected to a headset
     *                  or a audio output device.
     * @param isDocked {@code true} if the device is docked.
     */
    void onInitialize(boolean isCharging, boolean isHeadset, boolean isDocked);

    /**
     * On call state changed. Either on call or the device is idle.
     *
     * @param isCall {@code true} if the device is on call. Either ringing
     *               or answered.
     */
    void onCallStateChange(boolean isCall);

    /**
     * On lock state changed. Either the device is in the locked or unlocked
     * state independent of the PIN, password or any other security lock.
     *
     * @param isLocked {@code true} if the device is in the locked state or
     *                  the lock screen is shown.
     */
    void onLockStateChange(boolean isLocked);

    /**
     * On headset state changed. Either teh device is connected to a audio
     * output device or volume is routed through the internal speaker.
     *
     * @param isConnected {@code true} if the device is connected to a headset
     *                    or a audio output device.
     */
    void onHeadsetStateChange(boolean isConnected);

    /**
     * On charging state changed. Either the device is connected to a power
     * source using the battery.
     *
     * @param isCharging {@code true} if the device is charging or connected
     *                   to a power source.
     */
    void onChargingStateChange(boolean isCharging);

    /**
     * On dock state changed. Either the device is docked or not.
     *
     * @param isDocked {@code true} if the device is docked.
     */
    void onDockStateChange(boolean isDocked);

    /**
     * On foreground app changed. Use it to provide the app specific
     * functionality in the app.
     *
     * @param dynamicAppInfo {@link DynamicAppInfo} of the foreground package.
     */
    void onAppChange(DynamicAppInfo dynamicAppInfo);

    /**
     * On app package added or changed. Useful to show a notification if
     * an app is updated or a new app is installed.
     *
     * @param dynamicAppInfo {@link DynamicAppInfo} of the updated or added package.
     * @param isNewPackage {@code true} if the package is newly added.
     */
    void onPackageUpdated(DynamicAppInfo dynamicAppInfo, boolean isNewPackage);
}
