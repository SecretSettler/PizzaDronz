package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class NoFlySingleton {

    private static NoFlySingleton noFlySingleton;
    public NoFlyZone[] allNoFlyZones;

    public static class NoFlyZone {
        @JsonProperty("name")
        private String name;
        @JsonProperty("coordinates")
        private double[][] coordinates;

        public String getName() {
            return name;
        }

        public double[][] getCoordinates() {
            return coordinates;
        }

        public LngLat[] transfer2LngLat() {
            ArrayList<LngLat> lngLats = new ArrayList<>();
            for (double[] i : coordinates) {
                LngLat point = new LngLat(i[0], i[1]);
                lngLats.add(point);
            }

            int len = lngLats.size();
            LngLat[] points = new LngLat[len];
            for (int i = 0; i < len; i++) {
                points[i] = lngLats.get(i);
            }
            return points;
        }
    }

    public NoFlyZone[] getNoFlyZones() throws MalformedURLException {
        URL url = new URL("https://ilp-rest.azurewebsites.net/noflyzones");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
            return objectMapper.readValue(
                    url, new TypeReference<NoFlyZone[]>() {
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private NoFlySingleton() throws MalformedURLException {
        allNoFlyZones = getNoFlyZones();
    }

    public static NoFlySingleton getInstance() {
        try {
            if (noFlySingleton == null) {
                noFlySingleton = new NoFlySingleton();
            }
            return noFlySingleton;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return noFlySingleton;
    }
}
