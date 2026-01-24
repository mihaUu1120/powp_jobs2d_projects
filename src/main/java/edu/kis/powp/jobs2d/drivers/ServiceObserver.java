package edu.kis.powp.jobs2d.drivers;

public interface ServiceObserver {
    void updateServiceState(int usageCount, int maxUsage);
}