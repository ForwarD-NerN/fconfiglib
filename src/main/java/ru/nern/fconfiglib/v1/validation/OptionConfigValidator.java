package ru.nern.fconfiglib.v1.validation;

import ru.nern.fconfiglib.v1.ConfigManager;
import ru.nern.fconfiglib.v1.api.OptionValidator;
import ru.nern.fconfiglib.v1.api.annotations.restriction.*;

import java.lang.reflect.Field;

public class OptionConfigValidator extends AbstractConfigValidator {
    @Override
    public <T, R> void validate(ConfigManager<T, R> manager, R raw, int lastLoadedVersion) throws ReflectiveOperationException {
        if(this.invokeOptionValidators(manager.config(), manager.config())) {
            manager.save();
        }
    }

    /*
     * Returns false if at least one of the fields was invalid
     */
    public boolean invokeOptionValidators(Object root, Object obj) throws ReflectiveOperationException {
        boolean shouldSaveConfig = false;

        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(ValidateOption.class)) {
                ValidateOption annotation = field.getAnnotation(ValidateOption.class);
                OptionValidator<Object> optionValidator = (OptionValidator<Object>) annotation.value().getDeclaredConstructor().newInstance();
                optionValidator.validate(root);
                shouldSaveConfig = optionValidator.shouldSaveConfig();
            }else if(!field.getType().isPrimitive() && field.getType() != String.class) {
                Object fieldValue = field.get(obj);
                if (fieldValue != null) {
                    shouldSaveConfig = invokeOptionValidators(root, fieldValue);
                }
            }
        }
        return shouldSaveConfig;
    }

    @Override
    public int getExecutionPriority() {
        return 5;
    }
}
