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
var curBooth;

function uniqeBoothName (boothName) {
    for (var i = allBooths.length - 1; i >= 0; i--) {
        if(allBooths[i].boothId == boothName)
            return false;
    };
    return true;
}

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
    if(markerIndex == allPoints.length)
    {
        console.log("Total length");
        allPoints.splice(markerIndex, 0, tempStore);
        allPoints.splice(markerIndex+1, 0, allPoints[markerIndex]);
    }
    else
    {
        console.log("Not total length");
        allPoints.splice(markerIndex+1, 0, tempStore);
        allPoints.splice(markerIndex+2, 0, allPoints[markerIndex]);
    }

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

            //MAKE BOOTH!
            curBooth = booth;
            createBooth();
        }
        else
        {
            for (var i = allBooths.length - 1; i >= 0; i--) {
                if(getBounds[i].rect.getBounds().contains(event.latLng))
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

function setBoothName(){
    var boothName = "TestBooth";
    $("#hidden-input").val(boothName);
    $("#dialogForm").dialog("open");
}

function loadCompanies(){
    $.getJSON('loadCompanies.php', function(jsonData) {
        allCompanies = jsonData;
        console.log(allCompanies);
    });
}

function loadCategories(){
    $.getJSON('loadCategories.php', function(jsonData) {
        allCategories = jsonData;
        console.log(allCategories);
    });
}

function loadAllExhibitions() {
    $.getJSON('loadExhibits.php', function(jsonData) {
        allExhibts = jsonData;
        $("#exhibSelect").append($("<option></option>")
        .attr("value",null)
        .text(""));
        for (var i = 0; i < allExhibts.length; i++) {
            $("#exhibSelect").append($("<option></option>")
                .attr("value",i)
                .text(allExhibts[i].name));
        };
        console.log(allExhibts);
        $("#exhibSelect").change(function() {
            var name = $("#exhibSelect option[value='"+this.value+"']").text();
            $("#exhibitionName").text("Exhibition: "+name);
            $("#finalexhibitionName").text(name);
            loadExhib();
        });
    });
}

function loadExhib() {
    console.log("START LOAD...");
    var namevar = $("#finalexhibitionName").text();
    console.log(namevar);
    $.getJSON('loadExhibtion.php', {name: namevar}, function(json) {
        console.log(json);
        exhib = new Exhibition(
            json["exhibition"]["dbid"],
            json["exhibition"]["name"],
            json["exhibition"]["address"],
            json["exhibition"]["zip"],
            json["exhibition"]["country"],
            json["exhibition"]["description"],
            json["exhibition"]["logo"]);
        for (var i = json["coordinates"].length - 1; i >= 0; i--) {
            if(json["coordinates"][i]["isroad"]) {
                var topLeftx = json["coordinates"][i]["x"];
                var topLefty = json["coordinates"][i]["y"];
                var marker = new google.maps.Marker({
                    position: new google.maps.LatLng(topLefty,topLeftx),
                    map: map,
                });
                placeMarker(new google.maps.LatLng(topLefty,topLeftx));
            }
        };


        for (var i = json["booths"].length - 1; i >= 0; i--) {
            //var tmpBooth = new Booth(null,retVal,curBooth,$("#boothDescription").val());
            var topLeftx = null;
            var topLefty = null;
            var bottomRightx = null;
            var bottomRighty = null;
            
            for (var x = json["coordinates"].length - 1; x >= 0; x--) {
                console.log(json["coordinates"][x]["isroad"]);
                if(json["coordinates"][x]["isroad"]) {continue;}
                if(json["coordinates"][x]["boothid"] == null) {continue;}
                if(json["coordinates"][x]["boothid"] == json["booths"][i]["dbid"]) {
                    
                    if(topLeftx == null) {
                        topLeftx = json["coordinates"][x]["x"];
                        topLefty = json["coordinates"][x]["y"];
                    }
                    else {
                        if(json["coordinates"][x]["x"] > topLeftx) {
                            bottomRightx = topLeftx;
                            bottomRighty = topLefty;
                            topLeftx = json["coordinates"][x]["x"];
                            topLefty = json["coordinates"][x]["y"];
                        }
                        else {
                            bottomRightx = json["coordinates"][x]["x"];
                            bottomRighty = json["coordinates"][x]["y"];
                            break;
                        }
                    }
                }
            }
            var bounds = new google.maps.LatLngBounds(
                new google.maps.LatLng(bottomRighty, bottomRightx),
                new google.maps.LatLng(topLefty,topLeftx));
            var rect = new google.maps.Rectangle({
                strokeColor: '#00FF00',
                strokeOpacity: 0.8,
                strokeWeight: 2,
                fillColor: '#00FF00',
                fillOpacity: 0.35,
                map:map,
                bounds: bounds,
                editable: false,
                draggable: false
            });
            //var tmpBooth = new Booth(null,retVal,curBooth,$("#boothDescription").val());
            var tmpBooth = new Booth(json["booths"][i]["dbid"],
                json["booths"][i]["name"],
                rect,
                json["booths"][i]["description"]);
            allBooths.push(tmpBooth);
        }
    });

        /*
        json["booths"] //
        json["edges"] //
        json["coordinates"]*/ 
        //roadMapGraphFinal.nodes = json["nodes"];
        //roadMapGraphFinal.edges = json["edges"];
        //allBooths = json["nodes"];
        //After correct insert into AllMarkers, AllPointers, boothCompany, boothCategory
        //redrawGraph();
    //roadMapGraphFinal.nodes and edges
    //allBooths, rectangles and stuff also, booth relations
}

var exhibLogoName;

var retVal;
//initialize the map
function initialize() {

    loadAllExhibitions();
    loadCompanies();
    loadCategories();
    $("#boothfinalName").hide();
    $("#exhibfinalName").hide();
    $("#finalexhibitionName").hide();

    $("#exhibDialog").dialog({
        autoOpen: false,
        modal : true,
        show:{
            effect:"clip",
            duration: 300
        },
        hide: {
            effect:"clip",
            duration: 300
        },
        buttons: {
            "Done": function() {
                $("#exhibForm").submit();
                $(this).dialog("close");
            },
            Cancel: function () {
                $(this).dialog("close");
            }
        }
    });

    $("#boothDialog").dialog({
        autoOpen: false,
        modal : true,
        show:{
            effect:"clip",
            duration: 300
        },
        hide: {
            effect:"clip",
            duration: 300
        },
        buttons: {
            "Done": function() {
                $("#boothForm").submit();
                $(this).dialog("close");
            },
            Cancel: function () {
                $(this).dialog("close");
            }
        }
    });

    $("#exhibForm").ajaxForm({
        success: function(msg) {
            exhibLogoName = msg;
            updateExhib();
        }
    });

    $("#boothForm").ajaxForm({
        success: function(msg) {

            retVal = $("#boothName").val();

            var boothIsValid = false;
            while(!boothIsValid)
            {
                boothIsValid = uniqeBoothName(retVal);
                if(boothIsValid) {break;}
                retVal += "(1)";
            }

            console.log("Booth name is: " + retVal);


            var tmpBooth = new Booth(null,retVal,curBooth,$("#boothDescription").val());
            boothCount++;
            allBooths.push(tmpBooth);
            boothCategories.push(new boothCategory(tmpBooth.boothId,$("#boothCategory").val()));
            boothCompanies.push(new boothCompany(tmpBooth.boothId,$("#boothCompanyName").val()));
            console.log("Booth Locked " + tmpBooth.boothId);

            var match = false;
            for (var i = allCompanies.length - 1; i >= 0; i--) {
                if(allCompanies[i].name == $("#boothCompanyName").val())
                {
                    match = true;
                }
            };
            if(match == false)
            {
                allCompanies.push(new Company(null,$("#boothCompanyName").val(),msg));
                companiesToInsert.push(new Company(null,$("#boothCompanyName").val(),msg));
            }

            match = false;
            for (var i = allCategories.length - 1; i >= 0; i--) {
                if(allCategories[i].name == $("#boothCategory").val())
                {
                    match = true;
                }
            };
            if(match == false)
            {
                allCategories.push(new Category(null,$("#boothCategory").val()));
                categoriesToInsert.push(new Category(null,$("#boothCategory").val()));
            }
        }
    });

    $("#exhibLogo").change(function() {
        if($(this).val() != ""){
            $("#exhibfinalName").val(encodeURIComponent($("#exhibName").val()));
            //$("#exhibForm").submit();
            //uploadExhibLogo();
        }
    });

    $("#boothCompanyLogo").change(function() {
        if($(this).val() != ""){
            $("#boothfinalName").val(encodeURIComponent($("#finalexhibitionName").text()+$("#boothCompanyName").val()));
            console.log($("#boothfinalName").val());
            //$("#boothForm").submit();
            //uploadBoothLogo();
        }
    });

    $("#boothCompanyName").change(function() {
        for (var i = allCompanies.length - 1; i >= 0; i--) {
            if($("#boothCompanyName").val() == allCompanies[i].name)
            {
                $("#boothCompanyLogo").hide();
                break;
            }
            else
            {
                $("#boothCompanyLogo").show();
                break;
            }
        };
    });

    var myCenter = new google.maps.LatLng(0, 0);
    var mapOptions = {
        center: myCenter,
        zoom: 3,
        streetViewControl: false,
        mapTypeControlOptions: {
            mapTypeIds: ['floorPlan']
        }
    };

    map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
    map.mapTypes.set('floorPlan', floorPlanMapType);
    map.setMapTypeId('floorPlan');
    //map.fitBounds(new google.maps.LatLngBounds());

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
    var tempIndex = null;
    for (var i = allPoints.length - 1; i >= 0; i--) {
        for (var x = markerArray.length - 1; x >= 0; x--) {
            if(markerArray[x] == allPoints[i].name)
            {
                for (var y = allMarkers.length - 1; y >= 0; y--) {
                    if(allMarkers[y].getPosition().equals(allPoints[i].location))
                    {
                        console.log("RemoveMarker @" + x.toString());
                        allMarkers[y].setMap(null);
                        allMarkers.splice(y,1);
                    }
                };
                allPoints.splice(i,1);
                if (tempIndex == null)
                {
                    console.log("TempIndex: " + i);
                    tempIndex = i;
                    allPoints.splice(tempIndex,1);
                }
            }
        };
    };    
}

function createExhibition() {
    initialize();
    categoriesToInsert = [];
    companiesToInsert = [];
    $("#exhibName").val("");
    $("#exhibAddress").val("");
    $("#exhibZip").val("");
    $("#exhibCountry").val("");
    $("#exhibDescription").val("");
    $("#exhibLogo").val("");
    $("#exhibDialog").dialog("open");
}

function createBooth() {
    $("#boothName").val("");
    $("#boothDescription").val("");
    $("#boothCategory").val("");
    var tempCatArray = [];
    for (var i = allCategories.length - 1; i >= 0; i--) {
        tempCatArray.push(allCategories[i].name);
    };
    $("#boothCategory").autocomplete({
        source: tempCatArray
    });
    $("#boothCompanyName").val("");
    var tempCompanyArray = [];
    for (var i = allCompanies.length - 1; i >= 0; i--) {
        tempCompanyArray.push(allCompanies[i].name);
    };
    $("#boothCompanyName").autocomplete({
        source: tempCompanyArray
    });
    $("#boothCompanyLogo").val("");
    $("#boothDialog").dialog("open");
}

function updateExhib() {
    exhib = new Exhibition(
        null,
        $("#exhibName").val(),
        $("#exhibAddress").val(),
        $("#exhibZip").val(),
        $("#exhibCountry").val(),
        $("#exhibDescription").val(),
        exhibLogoName);
    $("#exhibitionName").text("Exhibition: "+exhib.name);
    $("#finalexhibitionName").text(exhib.name);
}

function JsonNode(name, point, boothId){
    this.name = name;
    this.location = point;
    this.boothId = boothId;
}
function JsonEdge(from,to,weight){
    this.from = from;
    this.to = to;
    this.weight = weight;
}
function JsonBooth(id,dbid,desc,topLeft,bottomRight,category,company,newComp,newCat){
    this.dbid = dbid;
    this.id = id;
    this.description = desc;
    this.topLeft = topLeft;
    this.bottomRight = bottomRight;
    this.category = category;
    this.company = company;
    this.newComp = newComp;
    this.newCat = newCat;
}
function JsonPoint(x,y)
{
    this.x = x;
    this.y = y;
}

function getJsonElements(nodesArray, edgeArray, boothArray, exhibit,companiesArray,categoriesArray)
{
    var newNodes = [];
    for (var i = nodesArray.length - 1; i >= 0; i--) {
        var location = new JsonPoint(nodesArray[i].location.lng(), nodesArray[i].location.lat())
        var boothBind = null;
        for (var q = boothMarker.length - 1; q >= 0; q--) {
            if(boothMarker[q].markerId == nodesArray[i].name)
            {
                boothBind = boothMarker[q].boothId;
                break;
            }
        };
        var newBooth = new JsonNode(nodesArray[i].name, location, boothBind);
        newBooth.edges = newEdges;
        newNodes.push(newBooth);
    };

    var newEdges = [];
    for (var x = edgeArray.length - 1; x >= 0; x--) {
        var newEdge = new JsonEdge(edgeArray[x].from.name, edgeArray[x].to.name, edgeArray[x].weight);
        newEdges.push(newEdge);
    };

    var newBooths = [];
    for (var i = boothArray.length - 1; i >= 0; i--) {
        var topLeft = new JsonPoint(boothArray[i].rect.getBounds().getSouthWest().lng(), boothArray[i].rect.getBounds().getNorthEast().lat());
        var bottomRight = new JsonPoint(boothArray[i].rect.getBounds().getNorthEast().lng(), boothArray[i].rect.getBounds().getSouthWest().lat());
        var newCat = false;
        var newComp = false;

        var curBoothCompany = null;
        for (var z = boothCompanies.length - 1; z >= 0; z--) {
            if(boothCompanies[z].boothId == boothArray[i].boothId)
            {
                for (var x = companiesArray.length - 1; x >= 0; x--) {
                    if(companiesArray[x].name == boothCompanies[z].companyId)
                    {
                        for (var k = companiesToInsert.length - 1; k >= 0; k--) {
                            if(companiesToInsert[k].name == companiesArray[x].name)
                            {
                                curBoothCompany = k;
                                newComp = true;
                            }
                        };

                        if(newComp != true)
                            curBoothCompany = companiesArray[x].id;
                    }
                };
            }
        };


        var curBoothCategory = null;
        for (var z = boothCategories.length - 1; z >= 0; z--) {
            if(boothCategories[z].boothId == boothArray[i].boothId)
            {
                for (var x = categoriesArray.length - 1; x >= 0; x--) {
                    if(categoriesArray[x].name == boothCategories[z].categoryId)
                    {
                        for (var k = categoriesToInsert.length - 1; k >= 0; k--) {
                            if(categoriesToInsert[k].name == categoriesArray[x].name)
                            {
                                curBoothCategory = k;
                                newCat = true;
                            }
                        };

                        if(newCat != true)
                            curBoothCategory = categoriesArray[x].id;
                    }
                };
            }
        };


        var newBooth = new JsonBooth(boothArray[i].dbid, boothArray[i].boothId, boothArray[i].description, topLeft, bottomRight,curBoothCategory,curBoothCompany,newComp,newCat);
        newBooths.push(newBooth);
    };

    var tmpExhib = $("exhibSelect").val() == "" ? exhibit : null;

    return [newNodes, newEdges, newBooths, exhibit, companiesArray, categoriesArray];
}


function makeJson()
{
    //categoriesToInsert
    //companiesToInsert
    var myJson = getJsonElements(roadMapGraphFinal.nodes, roadMapGraphFinal.edges, allBooths, exhib,allCompanies,allCategories);
    console.log("Nodes: " + JSON.stringify(myJson[0]));
    var myNodes = JSON.stringify(myJson[0]);
    console.log("Edges: " + JSON.stringify(myJson[1]));
    var myEdges = JSON.stringify(myJson[1]);
    console.log("Booths: " + JSON.stringify(myJson[2]));
    var myBooths = JSON.stringify(myJson[2]);
    console.log("Exhibition: " + JSON.stringify(myJson[3]));
    var myExhib = JSON.stringify(myJson[3]);
    console.log("Companies: " + JSON.stringify(companiesToInsert));
    var myCompanies = JSON.stringify(companiesToInsert);
    console.log("Categories: " + JSON.stringify(categoriesToInsert));
    var myCategories = JSON.stringify(categoriesToInsert);

    $.ajax({
      url: "mapSide.php",
      type: "POST",
      data: { nodes: myNodes, edges: myEdges, booths: myBooths, exhibit: myExhib, companies: myCompanies, categories:myCategories}
    }).done(function(msg) {
        alert(msg);
    });
}