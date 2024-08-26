package ru.nern.fconfiglib.v1;

import ru.nern.fconfiglib.v1.api.annotations.mixin.MixinOption;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class MixinConfigHelper {
    private final Set<String> enabledMixinOptions;

    private MixinConfigHelper(Set<String> enabledMixins) {
        this.enabledMixinOptions = enabledMixins;
    }

    public boolean isMixinEnabled(String mixinName) {
        return enabledMixinOptions.contains(mixinName);
    }

    public static <T extends ConfigManager<?, ?>> MixinConfigHelper createFor(T instance) {
        if(!instance.isInitialized()) {
            throw new IllegalStateException("The config has not yet been initialized. ConfigManager.init() should be in preLoad entrypoint");
        }
        return new MixinConfigHelper(findEnabledMixinOptionsRecursively(instance, instance));
    }

    private static <T extends ConfigManager<?, ?>> Set<String> findEnabledMixinOptionsRecursively(T root, Object object) {
        Set<String> enabled = new HashSet<>();
        try {
            for(Field field : root.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if(field.isAnnotationPresent(MixinOption.class)) {
                    MixinOption mixinOption = field.getAnnotation(MixinOption.class);
                    if(field.getType() == Boolean.class && field.getBoolean(root)) {
                        enabled.add(mixinOption.value());
                    }else{
                        throw new IllegalArgumentException("@MixinOption can only be applied to boolean");
                    }
                }else if(!field.getType().isPrimitive()) {
                    enabled.addAll(findEnabledMixinOptionsRecursively(root, field.get(object)));
                }
            }
        }catch (Exception e) {
            root.getLogger().info("Exception occurred during initialization of " + root.getModId() + " MixinConfigHelper. " + e);
        }

        return enabled;
    }
}
