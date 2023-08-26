package com.vasll.airproject;

import org.json.*;
import javafx.scene.image.Image;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * @author <a href="https://github.com/vasll">Vasll</a>
 * The City class makes it easy to get air pollution data, latitude/longitude and static map images of a city/place.
 * An important thing to note: most countries have different AQI indexes and different ways to measure things
 * */
public class City {
    private final String WEATHERMAP_KEY;    // API key (https://openweathermap.org/api/)
    private final String MAPQUEST_KEY;      // API key (https://open.mapquestapi.com/api/)
    private final String city;              // City name i.e "Milan"

    /**
     * Constructor for the City class.
     * @param city The name of a city
     */
    public City(String city) {
        this.city = city;
        // TODO replace with your api keys or load them from a config file
        this.WEATHERMAP_KEY = "";  // Openweathermap key
        this.MAPQUEST_KEY = "";    // Openmapquest key
    }

    /**
     * Constructor for the City class.
     * @param city         The name of a city
     * @param MAPQUEST_KEY An API key for <a href="https://openweathermap.org/api/">openweathermap</a>
     * @param WEATHERMAP_KEY An API key for <a href="https://open.mapquestapi.com/api/">mapquestapi</a>
     */
    City(String city, String WEATHERMAP_KEY, String MAPQUEST_KEY) {
        this.city = city;
        this.WEATHERMAP_KEY = WEATHERMAP_KEY;
        this.MAPQUEST_KEY = MAPQUEST_KEY;
    }

    /**
     * Gets latitude and longitude based on the city name given in the constructor via openweathermap API
     */
    public String[] getLatitudeAndLongitude() {
        JSONObject jsonObject;
        try {
            JSONArray jsonArray = new JSONArray(getRequestFromURL(
                    new URL("https://api.openweathermap.org/geo/1.0/direct?q=" + city + "&appid=" + WEATHERMAP_KEY)));
            jsonObject = (JSONObject) jsonArray.get(0);
        } catch (MalformedURLException | JSONException e) {
            System.out.println(getClass() + ": Coordinates not found for "+city);
            return null;
        }
        return new String[]{jsonObject.get("lat").toString(), jsonObject.get("lon").toString()};
    }

    /**
     * Gets a static map image based on the latitude and longitude of the city via mapquest API
     *
     * @param sizeX width of the image
     * @param sizeY height of the image
     */
    public Image getStaticMapImage(int sizeX, int sizeY) {
        String[] coordinates = getLatitudeAndLongitude();
        return new Image("https://open.mapquestapi.com/staticmap/v5/map?key=" + MAPQUEST_KEY +
                "&center=" + coordinates[0] + "," + coordinates[1] + "&size=" + sizeX + "," + sizeY);
    }

    /**
     * Gets air pollution data from the city's latitude and longitude via the openweathermap API.
     * @return "{"coord":{elements},"list":[{"main":{elements},"components":{elements},"dt":"element"}]}"
     */
    public JSONObject getAirPollutionData() {
        String[] coordinates = getLatitudeAndLongitude();
        if(coordinates == null) {
            return null;
        }

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(getRequestFromURL(
                    new URL("https://api.openweathermap.org/data/2.5/air_pollution?lat="
                            + coordinates[0] + "&lon="
                            + coordinates[1] + "&appid=" + WEATHERMAP_KEY)));
        } catch (MalformedURLException | JSONException e) {
            System.out.println(getClass() + ": Air pollution data not found");
            return null;
        }
        return jsonObject;
    }

    /**
     * Gets the "list" JSONObject from the getAirPollutionData() JSONObject.
     * @return "{"list":[{"main":{elements},"components":{elements},"dt":"element"}]}"
     */
    public JSONObject getAirPollutionDataList() {
        JSONObject data = getAirPollutionData();
        if(data == null) {
            return null;
        }

        return (JSONObject) data.getJSONArray("list").get(0);
    }

    /**
     * Gets a page's JSON response as a String
     * @param url The url of the website that we want to get the response code from
     * @return String with the page response
     */
    private String getRequestFromURL(URL url) {
        HttpURLConnection conn = null;
        int responseCode = 404;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            responseCode = conn.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (responseCode != 200) {
            throw new RuntimeException("Error with connection, response: " + responseCode);
        } else {
            StringBuilder buffer = new StringBuilder();
            try {
                Scanner scanner = new Scanner(url.openStream());
                while (scanner.hasNext())
                    buffer.append(scanner.nextLine());
                scanner.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return buffer.toString();
        }
    }
}
