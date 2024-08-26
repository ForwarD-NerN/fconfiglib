package ru.nern.fconfiglib.v1.validation;

import ru.nern.fconfiglib.v1.ConfigManager;
import ru.nern.fconfiglib.v1.api.annotations.validation.ConfigValidators;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ValidationProcessor {
    public static <T, R> void invokeValidators(ConfigManager<T, R> manager, R raw, int lastLoadedVersion) throws Exception {
        ConfigValidators validatorAnnotation = manager.getType().getAnnotation(ConfigValidators.class);
        if(validatorAnnotation != null) {
            List<AbstractConfigValidator> validators = new ArrayList<>();

            for(Class<? extends AbstractConfigValidator> validator : validatorAnnotation.value()) {
                validators.add(validator.getDeclaredConstructor().newInstance());
            }
            validators.sort(Comparator.comparing(AbstractConfigValidator::getExecutionPriority));

            for(AbstractConfigValidator validator : validators) {
                validator.validate(manager, raw, lastLoadedVersion);
                if(validator.isCancelled()) return;
            }
        }else{
            new VersionConfigValidator().validate(manager, raw, lastLoadedVersion);
        }
    }
}
