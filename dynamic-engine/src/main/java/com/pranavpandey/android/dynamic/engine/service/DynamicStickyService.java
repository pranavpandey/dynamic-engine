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

package com.pranavpandey.android.dynamic.engine.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Sticky service which will restart automatically if killed by the
 * system. Useful in low RAM or similar situations where we need to
 * run the service continuously in the background.
 */
public abstract class DynamicStickyService extends Service {

    /**
     * Default interval after which try to restart the service.
     */
    public static final int DEFAULT_RESTART_INTERVAL = 2000;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartService = new Intent(this, this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                this, 1, restartService, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmService != null) {
            alarmService.set(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + getRestartInterval(), restartServicePI);
        }
        super.onTaskRemoved(rootIntent);
    }

    /**
     * Get restart interval after which try to restart the service.
     * Override this function int he extended class to change the
     * default interval.
     *
     * @return Interval in milliseconds after which service will be
     * restarted.
     *
     * @see #DEFAULT_RESTART_INTERVAL
     */
    protected int getRestartInterval() {
        return DEFAULT_RESTART_INTERVAL;
    }
}
