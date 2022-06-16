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

package com.pranavpandey.android.dynamic.engine;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.pranavpandey.android.dynamic.engine.listener.DynamicEventListener;
import com.pranavpandey.android.dynamic.engine.model.DynamicAppInfo;
import com.pranavpandey.android.dynamic.engine.model.DynamicEvent;
import com.pranavpandey.android.dynamic.engine.model.DynamicHinge;
import com.pranavpandey.android.dynamic.engine.model.DynamicPriority;
import com.pranavpandey.android.dynamic.engine.service.DynamicStickyService;
import com.pranavpandey.android.dynamic.engine.task.DynamicAppMonitor;
import com.pranavpandey.android.dynamic.engine.util.DynamicEngineUtils;
import com.pranavpandey.android.dynamic.util.DynamicDeviceUtils;
import com.pranavpandey.android.dynamic.util.DynamicSdkUtils;
import com.pranavpandey.android.dynamic.util.DynamicTaskUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to monitor various system events to provide event specific functionality in the app.
 * <p>Extend this service and implement the interface methods to monitor the different events.
 *
 * <p>Package must be granted {@link android.Manifest.permission_group#PHONE}
 * permission to listen call events on API 23 and above.
 *
 * <p>Package must be granted {@link android.Manifest.permission#PACKAGE_USAGE_STATS}
 * permission to detect the foreground app on API 21 and above.
 */
@TargetApi(Build.VERSION_CODES.R)
public abstract class DynamicEngine extends DynamicStickyService
        implements SensorEventListener, DynamicEventListener {

    /**
     * Intent extra for headset state.
     */
    private static final String ADE_EXTRA_HEADSET_STATE = "state";

    /**
     * Sensor manager to register listeners.
     */
    private SensorManager mSensorManager;

    /**
     * Keyguard manager to detect the lock screen state.
     */
    private KeyguardManager mKeyguardManager;

    /**
     * Task to monitor foreground app.
     */
    private DynamicAppMonitor mDynamicAppMonitor;

    /**
     * Broadcast receiver to receive special events.
     */
    private SpecialEventReceiver mSpecialEventReceiver;

    /**
     * The dynamic hinge state.
     */
    private @DynamicHinge int mHinge;

    /**
     * {@code true} if the device is on call. Either ringing or answered.
     */
    private boolean mCall;

    /**
     * {@code true} if the device screen is off.
     */
    private boolean mScreenOff;

    /**
     * {@code true} if the device is in the locked state or the lock screen is shown.
     */
    private boolean mLocked;

    /**
     * {@code true} if the device is connected to a headset or a audio output device.
     */
    private boolean mHeadset;

    /**
     * {@code true} if the device is charging or connected to a power source.
     */
    private boolean mCharging;

    /**
     * {@code true} if the device is docked.
     */
    private boolean mDocked;

    /**
     * Array list to store the events priority.
     */
    private List<String> mEventsPriority;

    /**
     * Hash map to store the active events.
     */
    private Map<String, String> mEventsMap;

    @Override
    public void onCreate() {
        super.onCreate();

        mSensorManager = ContextCompat.getSystemService(this, SensorManager.class);
        mKeyguardManager = ContextCompat.getSystemService(this, KeyguardManager.class);
        mDynamicAppMonitor = new DynamicAppMonitor(this);
        mSpecialEventReceiver = new SpecialEventReceiver();
        
        registerReceiver(mSpecialEventReceiver, DynamicEngineUtils.getEventsIntentFilter());
        registerReceiver(mSpecialEventReceiver, DynamicEngineUtils.getPackageIntentFilter());
        registerReceiver(mSpecialEventReceiver, DynamicEngineUtils.getCallIntentFilter());
        updateEventsPriority();

        mEventsMap = new LinkedHashMap<>();
        updateEventsMap(DynamicEvent.NONE, true);
    }

    /**
     * Initialize special events and check for some already occurred and ongoing events.
     */
    public void initializeEvents() {
        if (DynamicDeviceUtils.hasHingeFeature(this)) {
            mSensorManager.registerListener(this,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_HINGE_ANGLE),
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            setHinge(DynamicHinge.UNKNOWN);
        }

        Intent chargingIntent = registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (chargingIntent != null) {
            int status = chargingIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            mCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
        }

        Intent headsetIntent = registerReceiver(null,
                new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        if (headsetIntent != null) {
            mHeadset = headsetIntent.getIntExtra(ADE_EXTRA_HEADSET_STATE, -1) == 1;
        }

        Intent dockIntent = registerReceiver(null,
                new IntentFilter(Intent.ACTION_DOCK_EVENT));
        if (dockIntent != null) {
            mDocked = dockIntent.getIntExtra(Intent.EXTRA_DOCK_STATE, -1)
                    != Intent.EXTRA_DOCK_STATE_UNDOCKED;
        }

        onInitialize(mCharging, mHeadset, mDocked);
    }

    /**
     * Update the events priority.
     */
    public void updateEventsPriority() {
        mEventsPriority = DynamicPriority.getEventsPriority(this);
    }

    /**
     * Update the status of an event.
     *
     * @param event The event to update the status.
     * @param active {@code true} to if the event is currently active.
     */
    public void updateEventsMap(@DynamicEvent @NonNull String event, boolean active) {
        mEventsMap.remove(event);

        if (active) {
            mEventsMap.put(event, event);
        }
    }

    /**
     * Get the sensor manager used by this service.
     *
     * @return The sensor manager used by this service.
     */
    public @NonNull SensorManager getSensorManager() {
        return mSensorManager;
    }

    /**
     * Get the listener to listen special events.
     *
     * @return The listener to listen special events.
     */
    public @NonNull DynamicEventListener getSpecialEventListener() {
        return this;
    }

    /**
     * Get the task to monitor foreground app.
     *
     * @return The task to monitor foreground app.
     */
    public @NonNull DynamicAppMonitor getAppMonitor() {
        return mDynamicAppMonitor;
    }

    /**
     * Enable or disable the foreground app monitor task.
     *
     * @param running {@code true} to start monitoring the foreground app and receive
     *                listener callback.
     *
     * @see DynamicEventListener#onAppChange(DynamicAppInfo)
     */
    public void setAppMonitorTask(boolean running) {
        if (running) {
            if (mDynamicAppMonitor.isCancelled()) {
                mDynamicAppMonitor = new DynamicAppMonitor(this);
            }

            getAppMonitor().setRunning(true);
            DynamicTaskUtils.executeTask(getAppMonitor());
        } else {
            getAppMonitor().setRunning(false);
            DynamicTaskUtils.cancelTask(getAppMonitor(), true);
        }

        updateEventsMap(DynamicEvent.APP, getAppMonitor().isRunning());
    }

    /**
     * Pause or resume the foreground app monitor task.
     *
     * @param paused {@code true} to pause monitoring the foreground app and bo not receive
     *               listener callback.
     *
     * @see DynamicEventListener#onAppChange(DynamicAppInfo)
     */
    public void setAppMonitorTaskPaused(boolean paused) {
        getAppMonitor().setPaused(paused);
    }

    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(mSpecialEventReceiver);
            setAppMonitorTask(false);

            if (DynamicSdkUtils.is30()) {
                mSensorManager.unregisterListener(this);
            }
        } catch (Exception ignored) {
        }
        super.onDestroy();
    }

    public @DynamicHinge int getHinge() {
        return mHinge;
    }

    public void setHinge(@DynamicHinge int hinge) {
        if (hinge != getHinge()) {
            this.mHinge = hinge;
            
            onHingeStateChange(hinge);
        }
    }

    /**
     * Get the status of call event.
     *
     * @return {@code true} if the device is on call.
     *         <p>Either ringing or answered.
     */
    public boolean isCall() {
        return mCall;
    }

    /**
     * Set the status of call event.
     *
     * @param call {@code true} if the device is on call.
     *              <p>Either ringing or answered.
     */
    public void setCall(boolean call) {
        if (call != isCall()) {
            this.mCall = call;

            onCallStateChange(call);
        }
    }

    /**
     * Get the status of screen of event.
     *
     * @return {@code true} if the device screen is off.
     */
    public boolean isScreenOff() {
        return mScreenOff;
    }

    /**
     * Set the status of screen off event.
     *
     * @param screenOff {@code true} if the device screen is off.
     */
    public void setScreenOff(boolean screenOff) {
        if (screenOff != isScreenOff()) {
            this.mScreenOff = screenOff;

            onScreenStateChange(screenOff);
        }
    }

    /**
     * Get the status of lock event.
     *
     * @return {@code true} if the device is in the locked state or the lock screen is shown.
     */
    public boolean isLocked() {
        return mLocked;
    }

    /**
     * Set the status of lock event.
     *
     * @param locked {@code true} if the device is in the locked state or the lock screen
     *               is shown.
     */
    public void setLocked(boolean locked) {
        if (locked != isLocked()) {
            this.mLocked = locked;

            onLockStateChange(locked);
        }
    }

    /**
     * Get the status of the headset event.
     *
     * @return {@code true} if the device is connected to a headset or a audio output device.
     */
    public boolean isHeadset() {
        return mHeadset;
    }

    /**
     * Set the status of headset event.
     *
     * @param headset {@code true} if the device is connected to a headset or a audio output
     *                device.
     */
    public void setHeadset(boolean headset) {
        if (headset != isHeadset()) {
            this.mHeadset = headset;

            onHeadsetStateChange(headset);
        }
    }

    /**
     * Get the status of charging event.
     *
     * @return {@code true} if the device is charging or connected to a power source.
     */
    public boolean isCharging() {
        return mCharging;
    }

    /**
     * Set the status of charging event.
     *
     * @param charging {@code true} if the device is charging or connected to a power source.
     */
    public void setCharging(boolean charging) {
        if (charging != isCharging()) {
            this.mCharging = charging;

            onChargingStateChange(charging);
        }
    }

    /**
     * Get the status of dock event.
     *
     * @return {@code true} if the device is docked.
     */
    public boolean isDocked() {
        return mDocked;
    }

    /**
     * Set the status of dock event.
     *
     * @param docked {@code true} if the device is docked.
     */
    public void setDocked(boolean docked) {
        if (docked != isDocked()) {
            this.mDocked = docked;

            onDockStateChange(docked);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (DynamicSdkUtils.is30()
                && sensorEvent.sensor.getType() == Sensor.TYPE_HINGE_ANGLE) {
            final float[] value = sensorEvent.values;
            if (value == null || value.length == 0) {
                return;
            }

            if ((value[0] >= 90 && value[0] < 150) ||
                    (value[0] > 180 && value[0] <= 270)) {
                setHinge(DynamicHinge.HALF_EXPANDED);
            } else if ((value[0] >= 150 && value[0] <= 180)
                    || (value[0] > 270 && value[0] < 360)) {
                setHinge(DynamicHinge.FLAT);
            } else {
                setHinge(DynamicHinge.COLLAPSED);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }

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
    class SpecialEventReceiver extends BroadcastReceiver {

        private boolean isReplacing;

        @Override
        public void onReceive(@NonNull Context context, @Nullable Intent intent) {
            if (intent != null && intent.getAction() != null) {
                switch (intent.getAction()) {
                    case Intent.ACTION_POWER_CONNECTED:
                        setCharging(true);
                        break;
                    case Intent.ACTION_POWER_DISCONNECTED:
                        setCharging(false);
                        break;
                    case Intent.ACTION_HEADSET_PLUG:
                        setHeadset(intent.getIntExtra(ADE_EXTRA_HEADSET_STATE, 0) == 1);
                        break;
                    case Intent.ACTION_DOCK_EVENT:
                        setDocked(intent.getIntExtra(Intent.EXTRA_DOCK_STATE, -1)
                                != Intent.EXTRA_DOCK_STATE_UNDOCKED);
                        break;
                    case Intent.ACTION_SCREEN_OFF:
                        setScreenOff(true);
                        setAppMonitorTaskPaused(true);

                        if (mKeyguardManager != null) {
                            setLocked(isKeyguardLocked());
                        }
                        break;
                    case Intent.ACTION_SCREEN_ON:
                        setScreenOff(false);
                        setAppMonitorTaskPaused(false);

                        if (mKeyguardManager != null) {
                            setLocked(isKeyguardLocked());
                        }
                        break;
                    case Intent.ACTION_USER_PRESENT:
                        if (mKeyguardManager != null) {
                            setLocked(isKeyguardLocked());
                        }
                        break;
                    case Intent.ACTION_PACKAGE_REMOVED:
                        if (intent.getData() != null
                                && intent.getData().getSchemeSpecificPart() != null) {
                            isReplacing = intent.getBooleanExtra(
                                    Intent.EXTRA_REPLACING, false);

                            if (!isReplacing) {
                                onPackageRemoved(intent.getData().getSchemeSpecificPart());
                            }
                        }
                        break;
                    case Intent.ACTION_PACKAGE_ADDED:
                        if (intent.getData() != null
                                && intent.getData().getSchemeSpecificPart() != null) {
                            onPackageUpdated(DynamicEngineUtils.getAppInfoFromPackage(context,
                                    intent.getData().getSchemeSpecificPart()), !isReplacing);
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

        /**
         * Checks whether the keyguard is in the locked state.
         *
         * @return {@code true} if the keyguard is in the locked state.
         */
        @SuppressWarnings("deprecation")
        private boolean isKeyguardLocked() {
            if (mKeyguardManager == null) {
                return false;
            }

            return DynamicSdkUtils.is16() ? mKeyguardManager.isKeyguardLocked()
                    : mKeyguardManager.inKeyguardRestrictedInputMode();
        }
    }

    @CallSuper
    @Override
    public void onInitialize(boolean charging, boolean headset, boolean docked) {
        updateEventsMap(DynamicEvent.CHARGING, charging);
        updateEventsMap(DynamicEvent.HEADSET, headset);
        updateEventsMap(DynamicEvent.DOCK, docked);
    }

    @CallSuper
    @Override
    public void onHingeStateChange(@DynamicHinge int state) { }

    @CallSuper
    @Override
    public void onCallStateChange(boolean call) {
        updateEventsMap(DynamicEvent.CALL, call);
    }

    @CallSuper
    @Override
    public void onScreenStateChange(boolean screenOff) { }

    @CallSuper
    @Override
    public void onLockStateChange(boolean locked) {
        updateEventsMap(DynamicEvent.LOCK, locked);
    }

    @CallSuper
    @Override
    public void onHeadsetStateChange(boolean connected) {
        updateEventsMap(DynamicEvent.HEADSET, connected);
    }

    @CallSuper
    @Override
    public void onChargingStateChange(boolean charging) {
        updateEventsMap(DynamicEvent.CHARGING, charging);
    }

    @CallSuper
    @Override
    public void onDockStateChange(boolean docked) {
        updateEventsMap(DynamicEvent.DOCK, docked);
    }

    @CallSuper
    @Override
    public void onAppChange(@Nullable DynamicAppInfo dynamicAppInfo) {
        updateEventsMap(DynamicEvent.APP, getAppMonitor().isRunning());
    }

    @Override
    protected void onAccessibilityStateChanged(boolean enabled) {
        super.onAccessibilityStateChanged(enabled);

        getAppMonitor().setDormant(enabled);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        super.onAccessibilityEvent(event);

        getAppMonitor().setDormant(true);
        getAppMonitor().onAccessibilityEvent(event);
    }

    /**
     * Retrieve the current ongoing events.
     *
     * @return The list of current ongoing events.
     */
    protected @NonNull List<String> getCurrentEvents() {
        if (mEventsPriority == null) {
            updateEventsPriority();
        }

        List<String> currentEvents = new ArrayList<>();
        currentEvents.add(DynamicEvent.NONE);

        for (String eventPriority : mEventsPriority) {
            if (mEventsMap.containsKey(eventPriority)) {
                currentEvents.add(mEventsMap.get(eventPriority));
            }
        }

        return currentEvents;
    }

    /**
     * Get the event according to its priority.
     *
     * @param currentEvents The list of events.
     * @param priority The event priority to find event.
     *
     * @return The event according to its priority.
     */
    protected @DynamicEvent String getEventByPriority(@NonNull List<String> currentEvents,
            int priority) {
        if (!currentEvents.isEmpty() && priority > 0 && priority <= currentEvents.size()) {
            return currentEvents.get(currentEvents.size() - priority);
        } else {
            return DynamicEvent.NONE;
        }
    }

    /**
     * Get the event with highest priority.
     *
     * @return The highest priority event that has been occurred.
     */
    protected @DynamicEvent String getHighestPriorityEvent() {
        return getEventByPriority(getCurrentEvents(), 1);
    }
}
