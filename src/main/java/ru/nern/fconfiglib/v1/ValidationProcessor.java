package ru.nern.fconfiglib.v1;

import ru.nern.fconfiglib.v1.api.annotations.ConfigValidator;
import ru.nern.fconfiglib.v1.validators.AbstractConfigValidator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ValidationProcessor {
    public static <T, R> void invokeValidators(ConfigManager<T, R> manager, R raw, int lastLoadedVersion) throws Exception {
        ConfigValidator validatorAnnotation = manager.getClass().getAnnotation(ConfigValidator.class);
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
        }
    }
}
