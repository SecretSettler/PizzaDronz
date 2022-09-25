package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.HashMap;

public class Order {
    private static class Tuple{
        public String restaurant;
        public int price;

        public Tuple(String restaurant, int price){
            this.restaurant = restaurant;
            this.price = price;
        }
    }


    public static int getDeliveryCost(Restaurant[] restaurants, String[] pizzas) throws Exception {
        // if 0 orders?
        // if the name of pizza does not exist?
        HashMap<String, Tuple> dishes = new HashMap<>();
        int deliveryFee = 100;
        int totalPrice = 0;
        String lastRestaurant = null;
        for (Restaurant restaurant: restaurants) {
            for (Menu menu : restaurant.getMenu()) {
                dishes.put(menu.getName(), new Tuple(restaurant.getName(), menu.getPriceInPence()));
            }
        }

        for (String pizza: pizzas){
            if (!dishes.get(pizza).restaurant.equals(lastRestaurant) && lastRestaurant != null) {
                throw new Exception("InvalidPizzaCombinationException");
            }
            else {
                lastRestaurant = dishes.get(pizza).restaurant;
                totalPrice += dishes.get(pizza).price;
            }
        }
        totalPrice += deliveryFee;
        return totalPrice;
    }
}
