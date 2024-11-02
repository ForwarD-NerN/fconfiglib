package ru.nern.fconfiglib.v1.validation;

import ru.nern.fconfiglib.v1.ConfigManager;
import ru.nern.fconfiglib.v1.api.annotations.restrictions.ValidateOption;
import ru.nern.fconfiglib.v1.api.annotations.validation.OptionValidator;
import ru.nern.fconfiglib.v1.api.annotations.validation.ValidateField;
import ru.nern.fconfiglib.v1.log.LoggerWrapper;
import ru.nern.fconfiglib.v1.utils.ValueReference;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/*
 * Deprecated. Use {@link ru.nern.fconfiglib.v1.validation.FieldConfigValidator} instead
 */
@Deprecated
public class OptionConfigValidator extends AbstractConfigValidator {
    @Override
    public <T, R> void validate(ConfigManager<T, R> manager, R raw, int lastLoadedVersion) throws ReflectiveOperationException {
        if(this.invokeOptionValidators(manager.config(), manager.config(), manager.getLogger())) {
            manager.save();
        }
    }

    public boolean invokeOptionValidators(Object configInstance, Object current, LoggerWrapper logger) throws ReflectiveOperationException {
        boolean saveConfig = false;

        for (Field field : current.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(ValidateOption.class)) {
                saveConfig = validateOption(configInstance, current, field);
            } else if(!field.getType().isPrimitive() && field.getType() != String.class && !field.getType().isAssignableFrom(Collection.class)) {
                Object fieldValue = field.get(current);

                if (fieldValue != null) {
                    saveConfig = invokeOptionValidators(configInstance, fieldValue, logger);
                }
            }
        }
        return saveConfig;
    }

    public boolean validateOption(Object configInstance, Object current, Field field) throws ReflectiveOperationException {
        ValidateOption annotation = field.getAnnotation(ValidateOption.class);
        OptionValidator<Object> optionValidator = (OptionValidator<Object>) annotation.value().getDeclaredConstructor().newInstance();
        optionValidator.validate(configInstance);

        return optionValidator.shouldSaveConfig();
    }

    @Override
    public int getExecutionPriority() {
        return 30;
    }
}
