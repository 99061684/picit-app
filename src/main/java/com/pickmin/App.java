package com.pickmin;

import java.io.IOException;
import java.util.HashMap;

import com.pickmin.config.GlobalConfig;
import com.pickmin.controllers.ControllerWithParameters;
import com.pickmin.exceptions.InvalidParametersControllerException;
import com.pickmin.translation.TranslationHelper;

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

    // Deze functie wisselt de pagina van het scherm.
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    // Deze functie wisselt de pagina van het scherm en geeft argumenten mee aan de controller.
    public static void setRoot(String fxml, HashMap<String, Object> args) throws IOException, InvalidParametersControllerException {
        scene.setRoot(loadFXML(fxml, args));
    }

    // Deze functie laad een FXML bestand in met vertaling.
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"), TranslationHelper.getBundle());
        return fxmlLoader.load();
    }

    // Deze functie laad een FXML bestand in met vertaling.
    private static Parent loadFXML(String fxml, HashMap<String, Object> args) throws IOException, InvalidParametersControllerException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"), TranslationHelper.getBundle());

        Parent root = (Parent) fxmlLoader.load();
        ControllerWithParameters myController = fxmlLoader.getController();
        myController.setParameters(args);
        return root;
    }

    // De functie begint met het aanmaken van een scherm (Scene) met de login pagina, voegt styling en een titel toe aan het scherm en weergeeft het scherm.
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("Login"), GlobalConfig.DEFAULT_WIDTH, GlobalConfig.DEFAULT_HEIGHT);
        scene.getStylesheets().add(App.class.getResource("styling/stylesheet.css").toExternalForm());
        stage.setTitle(TranslationHelper.get("app.name"));
        stage.setScene(scene);
        stage.show();
    }

    // Start de applicatie op.
    // De launch functie is JavaFX functie die de start functie van deze class uit voert.
    public static void main(String[] args) {
        launch(args);
    }
}