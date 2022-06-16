/*
 * Copyright 2017-2022 Pranav Pandey
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

package com.pranavpandey.android.dynamic.engine.util;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pranavpandey.android.dynamic.engine.DynamicEngine;
import com.pranavpandey.android.dynamic.engine.model.DynamicAppInfo;
import com.pranavpandey.android.dynamic.util.DynamicSdkUtils;

import java.util.List;

/**
 * Helper class used for the {@link DynamicEngine}.
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
     * Constant for the unknown event type.
     */
    private static final int EVENT_UNKNOWN = -1;

    /**
     * Returns activity info from the component name.
     *
     * @param context The context to get {@link PackageManager}.
     * @param componentName The component name to be used.
     *
     * @return The activity info from the component name.
     */
    public static @Nullable ActivityInfo getActivityInfo(
            @NonNull Context context, @Nullable ComponentName componentName) {
        if (componentName != null) {
            try {
                return context.getPackageManager().getActivityInfo(
                        componentName, PackageManager.GET_META_DATA);
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    /**
     * Load dynamic app info from the package name.
     *
     * @param context The context to get {@link PackageManager}.
     * @param packageName The package name to build the dynamic app info.
     *
     * @return The dynamic app info from the package name.
     */
    public static @Nullable DynamicAppInfo getAppInfoFromPackage(@NonNull Context context,
            @Nullable String packageName) {
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
            } catch (Exception ignored) {
            }

            return dynamicAppInfo;
        }

        return null;
    }

    /**
     * Returns the intent filter to register various events.
     *
     * @return The intent filter to register a broadcast receiver which can listen special
     *         actions of the {@link DynamicEngine}.
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
     * Returns the intent filter to register the call event.
     *
     * @return The intent filter to register a broadcast receiver which can listen call events
     *         of the {@link DynamicEngine}.
     */
    public static @NonNull IntentFilter getCallIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_ON_CALL);
        intentFilter.addAction(ACTION_CALL_IDLE);

        return intentFilter;
    }

    /**
     * Returns the intent filter to register package intent.
     *
     * @return The intent filter to register a broadcast receiver which can listen package
     *         added or removed broadcasts.
     */
    public static @NonNull IntentFilter getPackageIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addDataScheme(PACKAGE_SCHEME);

        return intentFilter;
    }

    /**
     * Returns the correct type for the foreground event.
     *
     * @return The correct type for the foreground event.
     *
     * @see UsageEvents.Event#ACTIVITY_RESUMED
     * @see UsageEvents.Event#MOVE_TO_FOREGROUND
     */
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.Q)
    public static int getForegroundEventType() {
        if (!DynamicSdkUtils.is21()) {
            return EVENT_UNKNOWN;
        }

        return DynamicSdkUtils.is29() ? UsageEvents.Event.ACTIVITY_RESUMED
                : UsageEvents.Event.MOVE_TO_FOREGROUND;
    }

    /**
     * Retrieve the foreground package.
     *
     * @param usageStatsManager The usage stats manager instance.
     * @param time The start time to get the recent apps.
     * @param interval The interval for the requested events.
     *
     * @return The foreground package name on API 21 and above.
     */
    @TargetApi(Build.VERSION_CODES.Q)
    public static @Nullable String getForegroundPackage(
            @Nullable UsageStatsManager usageStatsManager, long time, long interval) {
        if (!DynamicSdkUtils.is21() || usageStatsManager == null) {
            return null;
        }

        String packageName = null;

        try {
            UsageEvents usageEvents = usageStatsManager.queryEvents(time - interval, time);
            UsageEvents.Event event = new UsageEvents.Event();

            if (usageEvents != null) {
                while (usageEvents.hasNextEvent()) {
                    usageEvents.getNextEvent(event);

                    if (event.getEventType() == getForegroundEventType()
                            && event.getTimeStamp() >= event.getTimeStamp()) {
                        packageName = event.getPackageName();
                    }
                }
            }

            // Alternate method
            if (DynamicSdkUtils.is29() && packageName == null) {
                List<UsageStats> usageStats = usageStatsManager.queryUsageStats(
                        UsageStatsManager.INTERVAL_BEST, time - interval, time);
                UsageStats usageStat = null;

                for (UsageStats usageStatsEntry : usageStats) {
                    if (usageStatsEntry.getTotalTimeVisible() > 0
                            && usageStatsEntry.getTotalTimeInForeground() > 0) {
                        if (usageStat == null) {
                            usageStat = usageStatsEntry;
                        } else if (usageStatsEntry.getLastTimeUsed()
                                >= usageStat.getLastTimeUsed()) {
                            usageStat = usageStatsEntry;
                        }
                    }
                }

                if (usageStat != null) {
                    packageName = usageStat.getPackageName();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return packageName;
    }

    /**
     * Retrieve the foreground package.
     *
     * @param activityManager The activity manager instance.
     *
     * @return The foreground package name on API 20 and below.
     */
    @SuppressWarnings("deprecation")
    public static @Nullable String getForegroundPackage(
            @Nullable ActivityManager activityManager) {
        if (activityManager == null) {
            return null;
        }

        ActivityManager.RunningTaskInfo runningTaskInfo =
                activityManager.getRunningTasks(1).get(0);

        if (runningTaskInfo.topActivity != null) {
            return runningTaskInfo.topActivity.getPackageName();
        }

        return null;
    }
}
