package uk.ac.ed.inf;

public class LngLat {

    public double lng;

    public double lat;

    public LngLat(double lng, double lat) {
        this.lng = lng;
        this.lat = lat;
    }

    private boolean isCentralArea() {
        CentralSingleton centralSingleton = CentralSingleton.getInstance();
        if (centralSingleton == null) {
            return false;
        }
        return !(lng < centralSingleton.leftLower.lng) && !(lng > centralSingleton.rightLower.lng) &&
                !(lat < centralSingleton.leftLower.lat) && !(lat > centralSingleton.rightUpper.lat);
    }

    private double distanceTo(LngLat lnglat) {
        double lng_diff = lng - lnglat.lng;
        double lat_diff = lat - lnglat.lat;
        return Math.sqrt(Math.pow(lng_diff, 2) + Math.pow(lat_diff, 2));
    }

    private boolean closeTo(LngLat lngLat) {
        return distanceTo(lngLat) < 0.00015;
    }

    private LngLat nextPosition(double direction) {
        double newLng = lng + 0.00015 * Math.sin(Math.toRadians(direction));
        double newLat = lat + 0.00015 * Math.cos(Math.toRadians(direction));
        return new LngLat(newLng, newLat);
    }
}
