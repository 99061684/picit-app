package com.pickmin.logic.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pickmin.config.GlobalConfig;
import com.pickmin.exceptions.JSONMissingObjectException;
import com.pickmin.logic.general.UtilityFunctions;
import com.pickmin.logic.model.Branch;
import com.pickmin.logic.model.BranchProduct;
import com.pickmin.logic.model.Product;
import com.pickmin.logic.model.ShoppingList;
import com.pickmin.logic.model.ShoppingListProduct;
import com.pickmin.logic.model.User;
import com.pickmin.logic.model.filter.BranchFilter;

public class JsonHandler {
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = JsonUtility.getObjectMapper();
    }

    // --- import data ---
    public static void importProductsFromJson() {
        ArrayList<Product> products = new ArrayList<>();
        ArrayList<ObjectNode> dataArray = JsonDataOperations.getJsonArrayFromFile(GlobalConfig.EXTERN_PRODUCTS_FILE_PATH, "vruchten");

        if (dataArray != null) {
            for (ObjectNode node : dataArray) {
                products.add(JsonMapper.getProductFromObjectNode(node));
            }
        }
        saveProductsToJson(products);
    }

    public static void importBranchesFromJson() {
        ArrayList<ObjectNode> dataArray2 = new ArrayList<>();
        ArrayList<ObjectNode> dataArray = JsonDataOperations.getJsonArrayFromFile(GlobalConfig.EXTERN_BRANCHES_FILE_PATH, "vestigingen");

        if (dataArray != null) {
            for (ObjectNode node : dataArray) {
                dataArray2.add(JsonMapper.createBranchObjectNode(node));
            }
        }
        ArrayNode branches = JsonUtility.arrayListObjectNodeToArrayNode(dataArray2);

        JsonDataOperations.writeJsonArrayToFile(GlobalConfig.BRANCHES_FILE_PATH, "branches", branches);
    }

    // --- load/save data ---
    private static ArrayList<Product> loadProductsFromJson(String filePath) {
        ArrayList<Product> products = new ArrayList<>();
        ArrayList<ObjectNode> dataArray = JsonDataOperations.getJsonArrayFromFile(GlobalConfig.PRODUCTS_FILE_PATH, "producten");

        if (dataArray != null) {
            for (ObjectNode node : dataArray) {
                products.add(JsonMapper.getProductFromObjectNode(node));
            }
        }
        return products;
    }

    public static ArrayList<Product> loadProductsFromJson() {
        return loadProductsFromJson(GlobalConfig.PRODUCTS_FILE_PATH);
    }

    public static ArrayList<Product> testLoadProductsFromJson() {
        return loadProductsFromJson(GlobalConfig.TEST_PRODUCTS_FILE_PATH);
    }

    public static void saveProductsToJson(ArrayList<Product> products) {
        ArrayNode productArray = objectMapper.createArrayNode();
        for (Product product : products) {
            productArray.add(JsonMapper.createProductObjectNode(product));
        }
        JsonDataOperations.writeJsonArrayToFile(GlobalConfig.PRODUCTS_FILE_PATH, "producten", productArray);
    }

    public static ArrayList<User> loadUsersFromJson() {
        ArrayList<User> users = new ArrayList<>();
        ArrayList<ObjectNode> dataArray = JsonDataOperations.getJsonArrayFromFile(GlobalConfig.USERS_FILE_PATH, "users");

        if (dataArray != null) {
            for (ObjectNode node : dataArray) {
                users.add(JsonMapper.getUserFromObjectNode(node));
            }
        }

        return users;
    }

    public static void saveUsersToJson(List<User> users) {
        ArrayNode userArray = objectMapper.createArrayNode();
        for (User user : users) {
            userArray.add(JsonMapper.createUserObjectNode(user));
        }

        JsonDataOperations.writeJsonArrayToFile(GlobalConfig.USERS_FILE_PATH, "users", userArray);
    }

    private static ShoppingList loadShoppingListFromJson(String shoppingListFilePath, String productFilePath, String userId) {
        ArrayList<ShoppingListProduct> shoppingListProducts = new ArrayList<>();
        SearchSettings searchSetting = SearchSettings.findFirst();

        ArrayList<SearchCriteria> searchCriteriaList = new ArrayList<>();
        searchCriteriaList.add(new SearchCriteria("userId", "=", userId));

        ArrayList<ObjectNode> dataArray = JsonDataOperations.searchForJsonArrayItems(shoppingListFilePath, "shoppingLists", searchCriteriaList, searchSetting);

        if (dataArray != null && !dataArray.isEmpty()) {
            ArrayList<ObjectNode> dataArray2 = JsonMapper.createProductObjectNodeArrayListFromObjectNode(dataArray.get(0));
            for (ObjectNode shoppingListProductObjectNode : dataArray2) {
                HashMap<String, String> branchProductIdentifier = JsonMapper.getBranchProductIdentifierFromObjectNode(shoppingListProductObjectNode);
                try {
                    BranchProduct product = JsonDataOperations.getBranchProductFromJsonWithIdentifier(branchProductIdentifier);
                    shoppingListProducts.add(JsonMapper.getShoppingListProductFromJson(shoppingListProductObjectNode, product));
                } catch (JSONMissingObjectException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return JsonMapper.getShoppingListFromObjectNode(dataArray.get(0), shoppingListProducts);
        }

        return null;
    }

    public static ShoppingList loadShoppingListFromJson(String userId) {
        return loadShoppingListFromJson(GlobalConfig.SHOPPINGLIST_FILE_PATH, GlobalConfig.PRODUCTS_FILE_PATH, userId);
    }

    public static ShoppingList testLoadShoppingListFromJson(String userId) {
        return loadShoppingListFromJson(GlobalConfig.TEST_SHOPPINGLIST_FILE_PATH, GlobalConfig.TEST_PRODUCTS_FILE_PATH, userId);
    }

    private static void saveShoppingListToJson(String pathFile, ShoppingList shoppingList) {
        ObjectNode shoppingListObjectNode = JsonMapper.createShoppingListObjectNode(shoppingList);

        JsonDataOperations.writeJsonArrayItemToFile(pathFile, "shoppingLists", "id", shoppingListObjectNode);
    }

    public static void saveShoppingListToJson(ShoppingList shoppingList) {
        saveShoppingListToJson(GlobalConfig.SHOPPINGLIST_FILE_PATH, shoppingList);
    }

    public static void testSaveShoppingListToJson(ShoppingList shoppingList) {
        saveShoppingListToJson(GlobalConfig.TEST_SHOPPINGLIST_FILE_PATH, shoppingList);
    }

    private static ArrayList<Branch> loadBranchesFromJson(String filePath) {
        SearchSettings searchSetting = SearchSettings.findAll();
        searchSetting.setExcludeFieldsToRetrieve(UtilityFunctions.createArrayListWithValues("fruit", "producten"));

        ArrayList<ObjectNode> dataArray = JsonDataOperations.searchForJsonArrayItems(filePath, "branches", new ArrayList<>(), searchSetting);
        ArrayList<Branch> branches = new ArrayList<>();

        if (dataArray != null) {
            for (ObjectNode node : dataArray) {
                branches.add(JsonMapper.getBranchFromObjectNode(node));
            }
        }

        return branches;
    }

    public static ArrayList<Branch> loadBranchesFromJson() {
        return JsonHandler.loadBranchesFromJson(GlobalConfig.BRANCHES_FILE_PATH);
    }

    public static ArrayList<Branch> testLoadBranchesFromJson() {
        return JsonHandler.loadBranchesFromJson(GlobalConfig.TEST_BRANCHES_FILE_PATH);
    }

    public static void saveBranchesToJson(List<Branch> branches) {
        ArrayNode branchArray = objectMapper.createArrayNode();
        for (Branch branch : branches) {
            branchArray.add(JsonMapper.createBranchObjectNode(branch));
        }

        JsonDataOperations.writeJsonArrayToFile(GlobalConfig.DATA_FILE_PATH + "branches.json", "branches", branchArray);
    }

    public static ArrayList<BranchProduct> loadBranchProductsFromJson(String branchId) {
        ArrayList<BranchProduct> branchProducts = new ArrayList<>();
        try {
            ArrayList<ObjectNode> branchProductObjectNodes = JsonDataOperationsCopy2.searchForJsonArrayItems(GlobalConfig.BRANCHES_FILE_PATH, "$.branches[?(@.id == '" + branchId + "')].producten[*]");

            if (branchProductObjectNodes != null && !branchProductObjectNodes.isEmpty()) {
                for (ObjectNode branchProductObjectNode : branchProductObjectNodes) {
                    HashMap<String, String> productIdentifier = JsonMapper.getProductIdentifierFromObjectNode(branchProductObjectNode);
                    try {
                        Product product = JsonDataOperations.getProductFromJsonWithIdentifier(GlobalConfig.PRODUCTS_FILE_PATH, productIdentifier);
                        branchProducts.add(JsonMapper.getBranchProductFromObjectNode(branchProductObjectNode, product));
                    } catch (JSONMissingObjectException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return branchProducts;
    }

    public static ArrayList<BranchProduct> loadBranchProductsFromJson(Branch branch) {
        return loadBranchProductsFromJson(branch.getId());
    }

    public static void saveBranchProductsToJson(String branchId, List<BranchProduct> branchProducts) {
        ArrayNode productArray = objectMapper.createArrayNode();
        for (BranchProduct product : branchProducts) {
            productArray.add(JsonMapper.createBranchProductJSONObject(product));
        }
        JsonDataOperations.writeJsonArrayToFile(GlobalConfig.DATA_FILE_PATH + "branch_" + branchId + "_products.json", "branchProducts", productArray);
    }

    private static HashMap<String, String> getProductNames(String filePath) {
        SearchSettings searchSetting = SearchSettings.findAll();
        searchSetting.setFieldsToRetrieve(UtilityFunctions.createArrayListWithValues("id", "name"), true);

        ArrayList<ObjectNode> dataArray = JsonDataOperations.searchForJsonArrayItems(filePath, "producten", new ArrayList<>(), searchSetting);
        HashMap<String, String> productNames = new HashMap<>();

        for (ObjectNode node : dataArray) {
            String id = JsonUtility.getFieldString(node, UtilityFunctions.createArrayListWithValues("id"));
            String name = JsonUtility.getFieldString(node, UtilityFunctions.createArrayListWithValues("name"));

            productNames.put(id, name);
        }

        return productNames;
    }

    public static HashMap<String, String> getProductNames() {
        return getProductNames(GlobalConfig.PRODUCTS_FILE_PATH);
    }

    public static ArrayList<ShoppingListProduct> getShoppingListProductsMissingInBranch(String branchId, ShoppingList shoppingList) {
        ArrayList<BranchProduct> branchProducts = loadBranchProductsFromJson(branchId);

        return shoppingList.getShoppingListProducts().stream()
                .filter(shoppingListProduct -> branchProducts.stream()
                        .noneMatch(branchProduct -> branchProduct.getId().equals(shoppingListProduct.getId())))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private static void saveProduct(String filePath, Product product) {
        ObjectNode item = JsonMapper.createProductObjectNode(product);
        JsonDataOperations.writeJsonArrayItemToFile(filePath, "producten", "id", item);
    }

    public static void saveProduct(Product product) {
        saveProduct(GlobalConfig.PRODUCTS_FILE_PATH, product);
    }

    // --- filterData functies ---

    private static ArrayList<BranchFilter> getBranchFilters(String filePath) {
        SearchSettings searchSetting = SearchSettings.findAll();
        searchSetting.setFieldsToRetrieve(UtilityFunctions.createArrayListWithValues("id", "city"), true);

        ArrayList<ObjectNode> dataArray = JsonDataOperations.searchForJsonArrayItems(filePath, "branches", new ArrayList<>(), searchSetting);
        ArrayList<BranchFilter> branchFilters = new ArrayList<>();

        for (ObjectNode node : dataArray) {
            String id = JsonUtility.getFieldString(node, UtilityFunctions.createArrayListWithValues("id"));
            String city = JsonUtility.getFieldString(node, UtilityFunctions.createArrayListWithValues("city"));

            BranchFilter branchFilter = new BranchFilter(id, city);
            branchFilters.add(branchFilter);
        }

        return branchFilters;
    }

    public static ArrayList<BranchFilter> getBranchFilters() {
        return getBranchFilters(GlobalConfig.BRANCHES_FILE_PATH);
    }

    public static ArrayList<BranchFilter> testGetBranchFilters() {
        return getBranchFilters(GlobalConfig.TEST_BRANCHES_FILE_PATH);
    }
}
