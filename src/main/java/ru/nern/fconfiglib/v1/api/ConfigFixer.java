package ru.nern.fconfiglib.v1.api;

public interface ConfigFixer<T, R> {
    void apply(T config, R raw);
}
