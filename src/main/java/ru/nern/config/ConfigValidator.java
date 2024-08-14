package ru.nern.config;

import ru.nern.config.annotations.*;

import java.lang.reflect.Field;

public class ConfigValidator {
    /*
     * Returns false if at least one of the fields was invalid
     */
    public static boolean validateFields(Object obj) throws IllegalAccessException {
        boolean valid = true;
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(MaxLength.class) && field.getType() == String.class) {
                String value = (String) field.get(obj);
                MaxLength annotation = field.getAnnotation(MaxLength.class);
                if (value.length() > annotation.length()) {
                    field.set(obj, value.substring(0, annotation.length()));
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
                    valid &= validateFields(fieldValue);
                }
            }
        }
        return valid;
    }


    public static float clamp(float value, float min, float max) {
        return value < min ? min : Math.min(value, max);
    }

    public static int clamp(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    public static long clamp(long value, long min, long max) {
        return Math.min(Math.max(value, min), max);
    }

    public static double clamp(double value, double min, double max) {
        return value < min ? min : Math.min(value, max);
    }
}
