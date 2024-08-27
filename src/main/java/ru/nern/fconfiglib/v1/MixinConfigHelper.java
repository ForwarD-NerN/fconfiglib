package ru.nern.fconfiglib.v1;

import ru.nern.fconfiglib.v1.api.annotations.mixins.MixinOption;

import java.lang.reflect.Field;
import java.util.*;

public class MixinConfigHelper {
    private final Map<String, Boolean> enabledMixinOptions = new HashMap<>();
    private final TreeMap<String, Boolean> enabledMixinPackages = new TreeMap<>();

    public <T> MixinConfigHelper init(ConfigManager<T, ?> configManager) {
        if(!configManager.isInitialized()) {
            throw new IllegalStateException("The config has not yet been initialized. ConfigManager.init() should be in MixinPlugin.onLoad()");
        }
        findMixinOptions(configManager);
        return this;
    }

    public boolean shouldApplyMixin(String mixinClassName) {
        Map.Entry<String, Boolean> packageEntry = enabledMixinPackages.floorEntry(mixinClassName);
        if(packageEntry != null && mixinClassName.startsWith(packageEntry.getKey())) {
            return packageEntry.getValue();
        }
        return !this.enabledMixinOptions.containsKey(mixinClassName) || this.enabledMixinOptions.get(mixinClassName);
    }

    private <T> void findMixinOptions(ConfigManager<T, ?> manager) {
        try {
            findMixinOptionsRecursively(manager.config());
        }catch (Exception e) {
            manager.getLogger().info("Exception occurred during field parsing of " + manager.getModId() + " config. MixinConfigHelper: " + e);
        }
    }

    private void findMixinOptionsRecursively(Object parent) throws Exception {
        for(Field field : parent.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if(field.isAnnotationPresent(MixinOption.class)) {
                if(field.getType() == boolean.class) {
                    handleMixinOption(field, field.getBoolean(parent));
                }else{
                    throw new IllegalArgumentException("@MixinOption can only be applied to a boolean");
                }
            }else if(!field.getType().isPrimitive()) {
                findMixinOptionsRecursively(field.get(parent));
            }
        }
    }

    private void handleMixinOption(Field field, boolean enabled) {
        MixinOption mixinOption = field.getAnnotation(MixinOption.class);
        enabled = enabled && !mixinOption.invert();

        if(!mixinOption.value().isEmpty()) this.addMixinPath(mixinOption.value(), enabled);

        for(String option : mixinOption.values()) {
            this.addMixinPath(option, enabled);
        }
    }

    public void addMixinPath(String path, boolean enabled) {
        if(!path.endsWith(".*")) {
            this.enabledMixinOptions.put(path, enabled);
        }else{
            this.enabledMixinPackages.put(path.substring(0, path.length() - 2), enabled);
        }
    }
}
