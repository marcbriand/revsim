var CommentBox = React.createClass({
  getInitialState: function() {
	  this.initialFrameData = this.getInitialFrame();
	return {
		frame: 0, 
		waiting: false, 
//		frameData: {connections: [], nodes: []}, 
		frameData: this.initialFrameData,
		view: "network",
	    cameraPosition: {x: 0, y: 0, z: 2}}  
  },
  getInitialFrame: function() {
	  console.log("CommentBox.getInitialFrame()");
	  var that = this;
	  var ret = null;
	  var success = function(data) {
		  console.log("CommentBox.getInitialFrame success, data:");
		  console.log(data);
		  ret = data;
	  }
	  
	  var error = function(msg) {
		  console.log("CommentBox.getInitialFrame() error: " + msg);
	  }
	  
	  ajaxGet("/api/nodes?frame=1", success, error, false); // false means synchronous
	  
	  return ret;
  },
  replaceCameraPosition(pos) {
	 return {
        frame: this.state.frame,
        waiting: this.state.waiting,
		frameData: this.state.frameData,
		view: this.state.view,
		cameraPosition: pos
     } 
  },
  rightClick: function() {
	  var newCameraPos = {x: this.state.cameraPosition.x + 0.2, y: this.state.cameraPosition.y, z: this.state.cameraPosition.z};
	  var newState = this.replaceCameraPosition(newCameraPos);
	  this.canvas.drawFrameOuter(this.state.frameData, this.state.view, newCameraPos);
	  this.setState(newState);
  },
  downClick: function() {
	  var newCameraPos = {x: this.state.cameraPosition.x, y: this.state.cameraPosition.y - 0.2, z: this.state.cameraPosition.z};
	  var newState = this.replaceCameraPosition(newCameraPos);
	  this.canvas.drawFrameOuter(this.state.frameData, this.state.view, newCameraPos);
	  this.setState(newState);	  
  },
  leftClick: function() {
	  var newCameraPos = {x: this.state.cameraPosition.x - 0.2, y: this.state.cameraPosition.y, z: this.state.cameraPosition.z};
	  var newState = this.replaceCameraPosition(newCameraPos);
	  this.canvas.drawFrameOuter(this.state.frameData, this.state.view, newCameraPos);
	  this.setState(newState);	  
  },
  upClick: function() {
	  var newCameraPos = {x: this.state.cameraPosition.x, y: this.state.cameraPosition.y + 0.2, z: this.state.cameraPosition.z};
	  var newState = this.replaceCameraPosition(newCameraPos);
	  this.canvas.drawFrameOuter(this.state.frameData, this.state.view, newCameraPos);
	  this.setState(newState);	  	  
  },
  topCenterClick: function() {
	 var newCameraPos = {x: this.state.cameraPosition.x, y: this.state.cameraPosition.y, z: this.state.cameraPosition.z + 0.2};
	 var newState = this.replaceCameraPosition(newCameraPos);
	 this.canvas.drawFrameOuter(this.state.frameData, this.state.view, newCameraPos);
	 this.setState(newState);
  },
  bottomCenterClick: function() {
     var newCameraPos = {x: this.state.cameraPosition.x, y: this.state.cameraPosition.y, z: this.state.cameraPosition.z - 0.2};
	 var newState = this.replaceCameraPosition(newCameraPos);
	 this.canvas.drawFrameOuter(this.state.frameData, this.state.view, newCameraPos);
	 this.setState(newState);
  },
  getNextFrame: function() {
	  console.log("CommentBox.getNextFrame()");
	  this.setState({frame: this.state.frame, waiting: true});
	  
	  var that = this;
	  var success = function(data) {
		  console.log("CommentBox.getNextFrame.got data:");
		  console.log(data);
		  var state = that.state;
		  state.frame++;
		  state.waiting = false;
		  state.frameData = data;
		  that.canvas.drawFrameOuter(data, that.state.view, that.state.cameraPosition);
		  that.setState(state);
	  }
	  
	  var error = function(msg) {
		  var state = that.state;
		  state.waiting = false;
		  this.setState(state);
		  console.log("CommentBox.getNextFrame.error " + msg);
	  }
	  
	  ajaxGet("/api/nodes?frame=" + this.state.frame + 1, success, error);
	  
//	  this.canvas.drawFrame(this.state.frame + 1, success, error);
  },
  getPrevFrame: function() {
	  console.log("CommentBox.getPrevFrame()");
	  this.setState({frame: this.state.frame, waiting: true});
	  
	  var that = this;
	  var success = function() {
		  console.log("CommentBox.getPrevFrame.success");
		  var state = that.state;
		  if (state.frame > 1)
			  state.frame--;
		  state.waiting = false;
		  that.setState(state);
	  }
	  
	  var error = function(msg) {
		  var state = that.state;
		  state.waiting = false;
		  this.setState(state);
		  console.log("CommentBox.getPrevFrame.error " + msg);		  
	  }
	  
	  var newFrame = this.state.frame > 1 ? this.state.frame - 1 : this.state.frame;
	  this.canvas.drawFrame(newFrame, success, error);
  },
  setToNetworkView: function() {
	  var state = this.state;
	  state.view = "network";
	  this.canvas.drawFrameOuter(this.state.frameData, state.view, state.cameraPosition);
	  this.setState(state);
  },
  setToApparentView: function() {
	  var state = this.state;
	  state.view = "apparent";
	  this.canvas.drawFrameOuter(this.state.frameData, state.view, state.cameraPosition);
	  this.setState(state);
  },
  grabCanvas: function(canvas) {
	  this.canvas = canvas;
  },
  render: function() {
	console.log("CommentBox.render()");
	var s = {colors: ButtonStyles.lightYellow};
	var foo = function(){
		console.log("outer click func");
	};
	
	var toDisable = this.state.frame <= 1 ? [1] : [];
	
	var titleStyle = {
		fontFamily: "Arial"	
	};
	
	var sIndex = this.state.view == "network" ? "0" : "1";
	
	var fourWayClickFuncs = {
	    up: this.upClick,
	    right: this.rightClick,
	    down: this.downClick,
	    left: this.leftClick,
	    topcenter: this.topCenterClick,
	    bottomcenter: this.bottomCenterClick
	}
	  
    return (
      <div className="commentBox">
        <h1 style={titleStyle}>Network</h1>
        <WaitChangeHPanel titles={["Next", "Prev"]} 
                          waiting={this.state.waiting}
                          toDisable={toDisable}
                          clickFuncs={[this.getNextFrame, this.getPrevFrame]}/>
        <RadioButtonHPanel titles={["Net", "Appar"]}
                           clickFuncs={[this.setToNetworkView, this.setToApparentView]}
                           selectionIndex={sIndex}/>
        <div style={{clear: "left"}} />
        <FourWayButton clickFuncs={fourWayClickFuncs} backgroundColor="yellow"/>
        <MyCanvas ref={this.grabCanvas} frameData={this.initialFrameData} view={this.state.view} cameraPosition={this.state.cameraPosition}/>
      </div>
    );
  }
});
function drawLine(ctx, nodeA, nodeB) {

  var avgZ = (nodeA.appZ + nodeB.appZ)/2;
  var green = 255/(1 + avgZ);
  green = Math.floor(green);
  var color = "rgb(255," + green;
  color += ",255)";
	
  ctx.strokeStyle = color;
  ctx.lineWidth = 2;
  ctx.beginPath();
  ctx.moveTo(nodeA.x, nodeA.y);
  ctx.lineTo(nodeB.x, nodeB.y);
  ctx.stroke();
}

