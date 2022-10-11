package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class of Menu
 */
public class Menu {
    @JsonProperty("name")
    private String name;
    @JsonProperty("priceInPence")
    private int priceInPence;

    /**
     * Get the name of the dish.
     * @return Name of the dish.
     */
    public String getName(){
        return name;
    }

    /**
     * Get the price of the dish
     * @return Price of the dish
     */
    public int getPriceInPence(){
        return priceInPence;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "name='" + getName() + '\'' +
                ", priceInPence=" + getPriceInPence() +
                '}';
    }
}
