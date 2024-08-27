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

    public boolean isMixinEnabled(String mixinName) {
        if(enabledMixinOptions.contains(mixinName)) {
            return true;
        }
        return enabledMixinPackages.stream().anyMatch(mixinName::startsWith);
    }

    public static <T extends ConfigManager<?, ?>> MixinConfigHelper createFor(T instance) {
        if(!instance.isInitialized()) {
            throw new IllegalStateException("The config has not yet been initialized. ConfigManager.init() should be in preLoad entrypoint");
        }
        Set<String> options = findEnabledOptionsRecursively(instance, instance);
        Set<String> packages = filterPackagesAndGet(options);

        return new MixinConfigHelper(options, packages);
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

    private static <T extends ConfigManager<?, ?>> Set<String> findEnabledOptionsRecursively(T configInstance, Object object) {
        Set<String> enabled = new HashSet<>();
        try {
            for(Field field : object.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if(field.isAnnotationPresent(MixinOption.class)) {
                    MixinOption mixinOption = field.getAnnotation(MixinOption.class);
                    if(field.getType() == Boolean.class && field.getBoolean(object)) {
                        if(!mixinOption.value().isEmpty()) enabled.add(mixinOption.value());
                        enabled.addAll(Arrays.asList(mixinOption.values()));
                    }else{
                        throw new IllegalArgumentException("@MixinOption can only be applied to a boolean");
                    }
                }else if(!field.getType().isPrimitive()) {
                    enabled.addAll(findEnabledOptionsRecursively(configInstance, field.get(object)));
                }
            }
        }catch (Exception e) {
            configInstance.getLogger().info("Exception occurred during initialization of " + configInstance.getModId() + " MixinConfigHelper. " + e);
        }

        return enabled;
    }
}
