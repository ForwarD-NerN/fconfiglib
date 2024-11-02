package ru.nern.fconfiglib.v1.utils;

public class ValueReference<V> {
    private V value;
    private boolean changed = false;

    public ValueReference(V value) {
        this.value = value;
    }

    public void set(V value) {
        this.value = value;
        this.changed = true;
    }

    public V get() {
        return this.value;
    }

    public boolean hasChanged() {
        return this.changed;
    }
}
