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
     * Default constructor to initialize DynamicAppInfo..
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

    public DynamicAppInfo(Parcel in) {
        this.applicationInfo = in.readParcelable(ApplicationInfo.class.getClassLoader());
        this.topActivity = in.readParcelable(ComponentName.class.getClassLoader());
        this.packageName = in.readString();
        this.label = in.readString();
    }

    /**
     * Getter fot {@link #applicationInfo}.
     */
    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    /**
     * Setter fot {@link #applicationInfo}.
     */
    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    /**
     * Getter fot {@link #topActivity}.
     */
    public ComponentName getTopActivity() {
        return topActivity;
    }

    /**
     * Setter fot {@link #topActivity}.
     */
    public void setTopActivity(ComponentName topActivity) {
        this.topActivity = topActivity;
    }

    /**
     * Getter fot {@link #packageName}.
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Setter fot {@link #packageName}.
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Getter fot {@link #label}.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Setter fot {@link #label}.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Compare this DynamicAppInfo with other.
     *
     * @param dynamicAppInfo Other DynamicAppInfo to compare.
     *
     * @return {@code true} if the two DynamicAppInfo are equal.
     */
    public boolean equals(DynamicAppInfo dynamicAppInfo) {
        return getPackageName().equals(dynamicAppInfo.getPackageName());
    }
}
