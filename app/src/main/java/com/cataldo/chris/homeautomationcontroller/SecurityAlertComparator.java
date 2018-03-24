package com.cataldo.chris.homeautomationcontroller;

import java.util.Comparator;

/**
 * Created by Chris on 5/1/2016.
 */

public class SecurityAlertComparator implements Comparator<SecurityAlert> {
    public int compare(SecurityAlert o1, SecurityAlert o2) {
        int value1 = o1.date.compareTo(o2.date);
        if (value1 == 0) {
            return o1.time.compareTo(o2.time);
        }
        return (-1) * (value1);
    }
}