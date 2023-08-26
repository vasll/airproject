package com.vasll.airproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * @author <a href="https://github.com/vasll">Vasll</a>
 * AIR Project
 * Logo from: https://www.vecteezy.com/members/marsono
 */
public class Main extends Application {
    public static Stage stage;
    public static Scene scene;
    // TODO bad path, will only work in a development environment
    public static CSVFile configFile = new CSVFile("src\\main\\resources\\config.csv",";");

    @Override
    public void start(Stage mainStage) throws IOException {
        // JAVAFX STAGE
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));    // Load main view
        scene = new Scene(fxmlLoader.load());       // Create scene

        // Load theme
        switch (configFile.getElement(1, 1)) {  // Gets the current theme saved in config.csv
            case "dark" -> scene.getStylesheets().add("CSS-Themes/dark-theme.css");
            case "light" -> scene.getStylesheets().add("CSS-Themes/light-theme.css");
            case "custom" -> scene.getStylesheets().add("CSS-Themes/custom-theme.css");
            default -> scene.getStylesheets().add("CSS-Themes/dark-theme.css");
        }

        mainStage.setScene(scene);
        mainStage.initStyle(StageStyle.UNDECORATED);    // Make window undecorated
        ResizeHelper.addResizeListener(mainStage);      // Make window resizable

        mainStage.setMinHeight(710);
        mainStage.setMinWidth(960);
        mainStage.setHeight(710);
        mainStage.setWidth(1020);
        mainStage.getIcons().add(new Image("logo.png"));

        stage = mainStage;
        mainStage.show();
    }

    public static void main(String[] args){
        launch();
    }
}