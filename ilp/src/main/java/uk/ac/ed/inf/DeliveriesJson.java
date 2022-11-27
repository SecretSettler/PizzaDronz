package uk.ac.ed.inf;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;

public class DeliveriesJson {
    public String orderNo;
    public String outcome;
    public int costInPence;

    public DeliveriesJson(String orderNo, String outcome, int costInPence){
        this.orderNo = orderNo;
        this.outcome = outcome;
        this.costInPence = costInPence;
    }

    public JsonObject generateJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("orderNo", orderNo);
        jsonObject.addProperty("outcome", outcome);
        jsonObject.addProperty("costInPence", costInPence);
        return jsonObject;
    }

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
