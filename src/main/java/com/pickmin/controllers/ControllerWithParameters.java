package com.pickmin.controllers;

import java.util.ArrayList;
import java.util.HashMap;

import com.pickmin.logic.model.Parameter;
import com.pickmin.logic.model.ParameterKey;

public abstract class ControllerWithParameters extends FormController {
    private final HashMap<ParameterKey, Object> parameterMap = new HashMap<>();

    public <T> void setParameters(ArrayList<Parameter<?>> parameters) {
        for (Parameter<?> param : parameters) {
            parameterMap.put(param.getKey(), param.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getParameter(ParameterKey key, Class<T> type) {
        Object value = parameterMap.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Parameter not found for key: " + key);
        }
        if (!type.isInstance(value)) {
            throw new IllegalArgumentException("Parameter type mismatch for key: " + key);
        } else
        return (T) value;
    }
}