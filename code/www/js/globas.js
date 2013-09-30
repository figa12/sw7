//Nodes for making the graph and polyline
var first = new Node("first", new google.maps.LatLng(47.51,84.37));
var second = new Node("second", new google.maps.LatLng(47.98,22.5));
var third = new Node("third", new google.maps.LatLng(48.92,-118.125));
var fourth = new Node("fourth", new google.maps.LatLng(-74.59,-118.82));
var fifth = new Node("fifth", new google.maps.LatLng(-75.32,24.50));

var allPoints = [first, second, third, second, third, fourth, fifth, second];

//setting for drawing the polyline, also called the roadmap
var roadMapPoints = [];
for (var i = 0; i < allPoints.length; i++) {
	roadMapPoints.push(allPoints[i].location);
}
var roadpath = new google.maps.Polyline({
    path:roadMapPoints,
    strokeColor:"#0000FF",
    strokeOpacity:0.8,
    strokeWeight:1.5
});

var roadMapGraph = makeGraph(allPoints);
console.log(roadMapGraph);

//graph used to find a route between points
var guidePathPoints = shortestRoute("fourth", "fifth", roadMapGraph);