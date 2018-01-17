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

import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Collection of various properties for a given package for an easy
 * data interchange.
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
     * Parcelable creator to create from parcel.
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DynamicAppInfo createFromParcel(Parcel in) {
            return new DynamicAppInfo(in);
        }

        public DynamicAppInfo[] newArray(int size) {
            return new DynamicAppInfo[size];
        }
    };

    /**
     * De-parcel {@link DynamicAppInfo} object.
     */
    public DynamicAppInfo(Parcel in) {
        this.applicationInfo = in.readParcelable(ApplicationInfo.class.getClassLoader());
        this.topActivity = in.readParcelable(ComponentName.class.getClassLoader());
        this.packageName = in.readString();
        this.label = in.readString();
    }

    /**
     * Getter for {@link #applicationInfo}.
     */
    public @Nullable ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    /**
     * Setter for {@link #applicationInfo}.
     */
    public void setApplicationInfo(@Nullable ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    /**
     * Getter for {@link #topActivity}.
     */
    public @Nullable ComponentName getTopActivity() {
        return topActivity;
    }

    /**
     * Setter for {@link #topActivity}.
     */
    public void setTopActivity(@Nullable ComponentName topActivity) {
        this.topActivity = topActivity;
    }

    /**
     * Getter for {@link #packageName}.
     */
    public @Nullable String getPackageName() {
        return packageName;
    }

    /**
     * Setter for {@link #packageName}.
     */
    public void setPackageName(@Nullable String packageName) {
        this.packageName = packageName;
    }

    /**
     * Getter for {@link #label}.
     */
    public @Nullable String getLabel() {
        return label;
    }

    /**
     * Setter for {@link #label}.
     */
    public void setLabel(@Nullable String label) {
        this.label = label;
    }

    /**
     * Compare this DynamicAppInfo with other.
     *
     * @param dynamicAppInfo Other DynamicAppInfo to compare.
     *
     * @return {@code true} if the two DynamicAppInfo are equal.
     */
    public boolean equals(@NonNull DynamicAppInfo dynamicAppInfo) {
        return !(getPackageName() != null && dynamicAppInfo.getPackageName() != null)
                || getPackageName().equals(dynamicAppInfo.getPackageName());
    }
}
