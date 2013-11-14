package map;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Jacob on 08-11-13.
 */
public class Graph {
    private ArrayList<Edge> edges;
    private ArrayList<Node> nodes;

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

    public Node getNodeById(long id){
        for(Node node : this.nodes){
            if(node.getID() == id){
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
            if (n.getID() == node.getID()){
                return true;
            }
        }
        return false;
    }

    public ArrayList<Node> shortestRoute(int sourceID, int targetID){
        Node source = this.getNodeById(sourceID);
        Node target = this.getNodeById(targetID);
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

        ArrayList<Node> path = makePath(target, previous, this.nodes);

        return path;
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
