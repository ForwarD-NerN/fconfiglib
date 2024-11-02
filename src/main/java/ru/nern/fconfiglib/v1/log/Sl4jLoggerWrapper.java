package ru.nern.fconfiglib.v1.log;

import org.slf4j.Logger;

public class Sl4jLoggerWrapper {
    public static LoggerWrapper createFrom(Logger logger) {
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

            @Override
            public void error(String message, Throwable throwable) {
                logger.error(message, throwable);
            }
        };
    }
}
