package edu.kis.powp.jobs2d.drivers;

import edu.kis.powp.jobs2d.Job2dDriver;
import edu.kis.powp.jobs2d.command.CompoundCommand;
import edu.kis.powp.jobs2d.visitor.DriverVisitor;
import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;

public class RecordingDriverDecorator implements VisitableJob2dDriver {
    private final VisitableJob2dDriver targetDriver;
    private final CompoundCommand.Builder commandBuilder = new CompoundCommand.Builder();
    private CompoundCommand recordedCommands = null;

    public RecordingDriverDecorator(VisitableJob2dDriver targetDriver) {
        this.targetDriver = targetDriver;
    }

    @Override
    public void setPosition(int x, int y) {
        targetDriver.setPosition(x, y);
        commandBuilder.addSetPosition(x, y);
    }

    @Override
    public void operateTo(int x, int y) {
        targetDriver.operateTo(x, y);
        commandBuilder.addOperateTo(x, y);
    }

    // Methods used in listener START

    public void stopRecording() {
        recordedCommands = commandBuilder.build();
    }

    public CompoundCommand getRecordedCommands() {
        return this.recordedCommands;
    }

    public void resetRecording() {
        commandBuilder.clear();
        this.recordedCommands = null;
    }
    
    // Methods used in listener END

    public VisitableJob2dDriver getDelegate() {
        return this.targetDriver;
    }

    @Override
    public String toString() {
        return "Recording Driver Decorator wrapping: " + targetDriver.toString();
    }

    // Visitor pattern accept method
    @Override
    public void accept(DriverVisitor visitor) {
        visitor.visit(this);
    }
}
