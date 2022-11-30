package uk.ac.ed.inf;

import java.net.URL;

/**
 * Class of the map of an order
 */
public class Map {
    public final LngLat APPLETON = new LngLat(-3.186874, 55.944494);
    public final Greedy.Node START = new Greedy.Node(APPLETON, null, 0, true, null);
    public NoFlySingleton noFlySingleton;
    public CentralSingleton centralSingleton;
    public NoFlySingleton.NoFlyZone[] noFlyZones;
    public LngLat[] centralArea;
    public Restaurant[] restaurants;
    public LngLat end;
    public Greedy.Node start;

    /**
     * Constructor
     * @param baseURL The server address
     * @param order The chosen order
     */
    public Map(URL baseURL, Order order){
        restaurants = Restaurant.getRestaurantsFromRestServer(baseURL);
        centralSingleton = CentralSingleton.getInstance();
        centralArea = centralSingleton.points;
        noFlySingleton = NoFlySingleton.getInstance();
        noFlyZones = noFlySingleton.allNoFlyZones;
        start = START;
        end = Order.getLngLatFromOrder(order, restaurants);
    }
}
