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

package com.pranavpandey.android.dynamic.engine.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.pranavpandey.android.dynamic.utils.DynamicDeviceUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Helper class to manage priority of the different events in case two or more events will
 * occur simultaneously.
 */
public class DynamicPriority {

    /**
     * Shared preference key for the event priorities.
     */
    private static final String ADE_PREF_EVENTS_PRIORITY = "ade_pref_events_priority";

    /**
     * DynamicPriority splitter to separate different events.
     */
    public static final String ADE_PRIORITY_SPLIT = ",";

    /**
     * Default priority for the events.
     *
     * <p>{@code 1.} Call (highest)
     * <p>{@code 2.} Lock
     * <p>{@code 3.} Headset
     * <p>{@code 4.} Charging
     * <p>{@code 5.} Dock
     * <p>{@code 6.} App (lowest)
     */
    private static final String ADE_DEFAULT_EVENTS_PRIORITY = DynamicEvent.DOCK
            + ADE_PRIORITY_SPLIT + DynamicEvent.CHARGING + ADE_PRIORITY_SPLIT
            + DynamicEvent.HEADSET + ADE_PRIORITY_SPLIT + DynamicEvent.LOCK
            + ADE_PRIORITY_SPLIT + DynamicEvent.CALL;

    /**
     * Get shared preferences of the app engine for a given context.
     *
     * @param context The context to get shared preferences.
     *
     * @return The shared preferences of the app engine for a given context.
     */
    private static @NonNull SharedPreferences getSharedPreferences(@NonNull Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Reset events priority to default.
     *
     * @param context The context to get shared preferences.
     *
     * @see #ADE_DEFAULT_EVENTS_PRIORITY
     */
    public static void resetPriority(@NonNull Context context) {
        getSharedPreferences(context).edit().putString(
                ADE_PREF_EVENTS_PRIORITY, ADE_DEFAULT_EVENTS_PRIORITY).apply();
    }

    /**
     * Save events priority.
     *
     * @param context The context to get shared preferences.
     * @param eventsPriority ArrayList containing events priority.
     */
    public static void saveEventsPriority(@NonNull Context context,
            @NonNull ArrayList<String> eventsPriority) {
        Collections.reverse(eventsPriority);
        StringBuilder priorities = new StringBuilder();
        for (int i = 0; i < eventsPriority.size(); i++) {
            priorities.append(eventsPriority.get(i)).append(ADE_PRIORITY_SPLIT);
        }

        getSharedPreferences(context).edit().putString(
                ADE_PREF_EVENTS_PRIORITY, priorities.toString()).apply();
    }

    /**
     * Get default events priority after checking the telephony functionality.
     *
     * @param context The context to get shared preferences.
     *
     * @return The default events priority.
     */
    public static ArrayList<String> getDefaultEventsPriority(@NonNull Context context) {
        return returnAfterDeviceCheck(context, new ArrayList<>(
                convertStringToArrayList(ADE_DEFAULT_EVENTS_PRIORITY)));
    }

    /**
     * Get saved events priority after checking the device for telephony and per app
     * functionality.
     *
     * @param context The context to get shared preferences.
     *
     * @return The saved events priority.
     */
    public static @NonNull ArrayList<String> getEventsPriority(@NonNull Context context) {
        String eventsPriority = getSharedPreferences(context).getString(
                ADE_PREF_EVENTS_PRIORITY, ADE_DEFAULT_EVENTS_PRIORITY);
        if (eventsPriority == null) {
            eventsPriority = ADE_DEFAULT_EVENTS_PRIORITY;
        }

        return returnAfterDeviceCheck(context,
                new ArrayList<>(convertStringToArrayList(eventsPriority)));
    }

    /**
     * Get events priority after checking the device for telephony and per app functionality.
     *
     * @param context The context to get shared preferences.
     * @param eventsPriority The array list containing events priority.
     *
     * @return The events priority after device check.
     */
    private static @NonNull ArrayList<String> returnAfterDeviceCheck(@NonNull Context context,
            @NonNull ArrayList<String> eventsPriority) {
        if (!DynamicDeviceUtils.hasTelephony(context)) {
            eventsPriority.remove(DynamicEvent.CALL);
        }

        if (!eventsPriority.contains(DynamicEvent.APP)) {
            eventsPriority.add(0, DynamicEvent.APP);
        }

        return eventsPriority;
    }

    /**
     * Convert string to array list according to the priority splitter.
     * <p>It will be used for the easy retrieval.
     *
     * @return The array list converted from the string.
     */
    private static @NonNull ArrayList<String> convertStringToArrayList(@NonNull String string) {
        return new ArrayList<>(Arrays.asList(string.split(ADE_PRIORITY_SPLIT)));
    }
}
