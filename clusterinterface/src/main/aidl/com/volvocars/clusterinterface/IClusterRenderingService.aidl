// IClusterRenderingService.aidl
package com.volvocars.clusterinterface;


interface IClusterRenderingService {
    void setNavigationActivityLaunchOptions(int displayId, in Bundle clusterActivityStateBundle);
    Bundle getClusterActivityOptions();
}
