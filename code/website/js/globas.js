//Nodes for making the graph and polyline
//var first = new Node("first", new google.maps.LatLng(47.51,84.37));
//var second = new Node("second", new google.maps.LatLng(47.98,22.5));
//var third = new Node("third", new google.maps.LatLng(48.92,-118.125));
//var fourth = new Node("fourth", new google.maps.LatLng(-74.59,-118.82));

//var allPoints = [first, second, third, second, third, fourth];

var allPoints = [];
var allBooths = [];
var allMarkers = [];
var allCategories = [];
var allExhibts = [];
var categoriesToInsert = []
var companiesToInsert = []
var allCompanies = [];
var boothCategories = [];
var boothCompanies = [];
var exhib;
var roadMapPoints = [];
var routeMapPoints = [];
var boothMarker = [];
var boothEntries;
var guidePathPoints;
var roadMapGraphFinal;
var roadPath;
var boothPlacement;
var modeEnum = {
    placeRoad : "road",
    placeBooth : "booth",
    bindBooth : "bind"
}
var mode = modeEnum.placeRoad;

//setting for drawing the polyline, also called the roadmap

function Exhibition(id,name, address, zip, country, description, logo){
    this.id = id;
    this.name = name;
    this.address = address;
    this.zip = zip;
    this.country = country;
    this.description = description;
    this.logo = logo;
}

function Category(id, name){
    this.id = id;
    this.name = name;
}

function boothCategory(boothId,categoryId){
    this.boothId = boothId;
    this.categoryId = categoryId;
}
function boothCompany(boothId,companyId){
    this.boothId = boothId;
    this.companyId = companyId;
}

function Company(id, name, logo){
    this.id = id;
    this.name = name;
    this.logo = logo;
}

function redrawGraph()
{
    if(roadPath != null)
    {
        roadPath.setMap(null);
    }
    roadMapPoints = [];
    for (var i = 0; i < allPoints.length; i++) {
        roadMapPoints.push(allPoints[i].location);
    }
    roadPath= new google.maps.Polyline({
        path:roadMapPoints,
        strokeColor:"#0033FF",
        strokeOpacity:0.5,
        strokeWeight:4
    });
    //console.log(allPoints);
    roadMapGraphFinal = makeGraph(allPoints);

    try {
        roadPath.setMap(map);
    } 
    catch(err) {
        //Do Nothing
    }

    try {
        booth.setMap(map);
    } 
    catch(err) {
        //Do Nothing
    }


    //ADDED EVENT FOR CLICK ON POLYLINE
    google.maps.event.addListener(roadPath, 'click', function(event) {
    if(document.getElementById("typeSelect").value == "booth")
    {
        if (mode == modeEnum.bindBooth)
        {
            if(boothEntries != null) { boothEntries.setMap(null); }
            clickMarker(event);
            mode = modeEnum.placeBooth;
        }
    }
  });

    //graph used to find a route between points
}

function calculateGraph()
{
    for (var i = 0; i < guidePathPoints.length; i++) {
        routeMapPoints.push(guidePathPoints[i].location);
    }
    var routePath = new google.maps.Polyline({
        path:routeMapPoints,
        strokeColor:"#FF0000",
        strokeOpacity:1,
        strokeWeight:1.5
    });
}

function removeLine() {
    roadPath.setMap(null);
}

//http://www.bdcc.co.uk/Gmaps/BdccGeo.js as help
function clickMarker(event)
{
    var smallestDist = null;
    var index = 0;
    for (var i = 0; i < allPoints.length -1; i++) {

        if(allPoints[i].name == allPoints[i+1].name)
            {
                continue;
            }
        console.log("changes " + allPoints[i].name);

        var first = new google.maps.LatLng(allPoints[i].location.lat(),allPoints[i].location.lng());
        var second = new google.maps.LatLng(allPoints[i+1].location.lat(),allPoints[i+1].location.lng());
        var testPath = new google.maps.Polyline({
            path:[first,second],
            strokeWeight:4
        });

        //var tolerance = Math.pow(map.getZoom(), -(map.getZoom()/50)); 
        //var tolerance = 00000000000001;
        //console.log("tolerance: " + tolerance);
        console.log("mouse point: " +event.latLng);
        console.log("first point: " +first);
        console.log("second point: " +second);
        console.log(testPath);
        var dist = bdccGeoDistanceToPolyMtrs(testPath,event.latLng);
        if(smallestDist == null) {
            smallestDist = dist;
            index = i;
        }
        else
        {
            if(dist < smallestDist) {
                smallestDist = dist;
                index = i;       
            }
        }
    };
    console.log("Index @" + index.toString());

    var boothIndex;
    for (var i = allBooths.length - 1; i >= 0; i--) {
        if(allBooths[i].rect.getBounds().contains(basepoint))
        {
            boothIndex = allBooths[i].boothId;
            var tmpBoothMarker = new BoothMarker(boothIndex, "node"+(count+1).toString());
            boothMarker.push(tmpBoothMarker);
            break;
        }
    };

    console.log("Bind @" + "node"+(count+1).toString() + " --> Booth @" + boothIndex);

    count++;
    var insertNode = new Node("node"+count.toString(), event.latLng);
    allPoints.splice(index+1,0,insertNode);
    //allPoints.splice(index+2,0,insertNode);
    var marker = new google.maps.Marker({
        position: event.latLng,
        map: map,
    });
    allMarkers.push(marker);

    //HACK
    //allPoints.splice(index, 0, allPoints[index]);
    redrawGraph();
    smallestCross = null;
}