function getUrlParams() {
	var ret = {};
	var loc = window.location + "";
	console.log("loc = " + loc + ", typeof loc = " + typeof(loc));
	var q = loc.indexOf("?");
	if (q < 0)
		return ret;
	var queryString = loc.substr(q+1);
	var params = queryString.split('&');
	for (var i = 0; i < params.length; i++) {
		var paramParts = params[i].split('=');
		var key = paramParts[0];
		var value = paramParts.length > 1 ? paramParts[1] : "";
		ret[key] = value;
	}
	return ret;
}

function drawConnections(ctx, data) {
  for (var i = 0; i < data.connections.length; i++) {
    var conn = data.connections[i];
    var nodeA = data.nodes[conn.a];
    var nodeB = data.nodes[conn.b];
    
    if (nodeA != null && nodeB != null) {
      drawLine(ctx, nodeA, nodeB);
    }
    else {
      console.log("failed to draw line, one or both nodes are null");
    }
  }
}

function add3DLine(x1, y1, z1, x2, y2, z2, c, scene) {
	var lineMaterial = new THREE.LineBasicMaterial({
        color: c
    });
	
	var lineGeometry = new THREE.Geometry();
    lineGeometry.vertices.push(new THREE.Vector3(x1, y1, z1));
    lineGeometry.vertices.push(new THREE.Vector3(x2, y2, z2));
    var line = new THREE.Line(lineGeometry, lineMaterial);
    
    scene.add(line);
	
}

