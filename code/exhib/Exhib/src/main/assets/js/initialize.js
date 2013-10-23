function CustomMapType() {
}
CustomMapType.prototype.tileSize = new google.maps.Size(256,256);
CustomMapType.prototype.maxZoom = 2;


CustomMapType.prototype.getTile = function(coord, zoom, ownerDocument) {
    var div = ownerDocument.createElement('DIV');
    var baseURL = ownerDocument.URL.substring(0, ownerDocument.URL.lastIndexOf("/") + 1);
    var url = getTileUrl(coord, zoom);
    if(!url){
        url = "none.png";
    }
    baseURL += url;
    div.style.width = this.tileSize.width + 'px';
    div.style.height = this.tileSize.height + 'px';
    div.style.backgroundImage = 'url(' + baseURL + ')';
    return div;
};

function getTileUrl(coord, zoom){
    var normalizedCoord = getNormalizedCoord(coord, zoom);
        if (!normalizedCoord) {
        return null;
    }
    return '' + zoom + '/' + normalizedCoord.x +'/'+normalizedCoord.y+'.png';
}

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

CustomMapType.prototype.name = "Custom";
CustomMapType.prototype.alt = "Tile Coordinate Map Type";
var map;
var CustomMapType = new CustomMapType();

function initialize() {
  var mapOptions = {
      minZoom: 0,
      maxZoom: 2,
      isPng: true,
      mapTypeControl: false,
      streetViewControl: false,
      center: new google.maps.LatLng(65.07,-56.08),
      zoom: 2,
      mapTypeControlOptions: {
      mapTypeIds: ['custom', google.maps.MapTypeId.ROADMAP],
      style: google.maps.MapTypeControlStyle.DROPDOWN_MENU
    }
  };
map = new google.maps.Map(document.getElementById("map_canvas"),mapOptions);
map.mapTypes.set('custom',CustomMapType);
map.setMapTypeId('custom');

roadPath.setMap(map);
routePath.setMap(map);
}