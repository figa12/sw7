//The settings for the custom map
var floorPlanTypeOptions = {
    getTileUrl: function(coord, zoom) {
    var normalizedCoord = getNormalizedCoord(coord, zoom);
        if (!normalizedCoord) {
        return null;
    }
    var url = 'http://figz.dk/dl/FloorPlan/';
    var bound = Math.pow(2, zoom);
    var bitShiftY = (1 << zoom) - (normalizedCoord.y - 1);
    return url + zoom + '/' + normalizedCoord.x +'/'+ bitShiftY +'.png';
},

tileSize: new google.maps.Size(256, 256),
maxZoom: 6,
minZoom: 2,
name: 'Floorplan'
};

// Normalizes the coords that tiles repeat across the x axis (horizontally)
// like the standard Google map tiles.
function getNormalizedCoord(coord, zoom) {
    var y = coord.y;
    var x = coord.x;

    // tile range in one direction range is dependent on zoom level
    // 0 = 1 tile, 1 = 2 tiles, 2 = 4 tiles, 3 = 8 tiles, etc
    var tileRange = 1 << zoom;

    // don't repeat across y-axis (vertically)
    if (y < 0 || y >= tileRange) {
        //y = (y % tileRange + tileRange) % tileRange;
        return null;
    }

    // repeat across x-axis
    if (x < 0 || x >= tileRange) {
        //return null;
        x = (x % tileRange + tileRange) % tileRange;
    }

    return {
        x: x,
        y: y
    };
}
//initialization of floorplan
var floorPlanMapType = new google.maps.ImageMapType(floorPlanTypeOptions);

var map;

var count = 0;
var markerIndex = 0;

//function to placemarkers, this is used for debug for easy calculation of coords
function placeMarker(location) {
    var marker = new google.maps.Marker({
        position: location,
        map: map,
    });
    allMarkers.push(marker);
    //Nodes for making the graph and polyline
    count++;
    var tempStore = new Node("node"+count.toString(), new google.maps.LatLng(location.lat(), location.lng()));
    console.log("markerIndex is: " + markerIndex);
    //var x = new Node(allPoints[markerIndex-1].name, allPoints[markerIndex-1].location);
    var x = allPoints[markerIndex-1];

    //DEBUG!
    //console.log(x);
    allPoints.splice(markerIndex, 0, tempStore);
    allPoints.splice(markerIndex+1, 0, allPoints[markerIndex]);

    //Deside what way it should work HACK!!
    markerIndex = allPoints.length;
    console.log("markerIndex is: " + markerIndex);


    google.maps.event.addListener(marker, 'click', function(event) {
       for (var i = allPoints.length - 1; i >= 0; i--) {
            if(allPoints[i].location.equals(marker.getPosition()))
            {
                markerIndex = i;
                console.log("Marker@@@ " + markerIndex);
                break;
            }
        }; 
    });

    google.maps.event.addListener(marker, 'rightclick', function(event) {

       for (var i = allPoints.length - 1; i >= 0; i--) {
            if(allPoints[i].location.equals(marker.getPosition()))
            {
                console.log("rightclick@@@ " + allPoints[i].name);
                var tempMarkerArray = [];
                tempMarkerArray.push(allPoints[i].name);
                removeAllMarkers(tempMarkerArray);
                markerIndex = allPoints.length;
                break;
            }
        }; 
        redrawGraph();
    });

    //graph used to find a route between points
    redrawGraph();
    //routePath.setMap(map);
}

