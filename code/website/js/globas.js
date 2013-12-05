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
var boothCount = 0;

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

function clickMarker(event)
{
        var curX = event.latLng.lng();
        var curY = event.latLng.lat();

        var smallestCross = null;
        var index = 0;
        var value = false;
        for (var i = 0; i < allPoints.length -1; i++) {

            if(allPoints[i].name == allPoints[i+1].name)
                {
                    continue;
                }
            console.log("changes " + allPoints[i].name);
            var dxc = curX - allPoints[i].location.lng();
            var dyc = curY - allPoints[i].location.lat();

            var dxl = allPoints[i+1].location.lng() - allPoints[i].location.lng();
            var dyl = allPoints[i+1].location.lat() - allPoints[i].location.lat();

            var cross = dxc * dyl - dyc * dxl;
            //console.log("Index: " + i);
            if(smallestCross == null)
            {
                smallestCross = cross;
                //console.log("sCross: " + smallestCross.toString());
                value = true;
                index = i;
            }
            else
            {
                //console.log("AbsCross: " + Math.abs(cross).toString() + " AbsSmall: " + Math.abs(smallestCross).toString());
                if (Math.abs(cross) < Math.abs(smallestCross)) 
                {
                    //console.log("Cross @" + dxl.toString() + " Index @" + dyl.toString());
                    if(Math.abs(dxl) >= Math.abs(dyl))
                    {
                        value = dxl > 0 ?
                        allPoints[i].location.lng() <= curX && curX <= allPoints[i+1].location.lng() :
                        allPoints[i+1].location.lng() <= curX && curX <= allPoints[i].location.lng();
                    }
                    else
                    {
                        value = dyl > 0 ?
                        allPoints[i].location.lat() <= curY && curY <= allPoints[i+1].location.lat() :
                        allPoints[i+1].location.lat() <= curY && curY <= allPoints[i].location.lat();
                    }
                    if(value)
                    {
                        index = i;
                        break;
                    }
                    smallestCross = cross;
                }
            }
        };
        console.log("Cross @" + value.toString() + " Index @" + index.toString());

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
        allPoints.splice(index+2,0,insertNode);
        var marker = new google.maps.Marker({
            position: event.latLng,
            map: map,
        });
        allMarkers.push(marker);

        //HACK
        //allPoints.splice(index, 0, allPoints[index]);
        redrawGraph();


        smallestCross = null;
        value = null;
}

