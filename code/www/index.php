<!DOCTYPE html>
<html>
  <head>
    <title>Image map types</title>
    <link href="http://code.google.com//apis/maps/documentation/javascript/examples/default.css" rel="stylesheet" type="text/css">
    <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDY0kkJiTPVd2U7aTOAwhc9ySH6oHxOIYM&sensor=false"></script>
    <script>
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
    radius: 1738000,
    name: 'Floorplan'
    };

    var floorPlanMapType = new google.maps.ImageMapType(floorPlanTypeOptions);

    var first = new google.maps.LatLng(47.51,84.37);
    var second = new google.maps.LatLng(47.98,22.5);
    var third = new google.maps.LatLng(48.92,-118.125);
    var fourth = new google.maps.LatLng(-74.59,-118.82);
    var fifth = new google.maps.LatLng(-75.32,24.50);

    var map;
    
    var points = [first,second,third,fourth,fifth,second];
    var roadpath = new google.maps.Polyline({
        path:points,
        strokeColor:"#0000FF",
        strokeOpacity:0.8,
        strokeWeight:1.5
    });

    function placeMarker(location) {
        var marker = new google.maps.Marker({
            position: location,
            map: map,
        });
        var infowindow = new google.maps.InfoWindow({
            content: 'Latitude: ' + location.lat() + '<br>Longitude: ' + location.lng()
        });

        infowindow.open(map,marker);
    }


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

    google.maps.event.addListener(map, 'click', 
        function(event) {
            placeMarker(event.latLng);
        });
    roadpath.setMap(map);
    }

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
            return null;
            //x = (x % tileRange + tileRange) % tileRange;
        }

        return {
            x: x,
            y: y
        };
    }

    google.maps.event.addDomListener(window, 'load', initialize);

    </script>
  </head>
  <body>
    <div id="map-canvas" ></div>
  </body>
</html>