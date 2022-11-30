package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Class of the coordinate system with methods checking the relation among various object positions.
 */

public class LngLat {

    // Parse json into parameters
    @JsonAlias("longitude")
    public double lng;

    @JsonAlias("latitude")
    public double lat;

    @JsonAlias("name")
    public String name;

    private final double TOLERANCE = 0.00015;

    private final double EPSILON = 1e-12;

    /**
     * Helper Constructor for receiving longitude and latitude from json.
     */
    public LngLat() {

    }

    /**
     * Class constructor of coordinates
     *
     * @param lng Longitude of the object
     * @param lat Latitude of the object
     */
    public LngLat(double lng, double lat) {
        this.lng = lng;
        this.lat = lat;
    }

    /**
     * Enum class for compass direction
     */
    public enum Direction {
        E(0), ENE(22.5), NE(45.0),
        NNE(67.5), N(90.0), NNW(112.5),
        NW(135.0), WNW(157.5), W(180.0),
        WSW(202.5), SW(225.0), SSW(247.5),
        S(270.0), SSE(292.5), SE(315), ESE(337.5),
        HOVER(-1.0);

        private final double angle;

        /**
         * Get the angle
         */
        public double getAngle(){
            return angle;
        }

        /**
         * Reverse the angle
         * @return The opposite direction
         */
        public Direction reverseAngle(){
            for (Direction d: Direction.values()){
                double epsilon = 1e-12;
                if (Math.abs(d.getAngle() - (angle + 180.0) % 360.0) < epsilon) return d;
            }
            return HOVER;
        }

        Direction(double angle) {
            this.angle = angle;
        }
    }

    /**
     * Check whether the object is inside or on the polygon
     *
     * @param point   Position of the checked object
     * @param polygon Array of vertex positions which form the area (polygon)
     * @return a boolean of whether the object is in this area. Return true if it's in.
     */
    private boolean isInPolygon(LngLat point, LngLat[] polygon) {
        int count = 0;  // a counter for number of interceptions.
        LngLat p1 = polygon[0]; // First vertex of the polygon
        int numPoints = polygon.length;
        int i;
        double x_interceptions; // Interceptions between point-ray and the polygon
        LngLat p2; // Second vertex
        boolean pdLine; // Points on the line checker
        double epsilon = 1e-12;

        // Check whether the point is on the sides of the polygon
        for (int j = 1; j <= numPoints; j++) {
            p2 = polygon[j % numPoints];
            if (point.lng >= Math.min(p1.lng, p2.lng) &&
                    point.lng <= Math.max(p1.lng, p2.lng)) {
                // lines formed by the point and related vertices on the sides should have the same slope as the sides.
                pdLine = Math.abs((point.lng - p1.lng) * (p1.lat - p2.lat) - (p1.lng - p2.lng)
                        * (point.lat - p1.lat)) < EPSILON; // for calibrating the error from double calculation
                if (pdLine) {
                    return true;
                }
            }
            p1 = p2;
        }

        // Start iterating all the vertices and calculate the number of interceptions between the point-ray and the lines formed
        // by every two continuous points.
        for (i = 1; i <= numPoints; i++) {
            p2 = polygon[i % numPoints];
            // Check whether the point is inside the domain.
            if (point.lng > Math.min(p1.lng, p2.lng) &&
                    point.lng <= Math.max(p1.lng, p2.lng)) {
                if (point.lat <= Math.max(p1.lat, p2.lat)) {
                    if (p1.lng != p2.lng) {
                        x_interceptions = (point.lng - p1.lng) * (p2.lat - p1.lat) / (p2.lng - p1.lng) + p1.lat;
                        if (p1.lat == p2.lat || point.lat < x_interceptions || Math.abs(point.lat - x_interceptions) < EPSILON) {
                            count++;
                        }
                    }
                }
            }
            p1 = p2;
        }
        // If the number of interceptions is odd then it's inside the polygon, otherwise it's outside.
        return count % 2 != 0;
    }

    /**
     * Check whether the object is inside the central area
     *
     * @return true if it's inside, false otherwise.
     */
    public boolean inCentralArea(){
        CentralSingleton centralSingleton = CentralSingleton.getInstance();
        if (centralSingleton == null) {
            return false;
        }
        return isInPolygon(new LngLat(lng, lat), centralSingleton.points);
    }

    /**
     * Check whether the next movement is valid or not. It cannot be inside the no-fly zones and also cannot cross the
     * no-fly zones.
     * @param next the coordinate of the next movement.
     * @return true if the next movement is invalid.
     */
    public boolean intersectWithNoFlyZones(LngLat next){
        boolean intersect;
        NoFlySingleton noFlySingleton = NoFlySingleton.getInstance();
        Line2D line = new Line2D.Double(new Point2D.Double(lng, lat), new Point2D.Double(next.lng, next.lat));
        if (noFlySingleton == null){
            return false;
        }

        for (NoFlySingleton.NoFlyZone noFlyZone : noFlySingleton.allNoFlyZones) {
            LngLat[] lngLats = noFlyZone.transfer2LngLat();
            for (int i = 0; i < lngLats.length - 1; i++){
                intersect = line.intersectsLine(lngLats[i].lng, lngLats[i].lat, lngLats[i + 1].lng, lngLats[i + 1].lat);
                if (intersect){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Calculate the distance between the object and the target point
     *
     * @param lnglat Position of the target point.
     * @return Euclidean distance between the object and the target point.
     */
    public double distanceTo(LngLat lnglat) {
        double lng_diff = lng - lnglat.lng;
        double lat_diff = lat - lnglat.lat;
        return Math.sqrt(Math.pow(lng_diff, 2) + Math.pow(lat_diff, 2));
    }

    /**
     * Check if two points are closed enough
     *
     * @param lngLat Position of the target point
     * @return true if the object is less than 0.00015 away from the target, false otherwise.
     */
    public boolean closeTo(LngLat lngLat) {
        return distanceTo(lngLat) < TOLERANCE;
    }

    /**
     * Return the next position after movement. If the drone hovers, the current position will be returned.
     *
     * @param direction The orientation of the object's next movement
     * @return Position after the next movement
     */
    public LngLat nextPosition(Direction direction) {
        // If the drone hovers
        if (direction == null) {
            return this;  // Return current position
        }
        // Calculate new coordinates according to directions
        double newLng = lng + TOLERANCE * Math.cos(Math.toRadians(direction.angle));
        double newLat = lat + TOLERANCE * Math.sin(Math.toRadians(direction.angle));
        return new LngLat(newLng, newLat);
    }

    /**
     * Overriding function
     *
     * @return Format after json deserialization
     */
    @Override
    public String toString() {
        return "[" +
                lng +
                ", " + lat +
                "]";
    }

    /**
     * Overriding function
     * @param o object
     * @return whether the coordinates are equal or not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LngLat lngLat = (LngLat) o;
        return Double.compare(lngLat.lng, lng) == 0 && Double.compare(lngLat.lat, lat) == 0;
    }

}
