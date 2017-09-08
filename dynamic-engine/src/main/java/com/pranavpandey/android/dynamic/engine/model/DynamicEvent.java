package com.pranavpandey.android.dynamic.engine.model;

import android.support.annotation.StringDef;

import static com.pranavpandey.android.dynamic.engine.model.DynamicEvent.EVENT_APP;
import static com.pranavpandey.android.dynamic.engine.model.DynamicEvent.EVENT_CALL;
import static com.pranavpandey.android.dynamic.engine.model.DynamicEvent.EVENT_CHARGING;
import static com.pranavpandey.android.dynamic.engine.model.DynamicEvent.EVENT_DOCK;
import static com.pranavpandey.android.dynamic.engine.model.DynamicEvent.EVENT_HEADSET;
import static com.pranavpandey.android.dynamic.engine.model.DynamicEvent.EVENT_LOCK;
import static com.pranavpandey.android.dynamic.engine.model.DynamicEvent.EVENT_NONE;

/**
 * Events supported by the
 * {@link com.pranavpandey.android.dynamic.engine.service.DynamicEngine}.
 */
@StringDef(value = { EVENT_NONE, EVENT_CALL, EVENT_LOCK, EVENT_HEADSET,
        EVENT_HEADSET, EVENT_CHARGING, EVENT_DOCK, EVENT_APP })
public @interface DynamicEvent {

    /**
     * Constant for no event.
     */
    String EVENT_NONE = "-1";

    /**
     * Constant for the call event.
     */
    String EVENT_CALL = "0";

    /**
     * Constant for the lock event.
     */
    String EVENT_LOCK = "1";

    /**
     * Constant for the headset event.
     */
    String EVENT_HEADSET = "2";

    /**
     * Constant for the charging event.
     */
    String EVENT_CHARGING = "3";

    /**
     * Constant for the dock event.
     */
    String EVENT_DOCK = "4";

    /**
     * Constant for the app event.
     */
    String EVENT_APP = "5";
}
