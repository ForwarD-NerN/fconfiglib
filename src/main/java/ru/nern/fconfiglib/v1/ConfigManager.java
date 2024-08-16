package ru.nern.fconfiglib.v1;

import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashSet;


public abstract class ConfigManager<T, R> {
    private final String modId;
    private final int version;
    @Nullable
    private final LinkedHashSet<ConfigFixer<T, R>> fixers;
    private final File file;
    protected T instance;
    protected final Class<T> type;
    private final boolean validateFields;
    private final boolean validateVersions;
    protected final LoggerWrapper logger;

    public ConfigManager(Builder<T, R> builder) {
        this.modId = builder.modId;
        this.version = builder.version;
        this.fixers = builder.fixers;
        this.file = builder.configFile == null ? FabricLoader.getInstance().getConfigDir().resolve(modId+"_config.json").toFile() : builder.configFile;
        this.type = builder.type;
        this.validateFields = builder.validateFields;
        this.validateVersions = builder.validateVersions;
        this.logger = builder.logger;
    }

    public void validate(int lastLoadedVersion, R raw) {
        try {
            if(this.validateVersions) {
                if(lastLoadedVersion < this.getConfigVersion()) {
                    this.applyFixes(raw, lastLoadedVersion);
                    this.load(raw);
                    this.save(this.getConfigFile());
                }else if(lastLoadedVersion > this.getConfigVersion()) {
                    logger.info(this.getModId() + " got downgraded. Creating a backup of the config...");
                    this.createBackup();
                    this.save(this.getConfigFile());
                }
            }
            // If something was invalid in the config, we fix it and save
            if(this.validateFields && !ConfigValidator.validateFields(this.config(), this.config())) {
                this.save(this.getConfigFile());
            }
        }catch (Exception e) {
            logger.info(String.format("Exception occurred during validation of the config: %s%n", e.fillInStackTrace()));
        }
    }

    protected void createBackup() {
        try {
            File backup = new File(this.getConfigFile().getParent(), this.getConfigFile().getName() + ".backup");
            Files.copy(this.getConfigFile().toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public T createEmptyConfig() {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract void init();
    public abstract void load(@Nullable R from);
    public abstract void save(File file);
    public abstract R getSerializedData();

    public T config() {
        if(this.instance == null) {
            throw new IllegalStateException("The config has not yet been initialized.");
        }
        return this.instance;
    }

    public void applyFixes(R raw, int lastLoadedVersion) {
        fixers.forEach(fixer -> {
            if(fixer.version > lastLoadedVersion) fixer.apply(instance, raw);
        });
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

    public LoggerWrapper getLoggerWrapper() {
        return logger;
    }

    @Nullable
    public LinkedHashSet<ConfigFixer<T, R>> getFixers() {
        return this.fixers;
    }

    public static abstract class Builder<T, R> {
        public Class<T> type;
        public String modId;
        public int version = 0;
        @Nullable
        public LinkedHashSet<ConfigFixer<T, R>> fixers = new LinkedHashSet<>();
        @Nullable
        public File configFile;
        public boolean validateFields = true;
        public boolean validateVersions = true;
        public LoggerWrapper logger = LoggerWrapper.DEFAULT;


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

        public Builder<T, R> fixers(LinkedHashSet<ConfigFixer<T, R>> fixers) {
            this.fixers = fixers;
            return this;
        }

        public Builder<T, R> file(File file) {
            this.configFile = file;
            return this;
        }

        public Builder<T, R> validateFields(boolean validate) {
            this.validateFields = validate;
            return this;
        }

        public Builder<T, R> validateVersions(boolean validate) {
            this.validateVersions = validate;
            return this;
        }

        public Builder<T, R> logger(LoggerWrapper logger) {
            this.logger = logger;
            return this;
        }


        public abstract ConfigManager<T, R> create();
    }
}
