package ru.nern.fconfiglib.v1.utils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class ReflectionUtils {

    //TODO: Find a proper way to do this. This is terrible.
    public static boolean shouldCheckFields(Field field) {
        Class<?> fieldType = field.getType();

        return !fieldType.isPrimitive() &&
                !fieldType.isInterface() &&
                !Map.class.isAssignableFrom(fieldType) &&
                !Collection.class.isAssignableFrom(fieldType) &&
                !fieldType.getName().startsWith("java.lang");
    }

}
