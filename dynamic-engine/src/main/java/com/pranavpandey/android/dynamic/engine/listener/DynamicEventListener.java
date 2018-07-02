/*
 * Copyright 2017 Pranav Pandey
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

import android.support.annotation.Nullable;

import com.pranavpandey.android.dynamic.engine.model.DynamicAppInfo;
import com.pranavpandey.android.dynamic.engine.service.DynamicEngine;

/**
 * Interface to listen various system events with the help of
 * {@link DynamicEngine}.
 */
public interface DynamicEventListener {

    /**
     * Called on initialize the service so that we can get the
     * current charging, headset and dock state.
     *
     * @param charging {@code true} if the device is charging
     *                 or connected to a power source.
     * @param headset {@code true} if the device is connected to
     *                a headset or a audio output device.
     * @param docked {@code true} if the device is docked.
     */
    void onInitialize(boolean charging, boolean headset, boolean docked);

    /**
     * Called on call state changed. Either on call or the device
     * is idle.
     *
     * @param call {@code true} if the device is on call.
     *             Either ringing or answered.
     */
    void onCallStateChange(boolean call);

    /**
     * Called on lock state changed. Either the device is in the
     * locked or unlocked state independent of the PIN, password
     * or any other security lock.
     *
     * @param locked {@code true} if the device is in the locked
     *               state or the lock screen is shown.
     */
    void onLockStateChange(boolean locked);

    /**
     * Called on headset state changed. Either the device is
     * connected to a audio output device or volume is routed
     * through the internal speaker.
     *
     * @param connected {@code true} if the device is connected to
     *                  a headset or a audio output device.
     */
    void onHeadsetStateChange(boolean connected);

    /**
     * Called on charging state changed. Either the device
     * is connected to a power source using the battery.
     *
     * @param charging {@code true} if the device is charging
     *                 or connected to a power source.
     */
    void onChargingStateChange(boolean charging);

    /**
     * Called on dock state changed. Either the device is docked
     * or not.
     *
     * @param docked {@code true} if the device is docked.
     */
    void onDockStateChange(boolean docked);

    /**
     * Called on foreground app changed. Use it to provide the app
     * specific functionality in the app.
     *
     * @param dynamicAppInfo The dynamic app info of the foreground
     *                       package.
     */
    void onAppChange(@Nullable DynamicAppInfo dynamicAppInfo);

    /**
     * Called on app package added or changed. Useful to show a
     * notification if an app is updated or a new app is installed.
     *
     * @param dynamicAppInfo The dynamic app info of the updated or
     *                       added package.
     * @param newPackage {@code true} if the package is newly added.
     */
    void onPackageUpdated(@Nullable DynamicAppInfo dynamicAppInfo, boolean newPackage);

    /**
     * On app package removed. Useful to show some work when a
     * package is removed.
     *
     * @param packageName The package which was removed.
     */
    void onPackageRemoved(@Nullable String packageName);
}
