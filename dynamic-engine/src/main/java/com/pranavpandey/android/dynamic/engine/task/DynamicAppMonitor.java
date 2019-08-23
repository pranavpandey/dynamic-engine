/*
 * Copyright 2019 Pranav Pandey
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.pranavpandey.android.dynamic.engine.model.DynamicAppInfo;
import com.pranavpandey.android.dynamic.engine.service.DynamicEngine;
import com.pranavpandey.android.dynamic.engine.utils.DynamicEngineUtils;
import com.pranavpandey.android.dynamic.utils.DynamicVersionUtils;

/**
 * AsyncTask to monitor foreground to provide app specific functionality.
 *
 * <p><p>Package must be granted {@link android.Manifest.permission#PACKAGE_USAGE_STATS}
 * permission to detect the foreground app on API 21 and above devices.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@TargetApi(Build.VERSION_CODES.Q)
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
    private static final int ADE_THREAD_SLEEP_INTERVAL = 300;

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
     * {@code true} if this task is paused.
     */
    private boolean mPaused;

    /**
     * Dynamic app info for the foreground package.
     */
    private DynamicAppInfo mDynamicAppInfo;

    /**
     * Activity manager to detect foreground package activities.
     */
    private ActivityManager mActivityManager;

    /**
     * UsageStatsManager to detect foreground package on API 21 and above devices.
     *
     * <p><p>Package must be granted {@link android.Manifest.permission#PACKAGE_USAGE_STATS}
     * permission to detect foreground app on API 21 and above devices.
     */
    private UsageStatsManager mUsageStatsManager;

    /**
     * Constructor to initialize an object of this class.
     *
     * @param dynamicEngine The dynamic engine using which is using this task.
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
        while (mRunning) {
            try {
                if (!mPaused) {
                    DynamicAppInfo dynamicAppInfo = getForegroundAppInfo();
                    if (dynamicAppInfo != null && dynamicAppInfo.getPackageName() != null
                            && (mDynamicAppInfo == null || !mDynamicAppInfo.equals(dynamicAppInfo))) {
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
     * Get the running status of this task.
     *
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
     * Get the paused status of this task.
     *
     * @return {@code true} if this task is paused.
     */
    public boolean isPaused() {
        return mPaused;
    }

    /**
     * Set the paused status of this task.
     *
     * @param paused {@code true} if this task is paused.
     */
    public void setPaused(boolean paused) {
        this.mPaused = paused;
    }

    /**
     * Get the current dynamic app info.
     *
     * @return The current dynamic app info.
     */
    public @Nullable DynamicAppInfo getCurrentAppInfo() {
        return mDynamicAppInfo;
    }

    /**
     * Set the current dynamic app info.
     *
     * @param dynamicAppInfo The current dynamic app info to be set.
     */
    public void setCurrentAppInfo(@Nullable DynamicAppInfo dynamicAppInfo) {
        this.mDynamicAppInfo = dynamicAppInfo;
    }

    /**
     * Retrieve the dynamic app info for the foreground package.
     *
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
     * Retrieve the foreground package.
     *
     * @param time The start time to get the recent apps.
     * @param interval The interval for the requested events.
     *
     * @return The foreground package name on API 21 and above devices.
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
