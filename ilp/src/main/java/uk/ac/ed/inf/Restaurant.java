package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
     * @throws IOException IO exception
     */
    public static Restaurant[] getRestaurantsFromServer(URL serverBaseAddress) throws IOException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
            return objectMapper.readValue(
                    serverBaseAddress, new TypeReference<Restaurant[]>() {
                    });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
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

//    public static void main(String[] args) throws IOException {
//        Restaurant[] fks = getRestaurantsFromServer(new URL("https://ilp-rest.azurewebsites.net/" + "restaurants" ));
//        for (Restaurant restaurant:fks){
//            System.out.println(restaurant.toString());
//        }
//
//    }


}


