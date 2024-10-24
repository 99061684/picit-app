package com.pickmin.logic.general;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

import com.pickmin.logic.model.filter.BranchFilter;

import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;

public class UtilityFunctions {
    private static DateTimeFormatter dateTimeFormatter;

    static {
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public static String generateID() {
        return UUID.randomUUID().toString();
    }

    public static String getTodayDate() {
        LocalDate localDate = LocalDate.now();
        return dateTimeFormatter.format(localDate);
    }

    public static int countDecimalPlaces(double value) {
        if (Math.round(value) == value) {
            return 0;
        }
        final String s = Double.toString(value);
        final int index = s.indexOf(',');
        if (index < 0) {
            return 0;
        }
        return s.length() - 1 - index;
    }

    public static ArrayList<String> createArrayListWithValues(String... values) {
        return new ArrayList<String>(Arrays.asList(values));
    }

    public static ArrayList<String> cleanStringArrayList(ArrayList<String> listToClean) {
        if (listToClean == null) {
            return new ArrayList<>();
        }

        return listToClean.stream().map(listToCleanItem -> listToCleanItem.trim()).collect(Collectors.toCollection(ArrayList::new));
    }

    public static boolean hashMapContainsAllKeys(HashMap<String, ?> hashMap, String... keys) {
        if (hashMap == null || hashMap.isEmpty()) {
            return false;
        }
        for (String key : keys) {
            if (!hashMap.containsKey(key)) {
                return false;
            }
        }
        return true;
    }

    public static BranchFilter getBranchFilterFromArrayListByCity(ArrayList<BranchFilter> branchFilters, String city) {
        return branchFilters.stream().filter(branchFilter -> branchFilter.getCity().equals(city)).findFirst().orElse(null);
    }

    public static ArrayList<String> extractArrayListFromString(String seasons, String splitString) {
        if (seasons == null || seasons.isBlank()) {
            return new ArrayList<>();
        }
        return new ArrayList<String>(Arrays.asList(seasons.split(",")));
    }

    public static ArrayList<String> extractArrayListFromString(String seasons) {
        return extractArrayListFromString(seasons, ",");
    }

    public static ArrayList<String> filterEmptyStrings(ArrayList<String> inputList) {
        return inputList.stream().filter(item -> item != null && !item.trim().isEmpty()).collect(Collectors.toCollection(ArrayList::new));
    }

    public static String localDateToString(LocalDate date) {
        return date.format(dateTimeFormatter);
    }

    public static LocalDate stringToLocalDate(String dateString) {
        try {
            return LocalDate.parse(dateString, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format: " + e.getMessage());
            return null;
        }
    }

    public static boolean hasProductName(HashMap<String, String> productNames, String productName) {
        return productNames.values().stream().anyMatch(value -> value.equals(productName));
    }

    public static boolean hasProductName(HashMap<String, String> productNames, String productName, String idToExclude) {
        productNames.remove(idToExclude);
        return productNames.values().stream().anyMatch(value -> value.equals(productName));
    }

    public static void configureDatePicker(DatePicker datePicker) {
        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate value) {
                if (value != null) {
                    return dateTimeFormatter.format(value);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String text) {
                if (text != null && !text.isEmpty()) {
                    try {
                        return LocalDate.parse(text, dateTimeFormatter);
                    } catch (DateTimeParseException ex) {
                        System.err.println("Invalid date format: " + ex.getMessage());
                        throw ex;
                    }
                } else {
                    return null;
                }
            }
        });
    }
}
