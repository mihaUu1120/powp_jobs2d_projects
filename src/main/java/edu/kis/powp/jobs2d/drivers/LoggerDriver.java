package edu.kis.powp.jobs2d.drivers;

import java.util.logging.Logger;
import edu.kis.powp.jobs2d.Job2dDriver;

public class LoggerDriver implements Job2dDriver {
    private final Logger logger;
    
    private int operationCount = 0;

    public LoggerDriver() {
        this(Logger.getLogger(Logger.GLOBAL_LOGGER_NAME));
    }

    public LoggerDriver(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void operateTo(int x, int y) {
        operationCount++;
        logger.info(String.format("operateTo: (%d, %d) [op: %d]", x, y, operationCount));
    }

    @Override
    public void setPosition(int x, int y) {
        operationCount++;
        logger.info(String.format("setPosition: (%d, %d) [op: %d]", x, y, operationCount));
    }

}