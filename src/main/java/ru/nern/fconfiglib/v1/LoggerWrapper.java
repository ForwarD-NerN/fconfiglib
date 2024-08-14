package ru.nern.fconfiglib.v1;

public interface LoggerWrapper {
    LoggerWrapper DEFAULT = new LoggerWrapper() {
        @Override
        public void info(String message) {
            System.out.println(message);
        }

        @Override
        public void warn(String message) {
            System.out.println("[WARN] " + message);
        }

        @Override
        public void error(String message) {
            System.err.println(message);
        }
    };

    void info(String message);
    void warn(String message);
    void error(String message);
}