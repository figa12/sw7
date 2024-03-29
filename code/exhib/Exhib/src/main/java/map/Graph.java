package map;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import aau.sw7.exhib.BoothItem;

/**
 * Created by Jacob on 08-11-13.
 */
public class Graph {
    private ArrayList<Edge> edges;
    private ArrayList<Node> nodes;
    private ArrayList<Node> polylinePath;
    private boolean updatePolyline;
    private LatLng userLocation;
    private BoothItem currentBoothLocation;


    public Graph() {
        this.edges = new ArrayList<Edge>();
        this.nodes = new ArrayList<Node>();
        this.polylinePath = new ArrayList<Node>();
        this.updatePolyline = true;
        this.userLocation = null;
    }

    /***
     * Makes a graph from a polyline array.
     * @param allPolyNodes
     */
    public Graph(ArrayList<Node> allPolyNodes) {
        this.nodes = this.distinct(allPolyNodes);
        this.edges = this.addEdges(allPolyNodes);
        this.polylinePath = new ArrayList<Node>();
        this.updatePolyline = true;
        this.userLocation = null;
    }

    /***
     * Makes a graph with all data available
     * @param nodes
     * @param edges
     */
    public Graph(ArrayList<Node> nodes, ArrayList<Edge> edges){
        this.edges = edges;
        this.nodes = nodes;
        this.polylinePath = new ArrayList<Node>();
        this.updatePolyline = true;
        this.userLocation = null;
    }

    public ArrayList<Node> getPolylinePath() {
        if(this.updatePolyline){
            makePolyLine();
            this.updatePolyline = false;
        }
        return polylinePath;
    }

    public boolean isUpdatePolyline() {
        return updatePolyline;
    }

    public LatLng getUserLocation(){
        return this.userLocation;
    }

    public void setUserLocation(LatLng newLocation){
        this.userLocation = newLocation;
    }

    public BoothItem getCurrentBoothLocation() {
        return currentBoothLocation;
    }

    public void setCurrentBoothLocation(BoothItem currentBoothLocation) {
        this.currentBoothLocation = currentBoothLocation;
    }

    public void setUpdatePolyline(boolean updatePolyline) {
        this.updatePolyline = updatePolyline;
    }

    public void makePolyLine(){
        this.polylinePath = new ArrayList<Node>();
        for(Edge edge : this.getEdges()){
            int indexNodeA = this.polylinePath.indexOf(edge.getNodeA());
            int indexNodeB = this.polylinePath.indexOf(edge.getNodeB());

            if(indexNodeB != -1){
                this.polylinePath.add(indexNodeB + 1, edge.getNodeA());
                this.polylinePath.add(this.polylinePath.indexOf(edge.getNodeA())+1, edge.getNodeB());
            }
            else if(indexNodeA != -1 ){
                this.polylinePath.add(indexNodeA + 1, edge.getNodeB());
                this.polylinePath.add(this.polylinePath.indexOf(edge.getNodeB())+1, edge.getNodeA());
            }
            else{
                this.polylinePath.add(edge.getNodeA());
                this.polylinePath.add(edge.getNodeB());
            }
        }

        ArrayList<LatLng> polyLine = new ArrayList<LatLng>();
        for(Node n : this.polylinePath){
            polyLine.add(n.getPosition());
        }
    }

    public ArrayList<Edge> getEdges() {
        return this.edges;
    }

    public ArrayList<Node> getNodes() {
        return this.nodes;
    }

    private ArrayList<Edge> addEdges(ArrayList<Node> allPolyNodes){
        ArrayList<Edge> edges = new ArrayList<Edge>();
        for(int i = 0; i < allPolyNodes.size() - 1; i++){
            Edge edge = new Edge(calcWeight(allPolyNodes.get(i).getPosition(), allPolyNodes.get(i+1).getPosition()), this.nodes.get(this.nodes.indexOf(allPolyNodes.get(i))), nodes.get(nodes.indexOf(allPolyNodes.get(i+1))));
            if (!existEdge(edges, edge)){
                edges.add(edge);
            }
        }
        return edges;
    }

    public Node getNodeByIndex(int index){
        if(index >= 0 && index < this.nodes.size()){
            return this.nodes.get(index);
        }
        return null;
    }

    public Node findNodeById(long id){
        for(Node node : this.nodes){
            if(node.getId() == id){
                return node;
            }
        }
        return null;
    }

    private double calcWeight(LatLng p, LatLng q){
        double res = 0.0;
        res = Math.sqrt((Math.pow(p.longitude - q.longitude, 2)) + (Math.pow(p.latitude - q.latitude, 2)));
        return res;
    }

