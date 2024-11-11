package com.pickmin.logic.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pickmin.config.GlobalConfig;
import com.pickmin.exceptions.JSONMissingFieldException;
import com.pickmin.exceptions.JSONMissingObjectException;
import com.pickmin.logic.general.UtilityFunctions;
import com.pickmin.logic.model.BranchProduct;
import com.pickmin.logic.model.Product;

class JsonDataOperations {
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = JsonUtility.getObjectMapper();
    }

    static ArrayList<ObjectNode> getJsonArrayFromFile(String filePath, String arrayKey) {
        return searchForJsonArrayItems(filePath, arrayKey, new ArrayList<>(), SearchSettings.findAll());
    }

    static ArrayList<ObjectNode> searchForJsonArrayItems(String filename, String arrayKey, ArrayList<SearchCriteria> searchCriteriaList, SearchSettings settings) {
        ArrayList<ObjectNode> foundItems = new ArrayList<>();
        int startIndex = (settings.getPageNumber() - 1) * settings.getPageSize();
        int endIndex = startIndex + settings.getPageSize();
        int currentItemIndex = 0;
        ArrayList<String> fieldsToRetrieve = settings.getFieldsToRetrieve();
        ArrayList<String> excludeFieldsToRetrieve = settings.getExcludeFieldsToRetrieve();

        try (JsonParser parser = objectMapper.getFactory().createParser(new FileInputStream(filename))) {
            boolean fieldExists = false;

            while (parser.nextToken() != null) {
                if (JsonToken.FIELD_NAME.equals(parser.getCurrentToken()) && arrayKey.equals(parser.currentName())) {
                    parser.nextToken();

                    while (parser.nextToken() != JsonToken.END_ARRAY) {
                        if (JsonToken.START_OBJECT.equals(parser.getCurrentToken())) {
                            ObjectNode tempObject = objectMapper.createObjectNode();
                            ObjectNode resultObject = objectMapper.createObjectNode();
                            boolean matchesAllCriteria = true;
                            ArrayList<String> missingFields = new ArrayList<>();

                            while (parser.nextToken() != JsonToken.END_OBJECT) {
                                String fieldName = parser.currentName();
                                parser.nextToken();

                                JsonNode fieldValue = parser.readValueAsTree();
                                tempObject.set(fieldName, fieldValue);

                                if (fieldsToRetrieve != null && !fieldsToRetrieve.isEmpty()) {
                                    if (fieldsToRetrieve.contains(fieldName)) {
                                        resultObject.set(fieldName, fieldValue);
                                    }
                                } else if (excludeFieldsToRetrieve != null && !excludeFieldsToRetrieve.isEmpty()) {
                                    if (!excludeFieldsToRetrieve.contains(fieldName)) {
                                        resultObject.set(fieldName, fieldValue);
                                    }
                                } else {
                                    resultObject.set(fieldName, fieldValue);
                                }

                                if (searchCriteriaList == null || searchCriteriaList.isEmpty()) {
                                    fieldExists = true;
                                } else {
                                    for (SearchCriteria criteria : searchCriteriaList) {
                                        if (criteria.getSearchField().equals(fieldName)) {
                                            fieldExists = true;

                                            if (!evaluateCondition(fieldValue, criteria.getOperator(), criteria.getSearchValue())) {
                                                matchesAllCriteria = false;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }

                            if (fieldsToRetrieve != null && !fieldsToRetrieve.isEmpty() && settings.isAllFieldsToRetrieveExists()) {
                                missingFields.addAll(fieldsToRetrieve);
                                ArrayList<String> foundFields = new ArrayList<>();
                                resultObject.fieldNames().forEachRemaining(foundFields::add);

                                missingFields.removeAll(foundFields);
                                if (!missingFields.isEmpty()) {
                                    throw new IllegalArgumentException(new JSONMissingFieldException(filename, arrayKey, missingFields.get(0)).getMessage());
                                }
                            }

                            if (matchesAllCriteria) {
                                if (currentItemIndex >= startIndex && currentItemIndex < endIndex) {
                                    foundItems.add(resultObject);
                                }

                                currentItemIndex++;

                                if (settings.isStopAtFirst()) {
                                    return foundItems;
                                }

                                if (foundItems.size() >= settings.getPageSize()) {
                                    return foundItems;
                                }
                            }

                            if (!fieldExists) {
                                throw new IllegalArgumentException(new JSONMissingFieldException(filename, arrayKey, searchCriteriaList.toString()).getMessage());
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return foundItems;
    }

    static void writeJsonArrayToFile(String filePath, String arrayKey, ArrayNode newArray) {
        File file = new File(filePath);
        File tempFile = JsonUtility.getTempFile(filePath);
        boolean arrayReplaced = false;
        boolean isEmptyFile = true;

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (JsonParser parser = objectMapper.getFactory().createParser(file);
                JsonGenerator generator = objectMapper.getFactory().createGenerator(new FileWriter(tempFile))) {

            generator.setPrettyPrinter(new CustomJsonPrettyPrinter());

            JsonToken token = parser.nextToken();

            if (token != null) {
                isEmptyFile = false;

                do {
                    if (JsonToken.FIELD_NAME.equals(token) && arrayKey.equals(parser.currentName())) {
                        generator.writeFieldName(arrayKey);
                        objectMapper.writeTree(generator, newArray);
                        arrayReplaced = true;

                        if (parser.nextToken() == JsonToken.START_ARRAY) {
                            parser.skipChildren();
                        }
                    } else {
                        writeCorrespondingToken(token, generator, parser);
                    }
                } while ((token = parser.nextToken()) != null);
            }

            if (isEmptyFile || !arrayReplaced) {
                if (isEmptyFile) {
                    generator.writeStartObject();
                }
                generator.writeFieldName(arrayKey);
                objectMapper.writeTree(generator, newArray);
                if (isEmptyFile) {
                    generator.writeEndObject();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        File originalFile = new File(filePath);
        if (originalFile.delete()) {
            tempFile.renameTo(originalFile);
        }
    }

    static void writeJsonArrayItemToFile(String filePath, String arrayKey, String nameKey, ObjectNode item) {
        if (!item.has(nameKey)) {
            throw new IllegalArgumentException("Item to write doesn't have field: " + nameKey);
        }
        File tempFile = JsonUtility.getTempFile(filePath);
        boolean arrayExists = false;
        boolean itemUpdated = false;

        File originalFile = new File(filePath);
        boolean fileExists = originalFile.exists();

        try (JsonParser parser = fileExists ? objectMapper.getFactory().createParser(new FileInputStream(filePath)) : null;
                JsonGenerator generator = objectMapper.getFactory().createGenerator(new FileWriter(tempFile))) {

            generator.setPrettyPrinter(new CustomJsonPrettyPrinter());
            generator.writeStartObject();

            if (fileExists) {
                JsonToken token;
                while ((token = parser.nextToken()) != null) {
                    if (JsonToken.FIELD_NAME.equals(token) && arrayKey.equals(parser.currentName())) {
                        arrayExists = true;
                        generator.writeFieldName(arrayKey);

                        token = parser.nextToken();
                        if (token == JsonToken.START_ARRAY) {
                            generator.writeStartArray();

                            while (parser.nextToken() != JsonToken.END_ARRAY) {
                                if (parser.currentToken() == JsonToken.START_OBJECT) {
                                    ObjectNode currentItem = (ObjectNode) objectMapper.readTree(parser);

                                    if (currentItem != null && currentItem.has(nameKey) && currentItem.get(nameKey).asText().equals(item.get(nameKey).asText())) {
                                        generator.writeObject(item);
                                        itemUpdated = true;
                                    } else {
                                        generator.writeObject(currentItem);
                                    }
                                }
                            }

                            if (!itemUpdated) {
                                generator.writeObject(item);
                            }

                            generator.writeEndArray();
                        } else {
                            throw new IOException("Expected START_ARRAY token but got: " + token);
                        }
                    } else if (JsonToken.FIELD_NAME.equals(token)) {
                        String fieldName = parser.currentName();
                        generator.writeFieldName(fieldName);

                        token = parser.nextToken();
                        if (token == JsonToken.START_ARRAY) {
                            generator.writeStartArray();
                            while (parser.nextToken() != JsonToken.END_ARRAY) {
                                generator.copyCurrentEvent(parser);
                            }
                            generator.writeEndArray();
                        } else if (token == JsonToken.START_OBJECT) {
                            generator.writeStartObject();
                            while (parser.nextToken() != JsonToken.END_OBJECT) {
                                generator.copyCurrentEvent(parser);
                            }
                            generator.writeEndObject();
                        } else {
                            generator.copyCurrentEvent(parser);
                        }
                        writeCorrespondingToken(token, generator, parser);
                    }
                }
            }

            if (!fileExists || !arrayExists) {
                generator.writeFieldName(arrayKey);
                generator.writeStartArray();
                generator.writeObject(item);
                generator.writeEndArray();
            }

            generator.writeEndObject();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (fileExists && originalFile.delete()) {
            tempFile.renameTo(originalFile);
        } else if (!fileExists) {
            tempFile.renameTo(originalFile);
        }
    }

    static Product getProductFromJsonWithIdentifier(String filePath, String fieldName, String fieldValue) throws JSONMissingObjectException {
        ArrayList<SearchCriteria> searchCriteriaList = new ArrayList<>();
        searchCriteriaList.add(new SearchCriteria(fieldName, "=", fieldValue));

        ArrayList<ObjectNode> results = JsonDataOperations.searchForJsonArrayItems(filePath, "producten", searchCriteriaList, SearchSettings.findFirst());
        if (results.isEmpty()) {
            throw new JSONMissingObjectException("Product", fieldName, fieldValue);
        }
        return JsonMapper.getProductFromObjectNode(results.get(0));
    }

    static BranchProduct getBranchProductFromJsonWithIdentifier(String fieldName, String fieldValue) throws JSONMissingObjectException, IOException {
        ObjectNode branchProductObjectNode = JsonDataOperationsCopy2.getFirstSearchForJsonArrayItems(GlobalConfig.BRANCHES_FILE_PATH, "$.branches[*].producten[?(@."+fieldName+" == '" + fieldValue + "')]");
        if (branchProductObjectNode != null) {
            String productId = JsonUtility.getFieldString(branchProductObjectNode, UtilityFunctions.createArrayListWithValues("id"));

            ObjectNode ProductObjectNode = JsonDataOperationsCopy2.getFirstSearchForJsonArrayItems(GlobalConfig.PRODUCTS_FILE_PATH, "$.producten[?(@.id == '" + productId + "')]");
            if (ProductObjectNode != null) {
                Product product = JsonMapper.getProductFromObjectNode(ProductObjectNode);
                return JsonMapper.getBranchProductFromObjectNode(branchProductObjectNode, product);
            }
        }
        return null;
    }

    static Product getProductFromJsonWithIdentifier(String filePath, HashMap<String, String> productIdentifier) throws JSONMissingObjectException {
        if (UtilityFunctions.hashMapContainsAllKeys(productIdentifier, "field", "value")) {
            return JsonDataOperations.getProductFromJsonWithIdentifier(GlobalConfig.PRODUCTS_FILE_PATH, productIdentifier.get("field"), productIdentifier.get("value"));
        } else {
            throw new IllegalArgumentException("Product identifier must contain 'field' and 'value' keys");
        }
    }

    static BranchProduct getBranchProductFromJsonWithIdentifier(HashMap<String, String> productIdentifier) throws JSONMissingObjectException, IOException {
        if (UtilityFunctions.hashMapContainsAllKeys(productIdentifier, "field", "value")) {
            return JsonDataOperations.getBranchProductFromJsonWithIdentifier(productIdentifier.get("field"), productIdentifier.get("value"));
        } else {
            throw new IllegalArgumentException("Branch Product identifier must contain 'field' and 'value' keys");
        }
    }

    static boolean hasFields(String filename, String arrayKey, ArrayList<String> fieldsToCheck) {
        try (JsonParser parser = objectMapper.getFactory().createParser(new FileInputStream(filename))) {

            while (parser.nextToken() != null) {
                if (JsonToken.FIELD_NAME.equals(parser.getCurrentToken()) && arrayKey.equals(parser.currentName())) {
                    parser.nextToken();

                    if (parser.nextToken() == JsonToken.START_OBJECT) {
                        ObjectNode tempObject = objectMapper.createObjectNode();
                        while (parser.nextToken() != JsonToken.END_OBJECT) {
                            String fieldName = parser.currentName();
                            parser.nextToken();

                            JsonNode fieldValue = parser.readValueAsTree();
                            tempObject.set(fieldName, fieldValue);
                        }

                        for (String field : fieldsToCheck) {
                            if (!tempObject.has(field)) {
                                return false;
                            }
                        }

                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    // --- writer helper functions ---
    private static void writeCorrespondingToken(JsonToken token, JsonGenerator generator, JsonParser parser) throws IOException {
        if (token == JsonToken.START_OBJECT) {
            generator.writeStartObject();
        } else if (token == JsonToken.END_OBJECT) {
            generator.writeEndObject();
        } else if (token == JsonToken.START_ARRAY) {
            if (parser.currentName() != null) {
                generator.writeFieldName(parser.currentName());
            }
            generator.writeStartArray();
        } else if (token == JsonToken.END_ARRAY) {
            generator.writeEndArray();
        } else if (token == JsonToken.FIELD_NAME) {
            generator.writeFieldName(parser.currentName());
        } else {
            generator.writeString(parser.getText());
        }
    }

    private static boolean evaluateCondition(JsonNode fieldValue, String operator, Object searchValue) {
        switch (operator) {
            case "=":
                return fieldValue.asText().equals(searchValue.toString());
            case "!=":
                return !fieldValue.asText().equals(searchValue.toString());
            case ">":
                if (fieldValue.isNumber() && searchValue instanceof Number) {
                    return fieldValue.asDouble() > ((Number) searchValue).doubleValue();
                }
                break;
            case "<":
                if (fieldValue.isNumber() && searchValue instanceof Number) {
                    return fieldValue.asDouble() < ((Number) searchValue).doubleValue();
                }
                break;
            case ">=":
                if (fieldValue.isNumber() && searchValue instanceof Number) {
                    return fieldValue.asDouble() >= ((Number) searchValue).doubleValue();
                }
                break;
            case "<=":
                if (fieldValue.isNumber() && searchValue instanceof Number) {
                    return fieldValue.asDouble() <= ((Number) searchValue).doubleValue();
                }
                break;
            case "contains":
                if (fieldValue.isTextual()) {
                    return fieldValue.asText().contains(searchValue.toString());
                }
                break;
            default:
                return false;
        }
        return false;
    }
}
