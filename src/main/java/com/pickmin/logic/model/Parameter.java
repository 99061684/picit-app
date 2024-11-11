package com.pickmin.logic.model;

public class Parameter<T> {
    private final ParameterKey key;
    private final T value;

    public Parameter(ParameterKey key, T value) {
        this.key = key;
        this.value = value;
    }

    public ParameterKey getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }
}
