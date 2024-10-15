package com.pickmin.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonFactory;
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
import com.pickmin.translation.Language;

public class JsonHandler {
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    public static ArrayList<Product> loadProductsFromJson(String filePath) {
        ArrayList<Product> products = new ArrayList<>();
        ArrayList<ObjectNode> dataArray = getJsonArrayFromFile(filePath, "vruchten");

        if (dataArray != null) {
            for (ObjectNode node : dataArray) {
                products.add(getExternProductFromObjectNode(node));
            }
        }
        return products;
    }

    public static ArrayList<Product> loadProductsFromJson() {
        return JsonHandler.loadProductsFromJson(GlobalConfig.EXTERN_PRODUCTS_FILE_PATH);
    }

    public static void saveProductsToJson(ArrayList<Product> products) {
        ArrayNode productArray = objectMapper.createArrayNode();
        for (Product product : products) {
            productArray.add(createProductObjectNode(product));
        }
        writeJsonArrayToFile(GlobalConfig.EXTERN_PRODUCTS_FILE_PATH, "vruchten", productArray);
    }

    public static ArrayList<User> loadUsersFromJson() {
        ArrayList<User> users = new ArrayList<>();
        ArrayList<ObjectNode> dataArray = getJsonArrayFromFile(GlobalConfig.USERS_FILE_PATH, "users");

        if (dataArray != null) {
            for (ObjectNode node : dataArray) {
                users.add(getUserFromObjectNode(node));
            }
        }

        return users;
    }

    public static void saveUsersToJson(List<User> users) {
        ArrayNode userArray = objectMapper.createArrayNode();
        for (User user : users) {
            userArray.add(createUserObjectNode(user));
        }

        writeJsonArrayToFile(GlobalConfig.USERS_FILE_PATH, "users", userArray);
    }

    public static ShoppingList loadShoppingListFromJson(String userId) {
        ArrayList<ShoppingListProduct> products = new ArrayList<>();
        ArrayList<ObjectNode> dataArray = getJsonArrayFromFile(GlobalConfig.DATA_FILE_PATH + "shoppinglist_" + userId + ".json", "shoppinglist");

        if (dataArray != null) {
            for (ObjectNode node : dataArray) {
                products.add(getShoppingListProductFromJson(node));
            }
        }

        ShoppingList shoppingList = new ShoppingList(products);
        return shoppingList;
    }

    public static void saveShoppingListToJson(String userId, List<ShoppingListProduct> products) {
        ArrayNode jsonArray = objectMapper.createArrayNode();
        for (ShoppingListProduct product : products) {
            jsonArray.add(createShoppingListProductObjectNode(product));
        }

        writeJsonArrayToFile(GlobalConfig.DATA_FILE_PATH + "shoppinglist_" + userId + ".json", "shoppinglist", jsonArray);
    }

    public static ArrayList<Branch> loadBranchesFromJson(String filePath) {
        SearchSettings searchSetting = SearchSettings.findAll();
        searchSetting.setExcludeFieldsToRetrieve(Validation.createArrayListWithValues("fruit", "producten"));

        ArrayList<ObjectNode> dataArray = JsonHandler.findJsonArrayItemsByFields(GlobalConfig.TEST_BRANCHES_FILE_PATH, "vestigingen", new ArrayList<>(), searchSetting);
        ArrayList<Branch> branches = new ArrayList<>();

        if (dataArray != null) {
            for (ObjectNode node : dataArray) {
                branches.add(getBranchFromObjectNode(node));
            }
        }

        return branches;
    }

    public static ArrayList<Branch> loadBranchesFromJson() {
        return JsonHandler.loadBranchesFromJson(GlobalConfig.EXTERN_PRODUCTS_FILE_PATH);
    }

    public static void saveBranchesToJson(List<Branch> branches) {
        ArrayNode branchArray = objectMapper.createArrayNode();
        for (Branch branch : branches) {
            branchArray.add(createBranchObjectNode(branch));
        }
        writeJsonArrayToFile(GlobalConfig.DATA_FILE_PATH + "branches.json", "branches", branchArray);
    }

