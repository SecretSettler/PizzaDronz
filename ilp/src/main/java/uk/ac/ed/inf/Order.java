package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.*;

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
        Delivered,
        ValidButNotDelivered,
        InvalidCardNumber,
        InvalidExpiryDate,
        InvalidCvv,
        InvalidTotal,
        InvalidPizzaNotDefined,
        InvalidPizzaCount,
        InvalidPizzaCombinationMultipleSuppliers
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

    public static HashMap<String, Tuple> getDishes(Restaurant[] restaurants) {
        HashMap<String, Tuple> dishes = new HashMap<>();
        // Put dishes of restaurants inside the hashmap for further reference.
        for (Restaurant restaurant : restaurants) {
            for (Menu menu : restaurant.getMenu()) {
                dishes.put(menu.getName(), new Tuple(restaurant.getName(), menu.getPriceInPence()));
            }
        }
        return dishes;
    }

    public static void pizzaCombinationCheck(Restaurant[] restaurants, String[] pizzas) throws Exception {
        String lastRestaurant = null;
        HashMap<String, Tuple> dishes = getDishes(restaurants);
        if (pizzas.length > 4) {
            throw new Exception(OrderOutcome.InvalidPizzaCount + " You can only order 4 pizzas at most");
        }
        for (String pizza : pizzas) {
            if (dishes.get(pizza) != null && !dishes.get(pizza).restaurant.equals(lastRestaurant) && lastRestaurant != null) {
                throw new Exception(OrderOutcome.InvalidPizzaCombinationMultipleSuppliers +
                        " You can only order pizzas from one restaurant");
            } else if (dishes.get(pizza) == null) {
                throw new Exception(OrderOutcome.InvalidPizzaNotDefined +
                        " Can't find corresponding pizzas from the chosen restaurant");
            } else {
                lastRestaurant = dishes.get(pizza).restaurant;
            }
        }
    }

    public static void paymentCheck(Restaurant[] restaurants, Order order) throws Exception {
        long cardNumber = Long.parseLong(order.creditCardNumber);
        if (!CreditCardCheck.validitychk(cardNumber)) {
            throw new Exception(OrderOutcome.InvalidCardNumber + " Invalid credit card number provided");
        }
        var i = Integer.parseInt(order.orderDate.substring(0, 2)
                + order.creditCardExpiry.substring(3, 5));
        if ((Integer.parseInt(order.orderDate.substring(0, 4)) > i) ||
                ((Integer.parseInt(order.orderDate.substring(0, 4)) == i) &&
                        (Integer.parseInt(order.orderDate.substring(5, 7)) >
                                Integer.parseInt(order.creditCardExpiry.substring(0, 2))))) {
            throw new Exception(OrderOutcome.InvalidExpiryDate + " Your credit card has expired");
        }
        if (order.cvv.length() != 3) {
            throw new Exception(OrderOutcome.InvalidCvv + " Invalid cvv provided");
        }

        if (Integer.parseInt(order.priceTotalInPence) != (Order.getDeliveryCost(restaurants, order.orderItems))) {
            throw new Exception(OrderOutcome.InvalidTotal + " You did not pay the correct amount of money");
        }
    }

    /**
     * Get the total cost of the order
     *
     * @param pizzas Array of pizza names
     * @return Total price of the order in pence by the sum of pizza prices and total delivery fees.
     * @throws Exception if the pizza combination can't be delivered, the exception is thrown.
     */
    public static int getDeliveryCost(Restaurant[] restaurants, String[] pizzas) throws Exception {
        int deliveryFee = 100;
        int totalPrice = 0;
        HashMap<String, Tuple> dishes = getDishes(restaurants);
        // Check whether the pizza combination is valid or not.
        pizzaCombinationCheck(restaurants, pizzas);
        for (String pizza : pizzas) {
            totalPrice += dishes.get(pizza).price;
        }

        // Include the delivery fee.
        totalPrice += deliveryFee;
        return totalPrice;
    }

    public static Order[] getOrdersFromServer(URL baseURL, String date) {
        try {
            String furtherAddress = "/orders/" + date;
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
            return objectMapper.readValue(
                    new URL(baseURL, furtherAddress), new TypeReference<Order[]>() {
                    });
        } catch (IOException f) {
            System.out.println("[ERROR] INVALID URL!!!");
            System.out.println(">>> SYSTEM EXIT <<<");
            System.exit(-1);
        }
        return null;
    }

    public static LngLat getLngLatFromOrder(Order order, Restaurant[] restaurants) {
        String name = order.orderItems[0];
        // Because we won't deliver invalid orders, thus definitely all pizzas should come from
        // the same restaurant in a single order. So we pick the first one as the representative.
        for (Restaurant restaurant : restaurants) {
            if (restaurant.getNameOfDishesFromMenus(restaurant.getMenu()).contains(name)) {
                return new LngLat(restaurant.getLongitude(), restaurant.getLatitude());
            }
        }
        return null;
    }

    public static ArrayList<Order> ordersFilter(Order[] orders, Restaurant[] restaurants) {
        if (orders == null) {
            return null;
        }
        ArrayList<Order> orderArrList = new ArrayList<>(Arrays.asList(orders));
        ArrayList<Order> removeList = new ArrayList<>();
        for (Order order : orderArrList) {
            try {
                pizzaCombinationCheck(restaurants, order.orderItems);
                paymentCheck(restaurants, order);
            } catch (Exception e) {
                removeList.add(order);
            }
        }
        orderArrList.removeAll(removeList);
        return orderArrList;
    }

    public static HashMap<Order, String> invalidOrders(Order[] orders, Restaurant[] restaurants){
        if (orders == null) {
            return null;
        }
        HashMap<Order, String> invalidOrders = new HashMap<>();
        for (Order order: orders){
            try {
                pizzaCombinationCheck(restaurants, order.orderItems);
                paymentCheck(restaurants, order);
            }catch (Exception e) {
                invalidOrders.put(order, e.getMessage());
            }
        }
        return invalidOrders;
    }

    public static void sortOrders(ArrayList<Order> orders, Restaurant[] restaurants) {
        if (orders == null){
            return;
        }
        LngLat appleton = new LngLat(-3.186874, 55.944494);
        orders.sort(Comparator.comparing((Order order) -> appleton.distanceTo(getLngLatFromOrder(order, restaurants))));
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderNo='" + orderNo + '\'' +
                ", orderDate='" + orderDate + '\'' +
                ", customer='" + customer + '\'' +
                ", creditCardNumber='" + creditCardNumber + '\'' +
                ", creditCardExpiry='" + creditCardExpiry + '\'' +
                ", cvv='" + cvv + '\'' +
                ", priceTotalInPence='" + priceTotalInPence + '\'' +
                ", orderItems=" + Arrays.toString(orderItems) +
                '}';
    }
}
