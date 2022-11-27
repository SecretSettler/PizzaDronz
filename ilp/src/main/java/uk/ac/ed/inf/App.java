package uk.ac.ed.inf;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Locale;

public class App {

    public static void main(String[] args) {

        try {
            var dataTime = args[0];
            var server = args[1];
            var hashCode = args[2];
            URL url = new URL(server);

            System.out.println(">>> STARTING DRONE SYSTEM <<<");
            Thread.sleep(500);
            System.out.println(">>> WELCOME TO PIZZADRONZE <<<");
            Thread.sleep(500);
            System.out.println(">>> RETRIEVING DATA FROM SERVER <<<");
            if (!isValidFormat("yyyy-MM-dd", dataTime, Locale.ENGLISH)){
                System.out.println("[ERROR] INVALID DATE TYPE!!!");
                System.out.println(">>> SYSTEM EXIT <<<");
                System.exit(-1);
            }
            Restaurant[] participants = Restaurant.getRestaurantsFromRestServer(url);
            Drone drone = new Drone(url, dataTime, participants);
            System.out.println(">>> FINISHING RETRIEVING <<<");
            Thread.sleep(500);

            System.out.println(">>> START DELIVERING <<<");
            ArrayList<JsonArray> deliverResults = drone.deliver(drone.filteredOrders);
            System.out.println(">>> FINISH DELIVERING <<<");

            System.out.println(">>> GENERATING RESULT FILES TO ../resultfiles <<<");
            JsonArray finalDeliveryResults = DeliveriesJson.generateJsonArray(deliverResults.get(0), drone.invalidOrders);
            JsonArray finalFlightPath = deliverResults.get(1);
            JsonArray points = deliverResults.get(2);
            JsonObject geometry = GeoJson.geometryGenerator("LineString", points);
            JsonObject geoJsonToday = GeoJson.generator("Feature", geometry);

            File deliveries = new File("../resultfiles", "deliveries-" + dataTime + ".json");
            File flightPath = new File("../resultfiles", "flightpath-" + dataTime + ".json");
            File geoJson = new File("../resultfiles", "drone-" + dataTime + ".geojson");
            deliveries.createNewFile();
            flightPath.createNewFile();
            geoJson.createNewFile();
            FileWriter deliverFile = new FileWriter(deliveries);
            FileWriter flightPathFile = new FileWriter(flightPath);
            FileWriter geoJsonFile = new FileWriter(geoJson);

            deliverFile.write(finalDeliveryResults.toString());
            flightPathFile.write(finalFlightPath.toString());
            geoJsonFile.write(geoJsonToday.toString());
            deliverFile.close();
            flightPathFile.close();
            geoJsonFile.close();
            System.out.println(">>> MISSIONS FINISH ON " + dataTime + " <<<");
            Thread.sleep(500);
            System.out.println(">>> SYSTEM EXIT <<<");

        } catch (Exception e) {
            System.out.println("[ERROR] OOPS, SOMETHING WENT WRONG.");
            System.out.println(">>> SYSTEM EXIT <<<");
            System.exit(-1);
        }
    }

    public static boolean isValidFormat(String format, String value, Locale locale) {
        LocalDateTime ldt;
        DateTimeFormatter fomatter = DateTimeFormatter.ofPattern(format, locale);

        try {
            ldt = LocalDateTime.parse(value, fomatter);
            String result = ldt.format(fomatter);
            return result.equals(value);
        } catch (DateTimeParseException e) {
            try {
                LocalDate ld = LocalDate.parse(value, fomatter);
                String result = ld.format(fomatter);
                return result.equals(value);
            } catch (DateTimeParseException exp) {
                try {
                    LocalTime lt = LocalTime.parse(value, fomatter);
                    String result = lt.format(fomatter);
                    return result.equals(value);
                } catch (DateTimeParseException e2) {
                    System.out.println("[ERROR] INVALID DATE TYPE!!!");
                    System.out.println(">>> SYSTEM EXIT <<<");
                    System.exit(-1);
                }
            }
        }

        return false;
    }
}
