package com.pickmin.logic;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.pickmin.config.GlobalConfig;
import com.pickmin.exceptions.EmptyFileException;
import com.pickmin.translation.Language;

public class JsonHandler {

    public static ArrayList<Product> importProducts() {
        ArrayList<Product> products = new ArrayList<>();
        JSONArray dataArray = getDataArray(GlobalConfig.PRODUCTS_FILE_PATH);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject obj = dataArray.getJSONObject(i);
            products.add(getProductFromJson(obj));
        }

        return products;
    }

    // Methode om producten naar JSON te exporteren
    public static void exportProducts(String filePath, ArrayList<Product> products) {
        JSONArray productArray = new JSONArray();

        for (Product product : products) {
            productArray.put(getProductJson(product));
        }

        writeToFile(filePath, productArray);
    }

    public static void exportProducts(ArrayList<Product> products) {
        exportProducts(GlobalConfig.PRODUCTS_FILE_PATH, products);
    }

    public static void saveUsersToFile(List<User> users) {
        JSONArray userArray = new JSONArray();
        for (User user : users) {
            userArray.put(getUserJson(user));
        }

        writeToFile(GlobalConfig.USERS_FILE_PATH, userArray);
    }

    public static ArrayList<User> loadUsersFromFile() {
        ArrayList<User> users = new ArrayList<>();
        JSONArray dataArray = getDataArray(GlobalConfig.USERS_FILE_PATH);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject obj = dataArray.getJSONObject(i);
            users.add(getUserFromJson(obj));
        }

        return users;
    }

    public static void saveShoppingList(String userId, List<Product> products) {
        JSONArray jsonArray = new JSONArray();
        for (Product product : products) {
            jsonArray.put(getProductJson(product));
        }

        writeToFile(GlobalConfig.DATA_FILE_PATH + "shoppinglist_" + userId + ".json", jsonArray);
    }

    public static ShoppingList loadShoppingList(String userId) {
        ArrayList<Product> products = new ArrayList<>();
        JSONArray dataArray = getDataArray(GlobalConfig.DATA_FILE_PATH + "shoppinglist_" + userId + ".json");

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject obj = dataArray.getJSONObject(i);
            products.add(getProductFromJson(obj));
        }

        ShoppingList shoppingList = new ShoppingList(products);
        return shoppingList;
    }

    // ----------------- helper functions -----------------

    // schrijf een JSONArray met data naar een bestand.
    private static void writeToFile(String filePath, JSONArray jsonArray) {
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonArray.toString(4)); // Schrijf netjes de JSON weg met inspringing
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Haal de JSONArray met data uit een bestand op
    private static JSONArray getDataArray(String filePath) {
        JSONArray jsonArray = new JSONArray();

        try {
            // Controleer of het bestand leeg is
            File file = new File(filePath);
            if (file.length() == 0) {
                if (GlobalConfig.EMPTY_FILE_ERROR_PATH_CONSOLE) {
                    throw new EmptyFileException(filePath);
                } else {
                    throw new EmptyFileException();
                }
            }

            // Lees het bestand in als het niet leeg is
            try (FileReader reader = new FileReader(file)) {
                Object json = new JSONTokener(reader).nextValue();
                jsonArray = (JSONArray) json;
            }
        } catch (IOException e) {
            System.out.println("Geen bestand gevonden.");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (EmptyFileException e) {
            if (GlobalConfig.EMPTY_FILE_ERROR_CONSOLE) {
                System.err.println(e.getMessage());
            }
        }

        return jsonArray;
    }

    // Haal de data van de user uit een JSONObject, maak een user aan met de data en
    // return deze.
    private static User getUserFromJson(JSONObject obj) {
        String id = obj.getString("id");
        String username = obj.getString("username");
        String password = obj.getString("password");
        UserType userType = UserType.getUserTypeFromType(obj.getString("userType"));
        Language preferredLanguage = Language.getLanguageFromCode(obj.getString("preferredLanguage"));

        if (userType.equals(UserType.EMPLOYEE)) {
            return new Employee(id, username, password, preferredLanguage);
        } else {
            return new Customer(id, username, password, preferredLanguage);
        }
    }

    // Maak een JSONObject aan met data van de user
    private static JSONObject getUserJson(User user) {
        JSONObject userJson = new JSONObject();
        userJson.put("id", user.getId());
        userJson.put("username", user.getUsername());
        userJson.put("password", user.getPassword());
        userJson.put("userType", user.getUserType().getType());
        userJson.put("preferredLanguage", user.getPreferredLanguage().getCode());
        return userJson;
    }

    // Haal de data van een product uit een JSONObject, maak een product aan met de
    // data en return deze.
    private static Product getProductFromJson(JSONObject obj) {
        String name = obj.getString("name");
        boolean isAvailable = obj.getBoolean("isAvailable");
        String ripeningDate = obj.getString("ripeningDate");
        int timesViewed = obj.getInt("timesViewed");
        String season = obj.getString("season");
        int stock = obj.getInt("stock");
        double price = obj.getDouble("price");

        return new Product(name, isAvailable, ripeningDate, timesViewed, season, stock, price);
    }

    // Maak een JSONObject aan met data van een product
    private static JSONObject getProductJson(Product product) {
        JSONObject productJson = new JSONObject();
        productJson.put("name", product.getName());
        productJson.put("isAvailable", product.isAvailable());
        productJson.put("ripeningDate", product.getRipeningDate());
        productJson.put("timesViewed", product.getTimesViewed());
        productJson.put("season", product.getSeason());
        productJson.put("stock", product.getStock());
        productJson.put("price", product.getPrice());
        return productJson;
    }
}