    public static ArrayList<BranchProduct> loadBranchProductsFromJson(Branch branch) throws JSONMissingFieldException, JSONMissingObjectException {
        SearchSettings searchSetting = SearchSettings.findFirst();
        searchSetting.setFieldsToRetrieve(Validation.createArrayListWithValues("fruit", "producten"), false);

        ArrayList<SearchCriteria> searchCriteriaList = new ArrayList<>();
        searchCriteriaList.add(new SearchCriteria("id", "=", branch.getId()));

        ArrayList<ObjectNode> dataArray = JsonHandler.findJsonArrayItemsByFields(GlobalConfig.TEST_BRANCHES_FILE_PATH, "vestigingen", new ArrayList<>(), searchSetting);
        ArrayList<BranchProduct> branchProducts = new ArrayList<>();

        if (dataArray != null && !dataArray.isEmpty()) {
            ArrayList<ObjectNode> dataArray2 = getBranchProductObjectNodeArrayListFromObjectNode(dataArray.get(0));
            for (ObjectNode branchProductObjectNode : dataArray2) {
                Product product = null;
                String id = getFieldString(branchProductObjectNode, Validation.createArrayListWithValues("id"), null);
                String nameBranchProduct = getFieldString(branchProductObjectNode, Validation.createArrayListWithValues("naam", "name"), null);
                if (id != null) {
                    product = getProductFromJsonWithIdentifier("id", id);
                } else {
                    product = getProductFromJsonWithIdentifier("name", nameBranchProduct);
                }
                branchProducts.add(getBranchProductFromObjectNode(branchProductObjectNode, product));
            }
                        
        }

        return branchProducts;
    }

    private static Product getProductFromJsonWithIdentifier(String fieldName, String fieldValue) throws JSONMissingObjectException {
        ArrayList<SearchCriteria> searchCriteriaList = new ArrayList<>();
        searchCriteriaList.add(new SearchCriteria(fieldName, "=", fieldValue));

        ArrayList<ObjectNode> results = JsonHandler.findJsonArrayItemsByFields(GlobalConfig.EXTERN_PRODUCTS_FILE_PATH, "vruchten", searchCriteriaList, SearchSettings.findFirst());
        if (results.isEmpty()) {
            throw new JSONMissingObjectException("Product", fieldName, fieldValue);
        }
        return getExternProductFromObjectNode(results.get(0));
    }

    public static void saveBranchProductsToJson(String branchId, List<BranchProduct> branchProducts) {
        ArrayNode productArray = objectMapper.createArrayNode();
        for (BranchProduct product : branchProducts) {
            productArray.add(createBranchProductJSONObject(product));
        }
        writeJsonArrayToFile(GlobalConfig.DATA_FILE_PATH + "branch_" + branchId + "_products.json", "branchProducts", productArray);
    }

