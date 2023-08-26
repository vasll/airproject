package com.vasll.airproject.Controllers;

import com.vasll.airproject.City;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.json.*;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="https://github.com/vasll">Vasll</a>
 * Controller for the "outdoors-view.fxml" view
 */
public class ControllerOutdoors {
    private City city;
    private final static Color GREEN = Color.rgb(82, 204, 0),
            DARK_GREEN = Color.rgb(0, 128, 0),
            YELLOW = Color.rgb(255, 255, 64),
            RED = Color.rgb(255, 42, 42);

    @FXML
    private ImageView mapImageView;
    @FXML
    private TextField cityTextField;
    @FXML
    private Label labelNO2, labelNO2status, labelO3, labelO3status, labelSO2, labelSO2status,
          labelPM2_5, labelPM2_5status, labelPM10, labelPM10status, labelCO, labelCOstatus,
          labelAQI, labelAQIstatus, labelCityNotFound;
    @FXML
    private FontIcon iconDownload;

    @FXML
    private void initialize(){
        labelCityNotFound.setOpacity(0.00);
        iconDownload.setOpacity(0.00);
    }

    @FXML
    private void onCityTextFieldAction(){
        if(cityTextField.getText().isBlank()) return;
        city = new City(cityTextField.getText());
        CompletableFuture.runAsync(this::updateLabelsAndMap);   // Async request
    }

    @FXML
    private void onAqiBoxClick() throws URISyntaxException, IOException {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("https://en.wikipedia.org/wiki/Air_quality_index#CAQI"));
        }
    }

    @FXML
    private void onNo2BoxClick() throws URISyntaxException, IOException {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("https://en.wikipedia.org/wiki/Nitrogen_dioxide"));
        }
    }

    @FXML
    private void onO3BoxClick() throws URISyntaxException, IOException {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("https://en.wikipedia.org/wiki/Ozone"));
        }
    }

    @FXML
    private void onSo2BoxClick() throws URISyntaxException, IOException {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("https://en.wikipedia.org/wiki/Sulfur_dioxide"));
        }
    }

    @FXML
    private void onPm2_5BoxClick() throws URISyntaxException, IOException {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("https://en.wikipedia.org/wiki/Particulates"));
        }
    }

    @FXML
    private void onPm10BoxClick() throws URISyntaxException, IOException {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("https://en.wikipedia.org/wiki/Particulates#Size,_shape_and_solubility_matter"));
        }
    }

    @FXML
    private void onCoBoxClick() throws URISyntaxException, IOException {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("https://en.wikipedia.org/wiki/Carbon_monoxide"));
        }
    }


    private void updateLabelsAndMap(){
        // Status labels are based on https://en.wikipedia.org/wiki/Air_quality_index#CAQI
        iconDownload.setOpacity(1.00);
        JSONObject airPollutionList = city.getAirPollutionDataList();

        if(airPollutionList == null){
            labelCityNotFound.setOpacity(1.00);
            iconDownload.setIconColor(Color.web("#FF0000"));
            return;
        }
        iconDownload.setIconColor(Color.web("#808080"));
        labelCityNotFound.setOpacity(0.00);

        JSONObject airComponents = airPollutionList.getJSONObject("components");
        int aqi = Integer.parseInt(airPollutionList.getJSONObject("main").get("aqi").toString());
        mapImageView.setImage(city.getStaticMapImage((int) mapImageView.getFitWidth(), (int) mapImageView.getFitHeight()));

        // "NO2" Label
        setLabelThreshold(Double.parseDouble(airComponents.get("no2").toString()),
                labelNO2, labelNO2status, 50, 100, 200, 400);
        // "O3" Label
        setLabelThreshold(Double.parseDouble(airComponents.get("o3").toString()),
                labelO3, labelO3status, 60, 120, 180, 240);
        // "SO2" Label
        setLabelThreshold(Double.parseDouble(airComponents.get("so2").toString()),
                labelSO2, labelSO2status, 0.4, 0.8, 3.80, 8.00);
        // "PM2_5" Label
        setLabelThreshold(Double.parseDouble(airComponents.get("pm2_5").toString()),
                labelPM2_5, labelPM2_5status, 40, 80, 380, 800);
        // "PM10" Label
        setLabelThreshold(Double.parseDouble(airComponents.get("pm10").toString()),
                labelPM10, labelPM10status, 25, 50, 90, 180);
        // "NO2" Label
        setLabelThreshold(Double.parseDouble(airComponents.get("pm10").toString()),
                labelPM10, labelPM10status, 25, 50, 90, 180);
        // "CO" Label
        setLabelThreshold(Double.parseDouble(airComponents.get("co").toString()),
                labelCO, labelCOstatus, 450, 1000, 2000, 5000);

        // "AQI" Label
        Platform.runLater(()->labelAQI.setText(aqi+""));
        if (aqi == 1) {
            Platform.runLater(() -> {
                labelAQI.setTextFill(GREEN);
                labelAQIstatus.setText("Air quality is good");
                labelAQIstatus.setTextFill(GREEN);
            });
        }else if(aqi == 2){
            Platform.runLater(() -> {
                labelAQI.setTextFill(DARK_GREEN);
                labelAQIstatus.setText("Air quality is fair");
                labelAQIstatus.setTextFill(DARK_GREEN);
            });
        }else if(aqi == 3){
            Platform.runLater(() -> {
                labelAQI.setTextFill(YELLOW);
                labelAQIstatus.setText("Air quality is moderate");
                labelAQIstatus.setTextFill(YELLOW);
            });
        }else if(aqi == 4){
            Platform.runLater(() -> {
                labelAQI.setTextFill(RED);
                labelAQIstatus.setText("Air quality is poor");
                labelAQIstatus.setTextFill(RED);
            });
        }else if(aqi == 5){
            Platform.runLater(() -> {
                labelAQI.setTextFill(RED);
                labelAQIstatus.setText("Air quality is very poor");
                labelAQIstatus.setTextFill(RED);
            });
        }
        iconDownload.setOpacity(0.00);
    }

    private void setLabelThreshold(double airComponentValue, Label labelComponent, Label labelStatus, double thresh0, double thresh1, double thresh2, double thresh3){
        Platform.runLater(()->labelComponent.setText(Double.toString(airComponentValue)));

        if(airComponentValue == 0.0){
            Platform.runLater(()->{
                labelComponent.setTextFill(Color.GRAY);
                labelStatus.setText("Unavailable");
                labelStatus.setTextFill(Color.GRAY);
            });
            return;
        }

        if(airComponentValue > 0 && airComponentValue <= thresh0){
            Platform.runLater(()->{
                labelComponent.setTextFill(GREEN);
                labelStatus.setText("Very good");
                labelStatus.setTextFill(GREEN);
            });
        }else if(airComponentValue <= thresh1){
            Platform.runLater(()->{
                labelComponent.setTextFill(DARK_GREEN);
                labelStatus.setText("Good");
                labelStatus.setTextFill(DARK_GREEN);
            });
        }else if(airComponentValue <= thresh2){
            Platform.runLater(()->{
                labelComponent.setTextFill(YELLOW);
                labelStatus.setText("Fair");
                labelStatus.setTextFill(YELLOW);
            });
        }else if(airComponentValue <= thresh3){
            Platform.runLater(()->{
                labelComponent.setTextFill(RED);
                labelStatus.setText("Poor");
                labelStatus.setTextFill(RED);
            });
        }else {
            Platform.runLater(()->{
                labelComponent.setTextFill(RED);
                labelStatus.setText("Very poor");
                labelStatus.setTextFill(RED);
            });
        }
    }
}

