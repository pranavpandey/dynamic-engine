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

package com.pranavpandey.android.dynamic.engine.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pranavpandey.android.dynamic.engine.model.DynamicAppInfo;
import com.pranavpandey.android.dynamic.engine.service.DynamicEngine;

/**
 * Collection of useful functions used by the
 * {@link DynamicEngine}.
 */
public class DynamicEngineUtils {

    /**
     * Intent action constant for the on call state.
     */
    public static final String ACTION_ON_CALL =
            "com.pranavpandey.android.dynamic.engine.ACTION_ON_CALL";

    /**
     * Intent action constant for the call idle state.
     */
    public static final String ACTION_CALL_IDLE =
            "com.pranavpandey.android.dynamic.engine.ACTION_CALL_IDLE";

    /**
     * Constant for the package scheme.
     */
    private static final String PACKAGE_SCHEME = "package";

    /**
     * Get dynamic app info from the package name.
     *
     * @param context The context to get {@link PackageManager}.
     * @param packageName The Package name to build the
     *                    dynamic app info.
     *
     * @return The dynamic app info from the package name.
     */
    public static @Nullable DynamicAppInfo getAppInfoFromPackage(
            @NonNull Context context, @Nullable String packageName) {
        if (packageName != null) {
            DynamicAppInfo dynamicAppInfo = new DynamicAppInfo();
            try {
                dynamicAppInfo.setApplicationInfo(
                        context.getPackageManager().getApplicationInfo(
                        packageName, PackageManager.GET_META_DATA));

                dynamicAppInfo.setPackageName(packageName);
                if (dynamicAppInfo.getApplicationInfo() != null) {
                    dynamicAppInfo.setLabel(dynamicAppInfo.getApplicationInfo().
                            loadLabel(context.getPackageManager()).toString());
                }
            } catch (PackageManager.NameNotFoundException ignored) {
            }

            return dynamicAppInfo;
        }

        return null;
    }

    /**
     * @return The intent filter to register a broadcast receiver
     *         which can listen special actions of the
     *         {@link DynamicEngine}.
     */
    public static @NonNull IntentFilter getEventsIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(Intent.ACTION_DOCK_EVENT);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

        return intentFilter;
    }

    /**
     * @return The intent filter to register a broadcast receiver
     *         which can listen call events of the
     *         {@link DynamicEngine}.
     */
    public static @NonNull IntentFilter getCallIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_ON_CALL);
        intentFilter.addAction(ACTION_CALL_IDLE);

        return intentFilter;
    }

    /**
     * @return The intent filter to register a broadcast receiver
     *         which can listen package added or removed broadcasts.
     */
    public static @NonNull IntentFilter getPackageIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addDataScheme(PACKAGE_SCHEME);

        return intentFilter;
    }
}
