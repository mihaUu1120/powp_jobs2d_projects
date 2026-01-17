package edu.kis.powp.jobs2d.features;

import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;

@FunctionalInterface
public interface DriverConfigurationStrategy {
  VisitableJob2dDriver configure(String name, VisitableJob2dDriver driver);
}
