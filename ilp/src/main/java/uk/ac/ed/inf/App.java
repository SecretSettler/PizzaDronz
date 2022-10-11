package uk.ac.ed.inf;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static uk.ac.ed.inf.Restaurant.getRestaurantsFromServer;

/**
 * Class of simple app
 */
public class App 
{
    public static void main( String[] args ) throws Exception {
        Restaurant[] participants = getRestaurantsFromServer(new URL("https://ilp-rest.azurewebsites.net/" + "restaurants" ));
//        for (Restaurant restaurant:participants){
//            System.out.println(restaurant.toString());
//        }
        String[] pizzas = {"Meat Lover", "Vegan Delight"};
        int price = Order.getDeliveryCost(participants, pizzas);
        System.out.println("Total cost is " + price + " pence");
        LngLat p = new LngLat(-3.189319,55.944618);
        System.out.println(p.isCentralArea());
    }
}
