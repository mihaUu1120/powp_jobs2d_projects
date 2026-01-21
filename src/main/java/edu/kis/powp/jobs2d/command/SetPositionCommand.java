package edu.kis.powp.jobs2d.command;

import edu.kis.powp.jobs2d.Job2dDriver;
import edu.kis.powp.jobs2d.visitor.CommandVisitor;

/**
 * Implementation of Job2dDriverCommand for setPosition command functionality.
 */
public class SetPositionCommand implements DriverCommand {

    private int posX, posY;

    public SetPositionCommand(int posX, int posY) {
        super();
        this.posX = posX;
        this.posY = posY;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    @Override
    public void execute(Job2dDriver driver) {
        driver.setPosition(posX, posY);
    }

    @Override
    public DriverCommand copy() {
        return new SetPositionCommand(posX, posY);
    }

    /**
     * Accepts a visitor and calls its visit method for this command.
     * @param visitor the visitor to accept.
     */
    @Override
    public void accept(CommandVisitor visitor) {
        visitor.visit(this);
    }
}
