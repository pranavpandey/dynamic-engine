<img src="./graphics/icon.png" height="160">

# Dynamic Engine

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Release](https://img.shields.io/maven-central/v/com.pranavpandey.android/dynamic-engine)](https://search.maven.org/artifact/com.pranavpandey.android/dynamic-engine)

**A collection of tasks to monitor various events including call, lock, headset, charging, dock and 
foreground app via service on Android 2.3 (API 9) and above.**

> [!IMPORTANT]
> Since v0.4.0, it uses [26.x.x support libraries][android-support] so, minimum SDK will be 
Android 4.0 (API 14).
<br/>Since v2.0.0, it uses [AndroidX][androidx] so, first [migrate][androidx-migrate] 
your project to AndroidX.
<br/>Since v4.1.0, it is dependent on Java 8 due to the dependency on
[Dynamic Utils][dynamic-utils].
<br/>Since v4.4.1, it is targeting Java 17 to provide maximum compatibility.
<br/>Since v4.5.0, the minimum SDK is Android 4.4 (API 19) to comply with the latest policies.
<br/>Since v4.5.1, the minimum SDK is Android 5.0 (API 21) to comply with the latest policies.

---

## Contents

- [Installation](#installation)
- [Usage](#usage)
    - [Special events](#monitor-special-events)
    - [Foreground app](#monitor-foreground-app)
    - [Package state](#monitor-package-state)
    - [Events priority](#events-priority)
    - [Accessibility](#accessibility)
    - [Dependency](#dependency)
- [Projects](#built-with-dynamic)
- [License](#license)

---

## Installation

It can be installed by adding the following dependency to your `build.gradle` file:

```groovy
dependencies {
    // For AndroidX enabled projects.
    implementation 'com.pranavpandey.android:dynamic-engine:4.5.1'

    // For legacy projects.
    implementation 'com.pranavpandey.android:dynamic-engine:1.3.0'
}
```

---

## Usage

It is a collection of different tasks which can monitor various events by running a service in 
the background. Initially, it can monitor call, lock, headset, charging, dock and foreground app 
related events. I will do my best to add more tasks later.

> For a complete reference, please read the [documentation][documentation].

### Special events

Extend the `DynamicEngine` service and implement the interface functions to monitor call, lock,
headset, charging and dock related events.

On Android 6.0 (API 23) and above, `READ_PHONE_STATE` permission must be granted for 
the app `package` to monitor call events. If this permission is not granted then,
`onCallStateChange(isCall)` method will never be called. For more information on the 
`runtime permissions`, please read official documentation [here][runtime permissions].

```java
public class MonitorService extends DynamicEngine {

    ...
    
    /**
     * This method will be called on initializing the service so that we can get the current
     * charging, headset and dock state.
     *
     * @param charging {@code true} if the device is charging or connected to a power source.
     * @param headset {@code true} if the device is connected to a headset or a audio output
     *                device.
     * @param docked {@code true} if the device is docked.
     */
    @Override
    public void onInitialize(boolean charging, boolean headset, boolean docked) {
        super.onInitialize(charging, headset, docked);

        // TODO: Do any work here.        
    }

    /**
     * This method will be called when call state is changed. 
     * Either on call or the device is idle.
     *
     * @param call {@code true} if the device is on call.
     *             Either ringing or answered.
     */
    @Override
    public void onCallStateChange(boolean call) {
        super.onCallStateChange(call);

        // TODO: Do any work here.        
    }

    /**
     * This method will be called when screen state is changed. 
     * Either the device screen is off or on.
     *
     * @param screenOff {@code true} if the device screen is off.
     */
    @Override
    public void onScreenStateChange(boolean screenOff) {
        super.onScreenStateChange(screenOff);

        // TODO: Do any work here.        
    }

    /**
     * This method will be called when lock state is changed. 
     * Either the device is in the locked or unlocked state independent of the PIN, 
     * password or any other security lock.
     *
     * @param locked {@code true} if the device is in the locked state or the lock screen is shown.
     */
    @Override
    public void onLockStateChange(boolean locked) {
        super.onLockStateChange(locked);

        // TODO: Do any work here.        
    }

    /**
     * This method will be called when headset state is changed. 
     * Either the device is connected to a audio output device or volume is routed through 
     * the internal speaker.
     *
     * @param connected {@code true} if the device is connected to a headset or a audio output
     *                  device.
     */
    @Override
    public void onHeadsetStateChange(boolean connected) {
        super.onHeadsetStateChange(connected);

        // TODO: Do any work here.        
    }

    /**
     * This method will be called when charging state is changed. 
     * Either the device is connected to a power source using the battery.
     *
     * @param charging {@code true} if the device is charging or connected to a power source.
     */
    @Override
    public void onChargingStateChange(boolean charging) {
        super.onChargingStateChange(charging);

        // TODO: Do any work here.        
    }

    /**
     * This method will be called when dock state is changed. 
     * Either the device is docked or not.
     *
     * @param docked {@code true} if the device is docked.
     */
    @Override
    public void onDockStateChange(boolean docked) {
        super.onDockStateChange(docked);

        // TODO: Do any work here.  
    }
    
    ...
}
```

### Foreground app

It can be used to monitor the foreground app to perform actions based on it. It is 
currently in `beta` stage so, more improvements will be done in the future.

It will not run by default to save resources. It should be started explicitly by calling the
`setAppMonitorTask(running)`.

On Android 5.0 (API 21) and above, `PACKAGE_USAGE_STATS` permission must be granted for 
the app `package` to monitor foreground app. If this permission is not granted then,
`onAppChange(dynamicAppInfo)` method will never be called. For more information on 
`UsageStatsManager`, please read the official documentation [here][usage stats manager].

```java
public class MonitorService extends DynamicEngine {

    ...
    
    /**
     * Enable or disable the foreground app monitor task.
     *
     * @param running {@code true} to start monitoring the foreground app and receive
     *                listener callback.
     */
    setAppMonitorTask(boolean running);
    
    ...
    
    /**
     * This method will be called when foreground app is changed. 
     * Use it to provide the app specific functionality in the app.
     *
     * @param dynamicAppInfo The dynamic app info of the foreground package.
     */
    @Override
    public void onAppChange(@Nullable DynamicAppInfo dynamicAppInfo) {
        super.onAppChange(dynamicAppInfo);

        // TODO: Do any work here.
    }
    
    ...
}
````

### Package state

It can be used to monitor app updates or new installs.

```java
public class MonitorService extends DynamicEngine {

    ...
    
    /**
     * This method will be called when an app package is added or changed. 
     * Useful to show a notification if an app is updated or a new app is installed.
     *
     * @param dynamicAppInfo The dynamic app info of the updated or added package.
     * @param newPackage {@code true} if the package is newly added.
     */
    @Override
    public void onPackageUpdated(@Nullable DynamicAppInfo dynamicAppInfo, boolean newPackage) {
        // TODO: Do any work here.
    }

    /**
     * This method will be called when an app package is removed. 
     * Useful to show some work when a package is removed.
     *
     * @param packageName The package which was removed.
     */
    @Override
    public void onPackageRemoved(@Nullable String packageName) {
        // TODO: Do any work here.
    }
    
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
     * 1. Call (highest)
     * 2. Lock
     * 3. Headset
     * 4. Charging
     * 5. Dock
     * 6. App (lowest)
     */
    
    /**
     * Save events priority.
     *
     * @param context The context to get shared preferences.
     * @param eventsPriority ArrayList containing events priority.
     */
    public static void saveEventsPriority(@NonNull Context context, 
        @NonNull ArrayList<String> eventsPriority);
    
    /**
     * Get saved events priority after checking the device for telephony and per app 
     * functionality.
     *
     * @param context The context to get shared preferences.
     *
     * @return The saved events priority.
     */
    public static @NonNull ArrayList<String> getEventsPriority(@NonNull Context context);
    
    /**
     * Reset events priority to default.
     *
     * @param context The context to get shared preferences.
     */
    public static void resetPriority(@NonNull Context context);
    
    ...

}
````

`DynamicEngine` has some useful functions to perform operations based on the events priority.

```java
public abstract class DynamicEngine {
    ...
    
    /**
     * Retrieve the current ongoing events.
     *
     * @return The list of current ongoing events.
     */
    protected @NonNull ArrayList<String> getCurrentEvents();
    
    /**
     * Get the event with highest priority.
     *
     * @return The highest priority event that has been occurred.
     */
    protected @DynamicEvent String getHighestPriorityEvent();

    ...
}
```

### Accessibility

It has support for [accessibility service][accessibility service] to provide a better experience. 
It also provides more accurate results when detecting the [foreground app][foreground app].

### Dependency

It depends on the [dynamic-utils][dynamic-utils] to perform
various internal operations. So, its functions can also be used to perform other useful operations.

---

<img src="./graphics/apps/pranavpandey-rotation.png" height="140">

## Built with Dynamic

Please email me if you are using this library and want to feature your app here. Also, please 
checkout the `Rotation` app to experience the full potential of this library.

- [Rotation][rotation]

---

## Author

Pranav Pandey

[![GitHub](https://img.shields.io/github/followers/pranavpandey?label=GitHub&style=social)](https://github.com/pranavpandey)
[![Follow on Twitter](https://img.shields.io/twitter/follow/pranavpandeydev?label=Follow&style=social)](https://twitter.com/intent/follow?screen_name=pranavpandeydev)
[![Donate via PayPal](https://img.shields.io/static/v1?label=Donate&message=PayPal&color=blue)](https://paypal.me/pranavpandeydev)

---

## License

    Copyright 2017-2025 Pranav Pandey

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


[android-support]: https://developer.android.com/topic/libraries/support-library/revisions.html#26-0-0
[androidx]: https://developer.android.com/jetpack/androidx
[androidx-migrate]: https://developer.android.com/jetpack/androidx/migrate
[documentation]: https://pranavpandey.github.io/dynamic-engine
[runtime permissions]: https://developer.android.com/training/permissions/requesting.html
[usage stats manager]: https://developer.android.com/reference/android/app/usage/UsageStatsManager.html
[accessibility service]: https://developer.android.com/guide/topics/ui/accessibility/service
[foreground app]: https://github.com/pranavpandey/dynamic-engine/blob/942aa452076c154a6fc3b9698d80b1164093958e/dynamic-engine/src/main/java/com/pranavpandey/android/dynamic/engine/DynamicEngine.java#L623
[dynamic-utils]: https://github.com/pranavpandey/dynamic-utils
[rotation]: https://play.google.com/store/apps/details?id=com.pranavpandey.rotation
