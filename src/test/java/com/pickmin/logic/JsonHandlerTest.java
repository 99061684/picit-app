package com.pickmin.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pickmin.config.GlobalConfig;

public class JsonHandlerTest {
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testLoadProductsFromJson() {
        try {
            ArrayList<Product> products = JsonHandler.loadProductsFromJson(GlobalConfig.TEST_PRODUCTS_FILE_PATH);

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
    public void testLoadBranchesFromJson() {
        try {
            ArrayList<Branch> branches = JsonHandler.loadBranchesFromJson(GlobalConfig.TEST_BRANCHES_FILE_PATH);

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
    public void testLoadBranchesFromJson2() {
        try {
            SearchSettings searchSetting = SearchSettings.findAll();
            searchSetting.setExcludeFieldsToRetrieve(Validation.createArrayListWithValues("fruit", "producten"));
            
            ArrayList<ObjectNode> dataArray = JsonHandler.findJsonArrayItemsByFields(GlobalConfig.TEST_BRANCHES_FILE_PATH, "vestigingen", new ArrayList<>(), searchSetting);
            assertNotNull(dataArray, "Branches zouden niet null mogen zijn");

            String objectString = objectMapper.writeValueAsString(dataArray.get(0));
            JSONObject json = new JSONObject(objectString);
            System.out.println(json.toString(4));
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

            ArrayList<ObjectNode> actualResults = JsonHandler.findJsonArrayItemsByFields(GlobalConfig.TEST_PRODUCTS_FILE_PATH, "vruchten", searchCriteriaList, SearchSettings.findFirst());

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

            ArrayList<ObjectNode> actualResults = JsonHandler.findJsonArrayItemsByFields(GlobalConfig.TEST_PRODUCTS_FILE_PATH, "vruchten", searchCriteriaList, SearchSettings.findFirst());

            assertEquals(1, actualResults.size());
            assertEquals("Mango", actualResults.get(0).get("name").asText());
            assertEquals(2.75, actualResults.get(0).get("price").asDouble());

            searchCriteriaList = new ArrayList<>();
            searchCriteriaList.add(new SearchCriteria("name", "=", "Mango"));
            searchCriteriaList.add(new SearchCriteria("price", ">=", 5.00));

            actualResults = JsonHandler.findJsonArrayItemsByFields(GlobalConfig.TEST_PRODUCTS_FILE_PATH, "vruchten", searchCriteriaList, SearchSettings.findFirst());

            assertEquals(0, actualResults.size());

            searchCriteriaList = new ArrayList<>();
            searchCriteriaList.add(new SearchCriteria("price", ">=", 4.00));

            actualResults = JsonHandler.findJsonArrayItemsByFields(GlobalConfig.TEST_PRODUCTS_FILE_PATH, "vruchten", searchCriteriaList, SearchSettings.findAll());

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

            ArrayList<ObjectNode> actualResults = JsonHandler.findJsonArrayItemsByFields(GlobalConfig.TEST_PRODUCTS_FILE_PATH, "vruchten", searchCriteriaList, SearchSettings.findFirst());

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

            ArrayList<ObjectNode> actualResults = JsonHandler.findJsonArrayItemsByFields(GlobalConfig.TEST_PRODUCTS_FILE_PATH, "vruchten", searchCriteriaList, SearchSettings.findAll());

            assertEquals(26, actualResults.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    public void testHasFields_AllFieldsExist() {
        List<String> fieldsToCheck = Arrays.asList("id", "name", "description");

        try {
            boolean result = JsonHandler.hasFields(GlobalConfig.TEST_PRODUCTS_FILE_PATH, "vruchten", fieldsToCheck);
            assertTrue(result, "Alle verwachte velden zijn aanwezig in de JSON-array.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testHasFields_FieldMissing() {
        List<String> fieldsToCheck = Arrays.asList("id", "name", "nonExistentField");

        try {
            boolean result = JsonHandler.hasFields(GlobalConfig.TEST_PRODUCTS_FILE_PATH, "vruchten", fieldsToCheck);
            assertFalse(result, "Verwacht dat het resultaat false is, aangezien een veld ontbreekt.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testHasFields_ArrayNotFound() {
        List<String> fieldsToCheck = Arrays.asList("id", "name", "description");

        try {
            boolean result = JsonHandler.hasFields(GlobalConfig.TEST_PRODUCTS_FILE_PATH, "doesNotExists", fieldsToCheck);
            assertFalse(result, "Verwacht dat het resultaat false is omdat de array niet bestaat.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
    }
}