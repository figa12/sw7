<!DOCTYPE html>
<html>
  <head>
    <title>Showing pixel and tile coordinates</title>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <meta charset="utf-8">
    <link href="/maps/documentation/javascript/examples/default.css" rel="stylesheet">
    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&key=AIzaSyBWdUoCxnvuLZy7S7Y_l2DbYcr4nrrbamM&sensor=false"></script>
    <script>
var map;
var TILE_SIZE = 256;
var chicago = new google.maps.LatLng(41.850033,-87.6500523);

function bound(value, opt_min, opt_max) {
  if (opt_min != null) value = Math.max(value, opt_min);
  if (opt_max != null) value = Math.min(value, opt_max);
  return value;
}

function degreesToRadians(deg) {
  return deg * (Math.PI / 180);
}

function radiansToDegrees(rad) {
  return rad / (Math.PI / 180);
}

/** @constructor */
function MercatorProjection() {
  this.pixelOrigin_ = new google.maps.Point(TILE_SIZE / 2,
      TILE_SIZE / 2);
  this.pixelsPerLonDegree_ = TILE_SIZE / 360;
  this.pixelsPerLonRadian_ = TILE_SIZE / (2 * Math.PI);
}

MercatorProjection.prototype.fromLatLngToPoint = function(latLng,
    opt_point) {
  var me = this;
  var point = opt_point || new google.maps.Point(0, 0);
  var origin = me.pixelOrigin_;

  point.x = origin.x + latLng.lng() * me.pixelsPerLonDegree_;

  // Truncating to 0.9999 effectively limits latitude to 89.189. This is
  // about a third of a tile past the edge of the world tile.
  var siny = bound(Math.sin(degreesToRadians(latLng.lat())), -0.9999,
      0.9999);
  point.y = origin.y + 0.5 * Math.log((1 + siny) / (1 - siny)) *
      -me.pixelsPerLonRadian_;
  return point;
};

MercatorProjection.prototype.fromPointToLatLng = function(point) {
  var me = this;
  var origin = me.pixelOrigin_;
  var lng = (point.x - origin.x) / me.pixelsPerLonDegree_;
  var latRadians = (point.y - origin.y) / -me.pixelsPerLonRadian_;
  var lat = radiansToDegrees(2 * Math.atan(Math.exp(latRadians)) -
      Math.PI / 2);
  return new google.maps.LatLng(lat, lng);
};

function createInfoWindowContent() {
  var numTiles = 1 << map.getZoom();
  var projection = new MercatorProjection();
  var worldCoordinate = projection.fromLatLngToPoint(chicago);
  var pixelCoordinate = new google.maps.Point(
      worldCoordinate.x * numTiles,
      worldCoordinate.y * numTiles);
  var tileCoordinate = new google.maps.Point(
      Math.floor(pixelCoordinate.x / TILE_SIZE),
      Math.floor(pixelCoordinate.y / TILE_SIZE));

  return [
    'Chicago, IL',
    'LatLng: ' + chicago.lat() + ' , ' + chicago.lng(),
    'World Coordinate: ' + worldCoordinate.x + ' , ' +
      worldCoordinate.y,
    'Pixel Coordinate: ' + Math.floor(pixelCoordinate.x) + ' , ' +
      Math.floor(pixelCoordinate.y),
    'Tile Coordinate: ' + tileCoordinate.x + ' , ' +
      tileCoordinate.y + ' at Zoom Level: ' + map.getZoom()
  ].join('<br>');
}

function initialize() {
  var mapOptions = {
    zoom: 3,
    center: chicago,
    mapTypeId: google.maps.MapTypeId.ROADMAP
  };

  map = new google.maps.Map(document.getElementById('map-canvas'),
      mapOptions);

  var coordInfoWindow = new google.maps.InfoWindow();
  coordInfoWindow.setContent(createInfoWindowContent());
  coordInfoWindow.setPosition(chicago);
  coordInfoWindow.open(map);

  google.maps.event.addListener(map, 'zoom_changed', function() {
    coordInfoWindow.setContent(createInfoWindowContent());
    coordInfoWindow.open(map);
  });
}

google.maps.event.addDomListener(window, 'load', initialize);

    </script>
  </head>
  <body>
    <div id="map-canvas"></div>
  </body>
</html>