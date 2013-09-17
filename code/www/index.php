<!DOCTYPE html>
<html>
  <head>
    <title>Image map types</title>
    <link href="http://code.google.com//apis/maps/documentation/javascript/examples/default.css" rel="stylesheet" type="text/css">
    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false"></script>
    <script>
var floorPlanTypeOptions = {
  getTileUrl: function(coord, zoom) {
      var normalizedCoord = getNormalizedCoord(coord, zoom);
      if (!normalizedCoord) {
        return null;
      }
      var bound = Math.pow(2, zoom);
      return '' + zoom + '/' + normalizedCoord.x +'/'+ normalizedCoord.y +'.png';
  },
  tileSize: new google.maps.Size(256, 256),
  maxZoom: 3,
  minZoom: 2,
  radius: 1738000,
  name: 'Floorplan'
};

var floorPlanMapType = new google.maps.ImageMapType(floorPlanTypeOptions);

function initialize() {
  var myLatlng = new google.maps.LatLng(0, 0);
  var mapOptions = {
    center: myLatlng,
    zoom: 2,
    streetViewControl: false,
    mapTypeControlOptions: {
      mapTypeIds: ['floorPlan']
    }
  };

  var map = new google.maps.Map(document.getElementById('map-canvas'),
      mapOptions);
  map.mapTypes.set('floorPlan', floorPlanMapType);
  map.setMapTypeId('floorPlan');
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
    return null;
  }

  // repeat across x-axis
  if (x < 0 || x >= tileRange) {
    x = (x % tileRange + tileRange) % tileRange;
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
    <div id="map-canvas"></div>
  </body>
</html>