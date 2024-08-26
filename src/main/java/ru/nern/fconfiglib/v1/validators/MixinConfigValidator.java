package ru.nern.fconfiglib.v1.validators;

import ru.nern.fconfiglib.v1.ConfigManager;
import java.util.HashSet;
import java.util.Set;

public class MixinConfigValidator extends AbstractConfigValidator {
    private final Set<String> enabledMixins = new HashSet<>();
    private boolean initialized = false;

    @Override
    public <T, R> void validate(ConfigManager<T, R> manager, R raw, int lastLoadedVersion) {
        enabledMixins.clear();
    }

    @Override
    public int getExecutionPriority() {
        return 5;
    }

    public boolean shouldApplyMixin(String mixinClassName) {
        if(!this.initialized) {
            throw new IllegalStateException("Mixin Config Helper has not yet been initialized. Is ConfigManager.init() called in preLoad entrypoint?");
        }
        return enabledMixins.contains(mixinClassName);
    }

}
