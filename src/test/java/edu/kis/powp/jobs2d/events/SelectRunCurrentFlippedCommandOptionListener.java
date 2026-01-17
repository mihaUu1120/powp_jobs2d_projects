package edu.kis.powp.jobs2d.events;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.drivers.DriverManager;
import edu.kis.powp.jobs2d.drivers.transformation.FlipStrategy;
import edu.kis.powp.jobs2d.drivers.transformation.RotateStrategy;
import edu.kis.powp.jobs2d.features.CommandsFeature;
import edu.kis.powp.jobs2d.visitor.CommandTransformerVisitor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelectRunCurrentFlippedCommandOptionListener implements ActionListener {
    private DriverManager driverManager;

    public SelectRunCurrentFlippedCommandOptionListener(DriverManager driverManager) {
        this.driverManager = driverManager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DriverCommand command = CommandsFeature.getDriverCommandManager().getCurrentCommand();
        CommandTransformerVisitor scaler = new CommandTransformerVisitor(new FlipStrategy(false, true));
        command.accept(scaler);
        DriverCommand command_flipped = scaler.getTransformedCommand();
        command_flipped.execute(driverManager.getCurrentDriver());
    }
}
