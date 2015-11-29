var CommentBox = React.createClass({
  getInitialState: function() {
	return {frame: 0, waiting: false}  
  },
  getNextFrame: function() {
	  console.log("CommentBox.getNextFrame()");
	  this.setState({frame: this.state.frame, waiting: true});
	  
	  var that = this;
	  var success = function() {
		  console.log("CommentBox.getNextFrame.success");
		  var state = that.state;
		  state.frame++;
		  state.waiting = false;
		  that.setState(state);
	  }
	  
	  var error = function(msg) {
		  var state = that.state;
		  state.waiting = false;
		  this.setState(state);
		  console.log("CommentBox.getNextFrame.error " + msg);
	  }
	  
	  this.canvas.drawFrame(this.state.frame + 1, success, error);
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
	  
    return (
      <div className="commentBox">
        <h1>Network</h1>
        <WaitChangeHPanel titles={["Next", "Prev"]} 
                          waiting={this.state.waiting}
                          toDisable={toDisable}
                          clickFuncs={[this.getNextFrame, this.getPrevFrame]}/>
        <div style={{clear: "left"}} />
        <MyCanvas ref={this.grabCanvas}/>
      </div>
    );
  }
});
function drawLine(ctx, nodeA, nodeB) {
  ctx.strokeStyle = "white";
  ctx.lineWidth = 3;
  ctx.beginPath();
  ctx.moveTo(nodeA.x, nodeA.y);
  ctx.lineTo(nodeB.x, nodeB.y);
  ctx.stroke();
}
function drawConnections(ctx, data) {
  for (var i = 0; i < data.connections.length; i++) {
    var conn = data.connections[i];
    console.log("got conn, a = " + conn.a + ", b = " + conn.b);
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
function ajaxGet(url, success, error) {
     $.ajax({
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
      var canvas = document.createElement('canvas');
      canvas.height = 600;
      canvas.width = 800;
      var ctx = canvas.getContext('2d');
      ctx.fillStyle = "black";
      ctx.fillRect(0, 0, 799, 599);
      console.log("..canvas is " + canvas);
      var node = ReactDOM.findDOMNode(this);
      console.log("..node is " + node);
      node.appendChild(canvas);
      
      var success = function(data) {
        console.log("got initial json");
        drawConnections(ctx, data);
      }
      
      var error = function(xhr, status, err) {
        console.error("/api/nodes", status, err.toString());
      }
      
//      ajaxGet("/api/nodes?frame=1", success, error);
      
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
        ctx.fillRect(0, 0, 799, 599);
    	drawConnections(ctx, data);
    	success();
    }
    
    ajaxGet("/api/nodes?frame=" + frameNo, successFunc, error);
  },
  render: function() {
    return <div className="myCanvas"/>;
  }
});


ReactDOM.render(
  <CommentBox url="/api/comments" pollInterval={2000}/>,
  document.getElementById('content')
);