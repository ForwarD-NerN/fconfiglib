package ru.nern.fconfiglib.v1;

import ru.nern.fconfiglib.v1.api.annotations.mixins.MixinOption;
import ru.nern.fconfiglib.v1.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MixinConfigHelper {
    private final Map<String, Boolean> enabledMixinOptions = new HashMap<>();
    private final TreeMap<String, Boolean> enabledMixinPackages = new TreeMap<>();
    private final String prefix;

    public MixinConfigHelper() {
        this.prefix = "";
    }

    public MixinConfigHelper(String prefix) {
        this.prefix = prefix + ".";
    }

    public <T> MixinConfigHelper init(ConfigManager<T, ?> configManager) {
        if(!configManager.isInitialized()) {
            throw new IllegalStateException("The config has not yet been initialized. ConfigManager.init() should be in MixinPlugin.onLoad()");
        }
        this.findMixinOptions(configManager);
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
            this.findMixinOptionsRecursively(manager.config());
        }catch (Exception e) {
            manager.getLogger().info("Exception occurred during field parsing of " + manager.getModId() + " config. MixinConfigHelper: " + e);
        }
    }

    private void findMixinOptionsRecursively(Object parent) throws Exception {
        for(Field field : parent.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if(field.isAnnotationPresent(MixinOption.class)) {
                if(field.getType() == boolean.class) {
                    this.handleMixinOption(field, field.getBoolean(parent));
                }else{
                    throw new IllegalArgumentException("@MixinOption can only be applied to a boolean");
                }
            }else if(ReflectionUtils.shouldCheckFields(field)) {
                findMixinOptionsRecursively(field.get(parent));
            }
        }
    }

    private void handleMixinOption(Field field, boolean enabled) {
        MixinOption mixinOption = field.getAnnotation(MixinOption.class);
        enabled = enabled && !mixinOption.invert();

        for(String path : mixinOption.value()) {
            this.addMixinPath(this.prefix + path, enabled);
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
