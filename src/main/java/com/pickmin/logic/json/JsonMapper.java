package com.pickmin.logic.json;

import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pickmin.config.GlobalConfig;
import com.pickmin.exceptions.JSONMissingObjectException;
import com.pickmin.logic.general.UtilityFunctions;
import com.pickmin.logic.model.Branch;
import com.pickmin.logic.model.BranchProduct;
import com.pickmin.logic.model.Customer;
import com.pickmin.logic.model.Employee;
import com.pickmin.logic.model.Product;
import com.pickmin.logic.model.ProductCategorie;
import com.pickmin.logic.model.ProductStatus;
import com.pickmin.logic.model.ProductUnit;
import com.pickmin.logic.model.ShoppingList;
import com.pickmin.logic.model.ShoppingListProduct;
import com.pickmin.logic.model.User;
import com.pickmin.logic.model.UserType;
import com.pickmin.translation.Language;

class JsonMapper {
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = JsonUtility.getObjectMapper();
    }

    static User getUserFromObjectNode(ObjectNode obj) {
        String id = JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("id"));
        String username = JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("username"));
        String password = JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("password"));
        UserType userType = UserType.getUserTypeFromType(JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("userType")));
        Language preferredLanguage = Language.getLanguageFromCode(JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("preferredLanguage")));

        if (userType.equals(UserType.EMPLOYEE)) {
            return new Employee(id, username, password, preferredLanguage);
        } else {
            return new Customer(id, username, password, preferredLanguage);
        }
    }

    static ObjectNode createUserObjectNode(User user) {
        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("id", user.getId());
        userNode.put("username", user.getUsername());
        userNode.put("password", user.getPassword());
        userNode.put("userType", user.getUserType().getType());
        userNode.put("preferredLanguage", user.getPreferredLanguage().getCode());
        return userNode;
    }

    static Product getProductFromObjectNode(ObjectNode obj) {
        String id = JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("id"), UtilityFunctions.generateID());
        String name = JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("naam", "name"));
        String origin = JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("herkomst", "origin"));
        ProductCategorie productCategorie = null;
        ProductUnit unit = ProductUnit.getByDescription(JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("unit"), ProductUnit.getDefault().toString()));
        String description = JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("verhaal", "description"));
        int timesViewed = JsonUtility.getFieldInt(obj, UtilityFunctions.createArrayListWithValues("timesViewed"), 0);
        int stockNL = JsonUtility.getFieldInt(obj, UtilityFunctions.createArrayListWithValues("beschikbaarheid_nl", "stockNL"), 1);
        String ripeningDate = JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("ripeningDate"), UtilityFunctions.getTodayDate());
        ArrayList<String> seasons = UtilityFunctions.extractArrayListFromString(JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("seasons"), ""));
        double price = JsonUtility.getFieldDouble(obj, UtilityFunctions.createArrayListWithValues("price"), 1.0);

        if (obj.has("categorie")) {
            productCategorie = new ProductCategorie(JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("categorie")));
        }

        return new Product(id, name, description, origin, ripeningDate, timesViewed, seasons, stockNL, price, productCategorie, unit);
    }

    static ObjectNode createProductObjectNode(Product product) {
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
        productNode.put("unit", product.getUnit().toString());
        return productNode;
    }

    static ShoppingList getShoppingListFromObjectNode(ObjectNode obj, ArrayList<ShoppingListProduct> shoppingListProducts) {
        String id = JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("id"), UtilityFunctions.generateID());
        String userId = JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("userId"));
        if (shoppingListProducts == null) {
            shoppingListProducts = new ArrayList<>();
        }

        return new ShoppingList(id, userId, shoppingListProducts);
    }

    static ObjectNode createShoppingListObjectNode(ShoppingList shoppingList) {
        ObjectNode obj = objectMapper.createObjectNode();
        obj.put("id", shoppingList.getId());
        obj.put("userId", shoppingList.getUserId());
        obj.set("producten", createShoppingListProductArrayNodeFromShoppingList(shoppingList));
        return obj;
    }

    static ShoppingListProduct getShoppingListProductFromJson(ObjectNode obj, BranchProduct product) {
        int amountInShoppingList = JsonUtility.getFieldInt(obj, UtilityFunctions.createArrayListWithValues("amountInShoppingList"), 1);

        return new ShoppingListProduct(product, amountInShoppingList);
    }

    static ObjectNode createShoppingListProductObjectNode(ShoppingListProduct product) {
        ObjectNode productJson = objectMapper.createObjectNode();
        productJson.put("idBranchProduct", product.getIdBranchProduct());
        productJson.put("amountInShoppingList", product.getAmountInShoppingList());
        return productJson;
    }

    static ArrayNode createShoppingListProductArrayNodeFromShoppingList(ShoppingList shoppingList) {
        ArrayNode productenArray = objectMapper.createArrayNode();

        for (ShoppingListProduct shoppingListProduct : shoppingList.getShoppingListProducts()) {
            ObjectNode shoppingListProductObject = createShoppingListProductObjectNode(shoppingListProduct);
            productenArray.add(shoppingListProductObject);
        }
        return productenArray;
    }

    static Branch getBranchFromObjectNode(ObjectNode obj) {
        String id = JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("id"), UtilityFunctions.generateID());
        String name = JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("naam", "name"));
        String province = JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("provincie", "province"));
        String city = JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("stad", "city"));
        String address = JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("adres", "address"));
        ArrayList<BranchProduct> branchProducts = new ArrayList<>();

        return new Branch(id, name, province, city, address, branchProducts);
    }

    static ObjectNode createBranchObjectNode(Branch branch) {
        ObjectNode branchObject = objectMapper.createObjectNode();
        branchObject.put("id", branch.getId());
        branchObject.put("name", branch.getName());
        branchObject.put("province", branch.getProvince());
        branchObject.put("city", branch.getCity());
        branchObject.put("address", branch.getAddress());
        branchObject.set("producten", createBranchProductArrayNodeFromBranch(branch));

        return branchObject;
    }

    static ObjectNode createBranchObjectNode(ObjectNode obj) {
        ObjectNode branchObject = objectMapper.createObjectNode();
        String id = JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("id"), UtilityFunctions.generateID());
        String name = JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("naam", "name"));
        String province = JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("provincie", "province"));
        String city = JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("stad", "city"));
        String address = JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("adres", "address"));

        ArrayList<ObjectNode> externProductArrayList = createProductObjectNodeArrayListFromObjectNode(obj);
        ArrayList<ObjectNode> internProductArrayList = new ArrayList<>();
        for (ObjectNode externProductObjectNode : externProductArrayList) {
            HashMap<String, String> productIdentifier = JsonMapper.getProductIdentifierFromObjectNode(externProductObjectNode);
            try {
                Product product = JsonDataOperations.getProductFromJsonWithIdentifier(GlobalConfig.PRODUCTS_FILE_PATH, productIdentifier);
                internProductArrayList.add(createBranchProductObjectNode(externProductObjectNode, product));
            } catch (JSONMissingObjectException e) {
                e.printStackTrace();
            }
        }
        ArrayNode branchProducts = JsonUtility.arrayListObjectNodeToArrayNode(internProductArrayList);

        branchObject.put("id", id);
        branchObject.put("name", name);
        branchObject.put("province", province);
        branchObject.put("city", city);
        branchObject.put("address", address);
        branchObject.set("producten", branchProducts);

        return branchObject;
    }

    static BranchProduct getBranchProductFromObjectNode(ObjectNode obj, Product product) {
        String idBranchProduct = JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("idBranchProduct"), UtilityFunctions.generateID());
        int stock = JsonUtility.getFieldInt(obj, UtilityFunctions.createArrayListWithValues("beschikbaarheid_locatie", "stock"), 0);

        ProductStatus status = ProductStatus.AVAILABLE;

        if (obj.has("status")) {
            ProductStatus productStatus = ProductStatus.getById(obj.get("status").asInt());
            if (productStatus != null) {
                status = ProductStatus.getById(obj.get("status").asInt());
            }
        }

        return new BranchProduct(product, idBranchProduct, stock, status);
    }

    static ObjectNode createBranchProductJSONObject(BranchProduct branchProduct) {
        ObjectNode branchProductObject = objectMapper.createObjectNode();
        branchProductObject.put("id", branchProduct.getId());
        branchProductObject.put("idBranchProduct", branchProduct.getIdBranchProduct());
        branchProductObject.put("stock", branchProduct.getStock());
        branchProductObject.put("status", branchProduct.getStatus().getId());
        return branchProductObject;
    }

    static ObjectNode createBranchProductObjectNode(ObjectNode obj, Product product) {
        try {
            ObjectNode branchProductObject = objectMapper.createObjectNode();
            String id = product.getId();
            String idBranchProduct = JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("idBranchProduct"), UtilityFunctions.generateID());
            int stock = JsonUtility.getFieldInt(obj, UtilityFunctions.createArrayListWithValues("beschikbaarheid_lokatie", "beschikbaarheid_locatie", "stock"), 0);
            int status = JsonUtility.getFieldInt(obj, UtilityFunctions.createArrayListWithValues("status"), ProductStatus.AVAILABLE.getId());

            branchProductObject.put("id", id);
            branchProductObject.put("idBranchProduct", idBranchProduct);            
            branchProductObject.put("stock", stock);
            branchProductObject.put("status", status);
            return branchProductObject;
        } catch (Exception e) {
            return null;
        }
    }

    static ArrayList<ObjectNode> createProductObjectNodeArrayListFromObjectNode(ObjectNode obj) {
        ArrayList<ObjectNode> productObjectNodes = new ArrayList<>();
        JsonNode productenNode = JsonUtility.getFieldJsonNode(obj, UtilityFunctions.createArrayListWithValues("fruit", "producten"), null);

        if (productenNode != null && productenNode.isArray()) {
            for (JsonNode productNode : productenNode) {
                if (productNode.isObject()) {
                    productObjectNodes.add((ObjectNode) productNode);
                }
            }
        }

        return productObjectNodes;
    }

    static ArrayNode createBranchProductArrayNodeFromBranch(Branch branch) {
        ArrayNode productenArray = objectMapper.createArrayNode();

        for (BranchProduct product : branch.getBranchProducts()) {
            ObjectNode productObject = createBranchProductJSONObject(product);
            productenArray.add(productObject);
        }
        return productenArray;
    }

    static HashMap<String, String> getProductIdentifierFromObjectNode(ObjectNode obj) {
        HashMap<String, String> productIdentifier = new HashMap<>();
        if (obj.has("id")) {
            productIdentifier.put("field", "id");
            productIdentifier.put("value", JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("id"), ""));
        } else if (obj.has("naam") || obj.has("name")) {
            productIdentifier.put("field", "name");
            productIdentifier.put("value", JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("naam", "name")));
        } else {
            return null;
        }
        return productIdentifier;
    }

    static HashMap<String, String> getBranchProductIdentifierFromObjectNode(ObjectNode obj) {
        HashMap<String, String> branchProductIdentifier = new HashMap<>();
        if (obj.has("idBranchProduct")) {
            branchProductIdentifier.put("field", "idBranchProduct");
            branchProductIdentifier.put("value", JsonUtility.getFieldString(obj, UtilityFunctions.createArrayListWithValues("idBranchProduct"), ""));
        } else {
            return null;
        }
        return branchProductIdentifier;
    }    
}
