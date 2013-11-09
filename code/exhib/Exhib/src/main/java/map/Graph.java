package map;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Jacob on 08-11-13.
 */
public class Graph {
    ArrayList<Edge> edges;
    ArrayList<Node> nodes;

    public Graph() {
        this.edges = new ArrayList<Edge>();
        this.nodes = new ArrayList<Node>();
    }

    /***
     * Makes a graph from a polyline array.
     * @param allPolyNodes
     */
    public Graph(ArrayList<Node> allPolyNodes) {
        this.nodes = this.distinct(allPolyNodes);
        this.edges = this.addEdges(allPolyNodes);
    }

    /***
     * Makes a graph with all data available
     * @param nodes
     * @param edges
     */
    public Graph(ArrayList<Node> nodes, ArrayList<Edge> edges){
        this.edges = edges;
        this.nodes = nodes;
    }

    private ArrayList<Edge> addEdges(ArrayList<Node> allPolyNodes){
        ArrayList<Edge> newArray = new ArrayList<Edge>();
        for(int i = 0; i < allPolyNodes.size() - 1; i++){
            Edge edge = new Edge(calcWeight(allPolyNodes.get(i).getPosition(), allPolyNodes.get(i+1).getPosition()), nodes.get(nodes.indexOf(allPolyNodes.get(i))), nodes.get(nodes.indexOf(allPolyNodes.get(i+1))));
            if (!existEdge(newArray, edge)){
                newArray.add(edge);
                this.nodes.get(nodes.indexOf(allPolyNodes.get(i))).addEdge(newArray.get(newArray.indexOf(edge)));
                this.nodes.get(nodes.indexOf(allPolyNodes.get(i+1))).addEdge(newArray.get(newArray.indexOf(edge)));
            }
        }
        return newArray;
    }

    public Node getNodeByIndex(int index){
        if(index >= 0 && index < nodes.size()){
            return nodes.get(index);
        }
        return null;
    }

    public Node getNodeByName(String name){
        for(Node node : this.nodes){
            if(node.getName().equals(name)){
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
            if(e.getFrom() == edge.getFrom() && e.getTo() == edge.getTo() ||
                    e.getFrom() == edge.getTo() && e.getTo() == edge.getFrom()){
                return true;
            }
        }
        return false;
    }

    public boolean existsNode (Node node){ //TODO: do we need this, maybe contains will work?
        for(Node n : this.nodes){
            if (n.getName().equals(node.getName())){
                return true;
            }
        }
        return false;
    }
}
