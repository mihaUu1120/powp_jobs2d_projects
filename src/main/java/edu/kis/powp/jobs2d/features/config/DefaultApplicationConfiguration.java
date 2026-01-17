package edu.kis.powp.jobs2d.features.config;

import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.features.CanvasFeature;
import edu.kis.powp.jobs2d.features.CommandsFeature;
import edu.kis.powp.jobs2d.features.DrawerFeature;
import edu.kis.powp.jobs2d.features.DriverFeature;
import edu.kis.powp.jobs2d.features.MonitoringFeature;
import java.util.logging.Logger;

public class DefaultApplicationConfiguration {
    
    public static void setupDefaultFeatures(Application app, Logger logger) {
        FeatureConfiguration config = new FeatureConfiguration()
            .addFeature(new DrawerFeature())
            .addFeature(new CanvasFeature())
            .addFeature(new CommandsFeature())
            .addFeature(new DriverFeature())
            .addFeature(new MonitoringFeature(logger));
        
        config.applyConfiguration(app);
    }
}
