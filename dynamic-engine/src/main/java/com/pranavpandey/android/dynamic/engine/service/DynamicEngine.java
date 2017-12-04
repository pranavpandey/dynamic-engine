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

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.pranavpandey.android.dynamic.engine.listener.DynamicEventListener;
import com.pranavpandey.android.dynamic.engine.model.DynamicAppInfo;
import com.pranavpandey.android.dynamic.engine.model.DynamicEvent;
import com.pranavpandey.android.dynamic.engine.model.DynamicPriority;
import com.pranavpandey.android.dynamic.engine.task.DynamicAppMonitor;
import com.pranavpandey.android.dynamic.engine.utils.DynamicEngineUtils;
import com.pranavpandey.android.dynamic.utils.DynamicTaskUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Service to monitor various system events to provide event specific
 * functionality in the app. Just extend this service and implement
 * the interface functions to monitor different events.
 */
public abstract class DynamicEngine extends DynamicStickyService implements DynamicEventListener {

    /**
     * Intent extra for headset state.
     */
    private static final String EXTRA_HEADSET_STATE = "state";

    /**
     * Listener to listen special events.
     */
    private DynamicEventListener mDynamicEventListener;

    /**
     * Broadcast receiver to receive special events.
     */
    private SpecialEventReceiver mSpecialEventReceiver;

    /**
     * Task to monitor foreground app.
     */
    private DynamicAppMonitor mDynamicAppMonitor;

    /**
     * {@code true} if the device is charging or connected
     * to a power source.
     */
    private boolean isCharging;

    /**
     * {@code true} if the device is connected to a headset
     * or a audio output device.
     */
    private boolean isHeadset;

    /**
     * {@code true} if the device is docked.
     */
    private boolean isDocked;

    /**
     * {@code true} if the device is in the locked state or
     * the lock screen is shown.
     */
    private boolean isLocked;

    /**
     * {@code true} if the device is on call. Either ringing
     * or answered.
     */
    private boolean isCall;

    @Override
    public void onCreate() {
        super.onCreate();

        mDynamicEventListener = this;
        mSpecialEventReceiver = new SpecialEventReceiver();
        mDynamicAppMonitor = new DynamicAppMonitor(this);

        registerReceiver(mSpecialEventReceiver, DynamicEngineUtils.getEventsIntentFilter());
        registerReceiver(mSpecialEventReceiver, DynamicEngineUtils.getPackageIntentFilter());
    }

