package map;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Jacob on 08-11-13.
 */
public class Node {
    private String name;
    private LatLng position;
    private ArrayList<Edge> edges;
    private boolean visited = false; //only used for djiktras

    public Node(LatLng position, String name) {
        this.position = position;
        this.name = name;
        this.edges = new ArrayList<Edge>();
    }

    public String getName() {
        return this.name;
    }

    public LatLng getPosition() {
        return this.position;
    }

    public boolean isVisited() {
        return this.visited;
    }

    public void addEdge(Edge edge){
        this.edges.add(edge);
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
            neighbours.add(edge.getNextNode(this));
        }
        return neighbours;
    }
}
