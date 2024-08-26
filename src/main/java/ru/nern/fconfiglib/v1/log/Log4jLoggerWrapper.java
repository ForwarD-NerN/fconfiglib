package ru.nern.fconfiglib.v1.log;

import org.apache.logging.log4j.Logger;

public class Log4jLoggerWrapper {
    public static LoggerWrapper createFor(Logger logger) {
        return new LoggerWrapper() {
            @Override
            public void info(String message) {
                logger.info(message);
            }

            @Override
            public void warn(String message) {
                logger.warn(message);
            }

            @Override
            public void error(String message) {
                logger.error(message);
            }
        };
    }
}