function draw3DCharObjAt(charObj, x, y, z, scale, color, scr) {
	for (var i = 0; i < charObj.arcs.length; i++) {
		var arc = charObj.arcs[i];
		var fromIndex = arc[0];
		var toIndex = arc[1];
		if (fromIndex < 0 || fromIndex >= charObj.nodes.length)
			throw "fromIndex out of range";
		if (toIndex < 0 || toIndex >= charObj.nodes.length)
			throw "toIndex out of range";
		var fromNode = charObj.nodes[fromIndex];
		var toNode = charObj.nodes[toIndex];
		var strokeStartX = x + scale*fromNode[0];
		var strokeStartY = y + scale*fromNode[1];
		var strokeEndX = x + scale*toNode[0];
		var strokeEndY = y + scale*toNode[1];
		add3DLine(strokeStartX, strokeStartY, z, strokeEndX, strokeEndY, z, color, scr.scene);
	}
}

function draw3DTextAt(text, x, y, z, scale, color, scr) {
	text = text + "";
	console.log("draw3DTextAt, text = " + text + ", x = " + x + ", y = " + y + ", z = " + z);
	for (var i = 0; i < text.length; i++) {
		var ch = text.charAt(i);
		var chObj = get3DCharObj(ch);
	    var xCoor = i*scale*6 + x;
	    console.log("xCoor = " + xCoor);
	    draw3DCharObjAt(chObj, xCoor, y, z, scale, color, scr);
	}
}

function showCanvas(canvasType) {
	var networkDiv = document.getElementById("networkDiv");
	var apparentDiv = document.getElementById("apparentDiv");
    if (canvasType == "network") {
    	networkDiv.style.display = "block";
    	apparentDiv.style.display = "none";
    }
    else {
    	networkDiv.style.display = "none";
    	apparentDiv.style.display = "block";
    }
}

