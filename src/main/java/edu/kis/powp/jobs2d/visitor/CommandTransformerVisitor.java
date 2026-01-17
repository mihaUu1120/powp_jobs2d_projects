package edu.kis.powp.jobs2d.visitor;

import edu.kis.powp.jobs2d.command.*;
import edu.kis.powp.jobs2d.drivers.transformation.TransformCords;
import edu.kis.powp.jobs2d.drivers.transformation.TransformStrategy;
import edu.kis.powp.jobs2d.Job2dDriver;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class CommandTransformerVisitor implements CommandVisitor {

    private final TransformStrategy strategy;
    private DriverCommand transformedCommand;

    public CommandTransformerVisitor(TransformStrategy strategy) {
        this.strategy = strategy;
    }

    public DriverCommand getTransformedCommand() {
        return transformedCommand;
    }

    @Override
    public void visit(SetPositionCommand cmd) {
        // Capture coordinates using anonymous Job2dDriver
        final TransformCords[] coords = new TransformCords[1];

        Job2dDriver capturingDriver = new Job2dDriver() {
            @Override
            public void setPosition(int x, int y) {
                coords[0] = strategy.transform(new TransformCords(x, y));
            }

            @Override
            public void operateTo(int x, int y) {
                // Not called for SetPositionCommand
            }
        };

        cmd.execute(capturingDriver);
        // Use TransformCords public fields directly
        transformedCommand = new SetPositionCommand(coords[0].x, coords[0].y);
    }

    @Override
    public void visit(OperateToCommand cmd) {
        // Capture coordinates using anonymous Job2dDriver
        final TransformCords[] coords = new TransformCords[1];

        Job2dDriver capturingDriver = new Job2dDriver() {
            @Override
            public void setPosition(int x, int y) {
                // Not called for OperateToCommand
            }

            @Override
            public void operateTo(int x, int y) {
                coords[0] = strategy.transform(new TransformCords(x, y));
            }
        };

        cmd.execute(capturingDriver);
        // Use TransformCords public fields directly
        transformedCommand = new OperateToCommand(coords[0].x, coords[0].y);
    }

    @Override
    public void visit(ICompoundCommand compound) {
        // Transform all commands in the compound command
        List<DriverCommand> transformed = new ArrayList<>();
        Iterator<DriverCommand> iterator = compound.iterator();

        while (iterator.hasNext()) {
            DriverCommand cmd = iterator.next();
            cmd.accept(this);
            transformed.add(transformedCommand);
        }
        transformedCommand = CompoundCommand.fromListOfCommands(transformed, "Transformed Command");
    }
}
