/*
 * Copyright 2017-2020 Pranav Pandey
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

import androidx.annotation.Nullable;

import com.pranavpandey.android.dynamic.engine.model.DynamicAppInfo;
import com.pranavpandey.android.dynamic.engine.DynamicEngine;

/**
 * Interface to listen various system events with the help of {@link DynamicEngine}.
 */
public interface DynamicEventListener {

    /**
     * This method will be called on initializing the service so that we can get the current
     * charging, headset and dock state.
     *
     * @param charging {@code true} if the device is charging or connected to a power source.
     * @param headset {@code true} if the device is connected to a headset or a audio output
     *                device.
     * @param docked {@code true} if the device is docked.
     */
    void onInitialize(boolean charging, boolean headset, boolean docked);

    /**
     * This method will be called when call state is changed.
     * <p>Either on call or the device is idle.
     *
     * @param call {@code true} if the device is on call.
     *             <p><Either ringing or answered.
     */
    void onCallStateChange(boolean call);

    /**
     * This method will be called when screen state is changed.
     * <p>Either the device screen is off or on.
     *
     * @param screenOff {@code true} if the device screen is off.
     */
    void onScreenStateChange(boolean screenOff);

    /**
     * This method will be called when lock state is changed.
     * <p>Either the device is in the locked or unlocked state independent of the PIN,
     * password or any other security lock.
     *
     * @param locked {@code true} if the device is in the locked state or the lock screen is shown.
     */
    void onLockStateChange(boolean locked);

    /**
     * This method will be called when headset state is changed.
     * <p>Either the device is connected to a audio output device or volume is routed through
     * the internal speaker.
     *
     * @param connected {@code true} if the device is connected to a headset or a audio output
     *                  device.
     */
    void onHeadsetStateChange(boolean connected);

    /**
     * This method will be called when charging state is changed.
     * <p>Either the device is connected to a power source using the battery.
     *
     * @param charging {@code true} if the device is charging or connected to a power source.
     */
    void onChargingStateChange(boolean charging);

    /**
     * This method will be called when dock state is changed.
     * <p>Either the device is docked or not.
     *
     * @param docked {@code true} if the device is docked.
     */
    void onDockStateChange(boolean docked);

    /**
     * This method will be called when foreground app is changed.
     * <p>Use it to provide the app specific functionality in the app.
     *
     * @param dynamicAppInfo The dynamic app info of the foreground package.
     */
    void onAppChange(@Nullable DynamicAppInfo dynamicAppInfo);

    /**
     * This method will be called when an app package is added or changed.
     * <p>Useful to show a notification if an app is updated or a new app is installed.
     *
     * @param dynamicAppInfo The dynamic app info of the updated or added package.
     * @param newPackage {@code true} if the package is newly added.
     */
    void onPackageUpdated(@Nullable DynamicAppInfo dynamicAppInfo, boolean newPackage);

    /**
     * This method will be called when an app package is removed.
     * <p>Useful to show some work when a package is removed.
     *
     * @param packageName The package which was removed.
     */
    void onPackageRemoved(@Nullable String packageName);
}
