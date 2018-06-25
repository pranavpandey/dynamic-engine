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

package com.pranavpandey.android.dynamic.engine.task;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

import com.pranavpandey.android.dynamic.engine.model.DynamicAppInfo;
import com.pranavpandey.android.dynamic.engine.service.DynamicEngine;
import com.pranavpandey.android.dynamic.engine.utils.DynamicEngineUtils;
import com.pranavpandey.android.dynamic.utils.DynamicVersionUtils;

/**
 * AsyncTask to monitor foreground to provide app specific functionality.
 *
 * <p>Package must be granted {@link android.Manifest.permission#PACKAGE_USAGE_STATS}
 * permission to detect foreground app on Android L and above devices.</p>
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
public class DynamicAppMonitor extends AsyncTask<Void, DynamicAppInfo, Void> {

    /**
     * Context constant for usage stats service.
     *
     * @see Context#USAGE_STATS_SERVICE
     */
    private static final String ADE_USAGE_STATS = "usagestats";

    /**
     * Default usage stats interval.
     */
    private static final int ADE_USAGE_STATS_INTERVAL = 50;

    /**
     * Default thread sleep interval.
     */
    private static final int ADE_THREAD_SLEEP_INTERVAL = 250;

    /**
     * Dynamic engine to initialize usage stats service.
     */
    @SuppressLint("StaticFieldLeak")
    private DynamicEngine mDynamicEngine;

    /**
     * {@code true} if this task is running.
     */
    private boolean mRunning;

    /**
     * Dynamic app info for the foreground package.
     */
    private DynamicAppInfo mDynamicAppInfo;

    /**
     * Activity manager to detect foreground package activities.
     */
    private ActivityManager mActivityManager;

    /**
     * UsageStatsManager to detect foreground package on Android L
     * and above devices.
     *
     * <p>Package must be granted {@link android.Manifest.permission#PACKAGE_USAGE_STATS}
     * permission to detect foreground app on Android L and above devices.</p>
     */
    private UsageStatsManager mUsageStatsManager;

    /**
     * Constructor to initialize DynamicAppMonitor for the give
     * DynamicEngine.
     *
     * @param dynamicEngine The dynamic engine using which is
     *                      using this task.
     */
    @SuppressLint("WrongConstant")
    public DynamicAppMonitor(@NonNull DynamicEngine dynamicEngine) {
        this.mDynamicEngine = dynamicEngine;
        this.mActivityManager = (ActivityManager)
                dynamicEngine.getSystemService(Context.ACTIVITY_SERVICE);

        if (DynamicVersionUtils.isLollipop()) {
            if (DynamicVersionUtils.isLollipopMR1()) {
                this.mUsageStatsManager = (UsageStatsManager)
                        dynamicEngine.getSystemService(Context.USAGE_STATS_SERVICE);
            }

            if (mUsageStatsManager == null) {
                mUsageStatsManager = (UsageStatsManager)
                        dynamicEngine.getSystemService(ADE_USAGE_STATS);
            }
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mDynamicAppInfo = null;
    }

    @Override
    protected Void doInBackground(Void... params) {
        while(isRunning()) {
            try {
                DynamicAppInfo dynamicAppInfo = getForegroundAppInfo();
                if (dynamicAppInfo != null && dynamicAppInfo.getPackageName() != null) {
                    if (mDynamicAppInfo == null
                            || !mDynamicAppInfo.equals(dynamicAppInfo)) {
                        publishProgress(dynamicAppInfo);
                    }
                }

                Thread.sleep(ADE_THREAD_SLEEP_INTERVAL);
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(DynamicAppInfo... dynamicAppInfo) {
        super.onProgressUpdate(dynamicAppInfo);

        mDynamicAppInfo = dynamicAppInfo[0];
        mDynamicEngine.getSpecialEventListener().onAppChange(mDynamicAppInfo);
    }

    @Override
    protected void onPostExecute(Void param) {
        super.onPostExecute(param);

        mDynamicAppInfo = null;
        mDynamicEngine = null;
    }

    /**
     * @return {@code true} if this task is running.
     */
    public boolean isRunning() {
        return mRunning;
    }

    /**
     * Set the running status of this task.
     *
     * @param running {@code true} if this task is running.
     */
    public void setRunning(boolean running) {
        this.mRunning = running;
    }

    /**
     * @return The Dynamic app info for the foreground package.
     */
    public @Nullable DynamicAppInfo getCurrentAppInfo() {
        return mDynamicAppInfo;
    }

    /**
     * Set the dynamic app info.
     *
     * @param dynamicAppInfo The Dynamic app info for the foreground
     *                       package to be set.
     */
    public void setCurrentAppInfo(@Nullable DynamicAppInfo dynamicAppInfo) {
        this.mDynamicAppInfo = dynamicAppInfo;
    }

    /**
     * @return The dynamic app info from the foreground package name.
     */
    private @Nullable DynamicAppInfo getForegroundAppInfo() {
        String packageName = null;
        DynamicAppInfo dynamicAppInfo = null;

        if (DynamicVersionUtils.isLollipop()) {
            packageName = getForegroundPackage(
                    System.currentTimeMillis(), ADE_USAGE_STATS_INTERVAL);
        } else {
            @SuppressWarnings("deprecation")
            ActivityManager.RunningTaskInfo runningTaskInfo =
                    mActivityManager.getRunningTasks(1).get(0);
            if (runningTaskInfo.topActivity != null) {
                packageName = runningTaskInfo.topActivity.getPackageName();
            }
        }

        if (packageName != null) {
            dynamicAppInfo = DynamicEngineUtils.getAppInfoFromPackage(
                    mDynamicEngine, packageName);
        }

        return dynamicAppInfo;
    }

    /**
     * @return The foreground package name on Android L and above
     *         devices.
     *
     * @param time The start time to get the recent apps.
     * @param interval The interval for the requested events.
     */
    private @Nullable String getForegroundPackage(long time, long interval) {
        String packageName = null;

        UsageEvents usageEvents = mUsageStatsManager.queryEvents(time - interval * 1000, time);
        UsageEvents.Event event = new UsageEvents.Event();

        // get last event
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event);

            if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                packageName = event.getPackageName();
            }
        }

        return packageName;
    }
}
