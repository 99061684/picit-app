package com.pickmin.logic.json;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.pickmin.exceptions.JSONMissingObjectException;

class JsonDataOperationsCopy2 {
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = JsonUtility.getObjectMapper();
    }

    // Functie voor het zoeken naar JSON-items met een query
    static ArrayList<ObjectNode> searchForJsonArrayItems(String filePath, String query) throws IOException {
        File file = new File(filePath);
        DocumentContext context = JsonPath.parse(file);

        List<Object> rawResults = context.read(query);
        ArrayList<ObjectNode> result = new ArrayList<>();

        for (Object rawResult : rawResults) {
            if (rawResult instanceof LinkedHashMap) {
                // Converteer LinkedHashMap naar ObjectNode
                ObjectNode objectNode = objectMapper.convertValue(rawResult, ObjectNode.class);
                result.add(objectNode);
            }
        }

        return result;
    }

    // static ArrayList<ObjectNode> searchForJsonArrayItems(String filePath, String query) throws IOException {
    //     File file = new File(filePath);
    //     DocumentContext context = JsonPath.parse(file);

    //     ArrayList<ObjectNode> result = context.read(query);
    //     return result;
    // }

    static ObjectNode getFirstSearchForJsonArrayItems(String filePath, String query) throws IOException, JSONMissingObjectException {
        File file = new File(filePath);
        DocumentContext context = JsonPath.parse(file);

        ArrayList<ObjectNode> result = context.read(query);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new JSONMissingObjectException(filePath, query);
    }

    // Functie om het aantal resultaten van een query te tellen
    static int countResults(String filePath, String query) throws IOException {
        ArrayList<ObjectNode> result = searchForJsonArrayItems(filePath, query);
        return result.size();
    }

    // Functie om een JSON-array te vervangen of toe te voegen
    static void writeJsonArrayToFile(String filePath, String query, ArrayNode newArray) throws IOException {
        File file = new File(filePath);
        DocumentContext context = JsonPath.parse(file);

        context.set(query, newArray);

        objectMapper.writeValue(file, context.json());
    }

    // Functie om een JSON-item te vervangen of toe te voegen aan een array
    static boolean writeJsonArrayItemToFile(String filePath, String query, ObjectNode item, boolean addIfNotFound) throws IOException {
        File file = new File(filePath);
        DocumentContext context = JsonPath.parse(file);

        ArrayList<ObjectNode> result = context.read(query);
        boolean itemReplaced = false;

        // Vervang het item in de array als het gevonden is
        for (int i = 0; i < result.size(); i++) {
            ObjectNode currentItem = result.get(i);
            if (currentItem.get("id").asText().equals(item.get("id").asText())) {
                result.set(i, item);
                itemReplaced = true;
                break;
            }
        }

        // Als het item niet gevonden is en addIfNotFound true is, voeg het toe
        if (!itemReplaced && addIfNotFound) {
            result.add(item);
        }

        // Schrijf het bijgewerkte bestand terug
        context.set(query, result);
        objectMapper.writeValue(file, context.json());

        return itemReplaced || addIfNotFound;
    }

    // Functie om te controleren of bepaalde velden in de resultaten van een query aanwezig zijn
    static boolean hasFields(String filePath, String query, ArrayList<String> fieldsToCheck) throws IOException {
        ArrayList<ObjectNode> results = searchForJsonArrayItems(filePath, query);

        for (ObjectNode result : results) {
            for (String field : fieldsToCheck) {
                if (!result.has(field)) {
                    return false;
                }
            }
        }
        return true;
    }
}