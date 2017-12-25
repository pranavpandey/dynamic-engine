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

package com.pranavpandey.android.dynamic.engine.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.pranavpandey.android.dynamic.utils.DynamicDeviceUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;

/**
 * Helper class to manage priority of the different events in case
 * two or more events will occur simultaneously.
 */
public class DynamicPriority {

    /**
     * Shared preference key for the engine preferences.
     */
    private static final String DAS_PREF_ENGINE = "das_engine_preferences";

    /**
     * Shared preference key for the event priorities.
     */
    private static final String DAS_PREF_EVENTS_PRIORITY = "das_pref_events_priority";

    /**
     * Priority splitter to separate different events.
     */
    public static final String PRIORITY_SPLIT = ",";

    /**
     * Constant for the call event.
     *
     * @deprecated Use {@link DynamicEvent#CALL}.
     */
    @Deprecated
    public static final String EVENT_CALL = "0";

    /**
     * Constant for the lock event.
     *
     * @deprecated Use {@link DynamicEvent#LOCK}.
     */
    @Deprecated
    public static final String EVENT_LOCK = "1";

    /**
     * Constant for the headset event.
     *
     * @deprecated Use {@link DynamicEvent#HEADSET}.
     */
    @Deprecated
    public static final String EVENT_HEADSET = "2";

    /**
     * Constant for the charging event.
     *
     * @deprecated Use {@link DynamicEvent#CHARGING}.
     */
    @Deprecated
    public static final String EVENT_CHARGING = "3";

    /**
     * Constant for the dock event.
     *
     * @deprecated Use {@link DynamicEvent#DOCK}.
     */
    @Deprecated
    public static final String EVENT_DOCK = "4";

    /**
     * Constant for the app event.
     *
     * @deprecated Use {@link DynamicEvent#APP}.
     */
    @Deprecated
    public static final String EVENT_APP = "5";

    /**
     * Default priority for the events.
     *
     * <br />1. Call (highest)
     * <br />2. Lock
     * <br />3. Headset
     * <br />4. Charging
     * <br />5. Dock
     * <br />6. App (lowest)
     */
    private static final String DAS_DEFAULT_EVENTS_PRIORITY = DynamicEvent.DOCK
            + PRIORITY_SPLIT + DynamicEvent.CHARGING + PRIORITY_SPLIT
            + DynamicEvent.HEADSET + PRIORITY_SPLIT + DynamicEvent.LOCK
            + PRIORITY_SPLIT + DynamicEvent.CALL;

    /**
     * Get shared preferences of the app engine for a given context.
     *
     * @param context Context to get shared preferences.
     *
     * @see Context
     */
    private static SharedPreferences getSharedPreferences(@NonNull Context context) {
       return context.getSharedPreferences(DAS_PREF_ENGINE, MODE_PRIVATE);
    }

    /**
     * Reset events priority to default.
     *
     * @param context Context to get shared preferences.
     *
     * @see #DAS_DEFAULT_EVENTS_PRIORITY
     */
    public static void resetPriority(@NonNull Context context) {
        getSharedPreferences(context).edit().clear().apply();
    }

    /**
     * Save events priority.
     *
     * @param context Context to get shared preferences.
     * @param eventsPriority ArrayList containing events priority.
     */
    public static void saveEventsPriority(@NonNull Context context,
                                          ArrayList<String> eventsPriority) {
        Collections.reverse(eventsPriority);
        StringBuilder priorities = new StringBuilder();
        for (int i = 0; i < eventsPriority.size(); i++) {
            priorities.append(eventsPriority.get(i)).append(PRIORITY_SPLIT);
        }

        getSharedPreferences(context).edit().clear().putString(
                DAS_PREF_EVENTS_PRIORITY, priorities.toString()).apply();
    }

    /**
     * Get default events priority after checking the telephony functionality.
     *
     * @param context Context to get shared preferences.
     *
     * @return Default events priority.
     */
    public static ArrayList<String> getDefaultEventsPriority(Context context) {
        return returnAfterDeviceCheck(context, new ArrayList<>(
                convertStringToArrayList(DAS_DEFAULT_EVENTS_PRIORITY)));
    }

    /**
     * Get saved events priority after checking the device for telephony
     * and per app functionality.
     *
     * @param context Context to get shared preferences.
     *
     * @return Saved events priority.
     */
    public static ArrayList<String> getEventsPriority(Context context) {
        return returnAfterDeviceCheck(context,
                new ArrayList<>(convertStringToArrayList(getSharedPreferences(context)
                        .getString(DAS_PREF_EVENTS_PRIORITY, DAS_DEFAULT_EVENTS_PRIORITY))));
    }

    /**
     * Get events priority after checking the device for telephony and
     * per app functionality.
     *
     * @param context Context to get shared preferences.
     * @param eventsPriority ArrayList containing events priority.
     *
     * @return Events priority after device check.
     */
    private static ArrayList<String> returnAfterDeviceCheck(Context context,
                                                            ArrayList<String> eventsPriority) {
        if (!DynamicDeviceUtils.hasTelephony(context)
                && eventsPriority.contains(DynamicEvent.CALL)) {
            eventsPriority.remove(DynamicEvent.CALL);
        }

        if (!eventsPriority.contains(DynamicEvent.APP)) {
            eventsPriority.add(0, DynamicEvent.APP);
        }

        return eventsPriority;
    }

    /**
     * Convert string to array list according to the priority splitter.
     * It will be used for the easy retrieval.
     *
     * @return String converted from the ArrayList.
     */
    private static ArrayList<String> convertStringToArrayList(String string) {
        return new ArrayList<>(Arrays.asList(string.split(PRIORITY_SPLIT)));
    }
}
