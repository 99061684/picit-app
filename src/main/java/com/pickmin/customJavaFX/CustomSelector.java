package com.pickmin.customJavaFX;

import java.util.HashMap;
import java.util.stream.Collectors;

import org.controlsfx.control.CheckComboBox;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class CustomSelector<T> extends VBox {

    private CheckComboBox<String> checkComboBox;
    private ComboBox<String> comboBox;
    private TextField searchField;
    private Label label;

    private HashMap<String, T> valueHashMap;
    private ObservableList<String> displayItems;

    private final StringProperty labelText = new SimpleStringProperty("Maak een keuze:");
    private final BooleanProperty allowMultipleSelection = new SimpleBooleanProperty(false);
    private final BooleanProperty selectAllOption = new SimpleBooleanProperty(false);
    private final BooleanProperty showSearchField = new SimpleBooleanProperty(false);

    // Constructor zonder parameters (voor FXML)
    public CustomSelector() {
        this.valueHashMap = new HashMap<>();
        this.displayItems = FXCollections.observableArrayList();
        setupUI();
    }

    // Constructor met HashMap voor de opties
    public CustomSelector(HashMap<String, T> valueHashMap, String labelText) {
        this.valueHashMap = valueHashMap;
        this.displayItems = FXCollections.observableArrayList(valueHashMap.keySet());
        this.setLabelText(labelText);
        setupUI();
    }

    // Setup UI-elementen
    private void setupUI() {
        if (this.label == null) {
            this.label = new Label(getLabelText());
            getChildren().add(label);
        } else {
            getChildren().remove(label);
            this.label = new Label(getLabelText());
            getChildren().add(label);
        }

        setUpSearchField();

        setUpAllowMultipleSelection();
    }

    // Aparte methode om de zoekfunctie te behandelen
    private void handleSearch(ObservableValue<? extends String> obs, String oldValue, String newValue) {
        filterDisplayItems(newValue);
    }

    // Functie voor het filteren van de getoonde items op basis van de zoekopdracht
    private void filterDisplayItems(String filter) {
        displayItems.setAll(valueHashMap.keySet().stream().filter(item -> item.toLowerCase().contains(filter.toLowerCase())).collect(Collectors.toList()));
    }

    // Aparte methode voor het afhandelen van wijzigingen in geselecteerde items
    private void handleCheckedItemsChange(ListChangeListener.Change<? extends String> change) {
        while (change.next()) {
            // Wanneer "Select All" is geselecteerd, selecteer alle items
            if (change.wasAdded() && change.getAddedSubList().contains("Select All")) {
                checkComboBox.getCheckModel().checkAll();
            }

            // Wanneer "Select All" is uitgecheckt, deselecteer alle items
            if (change.wasRemoved() && change.getRemoved().contains("Select All")) {
                checkComboBox.getCheckModel().clearChecks(); // Uncheck all items
            }
        }
    }

    // Functie voor het ophalen van alle geselecteerde waarden
    public ObservableList<T> getSelectedValues() {
        ObservableList<T> selectedValues = FXCollections.observableArrayList();
        for (String display : checkComboBox.getCheckModel().getCheckedItems()) {
            selectedValues.add(valueHashMap.get(display));
        }
        return selectedValues;
    }

    // Functie voor het ophalen van alle geselecteerde displayStrings
    public ObservableList<String> getSelectedDisplayStrings() {
        return checkComboBox.getCheckModel().getCheckedItems();
    }

    public void setUpAllowMultipleSelection() {
        if (getAllowMultipleSelection() && checkComboBox == null) {
            if (comboBox != null && getChildren().contains(comboBox)) {
                getChildren().remove(comboBox);
                comboBox = null;
            }
            checkComboBox = new CheckComboBox<>(displayItems);
            checkComboBox.getCheckModel().getCheckedItems().addListener(this::handleCheckedItemsChange);
            setSelectAllOption();
            getChildren().add(checkComboBox);
        } else if (!getAllowMultipleSelection() && comboBox == null) {
            if (checkComboBox != null && getChildren().contains(checkComboBox)) {
                getChildren().remove(checkComboBox);
                checkComboBox = null;
            }
            comboBox = new ComboBox<>(displayItems);
            getChildren().add(comboBox);
        }
    }

    public void setUpSearchField() {
        if (!getShowSearchField() && searchField != null) {
            this.getChildren().remove(searchField);
            searchField = null;
        } else if (getShowSearchField() && searchField != null && !this.getChildren().contains(searchField)) {
            searchField = new TextField();
            searchField.setPromptText("Search...");
            searchField.textProperty().addListener(this::handleSearch);
            getChildren().add(searchField);
        }
    }

    public void setSelectAllOption() {
        if (getSelectAllOption() && getAllowMultipleSelection() && checkComboBox != null) {
            checkComboBox.getItems().add(0, "Select All");
        } else if (getSelectAllOption() == false && getAllowMultipleSelection() && checkComboBox != null) {
            checkComboBox.getItems().remove("Select All");
        }
    }

    // Eigenschappen voor FXML bindings
    public void setLabelText(String labelText) {
        this.labelText.set(labelText);
    }

    public void setAllowMultipleSelection(boolean allowMultipleSelection) {
        this.allowMultipleSelection.set(allowMultipleSelection);
        setupUI();
    }

    public void setSelectAllOption(boolean selectAllOption) {
        this.selectAllOption.set(selectAllOption);
        setupUI();
    }

    public void setShowSearchField(boolean showSearchField) {
        this.showSearchField.set(showSearchField);
        setupUI();
    }

    public HashMap<String, T> getValueHashMap() {
        return this.valueHashMap;
    }

    public void setValueHashMap(HashMap<String, T> valueHashMap) {
        this.valueHashMap = valueHashMap;
        this.displayItems.setAll(valueHashMap.keySet());
        setupUI();
    }

    public String getLabelText() {
        return labelText.get();
    }

    public boolean getAllowMultipleSelection() {
        return allowMultipleSelection.get();
    }

    public boolean getSelectAllOption() {
        return selectAllOption.get();
    }

    public boolean getShowSearchField() {
        return showSearchField.get();
    }

    public StringProperty getLabelTextProperty() {
        return labelText;
    }

    public BooleanProperty getAllowMultipleSelectionProperty() {
        return allowMultipleSelection;
    }

    public BooleanProperty getSelectAllOptionProperty() {
        return selectAllOption;
    }

    public BooleanProperty getShowSearchFieldProperty() {
        return showSearchField;
    }
}

