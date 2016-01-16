var FourWayButton = React.createClass({
	getInitialState: function() {
		if (!this.props.clickFuncs)
			throw "missing required 'clickFuncs' property";
		if (!this.props.clickFuncs.up)
			throw "missing required 'clickFuncs.up' property";
		if (!this.props.clickFuncs.right)
			throw "missing required 'clickFuncs.right' property";
		if (!this.props.clickFuncs.down)
			throw "missing required 'clickFuncs.down' property";
		if (!this.props.clickFuncs.left)
			throw "missing required 'clickFuncs.left' property";
		return {depressed: 'none', hovered: 'none'};
	},
	
	allReadyState: {
		depressed: 'none',
		hovered: 'none' 
	},
	
	handleUpMouseover: function() {
		this.setState({depressed: 'none', hovered: 'up'});
	},
	
	handleRightMouseover: function() {
		this.setState({depressed: 'none', hovered: 'right'});
	},
	
	handleDownMouseover: function() {
		this.setState({depressed: 'none', hovered: 'down'});
	},
	
	handleLeftMouseover: function() {
		this.setState({depressed: 'none', hovered: 'left'});
	},
	
	handleTopCenterMouseover: function() {
		if (this.props.clickFuncs.topcenter) {
		   this.setState({depressed: 'none', hovered: 'topcenter'});
		}
	},
	
	handleBottomCenterMouseover: function() {
		if (this.props.clickFuncs.bottomcenter) {
			this.setState({depressed: 'none', hovered: 'bottomcenter'});
		}
	},
	
	handleMouseout: function() {
		this.setState(this.allReadyState);
	},
	
	handleUpDown: function() {
        this.props.clickFuncs.up();
		this.setState({depressed: 'up', hovered: 'none'});
	},
	
	handleRightDown: function() {
		this.props.clickFuncs.right();
		this.setState({depressed: 'right', hovered: 'none'});		
	},
		
	handleDownDown: function() {
		this.props.clickFuncs.down();
		this.setState({depressed: 'down', hovered: 'none'});
	},
	
	handleLeftDown: function() {
		this.props.clickFuncs.left();
		this.setState({depressed: 'left', hovered: 'none'});
	},
	
	handleTopCenterDown: function() {
		if (this.props.clickFuncs.topcenter) {
			this.props.clickFuncs.topcenter();
		    this.setState({depressed: 'topcenter', hovered: 'none'});
		}
	},
	
	handleBottomCenterDown: function() {
		if (this.props.clickFuncs.bottomcenter) {
			this.props.clickFuncs.bottomcenter();
			this.setState({depressed: 'bottomcenter', hovered: 'none'});
		}
	},
	
	handleUp: function() {
		console.log("handleUp()");
		var newState = {
			depressed: 'none',
			hovered: this.state.depressed
		};
		this.setState(newState);
	},

	render: function() {
		
		var containerStyle = {
			position: "relative",
			border: "1px solid blue",
			backgroundColor: this.props.backgroundColor ? this.props.backgroundColor : "white",
			width: "100px",
			height: "100px"
		};
		
		var rightStyle = {
			position: "absolute",
			top: "34px",
			left: "70px",
			backgroundImage: "url('images/rtarrowlilacready.png')",
			backgroundRepeat: "no-repeat",
			backgroundSize: "30px 30px",
			backgroundColor: this.props.backgroundColor ? this.props.backgroundColor : "white",
		    width: "30px",
		    height: "30px"
		};
		
		var downStyle = {
			position: "absolute",
			top: "70px",
			left: "34px",
			backgroundImage: "url('images/downarrowlilacready.png')",
			backgroundRepeat: "no-repeat",
			backgroundSize: "30px 30px",
			backgroundColor: this.props.backgroundColor ? this.props.backgroundColor : "white",
		    width: "30px",
		    height: "30px"
		};
		
		var leftStyle = {
		    position: "absolute",
		    top: "34px",
		    left: "0px",
		    backgroundImage: "url('images/leftarrowlilacready.png')",
		    backgroundRepeat: "no-repeat",
		    backgroundSize: "30px 30px",
		    backgroundColor: this.props.backgroundColor ? this.props.backgroundColor : "white",
		    width: "30px",
		    height: "30px"
		};
		
		var topCenterStyle = {
			position: "absolute",
			top: "34px",
			left: "33px",
			backgroundImage: "url('images/topcenterlilacready.png')",
			backgroundRepeat: "no-repeat",
			backgroundSize: "34px, 25px",
			backgroundColor: this.props.backgroundColor ? this.props.backgroundColor: "white",
			width: "34px",
			height: "15px"
		};
		
		var bottomCenterStyle = {
				position: "absolute",
				top: "52px",
				left: "33px",
				backgroundImage: "url('images/botcenterlilacready.png')",
				backgroundRepeat: "no-repeat",
				backgroundSize: "34px, 25px",
				backgroundColor: this.props.backgroundColor ? this.props.backgroundColor: "white",
				width: "34px",
				height: "18px"
			};
		
		var upStyle = {
			position: "absolute",
			top: "0px",
			left: "34px",
			backgroundImage: "url('images/uparrowlilacready.png')",
			backgroundRepeat: "no-repeat",
			backgroundSize: "30px 30px",
			backgroundColor: this.props.backgroundColor ? this.props.backgroundColor : "white",
			width: "30px",
			height: "30px"
		};
		
		if (this.state.depressed == 'none') {
		    if (this.state.hovered == 'right')
			    rightStyle.backgroundImage = "url('images/rtarrowlilachover.png')";
		    else if (this.state.hovered == 'down')
		    	downStyle.backgroundImage = "url('images/downarrowlilachover.png')";
		    else if (this.state.hovered == 'left')
		    	leftStyle.backgroundImage = "url('images/leftarrowlilachover.png')";
		    else if (this.state.hovered == 'up')
		    	upStyle.backgroundImage = "url('images/uparrowlilachover.png')";
		    else if (this.state.hovered == 'topcenter')
		    	topCenterStyle.backgroundImage = "url('images/topcenterlilachover.png')";
		    else if (this.state.hovered == 'bottomcenter')
		    	bottomCenterStyle.backgroundImage = "url('images/botcenterlilachover.png')";
	    }
		else {
			if (this.state.depressed == 'right')
				rightStyle.backgroundImage = "url('images/rtarrowlilacdepressed.png')";
			else if (this.state.depressed == 'down')
				downStyle.backgroundImage = "url('images/downarrowlilacdepressed.png')";
			else if (this.state.depressed == 'left')
				leftStyle.backgroundImage = "url('images/leftarrowlilacdepressed.png')";
			else if (this.state.depressed == 'up')
				upStyle.backgroundImage = "url('images/uparrowlilacdepressed.png')";
			else if (this.state.depressed == 'topcenter')
				topCenterStyle.backgroundImage = "url('images/topcenterlilacdepressed.png')";
			else if (this.state.depressed == 'bottomcenter')
				bottomCenterStyle.backgroundImage = "url('images/botcenterlilacdepressed.png')";
		}
		
		return (
		    <div style={containerStyle}>
		        <div style={upStyle}
		             onMouseOver={this.handleUpMouseover}
		             onMouseOut={this.handleMouseout}
		             onMouseDown={this.handleUpDown}
		             onMouseUp={this.handleUp}></div>
		        <div style={leftStyle}
		             onMouseOver={this.handleLeftMouseover}
		             onMouseOut={this.handleMouseout}
		             onMouseDown={this.handleLeftDown}
		             onMouseUp={this.handleUp}></div>
		        <div style={topCenterStyle}
		             onMouseOver={this.handleTopCenterMouseover}
		             onMouseOut={this.handleMouseout}
		             onMouseDown={this.handleTopCenterDown}
		             onMouseUp={this.handleUp}></div>
		        <div style={bottomCenterStyle}
		             onMouseOver={this.handleBottomCenterMouseover}
		             onMouseOut={this.handleMouseout}
		             onMouseDown={this.handleBottomCenterDown}
		             onMouseUp={this.handleUp}></div>
		        <div style={rightStyle} 
		             onMouseOver={this.handleRightMouseover} 
		             onMouseOut={this.handleMouseout}
		             onMouseDown={this.handleRightDown}
		             onMouseUp={this.handleUp}></div>
		        <div style={downStyle}
		             onMouseOver={this.handleDownMouseover}
		             onMouseOut={this.handleMouseout}
		             onMouseDown={this.handleDownDown}
		             onMouseUp={this.handleUp}></div>
		    </div>
		);
	}
});
window.FourWayButton = FourWayButton;