// cylInfo:
//  radius, length, radialPoints, color
// alpha is applied first, it is the angle around the world y-axis, 9-o'clock is zero, ccw = positive
// beta is applied second, it is measured down from the north pole and up from the south pole; that is,
// an angle of 0 corresponds to the cylinder top being at the north pole and the bottom being at the south pole;
// a positive angle corresponds to the cylinder top being lower than the north pole and the bottom being higher than
// the south pole; an angle of pi/2 corresponds to the top and bottom being on the equator
function addCylinder(cylInfo, cx, cy, cz, alpha, beta, scene) {
	var cylg = new THREE.CylinderGeometry(cylInfo.radius, cylInfo.radius, cylInfo.length, cylInfo.radialPoints, 1, true);
//	var cylm = new THREE.MeshPhongMaterial({color: cylInfo.color, side:THREE.DoubleSide});
	var cylm = new THREE.MeshLambertMaterial({color: cylInfo.color, shading: THREE.FlatShading});
	var cyl = new THREE.Mesh(cylg, cylm);
	cyl.position.x = cx;
	cyl.position.y = cy;
	cyl.position.z = cz;
	var alphaAxis = new THREE.Vector3(0, 1, 0);
	cyl.rotateOnAxis(alphaAxis, alpha);
	var betaAxis = new THREE.Vector3(0, 0, 1);
	cyl.rotateOnAxis(betaAxis, beta);
	scene.add(cyl);
}

// upper left quadrant (deltax < 0, deltaz < 0):
// cw = -alpha, out = +beta
// upper right quadrant (deltax > 0, deltaz < 0):
// ccw = +alpha, out = -beta
// lower left quadrant (deltax < 0, deltaz > 0):
// ccw = +alpha, out = +beta
// lower right quadrant (deltax > 0, deltaz > 0):
// cw = -alpha, out = -beta
function findAlpha(deltax, deltaz, xzlen) {
	console.log("findAlpha(), deltax = " + deltax + ", deltaz = " + deltaz + ", xzlen = " + xzlen);
	if (deltax == 0 && deltaz == 0) {
		return 0;
	}
	
	if (Math.abs(deltax) > Math.abs(deltaz)) {
		if (deltax < 0) {
			return Math.asin(deltaz/xzlen);
		}
		else {
			return -Math.asin(deltaz/xzlen);
		}
	}
	else {
		if (deltaz < 0) {
			if (deltax < 0) {
				return -(0.5*Math.PI + Math.asin(deltax/xzlen));
			}
			else {
				return 0.5*Math.PI - Math.asin(deltax/xzlen);
			}
		}
		else {
			if (deltax < 0) {
				return 0.5*Math.PI + Math.asin(deltax/xzlen);
			}
			else {
				return -(0.5*Math.PI - Math.asin(deltax/xzlen));
			}
		}
	}
}

function findBeta(deltax, deltay, deltaz, alpha, xzlen, xyzlen) {
	console.log("findBeta(), deltax = " + deltax + ", deltay = " + deltay + "deltaz = " + deltaz +  ", alpha = " + alpha + ", xzlen = " + xzlen + ", xyzlen = " + xyzlen);
    var factor = 1;
    if (deltax == 0) {
    	if (alpha < 0) {
    		factor = deltaz < 0 ? 1 : -1;
    	}
    	else {
    		factor = deltaz < 0 ? -1 : 1;
    	}
    }
    else if (deltax > 0)
    	factor = -1;
	
	if (xzlen < deltay) {
		return factor*Math.asin(xzlen/xyzlen);
	}
	else {
		return factor*(0.5*Math.PI - Math.asin(deltay/xyzlen));
	}
}

function findAngleFrom9oclock(cx, cy, x, y) {
	var deltax = x - cx;
	var deltay = y - cy;
    if (deltax == 0 && deltay == 0)
    	return 0;
	var length = Math.sqrt(deltax*deltax + deltay*deltay);
	console.log("findAngleFrom9oclock, deltax = " + deltax + ", deltay = " + deltay + ", length = " + length);
	if (Math.abs(deltax) > Math.abs(deltay)) {
		console.log("taking deltay branch");
		// if Math.abs(deltay)/length > 1000000
		// if Math.abs(deltay) > 1000000*length
		if (Math.abs(deltay) > 10000000*length)
			return 0;
		var sin = deltay/length;
		if (deltax > 0)
			return Math.asin(sin);
		else
			return -Math.asin(sin);
	}
	else {
		console.log("..taking deltax branch");
		if (Math.abs(deltax) > 10000000*length)
			return 0;
		var sin = deltax/length;
		if (deltay > 0)
			return 0.5*Math.PI - Math.asin(sin);
		else
			return 0.5*Math.PI + Math.asin(sin);
	}
}

