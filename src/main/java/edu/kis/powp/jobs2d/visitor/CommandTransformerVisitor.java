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

    private TransformCords transformCords(DriverCommand cmd) {
        CapturingDriver driver = new CapturingDriver(strategy);
        cmd.execute(driver);
        return driver.getTransformedCoords();
    }

    @Override
    public void visit(SetPositionCommand cmd) {
        TransformCords cords = transformCords(cmd);
        transformedCommand = new SetPositionCommand(
                cords.x, cords.y
        );
    }

    @Override
    public void visit(OperateToCommand cmd) {
        TransformCords cords = transformCords(cmd);
        transformedCommand = new OperateToCommand(
                cords.x, cords.y
        );
    }

    @Override
    public void visit(ICompoundCommand compound) {
        List<DriverCommand> transformed = new ArrayList<>();
        Iterator<DriverCommand> iterator = compound.iterator();

        while (iterator.hasNext()) {
            DriverCommand cmd = iterator.next();
            cmd.accept(this);
            transformed.add(transformedCommand);
        }

        transformedCommand = CompoundCommand.fromListOfCommands(transformed, "Transformed Command");
    }

    private static class CapturingDriver implements Job2dDriver {

        private final TransformStrategy strategy;
        private TransformCords transformedCoords;


        public CapturingDriver(TransformStrategy strategy) {
            this.strategy = strategy;
        }

        @Override
        public void setPosition(int x, int y) {
            transformedCoords = strategy.transform(new TransformCords(x, y));
        }

        @Override
        public void operateTo(int x, int y) {
            transformedCoords = strategy.transform(new TransformCords(x, y));
        }

        public TransformCords getTransformedCoords() {
            return transformedCoords;
        }
    }
}
