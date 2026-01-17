package edu.kis.powp.jobs2d.features;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.drivers.UsageTrackingDriverDecorator;

/**
 * Provides monitoring capabilities for tracked drivers. Users can select drivers
 * with monitoring enabled from the driver menu, and use this feature to view
 * usage summaries and reset counters.
 */
public class MonitoringFeature implements IFeature {

    private static Map<String, UsageTrackingDriverDecorator> monitoredDrivers = new HashMap<>();
    private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public MonitoringFeature() {
    }

    public MonitoringFeature(Logger customLogger) {
        if (customLogger != null) {
            logger = customLogger;
        }
    }

    @Override
    public void setup(Application app) {
        app.addComponentMenu(MonitoringFeature.class, "Monitoring", 0);
        app.addComponentMenuElement(MonitoringFeature.class, "Report usage summary", MonitoringFeature::logUsage);
        app.addComponentMenuElement(MonitoringFeature.class, "Reset counters", MonitoringFeature::resetCounters);
    }

    public static void registerMonitoredDriver(String label, UsageTrackingDriverDecorator driver) {
        monitoredDrivers.put(label, driver);
    }

    private static void logUsage(ActionEvent e) {
        if (monitoredDrivers.isEmpty()) {
            logger.info("Monitoring: no monitored drivers registered");
            return;
        }
        logger.info("=== Usage Summary ===");
        for (Map.Entry<String, UsageTrackingDriverDecorator> entry : monitoredDrivers.entrySet()) {
            UsageTrackingDriverDecorator driver = entry.getValue();
            logger.info(String.format("[%s] travel=%.2f, ink=%.2f",
                    driver.getLabel(), driver.getTravelDistance(), driver.getDrawingDistance()));
        }
    }

    private static void resetCounters(ActionEvent e) {
        if (monitoredDrivers.isEmpty()) {
            logger.info("Monitoring: no monitored drivers registered");
            return;
        }
        for (UsageTrackingDriverDecorator driver : monitoredDrivers.values()) {
            driver.reset();
        }
        logger.info("Monitoring: all counters reset");
    }

    @Override
    public String getName() {
        return "Monitoring";
    }
}
