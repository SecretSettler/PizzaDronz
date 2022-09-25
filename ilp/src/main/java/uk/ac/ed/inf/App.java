package uk.ac.ed.inf;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static uk.ac.ed.inf.Restaurant.getRestaurantsFromServer;

/**
 * Hello world!
 *
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
    }
}