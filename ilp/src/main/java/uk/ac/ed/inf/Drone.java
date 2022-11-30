package uk.ac.ed.inf;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * The class represents the drone
 */
public class Drone {
    public URL baseURL;
    public String dateTime;
    public Order[] orders;
    public ArrayList<Order> filteredOrders;
    public HashMap<Order, String> invalidOrders;
    public final int BATTERY = 2000;

    /**
     * Constructor for the drone
     * @param baseURL server address
     * @param dateTime date
     * @param restaurants all restaurants
     */
    public Drone(URL baseURL, String dateTime, Restaurant[] restaurants) {
        this.baseURL = baseURL;
        this.dateTime = dateTime;
        orders = Order.getOrdersFromServer(baseURL, dateTime);
        filteredOrders = Order.ordersFilter(orders, restaurants);
        System.out.println();
        System.out.println("[RESULT] TOTAL NUMBER OF VALID DELIVERIES: " + filteredOrders.size());
        System.out.println();
        Order.sortOrders(filteredOrders, restaurants);
        invalidOrders = Order.invalidOrders(orders, restaurants);
    }

    /**
     * Start the drone to deliver orders
     * @param orders all the valid orders for the chosen day
     * @return JSONs of the delivering results
     */
    public ArrayList<JsonArray> deliver(ArrayList<Order> orders) {
        Greedy greedy = new Greedy();
        int totalMoves = 0;
        ArrayList<JsonArray> allJson = new ArrayList<>();
        JsonArray deliveries = new JsonArray();
        JsonArray flightPath = new JsonArray();
        JsonArray points = new JsonArray();
        if (orders == null) {
            System.out.println("No invalid orders");
        } else {
            int k = 0;
            for (Order order : orders) {
                if (totalMoves <= BATTERY) {
                    ArrayList<Greedy.Node> steps = orderPath(order, greedy);
                    if (totalMoves + steps.size() > BATTERY) {
                        // The battery of the drone cannot finish the next delivery
                        JsonObject record = conditionPath(order, "power out");
                        deliveries.add(record);
                    } else {
                        // Record the path and points.
                        JsonObject record = conditionPath(order, "valid");
                        flightPath = findFlightPath(flightPath, steps, order);
                        points = pointsForGeoJson(points, steps);
                        totalMoves += steps.size();
                        deliveries.add(record);
                        k++;
                    }
                }
            }
            allJson.add(deliveries);
            allJson.add(flightPath);
            allJson.add(points);
            System.out.println();
            System.out.println("[RESULT] TOTAL NUMBER OF SUCCESSFUL DELIVERIES: "+ k);
            System.out.println();
        }
        return allJson;
    }

    /**
     * Creates the reverse path for returning and combine it with the coming path to form the final path of an order
     * @param path coming path
     * @param map the map of this order
     * @return the final path including the coming and returning paths.
     */
    public ArrayList<Greedy.Node> finalPath(ArrayList<Greedy.Node> path, Map map){
        ArrayList<Greedy.Node> reversePath = new ArrayList<>(path.stream().map(Greedy.Node::cloneNode).toList());
        Collections.reverse(reversePath);
        var directionList = reversePath.stream().map(d->d.direction).toList();
        for (Greedy.Node node : reversePath) {
            node.ticksSinceStartOfCalculation = System.nanoTime();
            // Set ticks
        }
        for (int i = 0; i < reversePath.size()-1; i++) {
            reversePath.get(i+1).direction = directionList.get(i).reverseAngle();
            // Find the opposite angles of all nodes on the coming path
        }

        reversePath.get(0).direction = null; // Hover when reach the restaurant
        path.addAll(reversePath);
        path.add(map.START); // Hover when back to AT
        return path;
    }

    /**
     * Generate the coming path from the start to the destination
     * @param order the chosen order
     * @param greedy the search algorithm
     * @return the coming path
     */
    public ArrayList<Greedy.Node> orderPath(Order order, Greedy greedy){
        Map map = new Map(baseURL, order);
        ArrayList<Greedy.Node> steps = greedy.start(map);
        steps = finalPath(steps, map);
        return steps;
    }

    /**
     * Generate results of all orders
     * @param order the chosen order
     * @param condition the condition of the drone
     * @return JSON object of the path with outcomes.
     */
    public JsonObject conditionPath(Order order, String condition){
        if (condition.equals("power out")) {
            DeliveriesJson deliveriesJson = new DeliveriesJson(order.orderNo,
                    Order.OrderOutcome.ValidButNotDelivered + " Sorry the drone is out of service",
                    Integer.parseInt(order.priceTotalInPence));
            // Because we have filtered orders that have invalid total payments, so we can directly
            // use the money paid in the order.
            return deliveriesJson.generateJson();
        }

        else if (condition.equals("valid")){
            DeliveriesJson deliveriesJson = new DeliveriesJson(order.orderNo,
                    String.valueOf(Order.OrderOutcome.Delivered),
                    Integer.parseInt(order.priceTotalInPence));
            return deliveriesJson.generateJson();
        }

        else {
            System.out.println("UNKNOWN CONDITION FOR THE DRONE!");
            System.out.println(">>> SYSTEM EXIT <<<");
            System.exit(-1);
        }
        return null;
    }

    /**
     * Convert the path to the format of flightpath.json
     * @param flightPath the JSON array object for storing multiple JSON objects
     * @param steps flight paths
     * @param order the chosen order
     * @return the JSON array object with finished JSON objects stored inside
     */
    public JsonArray findFlightPath(JsonArray flightPath, ArrayList<Greedy.Node> steps, Order order){
        for (int i = 0; i < steps.size() - 1; i++) {
            Greedy.Node node1 = steps.get(i);
            Greedy.Node node2 = steps.get(i + 1);
            if (node2.direction != null){
                FlightPathJson flightPathJson = new FlightPathJson(order.orderNo,
                        node1.lngLat.lng, node1.lngLat.lat, node2.direction.getAngle(),
                        node2.lngLat.lng, node2.lngLat.lat, node2.ticksSinceStartOfCalculation);
                JsonObject jsonObject = flightPathJson.generateJson();
                flightPath.add(jsonObject);
            } else {
                FlightPathJson flightPathJson = new FlightPathJson(order.orderNo,
                        node1.lngLat.lng, node1.lngLat.lat, null,
                        node2.lngLat.lng, node2.lngLat.lat, node2.ticksSinceStartOfCalculation);
                JsonObject jsonObject = flightPathJson.generateJson();
                flightPath.add(jsonObject);
            }
        }
        return flightPath;
    }

    /**
     * Convert the point information to the format needed for GEOJSON
     * @param points Nodes on the flight path
     * @param steps all nodes
     * @return the JSON array with all coordinates stored
     */
    public JsonArray pointsForGeoJson(JsonArray points, ArrayList<Greedy.Node> steps){
        for (Greedy.Node node: steps) {
            JsonArray coords = new JsonArray();
            coords.add(node.lngLat.lng);
            coords.add(node.lngLat.lat);
            points.add(coords);
        }
        return points;
    }
}
