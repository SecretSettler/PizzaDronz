package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

/**
 * Class of order
 */
public class Order {

    @JsonProperty("orderNo")
    public String orderNo;
    @JsonProperty("orderDate")
    public String orderDate;
    @JsonProperty("customer")
    public String customer;
    @JsonProperty("creditCardNumber")
    public String creditCardNumber;
    @JsonProperty("creditCardExpiry")
    public String creditCardExpiry;
    @JsonProperty("cvv")
    public String cvv;
    @JsonProperty("priceTotalInPence")
    public String priceTotalInPence;
    @JsonProperty("orderItems")
    public String[] orderItems;

    public enum OrderOutcome {
        Delivered ,
        ValidButNotDelivered ,
        InvalidCardNumber ,
        InvalidExpiryDate ,
        InvalidCvv ,
        InvalidTotal ,
        InvalidPizzaNotDefined ,
        InvalidPizzaCount ,
        InvalidPizzaCombinationMultipleSuppliers ,
        Invalid
    }
    
    /**
     * Class of tuple data type.
     * Later the information for each pizza can be stored as a hashmap of tuples in the form of {"name": (restaurant, price)}
     */
    private static class Tuple {
        public String restaurant;
        public int price;

        public Tuple(String restaurant, int price) {
            this.restaurant = restaurant;
            this.price = price;
        }
    }

    /**
     * Get the total cost of the order
     * @param restaurants The array of restaurants mentioned in the order
     * @param pizzas Array of pizza names
     * @return Total price of the order in pence by the sum of pizza prices and total delivery fees.
     * @throws Exception if the pizza combination can't be delivered, the exception is thrown.
     */
    public static int getDeliveryCost(Restaurant[] restaurants, String[] pizzas) throws Exception {
        HashMap<String, Tuple> dishes = new HashMap<>(); // Use hashmap to speed up; reduce one for loop
        int deliveryFee = 100;
        int totalPrice = 0;
        String lastRestaurant = null;

        // Put information inside the hashmap.
        for (Restaurant restaurant : restaurants) {
            for (Menu menu : restaurant.getMenu()) {
                dishes.put(menu.getName(), new Tuple(restaurant.getName(), menu.getPriceInPence()));
            }
        }

        // Check whether the pizza combination is valid or not.
        for (String pizza : pizzas) {
            if (dishes.get(pizza) != null && !dishes.get(pizza).restaurant.equals(lastRestaurant) && lastRestaurant != null) {
                throw new Exception("InvalidPizzaCombinationException");
            }
            else if (dishes.get(pizza) == null) {
                throw new Exception(String.valueOf(OrderOutcome.InvalidPizzaNotDefined));
            }
            else {
                lastRestaurant = dishes.get(pizza).restaurant;
                totalPrice += dishes.get(pizza).price;
            }
        }

        // Include the delivery fee.
        totalPrice += deliveryFee * Math.ceil((float)pizzas.length / 4);
        return totalPrice;
    }
}