    // ----------------- helper functions -----------------
    private static void writeJsonArrayToFile(String filePath, String arrayKey, ArrayNode newArray) {
        File tempFile = getTempFile(filePath);
        boolean arrayReplaced = false;

        try (JsonParser parser = new JsonFactory().createParser(new FileInputStream(filePath));
                JsonGenerator generator = new JsonFactory().createGenerator(new FileWriter(tempFile))) {

            generator.useDefaultPrettyPrinter();
            JsonToken token;

            while ((token = parser.nextToken()) != null) {
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
            }

            if (!arrayReplaced) {
                generator.writeFieldName(arrayKey);
                objectMapper.writeTree(generator, newArray);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File originalFile = new File(filePath);
        if (originalFile.delete()) {
            tempFile.renameTo(originalFile);
        }
    }

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

    private static void updateJsonArrayItemByName(String filePath, String arrayKey, String valueKey, String nameKey, JSONObject newItem) {
        File tempFile = getTempFile(filePath);

        try (JsonParser parser = new JsonFactory().createParser(new File(filePath));
                JsonGenerator generator = new JsonFactory().createGenerator(new FileWriter(tempFile))) {

            generator.useDefaultPrettyPrinter();

            JsonToken token;
            boolean itemReplaced = false;

            while ((token = parser.nextToken()) != null) {
                if (JsonToken.FIELD_NAME.equals(token) && arrayKey.equals(parser.currentName())) {
                    generator.writeFieldName(arrayKey);
                    token = parser.nextToken();
                    generator.copyCurrentEvent(parser);

                    while (parser.nextToken() != JsonToken.END_ARRAY) {
                        boolean isMatch = false;

                        if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
                            JSONObject tempObject = new JSONObject();

                            while (parser.nextToken() != JsonToken.END_OBJECT) {
                                String fieldName = parser.currentName();
                                parser.nextToken();
                                tempObject.put(fieldName, parser.getText());

                                if (nameKey.equals(fieldName) && valueKey.equals(parser.getText())) {
                                    isMatch = true;
                                }
                            }

                            if (isMatch) {
                                generator.writeRawValue(newItem.toString());
                                itemReplaced = true;
                            } else {
                                generator.writeRawValue(tempObject.toString());
                            }
                        }
                    }

                    generator.writeEndArray();
                } else {
                    generator.copyCurrentEvent(parser);
                }
            }

            if (!itemReplaced) {
                System.err.println("Het item met key '" + nameKey + "' in array '" + arrayKey + "' is niet gevonden.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ObjectNode> getJsonArrayFromFile(String filePath, String arrayKey) {
        return findJsonArrayItemsByFields(filePath, arrayKey, new ArrayList<>(), SearchSettings.findAll());
    }

    private static File getTempFile(String filePath) {
        Path p = Paths.get(filePath);
        String fileName = p.getFileName().toString();
        File tempFile = new File(filePath.replace(fileName, "temp_" + fileName));
        return tempFile;
    }

    public static boolean hasFields(String filename, String arrayKey, List<String> fieldsToCheck) {
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

    public static ArrayList<ObjectNode> findJsonArrayItemsByFields(String filename, String arrayKey, ArrayList<SearchCriteria> searchCriteriaList, SearchSettings settings) {
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
                                    throw new InvalidParameterException(new JSONMissingFieldException(filename, arrayKey, missingFields.get(0)).getMessage());
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
                                throw new InvalidParameterException(new JSONMissingFieldException(filename, arrayKey, searchCriteriaList.toString()).getMessage());
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

    // --- objects to json and json to ObjectNode methods ---

    private static User getUserFromObjectNode(ObjectNode obj) {
        String id = obj.get("id").asText();
        String username = obj.get("username").asText();
        String password = obj.get("password").asText();
        UserType userType = UserType.getUserTypeFromType(obj.get("userType").asText());
        Language preferredLanguage = Language.getLanguageFromCode(obj.get("preferredLanguage").asText());

        if (userType.equals(UserType.EMPLOYEE)) {
            return new Employee(id, username, password, preferredLanguage);
        } else {
            return new Customer(id, username, password, preferredLanguage);
        }
    }

    private static ObjectNode createUserObjectNode(User user) {
        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("id", user.getId());
        userNode.put("username", user.getUsername());
        userNode.put("password", user.getPassword());
        userNode.put("userType", user.getUserType().getType());
        userNode.put("preferredLanguage", user.getPreferredLanguage().getCode());
        return userNode;
    }

    private static Product getExternProductFromObjectNode(ObjectNode obj) {
        String id = getFieldString(obj, Validation.createArrayListWithValues("id"), Validation.generateID());
        String name = getFieldString(obj, Validation.createArrayListWithValues("naam", "name"));
        String origin = getFieldString(obj, Validation.createArrayListWithValues("herkomst", "origin"));
        ProductCategorie productCategorie = null;
        String description = getFieldString(obj, Validation.createArrayListWithValues("verhaal", "description"));
        int timesViewed = getFieldInt(obj, Validation.createArrayListWithValues("timesViewed"), 0);
        int stockNL = getFieldInt(obj, Validation.createArrayListWithValues("beschikbaarheid_nl", "stockNL"), 1);
        String ripeningDate = getFieldString(obj, Validation.createArrayListWithValues("ripeningDate"), Validation.getTodayDate());
        ArrayList<String> seasons = new ArrayList<>();
        double price = getFieldDouble(obj, Validation.createArrayListWithValues("price"), 1.0);

        if (obj.has("categorie")) {
            productCategorie = new ProductCategorie(obj.get("categorie").asText());
        }
        if (obj.has("seasons")) {
            seasons = new ArrayList<String>(Arrays.asList(obj.get("seasons").asText().split(",")));
        }

        return new Product(id, name, description, origin, ripeningDate, timesViewed, seasons, stockNL, price, productCategorie);
    }

    private static ObjectNode createProductObjectNode(Product product) {
        ObjectNode productNode = objectMapper.createObjectNode();
        productNode.put("id", product.getId());
        productNode.put("name", product.getName());
        productNode.put("description", product.getDescription());
        productNode.put("origin", product.getOrigin());
        productNode.put("categorie", product.getCategorie());
        productNode.put("ripeningDate", product.getRipeningDate());
        productNode.put("timesViewed", product.getTimesViewed());
        productNode.put("seasons", product.getSeasons());
        productNode.put("stockNL", product.getStockNL());
        productNode.put("price", product.getPrice());
        return productNode;
    }

    private static ShoppingListProduct getShoppingListProductFromJson(ObjectNode obj) {
        String productId = obj.get("productId").asText();
        int amountInShoppinglist = obj.get("amountInShoppinglist").asInt();

        return new ShoppingListProduct(Inventory.findProductById(productId), amountInShoppinglist);
    }

    private static ObjectNode createShoppingListProductObjectNode(ShoppingListProduct product) {
        ObjectNode productJson = objectMapper.createObjectNode();
        productJson.put("productId", product.getId());
        productJson.put("amountInShoppinglist", product.getAmountInShoppingList());
        return productJson;
    }

    private static Branch getBranchFromObjectNode(ObjectNode obj) {
        String id = getFieldString(obj, Validation.createArrayListWithValues("id"), Validation.generateID());
        String name = getFieldString(obj, Validation.createArrayListWithValues("naam", "name"));
        String city = getFieldString(obj, Validation.createArrayListWithValues("stad", "city"));
        String province = getFieldString(obj, Validation.createArrayListWithValues("provincie", "province"));
        String address = getFieldString(obj, Validation.createArrayListWithValues("adres", "address"));
        ArrayList<BranchProduct> branchProducts = new ArrayList<>();

        return new Branch(id, name, province, city, address, branchProducts);
    }

    private static ObjectNode createBranchObjectNode(Branch branch) {
        ObjectNode branchObject = objectMapper.createObjectNode();
        branchObject.put("id", branch.getId());
        branchObject.put("name", branch.getName());
        branchObject.put("province", branch.getProvince());
        branchObject.put("city", branch.getCity());
        branchObject.put("address", branch.getAddress());
        branchObject.set("producten", createBranchProductArrayNodeFromBranch(branch));

        return branchObject;
    }

    private static BranchProduct getBranchProductFromObjectNode(ObjectNode obj, Product product) {
        int stock = getFieldInt(obj, Validation.createArrayListWithValues("stock"), 0);

        ProductStatus status = ProductStatus.AVAILABLE;

        if (obj.has("status")) {
            ProductStatus productStatus = ProductStatus.getById(obj.get("status").asInt());
            if (productStatus != null) {
                status = ProductStatus.getById(obj.get("status").asInt());
            }
        }

        return new BranchProduct(product, stock, status);
    }

    private static ObjectNode createBranchProductJSONObject(BranchProduct branchProduct) {
        ObjectNode productObject = objectMapper.createObjectNode();
        productObject.put("id", branchProduct.getId());
        productObject.put("stock", branchProduct.getStock());
        productObject.put("status", branchProduct.getStatus().getId());
        return productObject;
    }

    private static ArrayList<ObjectNode> getBranchProductObjectNodeArrayListFromObjectNode(ObjectNode obj) {
        ArrayList<ObjectNode> branchProductObjectNodes = new ArrayList<>();
        JsonNode productenNode = getFieldJsonNode(obj, Validation.createArrayListWithValues("fruit", "producten"), null);

        if (productenNode != null && productenNode.isArray()) {
            for (JsonNode productNode : productenNode) {
                if (productNode.isObject()) {
                    branchProductObjectNodes.add((ObjectNode) productNode);
                }
            }
        }

        return branchProductObjectNodes;
    }

    private static ArrayNode createBranchProductArrayNodeFromBranch(Branch branch) {
        ArrayNode productenArray = objectMapper.createArrayNode();

        for (BranchProduct product : branch.getBranchProducts()) {
            ObjectNode productObject = createBranchProductJSONObject(product);
            productenArray.add(productObject);
        }
        return productenArray;
    }

    // --- getField methods ---
    private static String getFieldString(ObjectNode obj, ArrayList<String> fieldNames) {
        return getFieldString(obj, fieldNames, "");
    }

    private static String getFieldString(ObjectNode obj, ArrayList<String> fieldNames, String defaultValue) {
        for (String fieldName : fieldNames) {
            if (obj.has(fieldName)) {
                return obj.get(fieldName).asText();
            }
        }
        return defaultValue;
    }

    private static int getFieldInt(ObjectNode obj, ArrayList<String> fieldNames, int defaultValue) {
        for (String fieldName : fieldNames) {
            if (obj.has(fieldName)) {
                return obj.get(fieldName).asInt(defaultValue);
            }
        }
        return defaultValue;
    }

    private static double getFieldDouble(ObjectNode obj, ArrayList<String> fieldNames, double defaultValue) {
        for (String fieldName : fieldNames) {
            if (obj.has(fieldName)) {
                return obj.get(fieldName).asDouble(defaultValue);
            }
        }
        return defaultValue;
    }

    private static JsonNode getFieldJsonNode(ObjectNode obj, ArrayList<String> fieldNames, JsonNode defaultValue) {
        for (String fieldName : fieldNames) {
            if (obj.has(fieldName)) {
                return obj.get(fieldName);
            }
        }
        return defaultValue;
    }
}
