package com.vasll.airproject.Controllers;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import static com.vasll.airproject.Main.configFile;

/**
 * @author <a href="https://github.com/vasll">Vasll</a>
 * Controller for the "indoors-view.fxml" view
 */

public class ControllerIndoors {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    private SerialPort currentPort;
    private int chartSamples;
    private XYChart.Series<String, Number> seriesCO2, seriesHUM, seriesTMP, seriesAIR;
    private TrayIcon trayIcon;

    @FXML
    private Label lblTMP, lblHUM, lblCO2, lblQuality, lblArduinoStatus;
    @FXML
    private Button btnRefresh, btn24Samples, btn12Samples;
    @FXML
    private LineChart<String, Number> lineChart;
    @FXML
    private ComboBox<SerialPort> comboBoxPorts;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private TextField textFieldRoomName;

    /**
     * JavaFX runs this method by default when the Stage is created
     */
    @FXML
    private void initialize(){
        // LINECHART -- X AND Y AXIS
        xAxis.setLabel("Time (s)");
        xAxis.setAnimated(false);
        yAxis.setAnimated(false);

        // LINECHART -- CHART SERIES
        seriesCO2 = new XYChart.Series<>();
        seriesCO2.setName("CO2 Level");

        seriesHUM = new XYChart.Series<>();
        seriesHUM.setName("Humidity Level");

        seriesTMP = new XYChart.Series<>();
        seriesTMP.setName("Temperature Level");

        seriesAIR = new XYChart.Series<>();
        seriesAIR.setName("Air Quality Level");
        lineChart.setAnimated(false);

        textFieldRoomName.setText(configFile.getElement(1,0));  // Load room name from config.csv
        initializeTray();   // Initialize system tray
        loadSerialPorts();  // Load serial ports into comboBox
        switchToCO2Chart(); // The first chart that is seen upon opening the app
        setSamples24();     // Set chart samples to 24 by default
    }

    /**
     * Loads all the serial ports available and adds them to a comboBox.
     */
    @FXML
    private void loadSerialPorts(){
        comboBoxPorts.getItems().clear();   //clear ports
        for(SerialPort port : SerialPort.getCommPorts())    //add new ports
            comboBoxPorts.getItems().add(port);
    }

    /**
     * Saves room name into config file when enter is pressed on the TextField
     */
    @FXML
    private void setRoomName(){
        configFile.writeLine(textFieldRoomName.getText() + ";" +
                            configFile.getElement(1,1), 1);
    }

    /**
     * Sets chartline samples to 12
     */
    @FXML
    private void setSamples12(){
        chartSamples = 12;
        Platform.runLater(() -> {
            btn12Samples.setUnderline(true);
            btn24Samples.setUnderline(false);
        });
        // If there are more than 12 samples remove the extra ones
        // seriesCO2 is used as size reference for all the other series because its count is the same as the others
        for(int i=seriesCO2.getData().size(); i>12; i--){
            seriesCO2.getData().remove(0);
            seriesAIR.getData().remove(0);
            seriesTMP.getData().remove(0);
            seriesHUM.getData().remove(0);
        }
    }

    /**
     * Sets chartline samples to 24
     */
    @FXML
    private void setSamples24(){
        chartSamples = 24;
        Platform.runLater(() -> {
            btn24Samples.setUnderline(true);
            btn12Samples.setUnderline(false);
        });
    }

    // Modifies the lineChart's yAxis and XYChart.Series, changing these two modify the data displayed
    private void modifyChart(String yAxisLabel, XYChart.Series<String, Number> chartSeries){
        yAxis.setLabel(yAxisLabel);
        lineChart.getData().clear();
        lineChart.getData().add(chartSeries);
    }

    // Each one of these methods is bound to a different button in the .fxml file
    @FXML
    private void switchToCO2Chart(){
        modifyChart("Parts Per Million (PPM)", seriesCO2);
    }
    @FXML
    private void switchToHumidityChart(){
        modifyChart("Humidity Percentage (%)", seriesHUM);
    }
    @FXML
    private void switchToTemperatureChart(){
        modifyChart("Degrees Celsius (C°)", seriesTMP);
    }
    @FXML
    private void switchToAirQualityChart(){
        modifyChart("Air Quality Index (Lower is better)", seriesAIR);
    }

