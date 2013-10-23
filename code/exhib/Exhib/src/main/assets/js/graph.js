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
	
	/*Adds a edge with weight between two node 
	multiple edges between the two nodes cannot exist.*/
	this.addEdge = function(weight, node1, node2){
		var indexMyNode1 = doesNodeAlreadyExist(this.nodes, node1);

		if(indexMyNode1 == -1){
			this.nodes.push(node1);
			indexMyNode1 = this.nodes.indexOf(node1);
		}

		var indexMyNode2 = doesNodeAlreadyExist(this.nodes, node2);
		if(indexMyNode2 == -1){
			this.nodes.push(node2);
			indexMyNode2 = this.nodes.indexOf(node2);
		}

		var myEdge = new Edge(weight, this.nodes[indexMyNode1], this.nodes[indexMyNode2]);

		if(doesEdgeAlreadyExist(this.edges,myEdge) == -1){
			this.edges.push(myEdge);
			this.nodes[indexMyNode1].addEdge(this.edges[this.edges.indexOf(myEdge)]);
			this.nodes[indexMyNode2].addEdge(this.edges[this.edges.indexOf(myEdge)]);
		}
	};

	/*Given a index the function returns the node.*/
	this.getNodeByIndex = function(index){
		if(index >= 0 && index < nodes.length){
			return nodes[index];
		}
		return -1;
	};

	/*Given a name the function returns the node*/
	this.getNodeByName = function(name){
		for (var i = 0; i < this.nodes.length; i++) {
			if(this.nodes[i].name == name){
				return this.nodes[i];
			}
		}
	};
}


/*Makes a graph with edges(and weights) 
  and nodes, later used to traverse.*/
function makeGraph(points){
	var myGraph = new Graph();

	for (var i = 0; i < points.length - 1; i++) {
		if(points[i].name != points[i+1].name){
			myGraph.addEdge(dist(points[i].location, points[i+1].location), points[i], points[i+1]);
		}
	}
	return myGraph;
}

/*Makes sure that we don't make the same edge twice. 
  Returns the index of the edge if it found, otherwise -1.*/
function doesEdgeAlreadyExist(edges, edge){
	for (var i = 0; i < edges.length; i++) {
		if(edges[i].from == edge.from && edges[i].to == edge.to || edges[i].from == edge.to && edges[i].to == edge.from ){
			return i;
		}
	}

	return -1;
}

/*Makes sure that we don't make the same node twice, 
  also return the index of the node if it found.*/
function doesNodeAlreadyExist(nodes, node){
	for (var i = 0; i < nodes.length; i++) {
		if(nodes[i].name == node.name){
			return i;
		}
	}

	return -1;
}

/*Used to calculate shortest route and return the route.*/
function shortestRoute(source, target, roadMapGraph){
	var previous = []; //contains the previous node for the indexnode, previous[0] contains the node that is fastest route to Q[0].
	var dist = []; //contains the value from the 
	var targetNode = roadMapGraph.getNodeByName(target);

	//initialize the dist and previous arrays.
	for (var i = 0; i < roadMapGraph.nodes.length; i++) {
		dist.push(Number.MAX_VALUE);
		previous.push(null);
	}

	//Used in the first run in dijkstra's
	dist[roadMapGraph.nodes.indexOf(roadMapGraph.getNodeByName(source))] = 0;

	//Q is the datastrukture that controls the visited nodes. it is identical with roadMapGraph.
	var Q = [];
	Q = Q.concat(roadMapGraph.nodes);


	//Main loop in dijkstra's
	while (areAllNodesVisited(Q) === false){
		var u = smallestDistanceNotVisited(Q, dist);
		var uIndex = Q.indexOf(u);
		Q[uIndex].visited = true;

		//if node is unavaible then quit the alorithm.
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

	//Get the whole node path (from -> to)
	var path = makePath(targetNode, previous, Q);
	return path;
}

/*Creates the sortest paths, and returns it as a Array.*/
function makePath(target, previous, Q){
	var path = [];
	var u = target;
	while (previous[Q.indexOf(u)] !== null){
		path.push(u);
		u = previous[Q.indexOf(u)];
	}
	//To get the full path we include the source
	u = Q[Q.indexOf(u)];
	path.push(u);
	return path;
}

/*Gets the distance(weight) between node u and node v, returns the weight
  of the edge.*/
function distBetween(u, v){
	for (var i = 0; i < u.edges.length; i++) {
		if(u.edges[i].getNextNode(u) == v ){
			return u.edges[i].weight;
		}
	}
}

/*if all nodes are visited it returns true, otherwise it returns true*/
function areAllNodesVisited(Q){
	var visited = true;
	for (var i = 0; i < Q.length; i++) {
		if (Q[i].visited === false) {
			visited = false;
			return visited;
		}
	}
	return visited;
}

/*Find the smallest value in dist and return the node in Q with the same index. */
function smallestDistanceNotVisited(Q, dist){
	var min = Number.MAX_VALUE;
	for (var i = 0; i < Q.length; i++) {
		if (Q[i].visited === false && min >= dist[i]) {
			min = dist[i];
		}
	}
	return Q[dist.indexOf(min)];
}

/*Returns the smallest value in the array*/
Array.prototype.min = function(){
	return Math.min.apply(null, this);
};

/*Return the maximum value in the array*/
Array.prototype.max = function(){
	return Math.max.apply(null, this);
};