package com.cataldo.chris.homeautomationcontroller;

import java.util.Comparator;

/**
 * Created by Chris on 4/20/2016.
 */

public class DeviceComparator implements Comparator<Device> {
    public int compare(Device o1, Device o2) {
        int value1 = o1.deviceType.compareTo(o2.deviceType);
        if (value1 == 0) {
            return o1.deviceName.compareTo(o2.deviceName);
        }
        return value1;
    }
}

