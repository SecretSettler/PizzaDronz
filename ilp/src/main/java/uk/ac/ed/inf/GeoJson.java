package uk.ac.ed.inf;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class GeoJson {

    public static JsonObject generator(String type, JsonObject geometry){
        JsonObject geoJson = new JsonObject();
        geoJson.addProperty("type", type);
        geoJson.add("geometry", geometry);
        geoJson.add("properties", new JsonObject());
        return geoJson;
    }

    public static JsonObject geometryGenerator(String type, JsonArray points){
        JsonObject geometry = new JsonObject();
        geometry.addProperty("type", type);
        geometry.add("coordinates", points);
        return geometry;
    }

}
