var map_;
var userMarker = null;
var markers = [];
var openInfoWindows = [];
var clustering_ = false;
var clusterer;
var clustering_proposal_threshold = 1000;
var direction_points = [];
var direction_limit = 10;
var directionArray = [];
var polyArray = [];
var directionsService = new google.maps.DirectionsService();
var statuses = [ 'finished', 'active', 'unfinished', 'inconsistent' ];
var route_colors = {
	finished : '#333333',
	active : '#118822',
	unfinished : '#0000FF',
	inconsistent : '#FF3333'
};
function setWindowSize() {
	var myHeight;
	if (typeof (window.innerWidth) == 'number') {
		myHeight = window.innerHeight;
	} else if (document.documentElement
			&& (document.documentElement.clientWidth || document.documentElement.clientHeight)) {
		myHeight = document.documentElement.clientHeight;
	} else if (document.body
			&& (document.body.clientWidth || document.body.clientHeight)) {
		myHeight = document.body.clientHeight;
	}
	document.getElementById('map_canvas').style.height = myHeight + 'px';
	document.body.style.height = myHeight + 'px';
}
function initialize() {
	createMap();
}
function gotoUserLocation(){
	if (navigator.geolocation) {
	    navigator.geolocation.getCurrentPosition(geoSuccess, geoFailure);
	} else {
		geoFailure();
	}
}
function geoSuccess(position) {
	if (userMarker != null) userMarker.setMap(null);
	userMarker = new google.maps.Marker( {
		position : new google.maps.LatLng(position.coords.latitude, position.coords.longitude),
		title : 'you',
		icon : 'user.png',
		map : map_,
		zIndex: 0
	});
	map_.setCenter(new google.maps.LatLng(position.coords.latitude, position.coords.longitude));
}
function geoFailure(position) {

}
function createMap() {
	var myOptions = {
		zoom : 2,
		center : new google.maps.LatLng(10.0, 0.0),
		mapTypeId : google.maps.MapTypeId.TERRAIN,
		mapTypeControl : false,
		navigationControlOptions : {
			position : google.maps.ControlPosition.BOTTOM_LEFT,
			style : google.maps.NavigationControlStyle.SMALL
		}
	};
	map_ = new google.maps.Map(document.getElementById('map_canvas'), myOptions);
	clusterer = new MarkerClusterer(map_, null, {
		zoomOnClick : false,
		gridSize : 40
	});
	directionsDisplay = new google.maps.DirectionsRenderer( {
		map : map_,
		suppressMarkers : true,
		preserveViewport : true
	});
}
function loadDirections() {
	if (direction_points.length > 0) {
		var previousGoogle = {
			location : '',
			status : '',
			poly : false
		};
		var previousPoly = {
			location : '',
			status : '',
			poly : true
		};
		var googleRoutes = {
			finished : [],
			active : [],
			unfinished : [],
			inconsistent : []
		};
		var polyRoutes = {
			finished : [],
			active : [],
			unfinished : [],
			inconsistent : []
		};
		for ( var j = 0; j < direction_points.length; j++) {
			var newer = direction_points[j];
			var routes = googleRoutes;
			var previous = previousGoogle;
			var newlocation = {
				location : newer.location
			};
			var previouslocation = {
				location : previous.location
			};
			if (newer.poly) {
				routes = polyRoutes;
				previous = previousPoly;
				var locsplit = newer.location.split(',');
				newlocation = new google.maps.LatLng(parseFloat(locsplit[0]),
						parseFloat(locsplit[1]));
				locsplit = previous.location.split(',');
				previouslocation = new google.maps.LatLng(
						parseFloat(locsplit[0]), parseFloat(locsplit[1]));
			}
			var route = routes[newer.status];
			if (newer.status == previous.status) {
				route[route.length - 1].push(newlocation);
			} else {
				if (newer.status == 'finished') {
					if (previous.status == 'unfinished') {
						routes.inconsistent.push( [ previouslocation ]);
						routes.inconsistent.push( [ newlocation ]);
					}
					route.push( [ newlocation ]);
				} else if (newer.status == 'unfinished') {
					if (previous.status == 'finished') {
						routes.active.push( [ previouslocation ]);
						routes.active[routes.active.length - 1]
								.push(newlocation);
					}
					route.push( [ newlocation ]);
				}
			}
			if (newer.poly) {
				previousPoly = newer;
			} else {
				previousGoogle = newer;
			}
		}
		for ( var k = 0; k < statuses.length; k++) {
			var status = statuses[k];
			for ( var l = 0; l < polyRoutes[status].length; l++) {
				polyArray.push(new google.maps.Polyline( {
					clickable : false,
					map : map_,
					path : polyRoutes[status][l],
					strokeWeight : 4,
					strokeColor : route_colors[status],
					strokeOpacity : 0.6
				}));
			}
			for ( var l = 0; l < googleRoutes[status].length; l++) {
				route = googleRoutes[status][l];
				for ( var i = 0; Math
						.ceil((route.length - i) / direction_limit) > 0; i = i
						+ direction_limit - 1) {
					var dirs = route.slice(i, i + direction_limit);
					var request = {
						origin : dirs[0].location,
						destination : dirs[dirs.length - 1].location,
						travelMode : google.maps.DirectionsTravelMode.DRIVING,
						waypoints : dirs.slice(1, dirs.length - 1)
					};
					directionsService
							.route(
									request,
									new Function(
											'response',
											'status',
											'route_callback(response, status, \'' + route_colors[status] + '\');'));
				}
			}
		}
	}
}
function route_callback(response, status, color) {
	if (status == google.maps.DirectionsStatus.OK) {
		directionArray.push(new google.maps.DirectionsRenderer( {
			map : map_,
			suppressMarkers : true,
			preserveViewport : true,
			polylineOptions : {
				clickable : false,
				strokeWeight : 4,
				strokeColor : color,
				strokeOpacity : 0.6
			}
		}));
		directionArray[directionArray.length - 1].setDirections(response);
	} else {
		/* alert('Routing service: '+status); */
	}
}
function setDetailsVisible(value) {
	if (value) {
		document.getElementById('details_canvas').style.display = 'block';
	} else {
		document.getElementById('details_canvas').style.display = 'none';
	}
}
function setClustering(value) {
	clustering_ = value;
	clearMap();
	drawMap();
}
function clearMarkers() {
	for (i in markers) {
		markers[i].setMap(null);
	}
	if (clusterer != null) {
		clusterer.clearMarkers();
	}
	markers = [];
	direction_points = [];
}
function clearMap() {
	clearInfoWindows();
	for (i in markers) {
		markers[i].setVisible(false);
	}
	for (i in directionArray) {
		directionArray[i].setMap(null);
	}
	for (i in polyArray) {
		polyArray[i].setMap(null);
	}
	directionArray = [];
	if (clusterer != null) {
		clusterer.clearMarkers();
	}
}
function clearInfoWindows() {
	for (i in openInfoWindows) {
		openInfoWindows[i].close();
	}
	openInfoWindows = [];
}
function drawMap() {
	if (clustering_) {
		clusterer.addMarkers(assoc_array_values(markers));
	} else {
		var size = assoc_array_length(markers);
		if (size > clustering_proposal_threshold
				&& confirm('' + size + ' markers, clustering recommended. Do you want to enable clustering?')) {
			clustering_ = true;
			document.getElementById('clustering_enabled').checked = 'checked';
			clusterer.addMarkers(assoc_array_values(markers));
		} else {
			for (i in markers) {
				markers[i].setMap(map_);
				markers[i].setVisible(true);
			}
		}
	}
	loadDirections();
}
function setDetailsSize(small){
	if (small) {
		document.getElementById('details_canvas').className = 'overview';
	} else {
		document.getElementById('details_canvas').className = '';
	}
}
function loadDetails(url) {
	var details_canvas = document.getElementById('details_canvas');
	if (details_canvas == null){
		return;
	}
	if (url.indexOf('?') < 0) {
		ajaxpage(url + '?timestamp=' + new Date().getTime(), 'details_canvas');
	} else {
		ajaxpage(url + '&timestamp=' + new Date().getTime(), 'details_canvas');
	}
	setDetailsVisible(true);
	details_canvas.scrollTop = 0;
}
function setCenter(latitude, longitude) {
	map_.setCenter(new google.maps.LatLng(latitude, longitude));
}
function panToLocation(latitude, longitude){
	map_.panTo(new google.maps.LatLng(latitude, longitude));
}
function setZoom(zoom){
	map_.setZoom(zoom);
}
function addMarker(latitude, longitude, title, mapicon, mapiconsize, zindex, showlabel, id) {
	var mapsw = mapiconsize / 2;
	var mapsh = mapiconsize - 2;
	var markerImage = new google.maps.MarkerImage(mapicon,
			new google.maps.Size(mapiconsize, mapiconsize), null,
			new google.maps.Point(mapsw, mapsh));
	var labeltitle = title;
	if (labeltitle.length > 7) {
		labeltitle = '...' + labeltitle.substring(labeltitle.length - 7,
				labeltitle.length);
	}
	var marker = new MarkerWithLabel( {
		position : new google.maps.LatLng(latitude, longitude),
		title : '' + title + '',
		icon : markerImage,
		zIndex : zindex,
		labelText : '' + labeltitle + '',
		labelClass : "labels", // the CSS class for the label
		labelStyle : {},
		labelVisible : showlabel,
		labelZIndex : zindex
	});
	markers[id] = marker;
}
function addMarkerBalloon(markerId, balloonHTMLContent, openInfoWindowOnLoad) {
	var marker = markers[markerId];
	if (marker) {
		var infowindow = new google.maps.InfoWindow({
			content : balloonHTMLContent
		});
		
		google.maps.event.addListener(marker, 'click', function() {
			clearInfoWindows();
			infowindow.open(map_, marker);
			openInfoWindows.push(infowindow);
		});
		if (openInfoWindowOnLoad) {
			google.maps.event.trigger(marker, 'click');
		}
	}
}
function addDirection(direction) {
	if (direction_points.length > 0) {
		var last = direction_points[direction_points.length - 1];
		if (last.location != direction.location
				|| last.status != direction.status) {
			direction_points.push(direction);
		}
	} else {
		direction_points.push(direction);
	}
}
function load() {
	setWindowSize();
	initialize();
}
function assoc_array_length(array) {
	var size = 0;
	for (var i in array) {
		size++;
	}
	return size;
}
function assoc_array_values(array) {
	var values = [];
	for (var i in array) {
		values.push(array[i]);
	}
	return values;
}