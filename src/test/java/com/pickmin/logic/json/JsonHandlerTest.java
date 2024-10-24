package com.pickmin.logic.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.ArrayList;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pickmin.config.GlobalConfig;
import com.pickmin.logic.general.UtilityFunctions;
import com.pickmin.logic.model.Branch;
import com.pickmin.logic.model.Customer;
import com.pickmin.logic.model.Product;
import com.pickmin.logic.model.ShoppingList;
import com.pickmin.logic.model.User;
import com.pickmin.logic.model.UserManagement;
import com.pickmin.logic.model.filter.BranchFilter;
import com.pickmin.translation.Language;

public class JsonHandlerTest {
    private static ObjectMapper objectMapper;

    @BeforeEach
    private void setup() {
        JsonHandlerTest.objectMapper = JsonUtility.getObjectMapper();
    }

    private void loginCustomerSetup() {
        User tempTestUser = new Customer("7d0d0422-964f-45d4-9e86-4bc09ded8f3b", "tempTestUser", "tempTestUser", Language.DUTCH);
        UserManagement.setLoggedInUser(tempTestUser);
    }

    @Test
    public void testLoadProductsFromJson() {
        try {
            ArrayList<Product> products = JsonHandler.testLoadProductsFromJson();

            assertNotNull(products, "Producten zouden niet null mogen zijn");

            String objectString = objectMapper.writeValueAsString(products.get(0));
            JSONObject json = new JSONObject(objectString);
            System.out.println(json.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Er ging iets mis bij het verwerken van JSON: " + e.getMessage());
        }
    }

    @Test
    public void testJsonPathSearch() {
        try {
            ArrayList<ObjectNode> branches = JsonDataOperationsCopy2.searchForJsonArrayItems(GlobalConfig.BRANCHES_FILE_PATH, "$.branches[*]['id', 'name', 'province', 'city', 'address']");

            assertNotNull(branches, "Producten zouden niet null mogen zijn");

            String objectString = objectMapper.writeValueAsString(branches.get(0));
            JSONObject json = new JSONObject(objectString);
            System.out.println(json.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Er ging iets mis bij het verwerken van JSON: " + e.getMessage());
        }
    }

    

    @Test
    public void tesGetBranchFilters() {
        try {
            ArrayList<BranchFilter> branchFilters = JsonHandler.testGetBranchFilters();

            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            String objectString = objectMapper.writeValueAsString(branchFilters);
            Object jsonObject = objectMapper.readValue(objectString, Object.class);
            String prettyPrintedJson = objectMapper.writeValueAsString(jsonObject);
            System.out.println(prettyPrintedJson);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Er ging iets mis bij het verwerken van JSON: " + e.getMessage());
        }
    }

    @Test
    public void testLoadBranchesFromJson() {
        try {
            ArrayList<Branch> branches = JsonHandler.testLoadBranchesFromJson();

            assertNotNull(branches, "Branches zouden niet null mogen zijn");

            String objectString = objectMapper.writeValueAsString(branches.get(0));
            JSONObject json = new JSONObject(objectString);
            System.out.println(json.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Er ging iets mis bij het verwerken van JSON: " + e.getMessage());
        }
    }

    @Test
    public void testLoadShoppingListFromJson() {
        try {
            loginCustomerSetup();

            ShoppingList shoppingList = JsonHandler.testLoadShoppingListFromJson(UserManagement.getLoggedInUser().getId());

            assertNotNull(shoppingList, "ShoppingList zou niet null mogen zijn");

            String objectString = objectMapper.writeValueAsString(shoppingList);
            JSONObject json = new JSONObject(objectString);
            System.out.println(json.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Er ging iets mis bij het verwerken van JSON: " + e.getMessage());
        }
    }

    @Test
    public void testSaveShoppingListToJson() {
        assumeTrue(GlobalConfig.TEST_SAVE_SHOPPINGLIST);
        try {
            loginCustomerSetup();

            ShoppingList shoppingList = new ShoppingList(UserManagement.getLoggedInUser().getId());
            JsonHandler.testSaveShoppingListToJson(shoppingList);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Er ging iets mis bij het verwerken van JSON: " + e.getMessage());
        }
    }

    @Test
    public void testImportBranchesFromJson() {
        assumeTrue(GlobalConfig.TEST_IMPORT_BRANCHES);
        try {
            JsonHandler.importBranchesFromJson();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Er ging iets mis bij het verwerken van JSON: " + e.getMessage());
        }
    }

    @Test
    public void testFindJsonArrayItemsByFields_singleCriteria() {
        try {
            ArrayList<SearchCriteria> searchCriteriaList = new ArrayList<>();
            searchCriteriaList.add(new SearchCriteria("name", "=", "Mango"));

            ArrayList<ObjectNode> actualResults = JsonDataOperations.searchForJsonArrayItems(GlobalConfig.TEST_PRODUCTS_FILE_PATH, "producten", searchCriteriaList, SearchSettings.findFirst());

            assertEquals(1, actualResults.size());
            assertEquals("Mango", actualResults.get(0).get("name").asText());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    public void testFindJsonArrayItemsByFields_multipleCriteria() {
        try {
            ArrayList<SearchCriteria> searchCriteriaList = new ArrayList<>();
            searchCriteriaList.add(new SearchCriteria("name", "=", "Mango"));
            searchCriteriaList.add(new SearchCriteria("price", ">", 2.00));

            ArrayList<ObjectNode> actualResults = JsonDataOperations.searchForJsonArrayItems(GlobalConfig.TEST_PRODUCTS_FILE_PATH, "producten", searchCriteriaList, SearchSettings.findFirst());

            assertEquals(1, actualResults.size());
            assertEquals("Mango", actualResults.get(0).get("name").asText());
            assertEquals(2.75, actualResults.get(0).get("price").asDouble());

            searchCriteriaList = new ArrayList<>();
            searchCriteriaList.add(new SearchCriteria("name", "=", "Mango"));
            searchCriteriaList.add(new SearchCriteria("price", ">=", 5.00));

            actualResults = JsonDataOperations.searchForJsonArrayItems(GlobalConfig.TEST_PRODUCTS_FILE_PATH, "producten", searchCriteriaList, SearchSettings.findFirst());

            assertEquals(0, actualResults.size());

            searchCriteriaList = new ArrayList<>();
            searchCriteriaList.add(new SearchCriteria("price", ">=", 4.00));

            actualResults = JsonDataOperations.searchForJsonArrayItems(GlobalConfig.TEST_PRODUCTS_FILE_PATH, "producten", searchCriteriaList, SearchSettings.findAll());

            assertEquals(4, actualResults.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    public void testFindJsonArrayItemsByFields_noMatchingItems() {
        try {
            ArrayList<SearchCriteria> searchCriteriaList = new ArrayList<>();
            searchCriteriaList.add(new SearchCriteria("name", "=", "NonExistingFruit"));

            ArrayList<ObjectNode> actualResults = JsonDataOperations.searchForJsonArrayItems(GlobalConfig.TEST_PRODUCTS_FILE_PATH, "producten", searchCriteriaList, SearchSettings.findFirst());

            assertEquals(0, actualResults.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    public void testFindJsonArrayItemsByFields_noSearchCriteria() {
        try {
            ArrayList<SearchCriteria> searchCriteriaList = new ArrayList<>();

            ArrayList<ObjectNode> actualResults = JsonDataOperations.searchForJsonArrayItems(GlobalConfig.TEST_PRODUCTS_FILE_PATH, "producten", searchCriteriaList, SearchSettings.findAll());

            assertEquals(26, actualResults.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    public void testHasFields_AllFieldsExist() {
        ArrayList<String> fieldsToCheck = UtilityFunctions.createArrayListWithValues("id", "name", "description");

        try {
            boolean result = JsonDataOperations.hasFields(GlobalConfig.TEST_PRODUCTS_FILE_PATH, "producten", fieldsToCheck);
            assertTrue(result, "Alle verwachte velden zijn aanwezig in de JSON-array.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testHasFields_FieldMissing() {
        ArrayList<String> fieldsToCheck = UtilityFunctions.createArrayListWithValues("id", "name", "nonExistentField");

        try {
            boolean result = JsonDataOperations.hasFields(GlobalConfig.TEST_PRODUCTS_FILE_PATH, "producten", fieldsToCheck);
            assertFalse(result, "Verwacht dat het resultaat false is, aangezien een veld ontbreekt.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testHasFields_ArrayNotFound() {
        ArrayList<String> fieldsToCheck = UtilityFunctions.createArrayListWithValues("id", "name", "description");

        try {
            boolean result = JsonDataOperations.hasFields(GlobalConfig.TEST_PRODUCTS_FILE_PATH, "doesNotExists", fieldsToCheck);
            assertFalse(result, "Verwacht dat het resultaat false is omdat de array niet bestaat.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
    }
}