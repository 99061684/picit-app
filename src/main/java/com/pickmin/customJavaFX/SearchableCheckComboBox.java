package com.pickmin.customJavaFX;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.controlsfx.control.CheckComboBox;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;

public class SearchableCheckComboBox<T> extends CheckComboBox<String> {
    
    private static final String DEFAULT_STYLE_CLASS = "searchable-combo-box";

    private HashMap<String, T> valueHashMap;
    private final ObservableList<String> displayItems;
    private Set<String> selectedItems;

    private final BooleanProperty selectAllOption = new SimpleBooleanProperty(false);

    public SearchableCheckComboBox() {
        this(new HashMap<>());
    }

    public SearchableCheckComboBox(HashMap<String, T> valueHashMap) {
        super(FXCollections.observableArrayList(valueHashMap.keySet()));
        this.valueHashMap = valueHashMap;
        this.displayItems = this.getItems();
        this.setSelectedItems(new HashSet<>());

        this.getCheckModel().getCheckedItems().addListener(this::handleCheckedItemsChange);
        setSelectAllOption();
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    public void setSelectAllOption() {
        if (getSelectAllOption() && !displayItems.isEmpty()) {
            this.displayItems.add(0, "Select All");
        }
    }

    // Aparte methode voor het afhandelen van wijzigingen in geselecteerde items
    private void handleCheckedItemsChange(ListChangeListener.Change<? extends String> change) {
        while (change.next()) {
            // Wanneer "Select All" is geselecteerd, selecteer alle items
            if (change.wasAdded() && change.getAddedSubList().contains("Select All")) {
                this.getCheckModel().checkAll();
            }

            // Wanneer "Select All" is uitgecheckt, deselecteer alle items
            if (change.wasRemoved() && change.getRemoved().contains("Select All")) {
                this.getCheckModel().clearChecks(); // Uncheck all items
            }
        }
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new SearchableCheckComboBoxSkin<>(this);
    }

    // Getters and setters
    public HashMap<String, T> getValueHashMap() {
        return valueHashMap;
    }

    public ObservableList<String> getDisplayItems() {
        return displayItems;
    }

    public void setSelectAllOption(boolean selectAllOption) {
        this.selectAllOption.set(selectAllOption);
    }

    public boolean getSelectAllOption() {
        return selectAllOption.get();
    }

    public BooleanProperty getSelectAllOptionProperty() {
        return selectAllOption;
    }

    public void setValueHashMap(HashMap<String, T> valueHashMap) {
        this.valueHashMap = valueHashMap;
        this.displayItems.setAll(valueHashMap.keySet());
    }

    public Set<String> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(Set<String> selectedItems) {
        this.selectedItems = selectedItems;
    }
}
