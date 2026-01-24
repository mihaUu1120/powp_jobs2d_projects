package edu.kis.powp.jobs2d.drivers;

import edu.kis.powp.jobs2d.Job2dDriver;
import edu.kis.powp.jobs2d.visitor.DriverVisitor;
import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;
import java.util.ArrayList;
import java.util.List;

public class ServiceDriverDecorator implements VisitableJob2dDriver {

    private final Job2dDriver driver;
    private int operationCount = 0;
    private final int maxOperations;

    private final List<ServiceObserver> observers = new ArrayList<>();

    public ServiceDriverDecorator(Job2dDriver driver, int maxOperations) {
        this.driver = driver;
        this.maxOperations = maxOperations;
    }

    @Override
    public void setPosition(int x, int y) {
        if (operationCount >= maxOperations) {
            notifyObservers();
            return;
        }

        this.driver.setPosition(x, y);
        countOperation();
    }

    @Override
    public void operateTo(int x, int y) {
        if (operationCount >= maxOperations) {
            notifyObservers();
            return;
        }

        this.driver.operateTo(x, y);
        countOperation();
    }

    private void countOperation() {
        this.operationCount++;
        notifyObservers();
    }

    public void performMaintenance() {
        this.operationCount = 0;
        notifyObservers();
    }

    public void addObserver(ServiceObserver observer) {
        this.observers.add(observer);
        observer.updateServiceState(operationCount, maxOperations);
    }

    private void notifyObservers() {
        for (ServiceObserver observer : observers) {
            observer.updateServiceState(operationCount, maxOperations);
        }
    }

    @Override
    public String toString() {
        return "Service simulator";
    }

    @Override
    public void accept(DriverVisitor visitor) {
        if (driver instanceof VisitableJob2dDriver) {
            ((VisitableJob2dDriver) driver).accept(visitor);
        }
    }
}