package ru.nern.fconfiglib.v1.validators;

import ru.nern.fconfiglib.v1.ConfigManager;
import ru.nern.fconfiglib.v1.api.OptionValidatorCallback;
import ru.nern.fconfiglib.v1.api.annotations.restriction.*;

import java.lang.reflect.Field;

public class RestrictionConfigValidator extends AbstractConfigValidator {
    @Override
    public <T, R> void validate(ConfigManager<T, R> manager, R raw, int lastLoadedVersion) throws ReflectiveOperationException {
        // If something is invalid in the config, we fix and save it
        if(!this.validateAllFields(manager.config(), manager.config())) {
            manager.save();
        }
    }

    /*
     * Returns false if at least one of the fields was invalid
     */
    public boolean validateAllFields(Object root, Object obj) throws ReflectiveOperationException {
        boolean valid = true;
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(OptionValidator.class)) {
                OptionValidator annotation = field.getAnnotation(OptionValidator.class);
                OptionValidatorCallback<Object> optionValidatorCallback = (OptionValidatorCallback<Object>) annotation.validator().getDeclaredConstructor().newInstance();
                valid = !optionValidatorCallback.validate(root);
            }

            if (field.isAnnotationPresent(MaxLength.class) && field.getType() == String.class) {
                String value = (String) field.get(obj);
                MaxLength annotation = field.getAnnotation(MaxLength.class);
                if (value.length() > annotation.value()) {
                    field.set(obj, value.substring(0, annotation.value()));
                    valid = false;
                }
            } else if (field.isAnnotationPresent(InRangeInt.class) && field.getType() == int.class) {
                int value = field.getInt(obj);
                InRangeInt annotation = field.getAnnotation(InRangeInt.class);
                if (value < annotation.min() || value > annotation.max()) {
                    field.set(obj, clamp(value, annotation.min(), annotation.max()));
                    valid = false;
                }
            } else if (field.isAnnotationPresent(InRangeLong.class) && field.getType() == long.class) {
                long value = field.getLong(obj);
                InRangeLong annotation = field.getAnnotation(InRangeLong.class);
                if (value < annotation.min() || value > annotation.max()) {
                    field.set(obj, clamp(value, annotation.min(), annotation.max()));
                    valid = false;
                }
            }else if (field.isAnnotationPresent(InRangeFloat.class) && field.getType() == float.class) {
                float value = field.getFloat(obj);
                InRangeFloat annotation = field.getAnnotation(InRangeFloat.class);
                if (value < annotation.min() || value > annotation.max()) {
                    field.set(obj, clamp(value, annotation.min(), annotation.max()));
                    valid = false;
                }
            }else if (field.isAnnotationPresent(InRangeDouble.class) && field.getType() == double.class) {
                double value = field.getDouble(obj);
                InRangeDouble annotation = field.getAnnotation(InRangeDouble.class);
                if (value < annotation.min() || value > annotation.max()) {
                    field.set(obj, clamp(value, annotation.min(), annotation.max()));
                    valid = false;
                }
            }else{
                Object fieldValue = field.get(obj);
                if (fieldValue != null && !field.getType().isPrimitive()) {
                    valid &= validateAllFields(root, fieldValue);
                }
            }
        }
        return valid;
    }

    @Override
    public int getExecutionPriority() {
        return 3;
    }

    private static float clamp(float value, float min, float max) {
        return value < min ? min : Math.min(value, max);
    }

    private static int clamp(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    private static long clamp(long value, long min, long max) {
        return Math.min(Math.max(value, min), max);
    }

    private static double clamp(double value, double min, double max) {
        return value < min ? min : Math.min(value, max);
    }
}
