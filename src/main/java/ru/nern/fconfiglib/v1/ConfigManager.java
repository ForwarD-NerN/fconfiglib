package ru.nern.fconfiglib.v1;

import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;
import ru.nern.fconfiglib.v1.api.ConfigFixer;
import ru.nern.fconfiglib.v1.log.LoggerWrapper;
import ru.nern.fconfiglib.v1.validation.ValidationProcessor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.Consumer;


public abstract class ConfigManager<T, R> {
    private final String modId;
    private final int version;

    private T instance;
    private final Class<T> type;

    private final Map<Integer, ConfigFixer<T, R>> fixers;
    private final File file;
    private final LoggerWrapper logger;

    protected ConfigManager(Builder<T, R> builder) {
        this.modId = builder.modId;
        this.version = builder.version;
        this.fixers = builder.fixers;
        this.file = builder.configFile == null ?
                FabricLoader.getInstance().getConfigDir().resolve(modId + "_config.json").toFile() : builder.configFile;
        this.type = builder.type;
        this.logger = builder.logger;
    }

    public void createBackup() {
        try {
            File backup = new File(this.getConfigFile().getParent(), this.getConfigFile().getName() + ".backup");
            Files.copy(this.getConfigFile().toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public T newInstance() {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract void init();
    public abstract void load();
    public abstract void load(@Nullable R raw);
    public abstract void save(File file);
    public abstract R getSerializedData();

    public void save() {
        this.save(this.getConfigFile());
    }

    public T config() {
        if(this.instance == null) {
            throw new IllegalStateException("The config has not yet been initialized.");
        }
        return this.instance;
    }

    public void applyFixers(R raw, int lastLoadedVersion) {
        this.fixers.forEach((targetVersion, fixer) -> {
            if(targetVersion > lastLoadedVersion && targetVersion <= this.version) fixer.apply(instance, raw);
        });
    }

    public void validate(R raw, int lastLoadedVersion) {
        try {
            ValidationProcessor.invokeValidators(this, raw, lastLoadedVersion);
        } catch (Exception e) {
            logger.error("Exception occurred during validation of " + this.getModId() + " config " + e);
        }
    }

    public boolean isInitialized() {
        return this.instance != null;
    }

    public File getConfigFile() {
        return this.file;
    }

    public int getConfigVersion() {
        return this.version;
    }

    public String getModId() {
        return this.modId;
    }

    public LoggerWrapper getLogger() {
        return logger;
    }

    public Class<T> getType() {
        return this.type;
    }

    @Nullable
    public Map<Integer, ConfigFixer<T, R>> getFixers() {
        return this.fixers;
    }

    public static abstract class Builder<T, R> {
        protected String modId;
        protected int version;
        protected Class<T> type;
        protected final Map<Integer, ConfigFixer<T, R>> fixers = new TreeMap<>();
        @Nullable
        protected File configFile;
        protected LoggerWrapper logger = LoggerWrapper.DEFAULT;

        public Builder<T, R> of(Class<T> type) {
            this.type = type;
            return this;
        }

        public Builder<T, R> modId(String modId) {
            this.modId = modId;
            return this;
        }

        public Builder<T, R> version(int version) {
            this.version = version;
            return this;
        }

        public Builder<T, R> fixers(Consumer<Map<Integer, ConfigFixer<T, R>>> fixersConsumer) {
            fixersConsumer.accept(this.fixers);
            return this;
        }

        public Builder<T, R> file(File file) {
            this.configFile = file;
            return this;
        }

        public Builder<T, R> logger(LoggerWrapper logger) {
            this.logger = logger;
            return this;
        }


        public abstract ConfigManager<T, R> create();
    }


}
