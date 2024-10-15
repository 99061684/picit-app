package com.pickmin.logic;

import java.util.ArrayList;

public class SearchSettings {
    private boolean stopAtFirst;
    private int pageSize;
    private int pageNumber;
    private ArrayList<String> fieldsToRetrieve;
    private ArrayList<String> excludeFieldsToRetrieve;
    private boolean allFieldsToRetrieveExists;

    public SearchSettings(boolean stopAtFirst, int pageSize, int pageNumber, ArrayList<String> fieldsToRetrieve, ArrayList<String> excludeFieldsToRetrieve, boolean allFieldsToRetrieveExists) {
        this.stopAtFirst = stopAtFirst;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        setFieldsToRetrieve(fieldsToRetrieve, allFieldsToRetrieveExists);
        setExcludeFieldsToRetrieve(excludeFieldsToRetrieve);
    }

    private void checkValidExcludeFieldsToRetrieveAndFieldsToRetrieve() {
        if (this.fieldsToRetrieve.isEmpty() || this.excludeFieldsToRetrieve.isEmpty()) {
            return;
        }
        boolean invalid = this.excludeFieldsToRetrieve.stream().anyMatch(element -> this.fieldsToRetrieve.contains(element));
        if (invalid) {
            throw new IllegalArgumentException("Exclude fields to retrieve cannot contain fields to retrieve");
        }
    }

    public static SearchSettings findAll() {
        return new SearchSettings(false, Integer.MAX_VALUE, 1, new ArrayList<>(), new ArrayList<>(), true);
    }

    public static SearchSettings findFirst() {
        return new SearchSettings(true, Integer.MAX_VALUE, 1, new ArrayList<>(), new ArrayList<>(), true);
    }

    // Getters
    public boolean isStopAtFirst() {
        return stopAtFirst;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public ArrayList<String> getFieldsToRetrieve() {
        return fieldsToRetrieve;
    }

    public ArrayList<String> getExcludeFieldsToRetrieve() {
        return excludeFieldsToRetrieve;
    }

    public boolean isAllFieldsToRetrieveExists() {
        return allFieldsToRetrieveExists;
    }

    // Setters
    public void setFieldsToRetrieve(ArrayList<String> fieldsToRetrieve, boolean allFieldsToRetrieveExists) {
        if (this.fieldsToRetrieve == null) {
            this.fieldsToRetrieve = new ArrayList<>();
        } else {
            this.fieldsToRetrieve = fieldsToRetrieve;
        }
        this.allFieldsToRetrieveExists = allFieldsToRetrieveExists;
        checkValidExcludeFieldsToRetrieveAndFieldsToRetrieve();
    }

    public void setExcludeFieldsToRetrieve(ArrayList<String> excludeFieldsToRetrieve) {
        if (this.excludeFieldsToRetrieve == null) {
            this.excludeFieldsToRetrieve = new ArrayList<>();
        } else {
            this.excludeFieldsToRetrieve = excludeFieldsToRetrieve;
        }
        checkValidExcludeFieldsToRetrieveAndFieldsToRetrieve();
    }
}