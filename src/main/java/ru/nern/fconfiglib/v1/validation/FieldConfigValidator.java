package ru.nern.fconfiglib.v1.validation;

import ru.nern.fconfiglib.v1.ConfigManager;
import ru.nern.fconfiglib.v1.api.annotations.validation.ValidateField;
import ru.nern.fconfiglib.v1.log.LoggerWrapper;
import ru.nern.fconfiglib.v1.utils.ValueReference;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

public class FieldConfigValidator extends AbstractConfigValidator {
    @Override
    public <T, R> void validate(ConfigManager<T, R> manager, R raw, int lastLoadedVersion) throws ReflectiveOperationException {
        if(this.invokeFieldValidators(manager.config(), manager.config(), manager.getLogger())) {
            manager.save();
        }
    }

    public boolean invokeFieldValidators(Object configInstance, Object current, LoggerWrapper logger) throws ReflectiveOperationException {
        boolean saveConfig = false;

        for (Field field : current.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(ValidateField.class)) {
                try {
                    saveConfig = validateField(configInstance, current, field);
                }catch (IllegalArgumentException e) {
                    logger.error("Unable to validate field:", e);
                }
            } else if(!field.getType().isPrimitive() && field.getType() != String.class && !field.getType().isAssignableFrom(Collection.class)) {
                Object fieldValue = field.get(current);

                if (fieldValue != null) {
                    saveConfig = invokeFieldValidators(configInstance, fieldValue, logger);
                }
            }
        }
        return saveConfig;
    }

    @SuppressWarnings("unchecked")
    public boolean validateField(Object configInstance, Object current, Field field) throws ReflectiveOperationException {
        ValidateField annotation = field.getAnnotation(ValidateField.class);
        Object fieldValue = field.get(current);

        FieldValidator<?, ?> validator = annotation.value().getDeclaredConstructor().newInstance();
        Type[] genericTypes = ((ParameterizedType) validator.getClass().getGenericInterfaces()[0]).getActualTypeArguments();

        Class<?> expectedFieldType = (Class<?>) genericTypes[0];
        if (fieldValue != null && !expectedFieldType.isInstance(fieldValue)) {
            throw new IllegalArgumentException("Field value type doesn't match with the FieldValidator value type. Expected: " + expectedFieldType.getName() + ", got: " + fieldValue.getClass().getName());
        }

        Class<?> expectedConfigType = (Class<?>) genericTypes[1];
        if (!expectedConfigType.isInstance(configInstance)) {
            throw new IllegalArgumentException("Config type doesn't match with the FieldValidator type. Expected: " + expectedConfigType.getName() + ", got: " + configInstance.getClass().getName());
        }


        ValueReference<Object> reference = new ValueReference<>(fieldValue);
        ((FieldValidator<Object, Object>) validator).validate(reference, configInstance);

        if(reference.hasChanged()) {
            field.set(current, reference.get());
            return true;
        }
        return false;
    }

    @Override
    public int getExecutionPriority() {
        return 25;
    }
}
