package com.pickmin.logic.json;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

class JsonUtility {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    static String getFieldString(ObjectNode obj, ArrayList<String> fieldNames) {
        return getFieldString(obj, fieldNames, "");
    }

    static String getFieldString(ObjectNode obj, ArrayList<String> fieldNames, String defaultValue) {
        for (String fieldName : fieldNames) {
            if (obj.has(fieldName)) {
                return obj.get(fieldName).asText();
            }
        }
        return defaultValue;
    }

    static int getFieldInt(ObjectNode obj, ArrayList<String> fieldNames, int defaultValue) {
        for (String fieldName : fieldNames) {
            if (obj.has(fieldName)) {
                return obj.get(fieldName).asInt(defaultValue);
            }
        }
        return defaultValue;
    }

    static double getFieldDouble(ObjectNode obj, ArrayList<String> fieldNames, double defaultValue) {
        for (String fieldName : fieldNames) {
            if (obj.has(fieldName)) {
                return obj.get(fieldName).asDouble(defaultValue);
            }
        }
        return defaultValue;
    }

    static JsonNode getFieldJsonNode(ObjectNode obj, ArrayList<String> fieldNames, JsonNode defaultValue) {
        for (String fieldName : fieldNames) {
            if (obj.has(fieldName)) {
                return obj.get(fieldName);
            }
        }
        return defaultValue;
    }

    static File getTempFile(String filePath) {
        Path p = Paths.get(filePath);
        String fileName = p.getFileName().toString();
        File tempFile = new File(filePath.replace(fileName, "temp_" + fileName));
        return tempFile;
    }

    static ArrayNode arrayListObjectNodeToArrayNode(ArrayList<ObjectNode> arrayList) {
        ArrayNode arrayNode = objectMapper.createArrayNode();
        arrayList.stream().forEach(item -> arrayNode.add(item));
        return arrayNode;
    }
}
