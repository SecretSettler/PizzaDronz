package uk.ac.ed.inf;

public class CentralSingleton {
    private static CentralSingleton centralSingleton;
    public LngLat leftUpper;
    public LngLat leftLower;
    public LngLat rightUpper;
    public LngLat rightLower;
    private CentralSingleton(){
        leftUpper = new LngLat(-3.192473, 55.946233);
        leftLower = new LngLat(-3.192473, 55.942617);
        rightLower = new LngLat(-3.184319, 55.942617);
        rightUpper = new LngLat(-3.184319, 55.946233);
    }

    public static CentralSingleton getInstance(){
        if (centralSingleton == null) {
            centralSingleton = new CentralSingleton();
        }
        return centralSingleton;
    }
}
