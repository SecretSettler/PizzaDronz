package uk.ac.ed.inf;

import com.google.gson.JsonObject;

/**
 * JSON Generator for flightpath
 */
public class FlightPathJson {
    public String orderNo;
    public double fromLongitude;
    public double fromLatitude;
    public Double angle;
    public double toLongitude;
    public double toLatitude;
    public Long ticksSinceStartOfCalculation;

    /**
     * Constructor
     * @param orderNo
     * @param fromLongitude
     * @param fromLatitude
     * @param angle
     * @param toLongitude
     * @param toLatitude
     * @param ticksSinceStartOfCalculation
     */
    public FlightPathJson(String orderNo, double fromLongitude, double fromLatitude, Double angle, double toLongitude,
                          double toLatitude, Long ticksSinceStartOfCalculation){
        this.orderNo = orderNo;
        this.fromLongitude = fromLongitude;
        this.fromLatitude = fromLatitude;
        this.angle = angle;
        this.toLongitude = toLongitude;
        this.toLatitude = toLatitude;
        this.ticksSinceStartOfCalculation = ticksSinceStartOfCalculation;
    }

    /**
     * Generate the JSON object of flight paths
     * @return the JSON object
     */
    public JsonObject generateJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("orderNo", orderNo);
        jsonObject.addProperty("fromLongitude", fromLongitude);
        jsonObject.addProperty("fromLatitude",fromLatitude);
        jsonObject.addProperty("angle", angle);
        jsonObject.addProperty("toLongitude", toLongitude);
        jsonObject.addProperty("toLatitude", toLatitude);
        jsonObject.addProperty("tickSinceStartOfCalculation", ticksSinceStartOfCalculation);
        return jsonObject;
    }


}
