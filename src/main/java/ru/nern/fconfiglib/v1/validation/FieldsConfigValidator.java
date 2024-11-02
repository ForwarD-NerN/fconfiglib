package ru.nern.fconfiglib.v1.validation;

import ru.nern.fconfiglib.v1.ConfigManager;
import ru.nern.fconfiglib.v1.api.annotations.validation.ValidateField;
import ru.nern.fconfiglib.v1.log.LoggerWrapper;
import ru.nern.fconfiglib.v1.utils.ReflectionUtils;
import ru.nern.fconfiglib.v1.utils.ValueReference;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class FieldsConfigValidator extends AbstractConfigValidator {
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
                    logger.error("Error occured while validating field:", e);
                }
            } else if(ReflectionUtils.shouldCheckFields(field)) {
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

        Constructor<?> constructor = annotation.value().getDeclaredConstructor();
        constructor.setAccessible(true);

        FieldValidator<?, ?> validator = (FieldValidator<?, ?>) constructor.newInstance();
        checkTypes(configInstance, validator, fieldValue);

        ValueReference<Object> reference = new ValueReference<>(fieldValue);
        ((FieldValidator<Object, Object>) validator).validate(reference, configInstance);

        if(reference.hasChanged()) {
            field.set(current, reference.get());
            return true;
        }
        return false;
    }

    protected void checkTypes(Object configInstance, FieldValidator<?, ?> validator, Object fieldValue) {
        Type[] genericTypes = ((ParameterizedType) validator.getClass().getGenericInterfaces()[0]).getActualTypeArguments();

        Class<?> expectedFieldType = getClassFromType(genericTypes[0]);
        if (expectedFieldType != null && fieldValue != null && !expectedFieldType.isInstance(fieldValue)) {
            throw new IllegalArgumentException("Field value type doesn't match with the FieldValidator value type. Expected: " + expectedFieldType.getName() + ", got: " + fieldValue.getClass().getName());
        }

        Class<?> expectedConfigType = getClassFromType(genericTypes[1]);
        if (expectedConfigType != null && !expectedConfigType.isInstance(configInstance)) {
            throw new IllegalArgumentException("Config type doesn't match with the FieldValidator type. Expected: " + expectedConfigType.getName() + ", got: " + configInstance.getClass().getName());
        }
    }

    private Class<?> getClassFromType(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }
        return null;
    }

    @Override
    public int getExecutionPriority() {
        return 25;
    }

    public static class Unsafe extends FieldsConfigValidator {
        @Override
        protected void checkTypes(Object configInstance, FieldValidator<?, ?> validator, Object fieldValue) {}
    }
}
