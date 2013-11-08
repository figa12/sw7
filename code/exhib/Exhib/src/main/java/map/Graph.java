package map;

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

    public void addEdge(double weight, Node node1, Node node2){
        if(!this.ExistsNode(node1)){
            this.nodes.add(node1);
        }

        if(!this.ExistsNode(node2)){
            this.nodes.add(node2);
        }

        Edge myEdge = new Edge(weight, this.nodes.get(this.nodes.indexOf(node1)), this.nodes.get(this.nodes.indexOf(node2)));
        if(!this.ExistEdge(myEdge)){
            this.edges.add(myEdge);
            this.nodes.get(this.nodes.indexOf(node1)).addEdge(this.edges.get(this.edges.indexOf(myEdge)));
            this.nodes.get(this.nodes.indexOf(node2)).addEdge(this.edges.get(this.edges.indexOf(myEdge)));
        }
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

    public boolean ExistEdge(Edge edge){ //TODO: do we need this, maybe contains will work?
        for(Edge e : this.edges){
            if(e.getFrom() == edge.getFrom() && e.getTo() == edge.getTo() ||
                    e.getFrom() == edge.getTo() && e.getTo() == edge.getFrom()){
                return true;
            }
        }
        return false;
    }

    public boolean ExistsNode (Node node){ //TODO: do we need this, maybe contains will work?
        for(Node n : this.nodes){
            if (n.getName().equals(node.getName())){
                return true;
            }
        }
        return false;
    }

    public void makeGraph(ArrayList<Node> nodes){

    }
}
