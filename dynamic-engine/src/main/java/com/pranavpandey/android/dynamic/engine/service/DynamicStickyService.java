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

package com.pranavpandey.android.dynamic.engine.service;

import android.app.Service;
import android.content.Intent;

import androidx.annotation.Nullable;

/**
 * Sticky service which will restart automatically if killed by the system.
 * <p>Useful in low RAM or similar situations where we need to run the service continuously
 * in the background.
 */
public abstract class DynamicStickyService extends Service {

    /**
     * Default interval after which try to restart the service.
     */
    public static final long ADE_DEFAULT_RESTART_INTERVAL = 2000;

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
}
