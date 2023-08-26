package com.vasll.airproject.Controllers;

import com.vasll.airproject.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Objects;

/**
 * @author <a href="https://github.com/vasll">Vasll</a>
 * Controller for the "main-view.fxml" view
 */
public class ControllerMain {
    private double x, y;
    private static Parent indoorsView;
    private static Parent outdoorsView;
    private static Parent themesView;

    @FXML
    private BorderPane titleBar;
    @FXML
    private BorderPane mainBorderPane;

    /**
     * JavaFX runs this method by default when the Stage is created
     */
    @FXML
    private void initialize() throws IOException {
        // LOAD FXML FILES
        indoorsView = FXMLLoader.load(
                Objects.requireNonNull(Main.class.getResource("indoors-view.fxml")));
        outdoorsView = FXMLLoader.load(
                Objects.requireNonNull(Main.class.getResource("outdoors-view.fxml")));
        themesView = FXMLLoader.load(
                Objects.requireNonNull(Main.class.getResource("themes-view.fxml")));

        mainBorderPane.setCenter(indoorsView);  //load the indoors view

        // MAKE UNDECORATED WINDOW DRAGGABLE
        titleBar.setOnMousePressed(mouseEvent -> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });
        titleBar.setOnMouseDragged(mouseEvent -> {
            Main.stage.setX(mouseEvent.getScreenX() - x);
            Main.stage.setY(mouseEvent.getScreenY() - y);
        });
    }

    @FXML
    private void switchToHouseView(){
        mainBorderPane.setCenter(indoorsView);
    }

    @FXML
    private void switchToCityView(){
        mainBorderPane.setCenter(outdoorsView);
    }

    @FXML
    private void switchToThemesView(){
        mainBorderPane.setCenter(themesView);
    }

    @FXML
    private void exitWindow(){
        System.exit(0);
    }

    @FXML
    private void minimizeWindow(){
        Main.stage.setIconified(true);
    }

}