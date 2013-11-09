package map;

/**
 * Created by Jacob on 08-11-13.
 */
public class Edge {
    private double weight;
    private Node from;
    private Node to;

    public Edge(double weight, Node from, Node to) {
        this.weight = weight;
        this.from = from;
        this.to = to;
    }

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }

    public Node getAdjacentNode(Node fromNode){
        if(fromNode == this.from){
            return this.to;
        }
        else{
            return this.from;
        }
    }

    public double getWeight(){
        return this.weight;
    }
}
