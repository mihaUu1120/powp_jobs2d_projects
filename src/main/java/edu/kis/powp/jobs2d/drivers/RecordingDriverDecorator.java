package edu.kis.powp.jobs2d.drivers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.kis.powp.jobs2d.Job2dDriver;
import edu.kis.powp.jobs2d.command.CompoundCommand;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;

public class RecordingDriverDecorator implements Job2dDriver {
    private final Job2dDriver targetDriver;
    private final CompoundCommand.Builder commandBuilder = new CompoundCommand.Builder();
    private CompoundCommand recordedCommands = null;

    public RecordingDriverDecorator(Job2dDriver targetDriver) {
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

    public void stopRecording() {
        recordedCommands = commandBuilder.build();
        setPosition(0, 0);
    }

    public CompoundCommand getRecordedCommands() {
        return this.recordedCommands;
    }

    @Override
    public String toString() {
        return "Recording Driver Decorator wrapping: " + targetDriver.toString();
    }
}
