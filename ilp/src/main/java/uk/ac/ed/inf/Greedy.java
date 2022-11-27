package uk.ac.ed.inf;

import java.time.Clock;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Greedy{

    public static class Node implements Comparable<Node> {
        public LngLat lngLat;
        public Node parent;
        public double H;
        public boolean isInCentral;
        public LngLat.Direction direction;
        public Long ticksSinceStartOfCalculation;

        public Node(LngLat lngLat, Node parent, double h, boolean isInCentral, LngLat.Direction direction) {
            this.lngLat = lngLat;
            this.parent = parent;
            this.H = h;
            this.isInCentral = isInCentral;
            this.direction = direction;
            ticksSinceStartOfCalculation = System.nanoTime();
        }

        public int compareTo(Node node){
            if (node == null) return -1;
            if (H > node.H){
                return 1;
            }else if (H < node.H){
                return -1;
            }
            return 0;
        }

        public Node cloneNode() {
            return new Node(lngLat, parent, H, isInCentral, direction);
        }
    }

    public Greedy(){

    }

    Queue<Node> openList = new PriorityQueue<>();
    ArrayList<Node> closeList = new ArrayList<>();

    private boolean isNearEndNode(LngLat end, LngLat current){
        return current.closeTo(end);
    }

    private boolean canAddNodeToOpen(LngLat lngLat, Node current){
        if (!current.isInCentral && lngLat.inCentralArea()) return false;
        if (current.lngLat.intersectWithNoFlyZones(lngLat)) return false;
        return !isLngLatInClosed(lngLat);
    }

    private boolean isLngLatInClosed(LngLat lngLat){
        if (lngLat == null || closeList.isEmpty()) return false;
        for (Node node: closeList) {
            if (node.lngLat.lng == lngLat.lng && node.lngLat.lat == lngLat.lat) {
                return true;
            }
        }
        return false;
    }

    private boolean isPointNearEndInClosed(LngLat end){
        for (Node node: closeList) {
            if (isNearEndNode(end, node.lngLat)){
                return true;
            }
        }
        return false;
    }

    private double calcH(LngLat end, LngLat current){
        return current.distanceTo(end);
    }

    private Node findNodeInOpen(LngLat lngLat){
        if (lngLat == null || openList.isEmpty()) return null;
        for (Node node: openList){
            if (node.lngLat.equals(lngLat)){
                return node;
            }
        }
        return null;
    }

    private void addNeighbourNode(Node current, LngLat end){
        addNeighbourNode(current, end, LngLat.Direction.E);
        addNeighbourNode(current, end, LngLat.Direction.ENE);
        addNeighbourNode(current, end, LngLat.Direction.NE);
        addNeighbourNode(current, end, LngLat.Direction.NNE);
        addNeighbourNode(current, end, LngLat.Direction.N);
        addNeighbourNode(current, end, LngLat.Direction.NNW);
        addNeighbourNode(current, end, LngLat.Direction.NW);
        addNeighbourNode(current, end, LngLat.Direction.WNW);
        addNeighbourNode(current, end, LngLat.Direction.W);
        addNeighbourNode(current, end, LngLat.Direction.WSW);
        addNeighbourNode(current, end, LngLat.Direction.SW);
        addNeighbourNode(current, end, LngLat.Direction.SSW);
        addNeighbourNode(current, end, LngLat.Direction.S);
        addNeighbourNode(current, end, LngLat.Direction.SSE);
        addNeighbourNode(current, end, LngLat.Direction.SE);
        addNeighbourNode(current, end, LngLat.Direction.ESE);
    }

    private void addNeighbourNode(Node current, LngLat end, LngLat.Direction direction){
        if (canAddNodeToOpen(current.lngLat.nextPosition(direction), current)){
            boolean isInCentral = current.lngLat.nextPosition(direction).inCentralArea();
            Node child = findNodeInOpen(current.lngLat.nextPosition(direction));
            if (child == null){
                double H = calcH(end, current.lngLat.nextPosition(direction));
                child = new Node(current.lngLat.nextPosition(direction), current, H, isInCentral, direction);
                openList.add(child);
            }
        }
    }

    public ArrayList<Node> start(Map map){
        if (map == null) return null;
        openList.clear();
        closeList.clear();

        //Start searching
        openList.add(map.start);
        return move(map);
    }

    public ArrayList<Node> move(Map map){
        ArrayList<Node> steps = new ArrayList<>();
        while (!openList.isEmpty()){
            Node current = openList.poll();
            closeList.add(current);
            steps.add(current);
            addNeighbourNode(current, map.end);
            if (isPointNearEndInClosed(map.end)){
                return steps;
            }
        }
        return null;
    }


}
