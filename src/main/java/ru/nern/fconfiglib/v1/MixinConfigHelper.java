package ru.nern.fconfiglib.v1;

import ru.nern.fconfiglib.v1.api.annotations.mixins.MixinOption;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MixinConfigHelper {
    private final Set<String> enabledMixinOptions;
    private final Set<String> enabledMixinPackages;

    private MixinConfigHelper(Set<String> enabledMixins, Set<String> enabledMixinPackages) {
        this.enabledMixinOptions = enabledMixins;
        this.enabledMixinPackages = enabledMixinPackages;
    }

    public boolean shouldApplyMixin(String mixinClassName) {
        if(enabledMixinOptions.contains(mixinClassName)) {
            return true;
        }
        return enabledMixinPackages.stream().anyMatch(mixinClassName::startsWith);
    }

    public static <T> MixinConfigHelper createFor(ConfigManager<T, ?> configManager) {
        if(!configManager.isInitialized()) {
            throw new IllegalStateException("The config has not yet been initialized. ConfigManager.init() should be in MixinPlugin.onLoad()");
        }
        Set<String> options = findEnabledOptions(configManager);
        Set<String> packages = filterPackagesAndGet(options);

        return new MixinConfigHelper(options, packages);
    }

    private static <T> Set<String> findEnabledOptions(ConfigManager<T, ?> manager) {
        Set<String> options = new HashSet<>();
        try {
            findEnabledOptionsRecursively(options, manager.config());
        }catch (Exception e) {
            manager.getLogger().info("Exception occurred during field parsing of " + manager.getModId() + "config. MixinConfigHelper: " + e);
        }
        return options;
    }

    private static void findEnabledOptionsRecursively(Set<String> options, Object parent) throws Exception {
        for(Field field : parent.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if(field.isAnnotationPresent(MixinOption.class)) {
                MixinOption mixinOption = field.getAnnotation(MixinOption.class);
                if(field.getType() == Boolean.class && field.getBoolean(parent)) {
                    if(!mixinOption.value().isEmpty()) options.add(mixinOption.value());
                    options.addAll(Arrays.asList(mixinOption.values()));
                }else{
                    throw new IllegalArgumentException("@MixinOption can only be applied to a boolean");
                }
            }else if(!field.getType().isPrimitive()) {
                findEnabledOptionsRecursively(options, field.get(parent));
            }
        }
    }

    private static Set<String> filterPackagesAndGet(Set<String> options) {
        Set<String> packages = new HashSet<>();

        Iterator<String> iterator = options.iterator();
        while (iterator.hasNext()) {
            String option = iterator.next();
            if(option.endsWith(".*")) {
                iterator.remove();
                packages.add(option.substring(0, option.length() - 2));
            }
        }
        return packages;
    }
}