// import javafx.collections.FXCollections;
// import javafx.collections.ObservableList;
// import javafx.collections.transformation.FilteredList;
// import javafx.scene.control.ComboBox;
// import javafx.scene.control.Label;
// import javafx.scene.control.TextField;
// import javafx.scene.layout.VBox;
// import org.controlsfx.control.CheckComboBox;

// import java.util.HashMap;
// import java.util.List;
// import java.util.stream.Collectors;

// public class CustomSelector<T> extends VBox {

// private CheckComboBox<String> checkComboBox;
// private ComboBox<String> comboBox;
// private TextField searchField;
// private ObservableList<String> displayItems;
// private Label label;

// private HashMap<String, T> valueHashMap;

// private boolean allowMultipleSelection = false;
// private boolean selectAllOption = false;
// private boolean showSearchField = true;

// // Parameterloze constructor voor FXML
// public CustomSelector() {
// this(new HashMap<>(), "Select an option");
// }

// // Constructor zonder multiple selection
// public CustomSelector(HashMap<String, T> valueHashMap, String labelText) {
// this.valueHashMap = valueHashMap;
// this.displayItems = FXCollections.observableArrayList(valueHashMap.keySet());

// label = new Label(labelText);
// searchField = new TextField();
// searchField.setPromptText("Zoek hier...");

// // Voeg standaard een ComboBox toe
// comboBox = new ComboBox<>(displayItems);
// this.getChildren().add(comboBox);

// // Voeg de searchField functionaliteit toe
// addSearchFieldListener();

// this.getChildren().add(label);
// if (showSearchField) {
// this.getChildren().add(searchField);
// }
// }

// // Functie voor het toevoegen van de searchField listener
// private void addSearchFieldListener() {
// FilteredList<String> filteredItems = new FilteredList<>(displayItems, p -> true);

// searchField.textProperty().addListener((observable, oldValue, newValue) -> {
// handleSearchFieldInput(newValue, filteredItems);
// });
// }

// // Verplaatst de zoeklogica naar een aparte functie
// private void handleSearchFieldInput(String newValue, FilteredList<String> filteredItems) {
// filteredItems.setPredicate(displayString -> {
// if (newValue == null || newValue.isEmpty()) {
// return true;
// }
// return displayString.toLowerCase().contains(newValue.toLowerCase());
// });

// if (allowMultipleSelection) {
// checkComboBox.getItems().setAll(filteredItems);
// } else {
// comboBox.getItems().setAll(filteredItems);
// }
// }

// // Zet de zoekfunctionaliteit aan/uit
// public void setShowSearchField(boolean showSearchField) {
// this.showSearchField = showSearchField;
// if (!showSearchField) {
// this.getChildren().remove(searchField);
// } else if (!this.getChildren().contains(searchField)) {
// this.getChildren().add(1, searchField);
// }
// }

// // Zet meerdere selecties aan/uit
// public void setAllowMultipleSelection(boolean allowMultipleSelection) {
// this.allowMultipleSelection = allowMultipleSelection;
// this.getChildren().remove(comboBox);
// if (allowMultipleSelection) {
// checkComboBox = new CheckComboBox<>(displayItems);
// if (selectAllOption) {
// checkComboBox.getItems().add(0, "Select All");
// }
// this.getChildren().add(checkComboBox);
// } else {
// this.getChildren().add(comboBox);
// }
// }

// // Zet de select all optie aan
// public void enableSelectAllOption(boolean enable) {
// this.selectAllOption = enable;
// if (allowMultipleSelection && checkComboBox != null) {
// checkComboBox.getItems().add(0, "Select All");
// checkComboBox.getCheckModel().getCheckedItems().addListener((obs, oldValue, newValue) -> {
// if (newValue.contains("Select All")) {
// checkComboBox.getCheckModel().checkAll();
// }
// });
// }
// }

// // Functie om de opties later toe te voegen
// public void setOptions(HashMap<String, T> valueHashMap) {
// this.valueHashMap.clear();
// this.valueHashMap.putAll(valueHashMap);
// this.displayItems.clear();
// this.displayItems.addAll(valueHashMap.keySet());

// if (allowMultipleSelection) {
// checkComboBox.getItems().setAll(displayItems);
// } else {
// comboBox.getItems().setAll(displayItems);
// }
// }

// // Functie om alle geselecteerde values op te halen
// public List<T> getSelectedValues() {
// if (allowMultipleSelection) {
// return checkComboBox.getCheckModel().getCheckedItems().stream()
// .map(valueHashMap::get)
// .collect(Collectors.toList());
// } else {
// return List.of(valueHashMap.get(comboBox.getSelectionModel().getSelectedItem()));
// }
// }

// // Functie om alle geselecteerde displayStrings op te halen
// public List<String> getSelectedDisplayStrings() {
// if (allowMultipleSelection) {
// return checkComboBox.getCheckModel().getCheckedItems();
// } else {
// return List.of(comboBox.getSelectionModel().getSelectedItem());
// }
// }
// }
