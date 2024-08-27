package ru.nern.fconfiglib.v1;

import ru.nern.fconfiglib.v1.api.annotations.mixins.MixinOption;

import java.lang.reflect.Field;
import java.util.*;

public class MixinConfigHelper {
    private final Map<String, Boolean> enabledMixinOptions;
    private final Set<String> enabledMixinPackages;

    private MixinConfigHelper(Map<String, Boolean> enabledMixins, Set<String> enabledMixinPackages) {
        this.enabledMixinOptions = enabledMixins;
        this.enabledMixinPackages = enabledMixinPackages;
    }

    public boolean shouldApplyMixin(String mixinClassName) {
        if(enabledMixinPackages.stream().anyMatch(mixinClassName::startsWith)) {
            return true;
        }
        return !enabledMixinOptions.containsKey(mixinClassName) || enabledMixinOptions.get(mixinClassName);
    }

    public static <T> MixinConfigHelper createFor(ConfigManager<T, ?> configManager) {
        if(!configManager.isInitialized()) {
            throw new IllegalStateException("The config has not yet been initialized. ConfigManager.init() should be in MixinPlugin.onLoad()");
        }
        Map<String, Boolean> options = findMixinOptions(configManager);
        Set<String> packages = filterPackagesAndGet(options);

        return new MixinConfigHelper(options, packages);
    }

    private static <T> Map<String, Boolean> findMixinOptions(ConfigManager<T, ?> manager) {
        Map<String, Boolean> options = new HashMap<>();
        try {
            findMixinOptionsRecursively(options, manager.config());
        }catch (Exception e) {
            manager.getLogger().info("Exception occurred during field parsing of " + manager.getModId() + "config. MixinConfigHelper: " + e);
        }
        return options;
    }

    private static void findMixinOptionsRecursively(Map<String, Boolean> mixinPaths, Object parent) throws Exception {
        for(Field field : parent.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if(field.isAnnotationPresent(MixinOption.class)) {
                if(field.getType() == boolean.class) {
                    addBooleanOptionPaths(mixinPaths, parent, field);
                }else{
                    throw new IllegalArgumentException("@MixinOption can only be applied to a boolean");
                }
            }else if(!field.getType().isPrimitive()) {
                findMixinOptionsRecursively(mixinPaths, field.get(parent));
            }
        }
    }

    private static void addBooleanOptionPaths(Map<String, Boolean> mixinPaths, Object parent, Field field) throws Exception {
        MixinOption mixinOption = field.getAnnotation(MixinOption.class);
        boolean enabled = !mixinOption.invert() && field.getBoolean(parent);
        if(!mixinOption.value().isEmpty()) mixinPaths.put(mixinOption.value(), enabled);

        for(String path : mixinOption.values()) {
            mixinPaths.put(path, enabled);
        }
    }

    private static Set<String> filterPackagesAndGet(Map<String, Boolean> options) {
        Set<String> packages = new HashSet<>();

        Iterator<Map.Entry<String, Boolean>> iterator = options.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Boolean> entry = iterator.next();
            String option = entry.getKey();
            if(option.endsWith(".*")) {
                iterator.remove();
                packages.add(option.substring(0, option.length() - 2));
            }
        }
        return packages;
    }
}
