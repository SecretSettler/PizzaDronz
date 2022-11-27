package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class of restaurants
 */
public class Restaurant {

    @JsonProperty("name")
    private String name;
    @JsonProperty("longitude")
    private double longitude;
    @JsonProperty("latitude")
    private double latitude;
    @JsonProperty("menu")
    private Menu[] menu;

    /**
     * Restaurant name getter
     *
     * @return name of the restaurant
     */
    public String getName() {
        return name;
    }

    /**
     * Latitude value getter
     *
     * @return value of latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Longitude value getter
     *
     * @return value of longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Array of menus getter
     *
     * @return array of menus
     */
    public Menu[] getMenu() {
        return menu;
    }

    /**
     * Parse and deserialize information of restaurants from json file on the server
     * @param serverBaseAddress URL of the server
     * @return Array of restaurants
     */
    public static Restaurant[] getRestaurantsFromRestServer(URL serverBaseAddress){
        try {
            String furtherAddress = "/restaurants";
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
            return objectMapper.readValue(
                    new URL(serverBaseAddress, furtherAddress), new TypeReference<Restaurant[]>() {
                    });
        } catch (IOException f){
            System.out.println("[ERROR] INVALID URL!!!");
            System.out.println(">>> SYSTEM EXIT <<<");
            System.exit(-1);
        }
        return null;
    }

    public ArrayList<String> getNameOfDishesFromMenus(Menu[] menus){
        ArrayList<String> names = new ArrayList<>();
        for (Menu menu: menus){
            names.add(menu.getName());
        }
        return names;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "name='" + getName() + '\'' +
                ", longitude=" + getLongitude() +
                ", latitude=" + getLatitude() +
                ", menu=" + Arrays.toString(getMenu()) +
                '}';
    }

}


