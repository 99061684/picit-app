package com.pickmin;

import java.io.IOException;
import java.util.ArrayList;

import com.pickmin.config.GlobalConfig;
import com.pickmin.controllers.ControllerWithParameters;
import com.pickmin.exceptions.InvalidParametersControllerException;
import com.pickmin.logic.model.Parameter;
import com.pickmin.translation.TranslationHelper;
import com.pickmin.translation.TranslationResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    public static void goToPage(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static void goToPage(String fxml, ArrayList<Parameter<?>> args) throws IOException, InvalidParametersControllerException {
        scene.setRoot(loadFXML(fxml, args));
    }

    // Laad het FXML-bestand in met de TranslationHelper class om vertalingen op te halen
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        fxmlLoader.setResources(new TranslationResourceBundle());

        return fxmlLoader.load();
    }

    // Laad het FXML-bestand in met de TranslationHelper class om vertalingen op te halen en geef argumenten mee aan de controller.
    private static Parent loadFXML(String fxml, ArrayList<Parameter<?>> parameters) throws IOException, InvalidParametersControllerException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        fxmlLoader.setResources(new TranslationResourceBundle());

        Parent root = (Parent) fxmlLoader.load();
        
        Object myController = fxmlLoader.getController();
        if (myController instanceof ControllerWithParameters) {
            ((ControllerWithParameters) myController).setParameters(parameters);
        } else if (parameters != null && !parameters.isEmpty()) {
            throw new InvalidParametersControllerException(fxml);
        }
        return root;
    }

    @Override
    public void start(Stage stage) throws IOException {
        App.scene = new Scene(loadFXML("Login"));
        App.scene.getStylesheets().add(App.class.getResource("styling/stylesheet.css").toExternalForm());
        stage.setWidth(GlobalConfig.DEFAULT_WIDTH);
        stage.setHeight(GlobalConfig.DEFAULT_HEIGHT);
        stage.setMinWidth(GlobalConfig.DEFAULT_WIDTH);
        stage.setMinHeight(GlobalConfig.DEFAULT_HEIGHT);
        stage.setTitle(TranslationHelper.get("app.name"));

        scene.getRoot().autosize();
        stage.setScene(App.scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}