/*
 * Copyright 2017-2020 Pranav Pandey
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

import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Collection of various properties for a given package for an easy data interchange.
 */
public class DynamicAppInfo implements Parcelable {

    /**
     * Application info.
     */
    private ApplicationInfo applicationInfo;

    /**
     * Package name.
     */
    private String packageName;

    /**
     * Top activity component name.
     */
    private ComponentName topActivity;

    /**
     * Application label or name.
     */
    private String label;

    /**
     * Default constructor to initialize the dynamic app info.
     */
    public DynamicAppInfo() { }

    /**
     * Parcelable creator to create from parcel.
     */
    public static final Parcelable.Creator<DynamicAppInfo> CREATOR =
            new Parcelable.Creator<DynamicAppInfo>() {
        @Override
        public DynamicAppInfo createFromParcel(Parcel in) {
            return new DynamicAppInfo(in);
        }

        @Override
        public DynamicAppInfo[] newArray(int size) {
            return new DynamicAppInfo[size];
        }
    };

    /**
     * Read an object of this class from the parcel.
     *
     * @param in The parcel to read the values.
     */
    public DynamicAppInfo(Parcel in) {
        this.applicationInfo = in.readParcelable(ApplicationInfo.class.getClassLoader());
        this.topActivity = in.readParcelable(ComponentName.class.getClassLoader());
        this.packageName = in.readString();
        this.label = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(applicationInfo, flags);
        dest.writeParcelable(topActivity, flags);
        dest.writeString(packageName);
        dest.writeString(label);
    }

    /**
     * Get the application info.
     *
     * @return The application info.
     */
    public @Nullable ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    /**
     * Set the application info.
     *
     * @param applicationInfo The application info to be set.
     */
    public void setApplicationInfo(@Nullable ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    /**
     * Get the package name.
     *
     * @return The package name.
     */
    public @Nullable String getPackageName() {
        return packageName;
    }

    /**
     * Set the package name.
     *
     * @param packageName The package name to be set.
     */
    public void setPackageName(@Nullable String packageName) {
        this.packageName = packageName;
    }

    /**
     * Get the top activity component name.
     *
     * @return The top activity component name.
     */
    public @Nullable ComponentName getTopActivity() {
        return topActivity;
    }

    /**
     * Set the top activity component name.
     *
     * @param topActivity The top activity component name to be set.
     */
    public void setTopActivity(@Nullable ComponentName topActivity) {
        this.topActivity = topActivity;
    }

    /**
     * Get the application label or name.
     *
     * @return The application label or name.
     */
    public @Nullable String getLabel() {
        return label;
    }

    /**
     * Set the application label or name.
     *
     * @param label The application label or name to be set.
     */
    public void setLabel(@Nullable String label) {
        this.label = label;
    }

    /**
     * Compare the object of this class with another object.
     *
     * @param dynamicAppInfo The other DynamicAppInfo to compare.
     *
     * @return {@code true} if the two objects are equal.
     */
    public boolean equals(@NonNull DynamicAppInfo dynamicAppInfo) {
        return !(getPackageName() != null && dynamicAppInfo.getPackageName() != null)
                || getPackageName().equals(dynamicAppInfo.getPackageName());
    }
}
