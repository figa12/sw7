//The settings for the custom map
var floorPlanTypeOptions = {
    getTileUrl: function(coord, zoom) {
    var normalizedCoord = getNormalizedCoord(coord, zoom);
        if (!normalizedCoord) {
        return null;
    }
    var bound = Math.pow(2, zoom);
    return '' + zoom + '/' + normalizedCoord.x +'/'+normalizedCoord.y+'.png';
},

tileSize: new google.maps.Size(256, 256),
maxZoom: 2,
minZoom: 0,
mapBounds: new google.maps.LatLngBounds(new google.maps.LatLng(-85.05112878, -180.0), new google.maps.LatLng(85.05112878, 179.988808672)),
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

//function to placemarkers, this is used for debug for easy calculation of coords
function placeMarker(location) {
    var marker = new google.maps.Marker({
        position: location,
        map: map,
    });
    var infowindow = new google.maps.InfoWindow({
        content: 'Latitude: ' + dist(first,second) + '<br>Longitude: ' + location.lng()
    });

    infowindow.open(map,marker);
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

//onClick event on map to place markers
google.maps.event.addListener(map, 'click', function(event) {
        placeMarker(event.latLng);
    });
roadPath.setMap(map);
routePath.setMap(map);
}




