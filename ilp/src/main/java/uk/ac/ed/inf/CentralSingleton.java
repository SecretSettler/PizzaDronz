package uk.ac.ed.inf;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

/**
 * Singleton class represents central area
 */
public class CentralSingleton {

    /**
     * singleton instance
     */
    private static CentralSingleton centralSingleton;
    public LngLat[] points;

    /**
     * Constructor for the singleton class contains vertex coordinates
     *
     * @throws MalformedURLException URL exception for invalid url form
     */
    private CentralSingleton() throws MalformedURLException {
        points = getCoordinates();
    }

    /**
     * Get the singleton instance
     *
     * @return the singleton instance
     */
    public static CentralSingleton getInstance() {
        try {
            if (centralSingleton == null) {
                centralSingleton = new CentralSingleton();
            }
            return centralSingleton;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return centralSingleton;
    }

    /**
     * Parse and get the area vertex coordinates from the server.
     * @return Array of Vertex coordinates parsed from the json file on the server.
     * @throws MalformedURLException invalid URL exception
     */
    public LngLat[] getCoordinates() throws MalformedURLException {
        URL url = new URL("https://ilp-rest.azurewebsites.net/centralArea");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
            return objectMapper.readValue(
                    url, new TypeReference<LngLat[]>() {
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public String toString() {
        return "CentralSingleton{" +
                "points=" + Arrays.toString(points) +
                '}';
    }
}
