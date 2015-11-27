var divStyle = {
    width: '100px',
    margin: '3px'
}

divStyle['backgroundColor'] = '#eeeeff';

var WaitChangeButton = React.createClass({
	// {
	//   isHovered:  (boolean) true if mouse is over button
	//   clickState: (string) "ready" -- ready to be clicked
	//                        "depressed" -- button is depressed. This state will automatically transition
	//                                       to "disabled" after a brief time interval
	//                        "disabled"  -- button is disabled and will ignore mouse clicks until
	//                                       an event it is waiting on is received
	//   
	getInitialState: function() {
		return {isHovered: false, clickState: "ready"};
	},
    onClick: function() {
    	console.log("WaitChangeButton.onClick(), title = " + this.props.title);
    	console.log("..isHovered = " + this.state.isHovered + ", clickState = " + this.state.clickState);
    	if (this.state.clickState === "ready") {
    		this.setState({isHovered: false, clickState: "depressed"});
    		if (this.props.onClick)
    			this.props.onClick();
    	}
    },
    handleMouseOver: function() {
    	console.log("onMouseOver()");
    	if (this.state.clickState === "ready") {
    		this.setState({isHovered: true, clickState: "ready"});
    	}
    },
    handleMouseOut: function() {
    	console.log("onMouseOut()");
    	this.setState({isHovered: false, clickState: this.state.clickState});
    },
    reset: function() {
    	this.setState(this.getInitialState());
    },
	render: function() {
		console.log("WaitChangeButton.render()");
		var style = this.props.styleFunc == null ? {colors: {
			                                          ready: "#eeeeee", 
			                                          depressed: "#cccccc", 
			                                          disabled: "#aaaaaa",
			                                          isHovered: "blue",
			                                          isNotHovered: "white"}} :
			                                       this.props.styleFunc();
		var divStyle = {
		    width: "100px",
		    margin: "10px",
		    textAlign: "center",
		    borderRadius: "10px"
		};
		
        if (this.state.clickState === "disabled") {
        	divStyle['backgroundColor'] = style.colors.disabled;
        }
        else if (this.state.clickState === "depressed") {
        	divStyle['backgroundColor'] = style.colors.depressed;
        }
        else {
        	divStyle['backgroundColor'] = style.colors.ready;
        }
        
    	if (this.state.isHovered)
    		divStyle['border'] = "1px solid " + style.colors.isHovered;
    	else
    		divStyle['border'] = "1px solid " + style.colors.isNotHovered;
    	
		return (
		    <div style={divStyle} 
		         onMouseOver={this.handleMouseOver}
		         onMouseOut={this.handleMouseOut}
		         onClick={this.onClick}>{this.props.title}</div>
		);
	}
});
window.WaitChangeButton = WaitChangeButton;