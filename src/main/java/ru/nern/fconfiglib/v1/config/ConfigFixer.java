package ru.nern.fconfiglib.v1.config;

public abstract class ConfigFixer<T, R> {
    public final int version;

    public ConfigFixer(int version) {
        this.version = version;
    }

    public abstract void apply(T config, R raw);
}