function addCylinderBetweenPoints(cylInfo, x1, y1, z1, x2, y2, z2, scene) {

	console.log("addCylinderBetweenPoints(), x1 = " + x1 + ", y1 = " + y1 + ", z1 = " + z1 + ", x2 = " + x2 + ", y2 = " + y2 + ", z2 = " + z2);
	
	var cx = (x1 + x2)/2;
	var cy = (y1 + y2)/2;
	var cz = (z1 + z2)/2;
	var alpha = 0;
	var beta = 0;
	var deltax = 0;
	var deltay = 0;
	var deltaz = 0;

	if (y1 < y2) {
		deltax = x2 - cx;
		deltay = y2 - cy;
		deltaz = z2 - cz;
	}
	else {
		deltax = x1 - cx;
		deltay = y1 - cy;
		deltaz = z1 - cz;
	}
	
	var xzlen = Math.sqrt(deltax*deltax + deltaz*deltaz);
	var xyzlen = Math.sqrt(deltax*deltax + deltay*deltay + deltaz*deltaz);
	
	alpha = findAlpha(deltax, deltaz, xzlen);
	beta = findBeta(deltax, deltay, deltaz, alpha, xzlen, xyzlen);
	
	var newCylInfo = {
		radius: cylInfo.radius,
		radialPoints: cylInfo.radialPoints,
		color: cylInfo.color,
		length: 2*xyzlen
	};
	
	console.log("cx = " + cx + ", cy = " + cy + ", cz = " + cz + ", alpha = " + alpha + ", beta" + beta);
	
	addCylinder(newCylInfo, cx, cy, cz, alpha, beta, scene);

}

function wfCircleZ(x, y, z, r, segs, scene) {
	var circleG = new THREE.CircleGeometry(r, segs);
	var circleM = new THREE.MeshBasicMaterial({
		color: 0x0000ff,
		wireframe: true
	});
	var circle = new THREE.Mesh(circleG, circleM);

	var circleAxis = new THREE.Vector3(1, 0, 0);
	circle.rotateOnAxis(circleAxis, -0.5*Math.PI);
	circle.position.set(x, y, z);
	scene.add(circle)	
}

function verticalConicTest(x, yl, yu, z, rl, ru, scene) {
   
    var rcl = rl > 0 ? rl : 0.01;
	wfCircleZ(x, yl, z, rcl, 12, scene);
	var rcu = ru > 0 ? ru : 0.01;
	wfCircleZ(x, yu, z, rcu, 12, scene);
	
	var cylInfo = {
			radius: 0.03,
			length: 2,
			radialPoints: 32,
			color: 0x00ff00
		};
	
	var adelta = 2.0*Math.PI/12;
	for (var i = 0; i < 12; i++) {
		var angle = adelta*i;
		var xl = x + rl*Math.cos(angle);
		var zl = z -rl*Math.sin(angle);
		var xu = x + ru*Math.cos(angle);
		var zu = z - ru*Math.sin(angle);
		
		addCylinderBetweenPoints(cylInfo, xl, yl, zl, xu, yu, zu, scene);
	}
	
}

