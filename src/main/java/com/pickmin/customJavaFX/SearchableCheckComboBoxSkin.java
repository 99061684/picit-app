package com.pickmin.customJavaFX;

import java.util.HashSet;
import java.util.stream.Collectors;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;

import com.pickmin.translation.TranslationHelper;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.SkinBase;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class SearchableCheckComboBoxSkin<T> extends SkinBase<CheckComboBox<String>> {

    private static final Image filterIcon = new Image(SearchableCheckComboBoxSkin.class.getResource("/impl/org/controlsfx/table/filter.png").toExternalForm());

    /**
     * A "normal" combobox used internally as a delegate to get the default combo box behavior.
     * This combo box contains the filtered items and handles the popup.
     */
    private final CheckComboBox<String> filteredCheckComboBox;

    /**
     * The search field shown when the popup is shown.
     */
    private final CustomTextField searchField;

    private SearchableCheckComboBox<T> searchableCheckComboBox;

    /**
     * Used when pressing ESC
     */
    private T previousValue;

    public SearchableCheckComboBoxSkin(SearchableCheckComboBox<T> searchableCheckComboBox) {
        super(searchableCheckComboBox);
        this.searchableCheckComboBox = searchableCheckComboBox;

        // first create the filtered combo box
        filteredCheckComboBox = createFilteredComboBox();
        getChildren().add(filteredCheckComboBox);

        // and the search field
        searchField = createSearchField();
        getChildren().add(searchField);

        bindSearchFieldAndFilteredComboBox();
        preventDefaultComboBoxKeyListener();

        // open the popup on Cursor Down and up
        searchableCheckComboBox.addEventHandler(KeyEvent.KEY_PRESSED, this::checkOpenPopup);
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);
        // ensure filteredCheckComboBox and searchField have the same size as the field
        filteredCheckComboBox.resizeRelocate(x, y, w, h);
        searchField.resizeRelocate(x, y, w, h);
    }

    private CustomTextField createSearchField() {
        CustomTextField field = (CustomTextField) TextFields.createClearableTextField();
        field.setPromptText(TranslationHelper.get("filterpanel.search.field"));
        field.setId("search");
        field.getStyleClass().add("combo-box-search");
        ImageView imageView = new ImageView(filterIcon);
        imageView.setFitHeight(15);
        imageView.setPreserveRatio(true);
        field.setLeft(imageView);
        return field;
    }

    private CheckComboBox<String> createFilteredComboBox() {
        CheckComboBox<String> box = new CheckComboBox<>(searchableCheckComboBox.getDisplayItems());
        box.setId("filtered");
        box.getStyleClass().add("combo-box-filtered");
        box.setFocusTraversable(false);

        // unidirectional bindings -- copy values from skinnable
        // Bindings.bindContent(box.getStyleClass(), getSkinnable().getStyleClass());
        // box.buttonCellProperty().bind(getSkinnable().buttonCellProperty());
        // box.cellFactoryProperty().bind(getSkinnable().cellFactoryProperty());
        // box.converterProperty().bind(getSkinnable().converterProperty());
        // box.placeholderProperty().bind(getSkinnable().placeholderProperty());
        // box.disableProperty().bind(getSkinnable().disableProperty());
        // box.visibleRowCountProperty().bind(getSkinnable().visibleRowCountProperty());
        // box.promptTextProperty().bind(getSkinnable().promptTextProperty());
        // getSkinnable().showingProperty().addListener((obs, oldVal, newVal) -> {
        // if (newVal)
        // box.show();
        // else
        // box.hide();
        // });

        // // bidirectional bindings
        // box.valueProperty().bindBidirectional(getSkinnable().valueProperty());

        return box;
    }

    private void bindSearchFieldAndFilteredComboBox() {
        // set the items of the filtered combo box
        // filteredCheckComboBox.setItems(createFilteredList());
        // and keep it up to date, even if the original list changes
        // getSkinnable().itemsProperty()
        // .addListener((obs, oldVal, newVal) -> filteredCheckComboBox.setItems(createFilteredList()));
        // and update the filter, when the text in the search field changes
        searchField.textProperty().addListener(this::handleSearch);

        // the search field must only be visible, when the popup is showing
        if (filteredCheckComboBox.isVisible()) {
            searchField.setVisible(true);
        } else {
            searchField.setVisible(false);
            searchField.setText("");
        }

        // filteredCheckComboBox.skinProperty().get()
        // filteredCheckComboBox.showingProperty().addListener((obs, oldVal, newVal) ->
        // {
        // if (newVal) {
        // // When the filtered combo box popup is showing, we must also set the showing property
        // // of the original combo box. And here we must remember the previous value for the
        // // ESCAPE behavior. And we must transfer the focus to the search field, because
        // // otherwise the search field would not allow typing in the search text.
        // getSkinnable().show();
        // previousValue = getSkinnable().getValue();
        // searchField.requestFocus();
        // } else {
        // // When the filtered combo box popup is hidden, we must also set the showing property
        // // of the original combo box to false, clear the search field.
        // getSkinnable().hide();
        // searchField.setText("");
        // }
        // });

        // Voeg een listener toe voor de popup show/hide gedrag
        // filteredCheckComboBox.skinProperty().addListener((obs, oldSkin, newSkin) -> {
        // if (newSkin != null) {
        // Popup popup = getPopupControlFromSkin();
        // if (popup != null) {
        // // Gebruik de showingProperty van de Popup
        // popup.showingProperty().addListener((obs2, oldVal, newVal) -> {
        // handlePopupVisibilityChange(newVal);
        // });
        // }
        // }
        // });

        searchField.setOnMouseClicked(event -> {
            this.filteredCheckComboBox.show();
        });

        // but when the search field is focussed, the popup must still be shown
        searchField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                filteredCheckComboBox.show();
            else
                filteredCheckComboBox.hide();
            System.out.println(newVal);
        });
    }

    // Aparte methode om de zoekfunctie te behandelen
    private void handleSearch(ObservableValue<? extends String> obs, String oldValue, String newValue) {
        // Bewaar geselecteerde items voordat je gaat filteren
        this.searchableCheckComboBox.getSelectedItems().clear();
        this.searchableCheckComboBox.getSelectedItems().addAll(filteredCheckComboBox.getCheckModel().getCheckedItems());
        filterDisplayItems(newValue);

        // Herstel de eerder geselecteerde items
        for (String selectedItem : this.searchableCheckComboBox.getSelectedItems()) {
            if (searchableCheckComboBox.getDisplayItems().contains(selectedItem)) {
                filteredCheckComboBox.getCheckModel().check(selectedItem);
            }
        }
    }

    // Functie voor het filteren van de getoonde items op basis van de zoekopdracht
    private void filterDisplayItems(String filter) {
        searchableCheckComboBox.getDisplayItems()
                .setAll(searchableCheckComboBox.getValueHashMap().keySet().stream().filter(item -> item.toLowerCase().contains(filter.toLowerCase())).collect(Collectors.toList()));
    }

    // Functie voor het ophalen van alle geselecteerde displayStrings
    public ObservableList<String> getSelectedDisplayStrings() {
        return filteredCheckComboBox.getCheckModel().getCheckedItems();
    }

    /**
     * The default behavior of the CheckComboBoxSkin is to close the popup on
     * ENTER and SPACE, but we need to override this behavior.
     */
    private void preventDefaultComboBoxKeyListener() {
        filteredCheckComboBox.skinProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal instanceof SkinBase) {
                ListView<T> listView = getListViewFromSkin(newVal);
                if (listView != null) {
                    listView.setOnKeyPressed(this::checkApplyAndCancel);
                }
            }
        });
    }

    /**
     * Haal de ListView uit de CheckComboBoxSkin.
     */
    private ListView<T> getListViewFromSkin(Object skin) {
        if (skin instanceof SkinBase) {
            SkinBase<?> skinBase = (SkinBase<?>) skin;
            // Controleer of de skin kinderen bevat en haal het juiste element op
            for (javafx.scene.Node node : skinBase.getChildren()) {
                if (node instanceof ListView) {
                    return (ListView<T>) node;
                }
            }
        }
        return null;
    }

    /**
     * Used to alter the behaviour. React on Enter, Tab and ESC.
     */
    private void checkApplyAndCancel(KeyEvent e) {
        KeyCode code = e.getCode();
        if (code == KeyCode.ENTER || code == KeyCode.TAB) {
            // select the first item if no selection
            if (getSelectedDisplayStrings().isEmpty() && !filteredCheckComboBox.getItems().isEmpty())
                filteredCheckComboBox.getCheckModel().check(0);
            getSkinnable().hide();
            if (code == KeyCode.ENTER) {
                // otherwise the focus would be somewhere else
                getSkinnable().requestFocus();
            }
        } else if (code == KeyCode.ESCAPE) {
            searchField.setText("");
            getSkinnable().hide();
            // otherwise the focus would be somewhere else
            getSkinnable().requestFocus();
        }
    }

    /**
     * Show the popup on UP, DOWN, and on beginning typing a word.
     */
    private void checkOpenPopup(KeyEvent e) {
        KeyCode code = e.getCode();
        if (code == KeyCode.UP || code == KeyCode.DOWN) {
            filteredCheckComboBox.show();
            // only open the box navigation
            e.consume();
        } else if (code.isLetterKey() || code.isDigitKey() || code == KeyCode.SPACE) {
            // show the box, let the box handle the KeyEvent
            filteredCheckComboBox.show();
        }
    }

}
