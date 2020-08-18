package com.volvocars.clusterinterface;

import android.content.ComponentName;
import android.content.Context;

public class ClusterConstants {
    public static final String LOCAL_BINDING_ACTION = "local";
    public static final ComponentName SERVICE_COMPONENT = new ComponentName("com.volvocars.clusterservice", "com.volvocars.clusterservice.ClusterRenderingServiceImpl");

    public static final String ACTION_CLUSTER_EVENT = "com.volvocars.clusterservice.ACTION_CLUSTER_EVENT";

    public static final String EXTRA_EVENT_TYPE= "com.volvocars.clusterservice.CLUSTER_EVENT_TYPE";
    public static final String EXTRA_BUTTON_PRESS_TYPE = "com.volvocars.clusterservice.BUTTON_PRESS_TYPE";
    public static final String EXTRA_BUTTON_CODE = "com.volvocars.clusterservice.BUTTON_CODE";

    public static final String BUTTON_PRESS_TYPE_UP = "com.volvocars.clusterservice.BUTTON_PRESS_TYPE_UP";
    public static final String BUTTON_PRESS_TYPE_DOWN = "com.volvocars.clusterservice.BUTTON_PRESS_TYPE_DOWN";


    public static final int EVENT_ASSISTANT = 10;
    public static final int EVENT_CONTEXT_SWITCH = 20;
    public static final int EVENT_ENTER = 30;
    public static final int EVENT_UP = 40;
    public static final int EVENT_DOWN = 50;
    public static final int EVENT_LEFT = 60;
    public static final int EVENT_RIGHT = 70;
}
