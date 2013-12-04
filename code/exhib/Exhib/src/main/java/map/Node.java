package map;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import aau.sw7.exhib.BoothItem;

/**
 * Created by Jacob on 08-11-13.
 */
public class Node {
    private long id;
    private LatLng position;
    private ArrayList<Edge> edges;
    private long boothId; //TODO implement Booth relation, if needed.
    private boolean visited = false; //only used for djiktras

    public long getBoothId() {
        return boothId;
    }

    public void setBoothId(long boothId) {
        this.boothId = boothId;
    }

    /***
     * Contructor
     * @param position

     * @param id
     */
    public Node(LatLng position, long id) {
        this.position = position;
        this.id = id;
        this.edges = new ArrayList<Edge>();
        this.boothId = -1;
    }

    public long getId() {
        return this.id;
    }

    public LatLng getPosition() {
        return this.position;
    }

    public boolean isVisited() {
        return this.visited;
    }

    public void setVisited(boolean bool){
        this.visited = bool;
    }
    public void addEdge(Edge edge){
        this.edges.add(edge);
    }

    public ArrayList<Edge> getEdges(){
        return this.edges;
    }

    public Edge getEdge(int index){
        if(index >= 0 && index < numberOfEdges()){
            return edges.get(index);
        }
        return null;
    }

    public int numberOfEdges(){
        return this.edges.size();
    }

    public ArrayList<Node> getAllNeighboursNodes(){
        ArrayList<Node> neighbours = new ArrayList<Node>();
        for(Edge edge : this.edges){
            neighbours.add(edge.getAdjacentNode(this));
        }
        return neighbours;
    }
}
