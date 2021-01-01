/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openjdk.cookbook;

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class SleepProbe implements SleepProbeMBean {
    private static final int SLEEP_MS = 100;
    private volatile long lastSleepSample;

    public static void main(String[] args) throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("org.openjdk.cookbook:type=SleepProbe");
        SleepProbe mbean = new SleepProbe();
        mbean.start();
        mbs.registerMBean(mbean, name);
        System.out.println("Started MBean");
        Thread.sleep(Long.MAX_VALUE);
    }

    @Override
    public long getActualSleepTime() {
        return lastSleepSample;
    }

    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while ( !Thread.currentThread().isInterrupted() ) {
                    try {
                        final long start = System.nanoTime();
                        Thread.sleep(SLEEP_MS);
                        final long end = System.nanoTime();

                        lastSleepSample = (long)((double)(end-start))/1000000;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }).start();
    }
}

