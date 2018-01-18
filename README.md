<img src="https://raw.githubusercontent.com/pranavpandey/dynamic-engine/master/graphics/dynamic-engine_512x512.png" width="160" height="160" align="right" hspace="20">

# Dynamic Engine

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Build Status](https://travis-ci.org/pranavpandey/dynamic-engine.svg?branch=master)](https://travis-ci.org/pranavpandey/dynamic-engine)
[![Download](https://api.bintray.com/packages/pranavpandey/android/dynamic-engine/images/download.svg)](https://bintray.com/pranavpandey/android/dynamic-engine/_latestVersion)

A collection of tasks to monitor various events including call, lock, headset, charging, dock and 
foreground app via service on Android 9+ (Gingerbread or above) devices.

> Since v0.4.0, it uses [26.x.x support libraries](https://developer.android.com/topic/libraries/support-library/revisions.html#26-0-0)
so, minimum SDK will be Android 14+ (ICS or above).

---

## Contents

- [Installation](https://github.com/pranavpandey/dynamic-engine#installation)
- [Usage](https://github.com/pranavpandey/dynamic-engine#usage)
    - [Monitor special events](https://github.com/pranavpandey/dynamic-engine#monitor-special-events)
    - [Monitor foreground app](https://github.com/pranavpandey/dynamic-engine#monitor-foreground-app)
    - [Monitor package state](https://github.com/pranavpandey/dynamic-engine#monitor-package-state)
    - [Events priority](https://github.com/pranavpandey/dynamic-engine#events-priority)
    - [Dependency](https://github.com/pranavpandey/dynamic-engine#dependency)
- [License](https://github.com/pranavpandey/dynamic-engine#license)

---

## Installation

It can be installed by adding the following dependency to your `build.gradle` file:

```groovy
dependencies {
    implementation 'com.pranavpandey.android:dynamic-engine:0.8.0'
}
```

---

## Usage

It is a collection of different tasks which can monitor various events by running a service in 
the background. Initially, can monitor call, lock, headset, charging, dock and foreground related
events. I will do my best to add more tasks later.

### Monitor special events

Extend the `DynamicEngine` service and implement the interface functions to monitor monitor 
call, lock, headset, charging and dock related events.

On Android M (Marshmallow) or above devices, `READ_PHONE_STATE` permission must be granted for 
the app `package` to monitor call events. If this permission is not granted then,
`onCallStateChange(isCall)` method will never be called. For more information on the 
`runtime permissions`, please read official documentation [here](https://developer.android.com/training/permissions/requesting.html).

```java
public class MonitorService extends DynamicEngine {

    ...
    
    /**
     * On initialize the service so that we can get the current
     * charging, headset and dock state.
     *
     * @param isCharging {@code true} if the device is charging or connected
     *                   to a power source.
     * @param isHeadset {@code true} if the device is connected to a headset
     *                  or a audio output device.
     * @param isDocked {@code true} if the device is docked.
     */
    @Override
    public void onInitialize(isCharging, isHeadset, isDocked) {
    
    }
    
    /**
     * On call state changed. Either on call or the device is idle.
     *
     * @param isCall {@code true} if the device is on call. Either ringing
     *               or answered.
     */    
    @Override
    public void onCallStateChange(isCall) {
    
    }
    
    /**
     * On lock state changed. Either the device is in the locked or unlocked
     * state independent of the PIN, password or any other security lock.
     *
     * @param isLocked {@code true} if the device is in the locked state or
     *                  the lock screen is shown.
     */
    @Override
    public void onLockStateChange(isLocked) {
    
    }
    
    /**
     * On headset state changed. Either the device is connected to a audio
     * output device or volume is routed through the internal speaker.
     *
     * @param isConnected {@code true} if the device is connected to a headset
     *                    or a audio output device.
     */
    @Override
    public void onHeadsetStateChange(isConnected) {
    
    }
    
    /**
     * On charging state changed. Either the device is connected to a power
     * source using the battery.
     *
     * @param isCharging {@code true} if the device is charging or connected
     *                   to a power source.
     */
    @Override
    public void onChargingStateChange(isCharging) {
    
    }
    
    /**
     * On dock state changed. Either the device is docked or not.
     *
     * @param isDocked {@code true} if the device is docked.
     */
    @Override
    public void onDockStateChange(isDocked) {
    
    }
    
    ...
}
```

### Monitor foreground app

It can be used to monitor the foreground app to perform actions based on it. It is 
currently in `beta` stage so, more improvements will be done in the future.

It will not run by default to save resources. It should be started explicitly by calling the
`setAppMonitor(isRunning)`.

On Android L (Lollipop) or above devices, `PACKAGE_USAGE_STATS` permission must be granted for 
the app `package` to monitor foreground app. If this permission is not granted then,
`onAppChange(dynamicAppInfo)` method will never be called. For more information on 
`UsageStatsManager`, please read the official documentation [here](https://developer.android.com/reference/android/app/usage/UsageStatsManager.html).

```java
public class MonitorService extends DynamicEngine {

    ...
    
    /**
     * Enable or disable foreground app monitor.
     *
     * @param isRunning {@code true} to start monitoring the foreground app
     *                  and receive listener callback.
     *
     * @see DynamicEventListener#onAppChange(DynamicAppInfo)
     */
    setAppMonitor(isRunning);
    
    ...
    
    /**
     * On foreground app changed. Use it to provide the app specific
     * functionality in the app.
     *
     * @param dynamicAppInfo {@link DynamicAppInfo} of the foreground package.
     */
    @Override
    public void onAppChange(dynamicAppInfo);
    
    ...
}
````

### Monitor package state

It can be used to monitor app updates or new installs.

```java
public class MonitorService extends DynamicEngine {

    ...
    
    /**
     * On app package added or changed. Useful to show a notification if
     * an app is updated or a new app is installed.
     *
     * @param dynamicAppInfo {@link DynamicAppInfo} of the updated or added package.
     * @param isNewPackage {@code true} if the package is newly added.
     */
    public void onPackageUpdated(dynamicAppInfo, isNewPackage);
    
    ...
}
````

### Events priority

It can be used to manage priority of the different events in case two or more events will occur 
simultaneously. Use `DynamicPriority` class to set or retrieve events priority.

```java
public class DynamicPriority {
    
    ...

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
    
    /**
     * Save events priority.
     *
     * @param context Context to get shared preferences.
     * @param eventsPriority ArrayList containing events priority.
     */
    public static void saveEventsPriority(context, eventsPriority)
    
    /**
     * Get saved events priority after checking the device for telephony
     * and per app functionality.
     *
     * @param context Context to get shared preferences.
     *
     * @return Saved events priority.
     */
    public static ArrayList<String> getEventsPriority(context)
    
    /**
     * Reset events priority to default.
     *
     * @param context Context to get shared preferences.
     */
    public static void resetPriority(context)
    
    ...

}
````

`DynamicEngine` has some useful functions to perform operations based on the events priority.

```java
public abstract class DynamicEngine {
    ...
    
    /**
     * Get a list of current ongoing events.
     */
    protected ArrayList<String> getCurrentEvents()
    
    /**
     * Get the highest priority event.
     */
    protected String getHighestPriorityEvent()

    ...
}
```

### Dependency

It depends on the [dynamic-utils](https://github.com/pranavpandey/dynamic-utils) to perform
various internal operations. So, its functions can also be used to perform other useful operations.

---

## License

    Copyright (c) 2017 Pranav Pandey

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
