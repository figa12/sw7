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
	this.getNextNode = function(fromNode){
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
	this.visited = false;
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

	this.getAllNeighbours = function(){
		var neighbours = [];
		for (var i = 0; i < this.edges.length; i++) {
			neighbours.push(this.edges[i].getNextNode(this));
		}
		return neighbours;
	};
}

//Graph object
function Graph(){
	this.edges = [];
	this.nodes = [];
	this.numberOfEdges = 0;
	
	this.addEdge = function(weight, node1, node2){
		var indexMyNode1 = doesAlreadyNodeExist(this.nodes, node1);

		if(indexMyNode1 == -1){
			this.nodes.push(node1);
			indexMyNode1 = this.nodes.indexOf(node1);
		}

		var indexMyNode2 = doesAlreadyNodeExist(this.nodes, node2);
		if(indexMyNode2 == -1){
			this.nodes.push(node2);
			indexMyNode2 = this.nodes.indexOf(node2);
		}

		var myEdge = new Edge(weight, this.nodes[indexMyNode1], this.nodes[indexMyNode2]);

		if(doesAlreadyEdgeExist(this.edges,myEdge) == -1){
			this.edges.push(myEdge);
			this.nodes[indexMyNode1].addEdge(this.edges[this.numberOfEdges]);
			this.nodes[indexMyNode2].addEdge(this.edges[this.numberOfEdges]);
			this.numberOfEdges = this.numberOfEdges + 1;
		}
	};

	this.getNodeByIndex = function(index){
		if(index >= 0 && index < nodes.length){
			return nodes[index];
		}
		return -1;
	};

	this.getNodeByName = function(name){
		for (var i = 0; i < this.nodes.length; i++) {
			if(this.nodes[i].name == name){
				return this.nodes[i];
			}
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
function doesAlreadyEdgeExist(edges, edge){
	for (var i = 0; i < edges.length; i++) {
		if(edges[i].from == edge.from && edges[i].to == edge.to || edges[i].from == edge.to && edges[i].to == edge.from ){
			return i;
		}
	}

	return -1;
}

//Makes sure that we don't make the same node twice, also return the index of the node if it found.
function doesAlreadyNodeExist(nodes, node){
	for (var i = 0; i < nodes.length; i++) {
		if(nodes[i].name == node.name){
			return i;
		}
	}

	return -1;
}

//Used to calculate shortest route and return the route.
function shortestRoute(from, to, roadMapGraph){
	var previous = [];
	var dist = [];
	var target = roadMapGraph.getNodeByName(to);
	for (var i = 0; i < roadMapGraph.nodes.length; i++) {
		dist.push(Number.MAX_VALUE);
		previous.push(null);
	}

	dist[roadMapGraph.nodes.indexOf(roadMapGraph.getNodeByName(from))] = 0;

	var Q = [];
	Q = Q.concat(roadMapGraph.nodes);

	while (Q.areAllNodesVisited() === false){
		var u = Q.smallestDistanceNotVisited(dist);
		var uIndex = Q.indexOf(u);
		Q[uIndex].visited = true;

		if(dist[uIndex] == Number.MAX_VALUE){
			break;
		}

		for (var k = 0; k < u.getAllNeighbours().length; k++) {
			var v = u.getAllNeighbours()[k];
			var vIndex = roadMapGraph.nodes.indexOf(v);
			var alt = dist[uIndex] + distBetween(u, v);
			if(alt < dist[vIndex]){
				dist[vIndex] = alt;
				previous[vIndex] = u;
			}
		}

	}

	var path = makePath(target, previous, Q);
	return path;
}
function makePath(target, previous, Q){
	var path = [];
	var u = target;
	while (previous[Q.indexOf(u)] !== null){
		path.push(u);
		previous.pop();
		u = previous[Q.indexOf(u)];
	}
	return path;
}

function distBetween(u, v){
	for (var i = 0; i < u.edges.length; i++) {
		if(u.edges[i].getNextNode(u) == v ){
			return u.edges[i].weight;
		}
	}
}

Array.prototype.areAllNodesVisited = function(){
	var visited = true;
	for (var i = 0; i < this.length; i++) {
		if (this[i].visited === false) {
			visited = false;
			return visited;
		}
	}
	return visited;
};

Array.prototype.smallestDistanceNotVisited = function(dist){
	var min = Number.MAX_VALUE;
	for (var i = 0; i < this.length; i++) {
		if (this[i].visited === false && min >= dist[i]) {
			min = dist[i];
		}
	}
	return this[dist.indexOf(min)];
};

Array.prototype.min = function(){
	return Math.min.apply(null, this);
};

Array.prototype.max = function(){
	return Math.max.apply(null, this);
};