function doPipeTests(scene, camera, cameraPosition) {
	console.log("doPipeTests()");
	add3DLine(-10, 0, 0, 10, 0, 0, 0x0000ff, scene);
	add3DLine(0, -10, 0, 0, 10, 0, 0x0000ff, scene);
	add3DLine(0, 0, -10, 0, 0, 10, 0x0000ff, scene);

	var cylInfo = {
		radius: 0.03,
		length: 2,
		radialPoints: 32,
		color: 0x00ff00
	};
/*	
	addCylinderBetweenPoints(cylInfo, 0,    0.4, 0,
			                          0.5, 3,   0, 
			                          scene);
	addCylinderBetweenPoints(cylInfo, 0,    0.4, 0,
			                          0.5*Math.cos(Math.PI/6), 3, -0.5*Math.sin(Math.PI/6),
			                          scene);	
*/	
	var pl = new THREE.PointLight(0xffffff, 1, 100);
	pl.position.set(3, 3, 0);
	scene.add(pl);

	wfCircleZ(0, 3, 0, 0.5, 12, scene);
	
	verticalConicTest(3, 1, 3, 0, 0, 0.5, scene);
	
//	camera.position.x = 3;
//	camera.position.y = 4;
//	camera.position.z = 1;
//	camera.position.x = cameraPosition.x;
//	camera.position.y = cameraPosition.y;
//	camera.position.z = cameraPosition.z;
//	camera.rotation.x = -0.2*Math.PI;
}

function drawApparentConnections(data, scene, camera, renderer) {
	console.log("drawApparentConnections");

	add3DLine(-10, 0, 0, 10, 0, 0, 0x0000ff, scene);
	add3DLine(0, -10, 0, 0, 10, 0, 0x0000ff, scene);
	add3DLine(0, 0, -10, 0, 0, 10, 0x0000ff, scene);

	var cylInfo = {
		radius: 0.03,
		length: 2,
		radialPoints: 32,
		color: 0x00ff00
	};
/*	
	addCylinderBetweenPoints(cylInfo, 0,    0.4, 0,
			                          0.5, 3,   0, 
			                          scene);
	addCylinderBetweenPoints(cylInfo, 0,    0.4, 0,
			                          0.5*Math.cos(Math.PI/6), 3, -0.5*Math.sin(Math.PI/6),
			                          scene);

	add3DLine(0, 0.4, 0.2, 0.5, 3, 0.2, 0xff00ff, scene);
	
	var circleG = new THREE.CircleGeometry(0.5, 12);
	var circleM = new THREE.MeshBasicMaterial({
		color: 0x0000ff,
		wireframe: true
	});
	var circle = new THREE.Mesh(circleG, circleM);

	var circleAxis = new THREE.Vector3(1, 0, 0);
	circle.rotateOnAxis(circleAxis, -0.5*Math.PI);
	circle.position.set(0, 3, 0);
	scene.add(circle);
*/	
	var pl = new THREE.PointLight(0xffffff, 1, 100);
	pl.position.set(3, 3, 0);
	scene.add(pl);
	
	for (var i = 0; i < data.connections.length; i++) {
		var conn = data.connections[i];
		var nodeAKey = conn.a
		var nodeBKey = conn.b
		var nodeA = data.nodes[nodeAKey];
		var nodeB = data.nodes[nodeBKey];
		add3DLine(nodeA.appX, nodeA.appY, nodeA.appZ, nodeB.appX, nodeB.appY, nodeB.appZ, 0xff0000, scene);
	}
	
}

function drawZ(ctx, data) {
	ctx.fillStyle = "green";
    ctx.font = "bold 15px Arial";

	for (var key in data.nodes) {
		var node = data.nodes[key];
		ctx.fillText(node.appZ, node.x, node.y);
	}
}

function drawPathInfo(ctx, data) {
	ctx.fillStyle = "red";
	ctx.font = "bold 15px Arial";
	
	for (var key in data.nodes) {
		var node = data.nodes[key];
		for (var pKey in node.pathInfo) {
			if (pKey == "0") {
			    var ord = node.pathInfo[pKey];
			    ctx.fillText(ord, node.x, node.y);
			}
		}
	}
}

function drawPathInfo3D(data, scr) {
	for (var key in data.nodes) {
		var node = data.nodes[key];
		for (var pKey in node.pathInfo) {
			if (pKey == "0") {
			    var ord = node.pathInfo[pKey];
                draw3DTextAt(ord, node.appX, node.appY, node.appZ, 0.03, 0x00ff00, scr);
			}
		}
	}	
}

