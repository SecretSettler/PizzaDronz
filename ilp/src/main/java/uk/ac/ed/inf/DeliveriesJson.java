package uk.ac.ed.inf;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;

/**
 * Class for generating delivery JSON files
 */
public class DeliveriesJson {
    public String orderNo;
    public String outcome;
    public int costInPence;

    /**
     * Constructor
     * @param orderNo order number
     * @param outcome order outcome
     * @param costInPence total cost in pence
     */
    public DeliveriesJson(String orderNo, String outcome, int costInPence){
        this.orderNo = orderNo;
        this.outcome = outcome;
        this.costInPence = costInPence;
    }

    /**
     * Generate the JSON file
     * @return corresponding JSON object
     */
    public JsonObject generateJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("orderNo", orderNo);
        jsonObject.addProperty("outcome", outcome);
        jsonObject.addProperty("costInPence", costInPence);
        return jsonObject;
    }

    /**
     * Add invalid orders together
     * @param results the results of valid orders
     * @param invalidOrders all invalid orders
     * @return the combination of these two categories.
     */
    public static JsonArray generateJsonArray(JsonArray results, HashMap<Order, String> invalidOrders) {
        for (Order order: invalidOrders.keySet()){
            DeliveriesJson deliveriesJson = new DeliveriesJson(order.orderNo,
                    invalidOrders.get(order),
                    0);
            //If the order is invalid, the user does not need to pay.
            JsonObject jsonObject = deliveriesJson.generateJson();
            results.add(jsonObject);
        }
        return results;
    }
}
