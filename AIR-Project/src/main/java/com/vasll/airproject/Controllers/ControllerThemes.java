package com.vasll.airproject.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static com.vasll.airproject.Main.configFile;
import static com.vasll.airproject.Main.scene;

/**
 * @author <a href="https://github.com/vasll">Vasll</a>
 * Controller for the "themes-view.fxml" view
 */
public class ControllerThemes {
    // Each color corresponds to a line from the CSS custom file
    private final static int ACCENT = 5, ACCENT_HARD = 6, PRIMARY = 7, PRIMARY_LIGHT = 8, ON_PRIMARY = 9,
                             ON_PRIMARY_LIGHT = 10, BACKGROUND = 11, BACKGROUND_HARD = 12, HOVER = 13;

    @FXML
    private RadioButton radioButtonLight, radioButtonDark, radioButtonCustom;
    @FXML
    private ColorPicker colorPickerAccent, colorPickerAccentHard, colorPickerPrimary, colorPickerPrimaryLight,
                        colorPickerOnPrimary, colorPickerOnPrimaryLight, colorPickerHover, colorPickerBackground,
                        colorPickerBackgroundHard;

    /**
     * JavaFX runs this method by default when the Stage is created
     */
    @FXML
    private void initialize(){
        // Radio group
        ToggleGroup radioButtonGroup = new ToggleGroup();
        radioButtonDark.setToggleGroup(radioButtonGroup);
        radioButtonLight.setToggleGroup(radioButtonGroup);
        radioButtonCustom.setToggleGroup(radioButtonGroup);

        // Load default radiobutton based on current theme
        switch (configFile.getElement(1, 1)) {
            case "light" -> radioButtonLight.setSelected(true);
            case "custom" -> radioButtonCustom.setSelected(true);
            default -> radioButtonDark.setSelected(true);
        }

        // Load color picker data from the CSS file
        colorPickerAccent.setValue(getColor(ACCENT));
        colorPickerAccentHard.setValue(getColor(ACCENT_HARD));
        colorPickerPrimary.setValue(getColor(PRIMARY));
        colorPickerPrimaryLight.setValue(getColor(PRIMARY_LIGHT));
        colorPickerOnPrimary.setValue(getColor(ON_PRIMARY));
        colorPickerOnPrimaryLight.setValue(getColor(ON_PRIMARY_LIGHT));
        colorPickerHover.setValue(getColor(HOVER));
        colorPickerBackground.setValue(getColor(BACKGROUND));
        colorPickerBackgroundHard.setValue(getColor(BACKGROUND_HARD));
    }

    @FXML
    private void switchToLightTheme(){
        scene.getStylesheets().clear();
        scene.getStylesheets().add("CSS-Themes/light-theme.css");
        configFile.writeLine(configFile.getElement(1, 0) + ";light;", 1);
    }
    @FXML
    private void switchToDarkTheme(){
        scene.getStylesheets().clear();
        scene.getStylesheets().add("CSS-Themes/dark-theme.css");
        configFile.writeLine(configFile.getElement(1, 0) + ";dark;", 1);
    }

    @FXML
    private void switchToCustomTheme(){
        // This works only the first time because JavaFX does weird things with caching and doesn't refresh the CSS
        scene.getStylesheets().clear();
        scene.getStylesheets().add("CSS-Themes/custom-theme.css");
        configFile.writeLine(configFile.getElement(1, 0) + ";custom;", 1);
    }

    public void refreshAccent(){setColor(ACCENT, colorToHex(colorPickerAccent.getValue()));}

    public void refreshAccentHard(){setColor(ACCENT_HARD, colorToHex(colorPickerAccentHard.getValue()));}

    public void refreshPrimary(){setColor(PRIMARY, colorToHex(colorPickerPrimary.getValue()));}

    public void refreshPrimaryLight(){setColor(PRIMARY_LIGHT, colorToHex(colorPickerPrimaryLight.getValue()));}

    public void refreshOnPrimary(){setColor(ON_PRIMARY, colorToHex(colorPickerOnPrimary.getValue()));}

    public void refreshOnPrimaryLight(){setColor(ON_PRIMARY_LIGHT, colorToHex(colorPickerOnPrimaryLight.getValue()));}

    public void refreshHover(){setColor(HOVER, colorToHex(colorPickerHover.getValue()));}

    public void refreshBackground(){setColor(BACKGROUND, colorToHex(colorPickerBackground.getValue()));}

    public void refreshBackgroundHard(){setColor(BACKGROUND_HARD, colorToHex(colorPickerBackgroundHard.getValue()));}

    private String colorToHex(Color color) {
        String hex1;
        String hex2;

        hex1 = Integer.toHexString(color.hashCode()).toUpperCase();

        hex2 = switch (hex1.length()) {
            case 2 -> "000000";
            case 3 -> String.format("00000%s", hex1.substring(0, 1));
            case 4 -> String.format("0000%s", hex1.substring(0, 2));
            case 5 -> String.format("000%s", hex1.substring(0, 3));
            case 6 -> String.format("00%s", hex1.substring(0, 4));
            case 7 -> String.format("0%s", hex1.substring(0, 5));
            default -> hex1.substring(0, 6);
        };
        return hex2;
    }

    private Color getColor(int colorIndex){
        String line = null;
        // TODO these paths are a horrible idea and will not work outside of a development environment
        String customThemePath = "src/main/resources/CSS-Themes/custom-theme.css";
        try (Stream<String> lines = Files.lines(Paths.get(customThemePath))) {
            line = lines.skip(colorIndex).findFirst().get().split(":")[1].replaceAll(";", ""); // "color:#HEX;"
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Color.web(line);
    }

    private void setColor(int colorIndex, String color){
        List<String> lines = null;
        Path customThemePath = Path.of("src/main/resources/CSS-Themes/custom-theme.css");
        try {
            lines = Files.readAllLines(customThemePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] line = lines.get(colorIndex).split(":");
        line[1] = ":#"+color+";";
        lines.set(colorIndex, line[0] + line[1]);
        try {
            Files.write(customThemePath, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
