package com.pickmin.controllers;

import java.util.ArrayList;
import java.util.HashMap;

import com.pickmin.exceptions.InvalidParametersControllerException;

public abstract class ControllerWithParameters {
    public abstract void setParameters(HashMap<String, Object> parameters) throws InvalidParametersControllerException;

    public boolean checkContainsAllParameters(HashMap<String, Object> checkHashMap, ArrayList<String> parameterKeys) {
        if (checkHashMap.keySet().containsAll(parameterKeys)) {
            return true;
        }
        return false;
    }
}