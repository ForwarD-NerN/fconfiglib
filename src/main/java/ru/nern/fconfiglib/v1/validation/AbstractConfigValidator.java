package ru.nern.fconfiglib.v1.validation;

import ru.nern.fconfiglib.v1.ConfigManager;

public abstract class AbstractConfigValidator {
    private boolean cancelled = false;

    public abstract <T, R> void validate(ConfigManager<T, R> manager, R raw, int lastLoadedVersion) throws Exception;

    public int getExecutionPriority() {
        return 100;
    }

    public final void cancelFurtherProcessing() {
        this.cancelled = true;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }
}