    private <T> ArrayList<T> distinct(ArrayList<T> arrayList){
        ArrayList<T> newArray = new ArrayList<T>();
        for(T item : arrayList){
            if(!newArray.contains(item)){
                newArray.add(item);
            }
        }
        return newArray;
    }

    public boolean existEdge(ArrayList<Edge> edges, Edge edge){ //contains will not work, we need this.
        for(Edge e : edges){
            if(e.getNodeA() == edge.getNodeA() && e.getNodeB() == edge.getNodeB() ||
                    e.getNodeA() == edge.getNodeB() && e.getNodeB() == edge.getNodeA()){
                return true;
            }
        }
        return false;
    }

    public boolean existsNode (Node node){ //TODO: do we need this, maybe contains will work?
        for(Node n : this.nodes){
            if (n.getId() == node.getId()){
                return true;
            }
        }
        return false;
    }

    public ArrayList<Node> shortestRoute(long sourceID, long targetID){
        Node source = this.findNodeById(sourceID);
        Node target = this.findNodeById(targetID);
        ArrayList<Node> previous = new ArrayList<Node>();
        ArrayList<Double> dist = new ArrayList<Double>();

        for (int i = 0; i< this.nodes.size() ;i++){
            dist.add(Double.MAX_VALUE);
            previous.add(null);
            this.nodes.get(i).setVisited(false);
        }

        dist.set(this.nodes.indexOf(source), 0.0);

        while (areAllNodesVisited(this.nodes) == false){
            Node u = smallestDistanceNotVisited(this.nodes, dist);
            int uIndex = this.nodes.indexOf(u);
            this.nodes.get(uIndex).setVisited(true);

            if(dist.get(uIndex) == Double.MAX_VALUE){
                break;
            }

            for (int i = 0; i< u.getAllNeighboursNodes().size(); i++){
                Node v = u.getAllNeighboursNodes().get(i);
                int vIndex = this.nodes.indexOf(v);
                double alt = dist.get(uIndex) + distBetween(u,v);
                if(alt < dist.get(vIndex)){
                    dist.set(vIndex, alt);
                    previous.set(vIndex, u);
                }
            }
        }

        this.resetNodesVisited();

        return makePath(target, previous, this.nodes);
    }

    private void resetNodesVisited(){
        for(Node n : nodes){
            n.setVisited(false);
        }
    }

    public ArrayList<Node> bestWaypoint(Node source, BoothItem targetBooth){
        Double smallestWeight = Double.MAX_VALUE;
        ArrayList<Node> result = new ArrayList<Node>();
            for (Node tn : targetBooth.getBoothEntryNodes()){
                double weight = distBetween(source, tn);
                if(weight < smallestWeight){
                    smallestWeight = weight;
                    result.clear();
                    result.add(0,source);
                    result.add(1,tn);
                }
            }
        return result;
    }

    public ArrayList<Node> bestWaypoint(BoothItem sourceBooth, BoothItem targetBooth){
        Double smallestWeight = Double.MAX_VALUE;
        ArrayList<Node> result = new ArrayList<Node>();
        for (Node sn : sourceBooth.getBoothEntryNodes()){
            for (Node tn : targetBooth.getBoothEntryNodes()){
                double weight = distBetween(sn, tn);
                if(weight < smallestWeight){
                    smallestWeight = weight;
                    result.clear();
                    result.add(0,sn);
                    result.add(1,tn);
                }
            }
        }
        return result;
    }

    private ArrayList<Node> makePath(Node target, ArrayList<Node> previous, ArrayList<Node> Q){
        ArrayList<Node> path = new ArrayList<Node>();
        Node u = target;
        while (previous.get(Q.indexOf(u)) != null){
            path.add(u);
            u = previous.get(Q.indexOf(u));
        }

        u = Q.get(Q.indexOf(u));
        path.add(u);
        return path;
    }

    private boolean areAllNodesVisited(ArrayList<Node> Q){
        boolean visited = true;
        for(int i = 0; i < Q.size(); i++){
            if(Q.get(i).isVisited() == false){
                visited = false;
                return visited;
            }
        }
        return visited;
    }

    private Node smallestDistanceNotVisited(ArrayList<Node> Q, ArrayList<Double> dist){
        double min = Double.MAX_VALUE;
        for(int i = 0; i < Q.size(); i++){
            if (Q.get(i).isVisited() == false && min >= dist.get(i)){
                min = dist.get(i);
            }
        }
        return Q.get(dist.indexOf(min));
    }

    private double distBetween(Node u, Node v){
        for (Edge e : u.getEdges()){
            if (e.getAdjacentNode(u) == v){
                return e.getWeight();
            }
        }
        return 0.0;
    }
}
