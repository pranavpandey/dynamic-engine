/*
 * Copyright 2017-2021 Pranav Pandey
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

package com.pranavpandey.android.dynamic.engine.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.pranavpandey.android.dynamic.engine.task.DynamicAppMonitor;
import com.pranavpandey.android.dynamic.util.DynamicSdkUtils;

/**
 * Sticky service which will restart automatically if killed by the system.
 * <p>Useful in low memory or similar situations where we need to run the service continuously
 * in the background.
 */
public abstract class DynamicStickyService extends AccessibilityService {

    /**
     * Default interval after which try to restart the service.
     */
    public static final long ADE_DEFAULT_RESTART_INTERVAL = 2000;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            AccessibilityManager am = ContextCompat.getSystemService(
                    this, AccessibilityManager.class);

            if (am != null) {
                am.addAccessibilityStateChangeListener(
                        new AccessibilityManager.AccessibilityStateChangeListener() {
                            @Override
                            public void onAccessibilityStateChanged(boolean enabled) {
                                DynamicStickyService.this.onAccessibilityStateChanged(enabled);
                            }
                        });
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * Get restart interval after which try to restart the service.
     * <p>Override this method in the extended class to change the default interval.
     *
     * @return The Interval in milliseconds after which service will be restarted.
     *
     * @see #ADE_DEFAULT_RESTART_INTERVAL
     */
    protected long getRestartInterval() {
        return ADE_DEFAULT_RESTART_INTERVAL;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) { }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        if (DynamicSdkUtils.is16()) {
            AccessibilityServiceInfo info = getServiceInfo();
            if (info != null) {
                info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                        | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
                info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
                info.notificationTimeout = DynamicAppMonitor.ADE_NOTIFICATION_TIMEOUT;
                setServiceInfo(info);
            }
        }
    }

    @Override
    public void onInterrupt() { }

    /**
     * Called back on change in the accessibility state.
     *
     * @param enabled Whether accessibility is enabled.
     */
    protected void onAccessibilityStateChanged(boolean enabled) { }
}