    /**
     * Initialize special events and check for some already occurred and
     * ongoing events.
     */
    public void initializeEvents() {
        Intent chargingIntent = registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (chargingIntent != null) {
            int status = chargingIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
        }

        Intent headsetIntent = registerReceiver(null,
                new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        if (headsetIntent != null) {
            isHeadset = headsetIntent.getIntExtra(EXTRA_HEADSET_STATE, -1) == 1;
        }

        Intent dockIntent = registerReceiver(null,
                new IntentFilter(Intent.ACTION_DOCK_EVENT));
        if (dockIntent != null) {
            isDocked = dockIntent.getIntExtra(Intent.EXTRA_DOCK_STATE, -1)
                    != Intent.EXTRA_DOCK_STATE_UNDOCKED;
        }

        mDynamicEventListener.onInitialize(isCharging, isHeadset, isDocked);
    }

    /**
     * Getter for {@link #mDynamicEventListener}.
     */
    public DynamicEventListener getSpecialEventListener() {
        return mDynamicEventListener;
    }

    /**
     * Getter for {@link #mDynamicAppMonitor}.
     */
    public DynamicAppMonitor getAppMonitor() {
        return mDynamicAppMonitor;
    }

    /**
     * Enable or disable foreground app monitor.
     *
     * @param isRunning {@code true} to start monitoring the foreground app
     *                  and receive listener callback.
     *
     * @see DynamicEventListener#onAppChange(DynamicAppInfo)
     */
    public void setAppMonitorTask(boolean isRunning) {
        if (isRunning) {
            mDynamicAppMonitor = new DynamicAppMonitor(this);
            mDynamicAppMonitor.setRunning(true);
            DynamicTaskUtils.executeTask(mDynamicAppMonitor);
        } else {
            if (mDynamicAppMonitor != null) {
                mDynamicAppMonitor.setRunning(false);
                DynamicTaskUtils.cancelTask(mDynamicAppMonitor);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mSpecialEventReceiver);
        setAppMonitorTask(false);
    }

    /**
     * Getter for {@link #isCall}.
     */
    public boolean isCall() {
        return isCall;
    }

    /**
     * Setter for {@link #isCall}.
     */
    public void setCall(boolean call) {
        if (call != isCall()) {
            this.isCall = call;
            mDynamicEventListener.onCallStateChange(call);
        }
    }

    /**
     * Getter for {@link #isLocked}.
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Setter for {@link #isLocked}.
     */
    public void setLocked(boolean locked) {
        if (locked != isLocked()) {
            this.isLocked = locked;
            mDynamicEventListener.onLockStateChange(locked);
        }
    }

    /**
     * Getter for {@link #isHeadset}.
     */
    public boolean isHeadset() {
        return isHeadset;
    }

    /**
     * Setter for {@link #isHeadset}.
     */
    public void setHeadset(boolean headset) {
        if (headset != isHeadset()) {
            this.isHeadset = headset;
            mDynamicEventListener.onHeadsetStateChange(headset);
        }
    }

    /**
     * Getter for {@link #isCharging}.
     */
    public boolean isCharging() {
        return isCharging;
    }

    /**
     * Setter for {@link #isCharging}.
     */
    public void setCharging(boolean charging) {
        if (charging != isCharging) {
            this.isCharging = charging;
            mDynamicEventListener.onChargingStateChange(charging);
        }
    }

    /**
     * Getter for {@link #isDocked}.
     */
    public boolean isDocked() {
        return isDocked;
    }

    /**
     * Setter for {@link #isDocked}.
     */
    public void setDocked(boolean docked) {
        if (docked != isDocked()) {
            this.isDocked = docked;
            mDynamicEventListener.onDockStateChange(docked);
        }
    }

    /**
     * Broadcast receiver to listen various events.
     *
     * @see Intent#ACTION_POWER_CONNECTED
     * @see Intent#ACTION_POWER_DISCONNECTED
     * @see Intent#ACTION_HEADSET_PLUG
     * @see Intent#ACTION_DOCK_EVENT
     * @see Intent#ACTION_SCREEN_OFF
     * @see Intent#ACTION_SCREEN_ON
     * @see Intent#ACTION_PACKAGE_ADDED
     * @see Intent#ACTION_PACKAGE_REMOVED
     * @see DynamicEngineUtils#ACTION_ON_CALL
     * @see DynamicEngineUtils#ACTION_CALL_IDLE
     */
    private class SpecialEventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case Intent.ACTION_POWER_CONNECTED:
                        setCharging(true);
                        break;
                    case Intent.ACTION_POWER_DISCONNECTED:
                        setCharging(false);
                        break;
                    case Intent.ACTION_HEADSET_PLUG:
                        setHeadset(intent.getIntExtra(EXTRA_HEADSET_STATE, 0) == 1);
                        break;
                    case Intent.ACTION_DOCK_EVENT:
                        setDocked(intent.getIntExtra(Intent.EXTRA_DOCK_STATE, -1)
                                != Intent.EXTRA_DOCK_STATE_UNDOCKED);
                        break;
                    case Intent.ACTION_SCREEN_OFF:
                    case Intent.ACTION_SCREEN_ON:
                    case Intent.ACTION_USER_PRESENT:
                        KeyguardManager keyguardManager = (KeyguardManager)context
                                .getSystemService(Context.KEYGUARD_SERVICE);
                        if (keyguardManager != null) {
                            setLocked(keyguardManager.inKeyguardRestrictedInputMode());
                        }
                        break;
                    case Intent.ACTION_PACKAGE_REMOVED:
                        break;
                    case Intent.ACTION_PACKAGE_ADDED:
                        if (intent.getData() != null
                                && intent.getData().getSchemeSpecificPart() != null) {
                            mDynamicEventListener.onPackageUpdated(
                                    DynamicEngineUtils.getAppInfoFromPackage(
                                            context, intent.getData().getSchemeSpecificPart()),
                                    !intent.getBooleanExtra(Intent.EXTRA_REPLACING, false));
                        }
                        break;
                    case DynamicEngineUtils.ACTION_ON_CALL:
                        setCall(true);
                        break;
                    case DynamicEngineUtils.ACTION_CALL_IDLE:
                        setCall(false);
                        break;
                }
            }
        }
    }

    /**
     * Get a list of current ongoing events.
     */
    protected ArrayList<String> getCurrentEvents() {
        ArrayList<String> currentEvents = new ArrayList<>();
        ArrayList<String> eventsPriority = DynamicPriority.getEventsPriority(this);

        for (String eventPriority : eventsPriority) {
            switch (eventPriority) {
                case DynamicEvent.EVENT_CALL:
                    if (isCall()) {
                        currentEvents.add(DynamicEvent.EVENT_CALL);
                    }
                    break;
                case DynamicEvent.EVENT_LOCK:
                    if (isLocked()) {
                        currentEvents.add(DynamicEvent.EVENT_LOCK);
                    }
                    break;
                case DynamicEvent.EVENT_HEADSET:
                    if (isHeadset()) {
                        currentEvents.add(DynamicEvent.EVENT_HEADSET);
                    }
                    break;
                case DynamicEvent.EVENT_CHARGING:
                    if (isCharging()) {
                        currentEvents.add(DynamicEvent.EVENT_CHARGING);
                    }
                    break;
                case DynamicEvent.EVENT_DOCK:
                    if (isDocked()) {
                        currentEvents.add(DynamicEvent.EVENT_DOCK);
                    }
                    break;
                case DynamicEvent.EVENT_APP:
                    if (getAppMonitor().isRunning()){
                        currentEvents.add(DynamicEvent.EVENT_APP);
                    }
                    break;
            }
        }

        if (currentEvents.isEmpty()) {
            currentEvents.add(DynamicEvent.EVENT_NONE);
        }
        return currentEvents;
    }

    /**
     * Get the highest priority event.
     */
    protected @DynamicEvent String getHighestPriorityEvent() {
        List<String> currentEvents = getCurrentEvents();

        if (!currentEvents.isEmpty()) {
            return currentEvents.get(currentEvents.size() - 1);
        } else {
            return null;
        }
    }
}