function ajaxGet(url, success, error, asyncIn) {
	var asyncVal = true;
	if (asyncIn === false)
		asyncVal = false;
     $.ajax({
      async: asyncVal,
      url: url,
      dataType: 'json',
      cache: false,
      success: success,
      error: error
    });
}
var MyCanvas = React.createClass({
  componentDidMount: function () {
      console.log("MyCanvas.componentDidMount()");
      var h = 600;
      var w = 550;
      var netCanvas = document.getElementById("netCanvas");
      var ctx = netCanvas.getContext('2d');
      ctx.fillStyle = "black";
      ctx.fillRect(0, 0, 549, 600); // seeming off-by-one bug in three.js, make draws 1px higher than specified?
      
      var appCanvas = document.getElementById("appCanvas");
      this.scene = new THREE.Scene();
      this.camera = new THREE.PerspectiveCamera(100, w / h, 0.1, 1000);

      this.renderer = new THREE.WebGLRenderer({canvas: appCanvas, alpha: true, antialias: true});
      this.renderer.setSize( w, h );
      this.renderer.setClearColor(0xeeeeff);
      this.renderer.shadowMapEnabled = true;
      var appNode = document.getElementById("apparentDiv");
      appNode.appendChild(this.renderer.domElement);
      
      this.drawFrameOuter(this.props.frameData, this.props.view, this.props.cameraPosition);
      
  },
  getCanvas: function() {
	  var els = document.getElementsByTagName('canvas');
	  if (els.length > 0)
		  return els[0];
	  return null;
  },
  getContext: function() {
	  var canvas = this.getCanvas();
	  if (canvas) {
		  return canvas.getContext('2d');
	  }
	  return null;
  },
    
  drawFrameOuter: function(data, view, cameraPosition) {
	    console.log("drawFrameOuter()");
	   
	    
	    
	    var ctx = this.getContext();
	    if (ctx == null) {
	    	error("could not get drawing context");
	    	return;
	    }
        ctx.fillStyle = "black";
        ctx.fillRect(0, 0, 599, 599);
        
    	drawConnections(ctx, data);
    	drawPathInfo(ctx, data);
    	    
    	var params = getUrlParams();
    	if (params.app == "test1")
    		doPipeTests(this.scene, this.camera, cameraPosition);
    	else {    	
        	drawApparentConnections(data, this.scene, this.camera, this.renderer);
            drawPathInfo3D(data, {scene: this.scene, camera: this.camera, renderer: this.renderer});
        	this.camera.position.x = 3;
        	this.camera.position.y = 5;
        	this.camera.position.z = 4;
    	}
    	this.camera.position.x = cameraPosition.x;
    	this.camera.position.y = cameraPosition.y;
    	this.camera.position.z = cameraPosition.z;
//    	this.camera.rotation.x = -0.2*Math.PI;
        this.renderer.render(this.scene, this.camera);
  },
  
  // function draw3DCharObjAt(charObj, x, y, z, scale, color, scr) {
  
  // gets next frame data from server and draws it
  drawFrame: function(frameNo, success, error) {
	console.log("MyCanvas.drawFrame, frameNo = " + frameNo);
    var ctx = this.getContext();
    if (ctx == null) {
    	error("could not get drawing context");
    	return;
    }
    
    var successFunc = function(data) {
    	console.log('got json for drawing next frame');
        ctx.fillStyle = "black";
        ctx.fillRect(0, 0, 599, 599);
    	drawConnections(ctx, data);
    	drawZ(ctx, data);
    	success();
    }
    
    ajaxGet("/api/nodes?frame=" + frameNo, successFunc, error);
  },
  render: function() {
	console.log("MyCanvas.render()");
    return <div className="myCanvas"/>;
  }
});


ReactDOM.render(
  <CommentBox url="/api/comments" pollInterval={2000}/>,
  document.getElementById('content')
);