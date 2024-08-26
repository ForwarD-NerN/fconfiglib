package ru.nern.fconfiglib.v1.validators;

import ru.nern.fconfiglib.v1.ConfigManager;

public class VersionConfigValidator extends AbstractConfigValidator {

    @Override
    public <T, R> void validate(ConfigManager<T, R> manager, R raw, int lastLoadedVersion) {
        if(lastLoadedVersion < manager.getConfigVersion()) {
            manager.applyFixers(raw, lastLoadedVersion);
            manager.load(raw);
            manager.save();
        }else if(lastLoadedVersion > manager.getConfigVersion()) {
            manager.logger.warn(manager.getModId() + " got downgraded. Creating a backup of the config...");
            manager.createBackup();
            manager.save();
        }
    }

    @Override
    public int getExecutionPriority() {
        return 1;
    }
}
