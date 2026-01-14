package edu.kis.powp.jobs2d.events;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.kis.powp.jobs2d.command.CompoundCommand;
import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.manager.CommandManager;
import edu.kis.powp.jobs2d.drivers.RecordingDriverDecorator;
import edu.kis.powp.jobs2d.features.CommandsFeature;

public class SelectLoadRecordedCommandOptionListener implements ActionListener {
    private final RecordingDriverDecorator recordingDriver;
    private CompoundCommand commands = null;

    public SelectLoadRecordedCommandOptionListener(RecordingDriverDecorator recordingDriver) {
        this.recordingDriver = recordingDriver;
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        this.recordingDriver.stopRecording();
        this.commands = recordingDriver.getRecordedCommands();

        Iterator<DriverCommand> iterator = commands.iterator();
        List<DriverCommand> commandList = new ArrayList<>();
        while (iterator.hasNext()) {
            commandList.add(iterator.next());
        }
        
        CommandManager manager = CommandsFeature.getDriverCommandManager();
        manager.setCurrentCommand(commandList, "RecordedCommand");
    }
}
