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

package com.pranavpandey.android.dynamic.engine.model;

import com.pranavpandey.android.dynamic.engine.DynamicEngine;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Events supported by the {@link DynamicEngine}.
 */
@Retention(RetentionPolicy.SOURCE)
public @interface DynamicEvent {

    /**
     * Constant for no event.
     */
    String NONE = "-1";

    /**
     * Constant for the call event.
     */
    String CALL = "0";

    /**
     * Constant for the lock event.
     */
    String LOCK = "1";

    /**
     * Constant for the headset event.
     */
    String HEADSET = "2";

    /**
     * Constant for the charging event.
     */
    String CHARGING = "3";

    /**
     * Constant for the dock event.
     */
    String DOCK = "4";

    /**
     * Constant for the app event.
     */
    String APP = "5";
}
