package com.pickmin.logic.json;

public class SearchCriteria {
    private String searchField;
    private String operator;
    private Object searchValue;

    public SearchCriteria(String searchField, String operator, Object searchValue) {
        this.searchField = searchField;
        this.operator = operator;
        this.searchValue = searchValue;
    }

    public String getSearchField() {
        return searchField;
    }

    public String getOperator() {
        return operator;
    }

    public Object getSearchValue() {
        return searchValue;
    }
}