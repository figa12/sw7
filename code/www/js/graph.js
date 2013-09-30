//Weight function(euclidean distance).
function dist(p, q){
    var res;
    res = Math.sqrt((Math.pow(p.lng() - q.lng(),2)) + (Math.pow(p.lat() - q.lat(),2)));
    if (res < 0){
        res = res * -1;
    }
    return res;
}

//Edge object.
function Edge(weight, from, to){
	this.weight = weight;
	this.from = from;
	this.to = to;

	//returns the node different from the node you are traveling from.
	this.getNextNode = function(node){
		if(fromNode == this.from){
			return this.to;
		}
		else{
			return this.from;
		}
	};

	//Gets the weight of the edge
	this.getWeight = function(){
		return this.weight;
	};
}

//Node object.
function Node(name, point){
	this.name = name;
	this.location = point;
	this.edges = [];
	this.addEdge = function(edge){
		this.edges.push(edge);
	};

	this.numberOfEdges = function(){
		return this.edges.length;
	};

	this.getEdge = function(index){
		if(index >= 0 && index < numberOfEdges()){
			return edges[index];
		}
		return false;
	};
}

//Graph object
function Graph(){
	this.edges = [];
	this.nodes = [];
	this.numberOfEdges = 0;
	
	this.addEdge = function(weight, node1, node2){
		var indexMyNode1 = doesNodeExist(this.nodes, node1);

		if(indexMyNode1 == -1){
			this.nodes.push(node1);
			indexMyNode1 = this.nodes.indexOf(node1);
		}

		var indexMyNode2 = doesNodeExist(this.nodes, node2);
		if(indexMyNode2 == -1){
			this.nodes.push(node2);
			indexMyNode2 = this.nodes.indexOf(node2);
		}

		var myEdge = new Edge(weight, this.nodes[indexMyNode1], this.nodes[indexMyNode2]);

		if(doesEdgeExist(this.edges,myEdge) == -1){
			this.edges.push(myEdge);
			this.nodes[indexMyNode1].addEdge(this.edges[this.numberOfEdges]);
			this.nodes[indexMyNode2].addEdge(this.edges[this.numberOfEdges]);
			this.numberOfEdges = this.numberOfEdges + 1;
		}
	};
}


//Makes a graph with edges(and weights) and node, later used to traverse. 
function makeGraph(points){
	var myGraph = new Graph();

	for (var i = 0; i < points.length - 1; i++) {
		if(points[i].name != points[i+1].name){
			myGraph.addEdge(dist(points[i].location, points[i+1].location), points[i], points[i+1]);
		}
	}
	return myGraph;
}

//Makes sure that we don't make the same edge twice. returns the index of the edge if it found, otherwise -1.
function doesEdgeExist(edges, edge){
	for (var i = 0; i < edges.length; i++) {
		if(edges[i].from == edge.from && edges[i].to == edge.to || edges[i].from == edge.to && edges[i].to == edge.from ){
			return i;
		}
	}

	return -1;
}

//Makes sure that we don't make the same node twice, also return the index of the node if it found.
function doesNodeExist(nodes, node){
	for (var i = 0; i < nodes.length; i++) {
		if(nodes[i].name == node.name){
			return i;
		}
	}

	return -1;
}

//Used to calculate shortest route and return the route.
function shortestRoute(from, to, roadMapGraph){
	var previous;
	var dist = [];
	for (var i = 0; i < roadMapGraph.nodes.length; i++) {
		dist.push(9999999);
	}

	
}