    // Starts listening for an arduino on the chosen port
    @FXML
    private void setCurrentPortAndListenArduino() {
        currentPort = comboBoxPorts.getValue();
        currentPort.setComPortParameters(9600,8,1,0);
        currentPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER,0,0);
        listenArduino();
    }

    //  Listens for the serial port in "currentPort"
    private void listenArduino(){
        if(!currentPort.openPort()) {   // If port can't be opened
            System.err.println(getClass().getName() + ": Serial port not found.");
            return;
        }
        currentPort.addDataListener(new SerialArduinoDataListener());
    }

    // Initializes system tray
    private void initializeTray(){
        SystemTray tray = SystemTray.getSystemTray();

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e->System.exit(0));

        PopupMenu popupMenu = new PopupMenu();
        popupMenu.add(exitItem);

        trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().createImage("src\\main\\resources\\logo.png"), "");
        trayIcon.setImageAutoSize(true);
        trayIcon.setPopupMenu(popupMenu);
        trayIcon.setToolTip("AIR");

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
    }

    private void showTrayNotification(String text){
        trayIcon.displayMessage("AIR App", text, TrayIcon.MessageType.WARNING);
    }

    /**
     * Inner class for reading data from arduino
     */
    private class SerialArduinoDataListener implements SerialPortDataListener {
        private final static Color GREEN = Color.rgb(82, 204, 0),
                DARK_GREEN = Color.rgb(0, 128, 0),
                YELLOW = Color.rgb(255, 255, 64),
                RED = Color.rgb(255, 42, 42);
        Scanner in = new Scanner(currentPort.getInputStream()); // Scanner to handle the serial port stream
        private boolean connected = false;
        private final int TRAY_TIME = 35;
        private int trayTimeout = 0, skipValues = 5;


        @Override
        public int getListeningEvents() {
            return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
        }
        /* serialEvent is toggled every time it receives new data from a SerialPort
         * Input from arduino is: "[ARDUINO],TMP,HUM,IGN,CO2"
         * I decided to treat the input string almost as a CSV file, the first field is used to detect if the
         * data is coming from an arduino, the others are self-explanatory. The IGN field is used for debugging.
         * "Platform.runLater(...)" is called every time something from the GUI is changed, this is because
         * JavaFX UI elements can be only changed within the same thread of the JavaFX application.
         */
        @Override
        public void serialEvent(SerialPortEvent serialPortEvent) {
            String[] formattedInput = in.nextLine().split(",");   // Split the string at each comma
            double temperature = Double.parseDouble(formattedInput[1]); // Second field is TMP
            double humidity = Double.parseDouble(formattedInput[2]);    // Third field is HUM
            double CO2 = Double.parseDouble(formattedInput[4]);         // Fifth field is CO2 (skipped the IGN field)
            AtomicInteger airQualityIndex = new AtomicInteger();
            Date now = new Date();  // Get current date

            // CHECK IF ARDUINO IS CONNECTED
            if(!connected){
                if(formattedInput[0].contains("[ARDUINO]")){  // Check if first field is "[ARDUINO]" otherwise return
                    Platform.runLater(()->{
                        connected = true;
                        lblArduinoStatus.setText("Arduino Status: Connected - Warming sensors");
                        lblArduinoStatus.setTextFill(GREEN);
                        comboBoxPorts.setDisable(true);
                        btnRefresh.setDisable(true);
                    });
                    return;
                }
            }

            // SKIP SOME VALUES BECAUSE THE FIRST ONES ARE NOT ACCURATE
            if(skipValues > 0){
                if(formattedInput[0].contains("[ARDUINO]")){  // Check if first field is "[ARDUINO]" otherwise return
                    Platform.runLater(()->{
                        lblArduinoStatus.setText("Arduino Status: Connected - Preparing to read data");
                        lblArduinoStatus.setTextFill(GREEN);
                    });
                }
                skipValues -= 1;
                return;
            }else{
                if(formattedInput[0].contains("[ARDUINO]")){  // Check if first field is "[ARDUINO]" otherwise return
                    Platform.runLater(()->{
                        lblArduinoStatus.setText("Arduino Status: Connected - Reading data");
                        lblArduinoStatus.setTextFill(GREEN);
                    });
                }
            }

            // TEMPERATURE LABEL
            Platform.runLater(()->lblTMP.setText(temperature+"°"));
            if(temperature <= 30.00) {
                Platform.runLater(()->lblTMP.setTextFill(GREEN));
            }else if(temperature > 30.00 && temperature < 35.00) {
                Platform.runLater(()->lblTMP.setTextFill(YELLOW));
                airQualityIndex.addAndGet(1);
                if(trayTimeout == 0){
                    showTrayNotification("Temperature is rising!");
                    trayTimeout = TRAY_TIME;
                }
            }else if(temperature > 35.00) {
                Platform.runLater(()->lblTMP.setTextFill(RED));
                airQualityIndex.addAndGet(2);
                if(trayTimeout == 0) {
                    showTrayNotification("Temperature is very high!");
                    trayTimeout = TRAY_TIME;
                }
            }

            // HUMIDITY LABEL
            Platform.runLater(()->lblHUM.setText(humidity+"%"));
            if(humidity >=30.00 && humidity <= 60.00) {
                Platform.runLater(()->lblHUM.setTextFill(GREEN));
            }else if((humidity < 30.00 && humidity > 15.00) || (humidity > 60.00 && humidity < 80.00)) {
                Platform.runLater(()->lblHUM.setTextFill(YELLOW));
                airQualityIndex.addAndGet(1);
                if(trayTimeout == 0){
                    showTrayNotification("Humidity levels are bad!");
                    trayTimeout = TRAY_TIME;
                }
            }else if((humidity < 15.00) || (humidity > 80.00)) {
                lblHUM.setTextFill(RED);
                airQualityIndex.addAndGet(2);
                if(trayTimeout == 0){
                    showTrayNotification("Humidity levels are very bad!");
                    trayTimeout = TRAY_TIME;
                }
            }

            // CO2 LABEL
            Platform.runLater(()->lblCO2.setText(CO2+""));
            if(CO2 < 700.00) {
                Platform.runLater(()->lblCO2.setTextFill(GREEN));
            }else if(CO2 >= 700.00 && CO2 < 1100) {
                Platform.runLater(()->lblCO2.setTextFill(DARK_GREEN));
            }else if(CO2 >= 1100 && CO2 < 1600) {
                Platform.runLater(()->lblCO2.setTextFill(YELLOW));
                airQualityIndex.addAndGet(1);
                if(trayTimeout == 0) {
                    showTrayNotification("CO2 levels are bad!");
                    trayTimeout = TRAY_TIME;
                }
            }else {
                Platform.runLater(()->lblCO2.setTextFill(RED));
                airQualityIndex.addAndGet(2);
                if(trayTimeout == 0) {
                    showTrayNotification("CO2 levels are very bad!");
                    trayTimeout = TRAY_TIME;
                }
            }

            // Check if the series have more samples than chartSamples
            // To optimize this code we just check if 1 of the 4 series has size greater than chartSamples
            if (seriesCO2.getData().size() >= chartSamples){
                Platform.runLater(()-> {
                    seriesCO2.getData().remove(0);
                    seriesHUM.getData().remove(0);
                    seriesTMP.getData().remove(0);
                    seriesAIR.getData().remove(0);
                });
            }

            // Add the new collected data to the lineChart series
            Platform.runLater(()-> {
                seriesCO2.getData().add(new XYChart.Data<>(simpleDateFormat.format(now), CO2));
                seriesHUM.getData().add(new XYChart.Data<>(simpleDateFormat.format(now), humidity));
                seriesTMP.getData().add(new XYChart.Data<>(simpleDateFormat.format(now), temperature));
                seriesAIR.getData().add(new XYChart.Data<>(simpleDateFormat.format(now), airQualityIndex.get()));
            });

            // AIR QUALITY LABEL
            if(airQualityIndex.get() <= 0){
                Platform.runLater(()->{
                    lblQuality.setText("Good");
                    lblQuality.setTextFill(GREEN);
                });
            }else if(airQualityIndex.get() == 1){
                Platform.runLater(()-> {
                    lblQuality.setText("OK");
                    lblQuality.setTextFill(DARK_GREEN);
                });
            }else if(airQualityIndex.get() <= 4){
                Platform.runLater(()-> {
                    lblQuality.setText("Moderate");
                    lblQuality.setTextFill(YELLOW);
                    if(trayTimeout == 0){
                        showTrayNotification("Air quality is moderate");
                        trayTimeout = TRAY_TIME;
                    }

                });
            }else {
                Platform.runLater(()-> {
                    lblQuality.setText("BAD");
                    lblQuality.setTextFill(RED);
                    if(trayTimeout == 0) {
                        showTrayNotification("Air quality is very bad");
                        trayTimeout = TRAY_TIME;
                    }
                });
            }
            if(trayTimeout > 0)trayTimeout -= 1;
        }
    }


}