var boothLine = null;
var basepoint = null;
function placeBooth (location) {

    var bounds = new google.maps.LatLngBounds(
      location,
      new google.maps.LatLng(location.lat()+10, location.lng()+10));
    var booth = new google.maps.Rectangle({
        strokeColor: '#FF0000',
        strokeOpacity: 0.8,
        strokeWeight: 2,
        fillColor: '#FF0000',
        fillOpacity: 0.35,
        map:map,
        bounds: bounds,
        editable: true,
        draggable: true
    });
    
    var rectOptsLocked = {
        strokeColor: '#00FF00',
        fillColor: '#00FF00',
        editable: false,
        draggable: false
    };
    var rectOptsUnlocked = {
        strokeColor: '#FF0000',
        fillColor: '#FF0000',
        editable: true,
        draggable: true
    };
    var rectOptsRemoved = {
        strokeColor: '#0000FF',
        fillColor: '#0000FF',
        editable: false,
        draggable: false,
        clickable: false
    };

    google.maps.event.addListener(booth, 'click', function(event) {
        if (booth.getDraggable()) {
            booth.setDraggable(false);
            booth.setOptions(rectOptsLocked);
            basepoint = event.latLng;

            var tmpBooth = new Booth("booth"+boothCount.toString(),booth);
            boothCount++;
            allBooths.push(tmpBooth);
            console.log("Booth Locked " + tmpBooth.boothId);
        }
        else
        {
            for (var i = allBooths.length - 1; i >= 0; i--) {
                if(allBooths[i].rect.getBounds().contains(event.latLng))
                {
                    removeAllRefsForBooth(allBooths[i].boothId);
                    allBooths.splice(i,1);
                    break;
                }
            };
            booth.setOptions(rectOptsUnlocked);
            mode = modeEnum.placeBooth;
        }
    });

    google.maps.event.addListener(booth, 'rightclick', function(event) {
        if (booth.getDraggable()) {
            google.maps.event.clearInstanceListeners(booth);
            mode = modeEnum.placeBooth;

            booth.setOptions(rectOptsRemoved);
            booth.setMap(null);
            if(boothEntries != null) { boothEntries.setMap(null); }
            redrawGraph();
        }
        else
        {
            basepoint = event.latLng;
            mode = modeEnum.bindBooth;

            google.maps.event.addListener(map,'mousemove', function(event) {
                if(mode == modeEnum.bindBooth)
                {
                    redrawLine(event);
                }
                else
                {
                    basepoint=null;
                    if(boothLine != null) { boothLine.setMap(null);}
                    boothLine=null;
                }
            });
        }
  });

    google.maps.event.addListener(booth, 'mouseover', function(event) {
        if(booth.getDraggable() == false)
        {
            if(mode != modeEnum.bindBooth)
            {
                var centerPoint;
                var boothIndex;
                for (var i = allBooths.length - 1; i >= 0; i--) {
                    if(allBooths[i].rect.getBounds().contains(event.latLng))
                    {
                        centerPoint = allBooths[i].rect.getBounds().getCenter();
                        boothIndex = allBooths[i].boothId;
                        break;
                    }
                };

                var newboothMapperPoints = [];
                newboothMapperPoints.push(centerPoint);
                for (var i = boothMarker.length - 1; i >= 0; i--) {
                    if(boothMarker[i].boothId == boothIndex)
                    {
                        for (var x = allPoints.length - 1; x >= 0; x--) {
                            if(allPoints[x].name == boothMarker[i].markerId)
                            {
                                newboothMapperPoints.push(allPoints[x].location);
                                newboothMapperPoints.push(centerPoint);
                            }
                        };
                    }
                };
                if(boothEntries != null) { boothEntries.setMap(null); }

                boothEntries = new google.maps.Polyline({
                    path:newboothMapperPoints,
                    strokeColor:"#FF00DD",
                    strokeOpacity:0.5,
                    strokeWeight:4,
                    clickable: false,
                    zIndex: 2
                });
                boothEntries.setMap(map);
            }
        }
    });

    google.maps.event.addListener(booth, 'mouseout', function(event) {
        if(booth.getDraggable() == false)
        {
            if(mode != modeEnum.bindBooth)
            {
                if(boothEntries != null) { boothEntries.setMap(null); }
            }
        }
    });
}

//initialize the map
function initialize() {
    var myCenter = new google.maps.LatLng(0, 0);
    var mapOptions = {
        center: myCenter,
        zoom: 0,
        streetViewControl: false,
        mapTypeControlOptions: {
            mapTypeIds: ['floorPlan']
        }
    };

map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
map.mapTypes.set('floorPlan', floorPlanMapType);
map.setMapTypeId('floorPlan');
map.fitBounds(new google.maps.LatLngBounds());

redoMapListeners();
}

function redrawLine(event){
    if(basepoint != null)
    {
        if(boothLine != null)
            boothLine.setMap(null);

        boothMapperPoints = [];
        boothMapperPoints.push(basepoint);
        boothMapperPoints.push(event.latLng)
        boothLine = new google.maps.Polyline({
            path:boothMapperPoints,
            strokeColor:"#00FFFF",
            strokeOpacity:0.5,
            strokeWeight:4,
            clickable: false
        });

        
        boothLine.setMap(map);
    }
}


function redoMapListeners(){
    //onClick event on map to place markers
    google.maps.event.addListener(map, 'click', function(event) {
        if(document.getElementById("typeSelect").value == "marker")
        {
            mode = modeEnum.placeMarker;
            console.log("Marker @" + event.latLng.toString());
            placeMarker(event.latLng);
        }
        else if(document.getElementById("typeSelect").value == "booth")
        {
            if(mode != modeEnum.bindBooth)
            {
                mode = modeEnum.placeBooth;
                console.log("Booth @" + event.latLng.toString());
                placeBooth(event.latLng);
            }
        }
    });
}
function removeAllRefsForBooth(boothIndex) {
    var markerPoints = [];
    for (var i = boothMarker.length - 1; i >= 0; i--) {
        if(boothMarker[i].boothId == boothIndex)
        {
            markerPoints.push(boothMarker[i].markerId);
            boothMarker.splice(i,1);
        }
    };
    removeAllMarkers(markerPoints);
}

function removeAllMarkers(markerArray) {
    for (var i = allPoints.length - 1; i >= 0; i--) {
        for (var x = markerArray.length - 1; x >= 0; x--) {
            if(markerArray[x] == allPoints[i].name)
            {
                for (var y = allMarkers.length - 1; y >= 0; y--) {
                    if(allMarkers[y].getPosition().equals(allPoints[i].location))
                    {
                        console.log("RemoveMarker @" + x.toString());
                        allMarkers[y].setMap(null);
                        allMarkers.splice(y,1)
                    }
                };
                allPoints.splice(i,1);
            }
        };
    };
}