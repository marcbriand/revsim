var WaitChangeButton = React.createClass({
	// {
	//   isHovered:  (boolean) true if mouse is over button
	getInitialState: function() {
		console.log("WaitChangeButton, title = " + this.props.title + " renderState = " + this.props.renderState);
		return {isHovered: false};
	},
    onClick: function() {
    	console.log("WaitChangeButton.onClick(), title = " + this.props.title);
    	console.log("..isHovered = " + this.state.isHovered);
    	if (this.props.renderState === "ready") {
    		if (this.props.onClick)
    			this.props.onClick();
    	}
    },
    handleMouseOver: function() {
    	console.log("onMouseOver()");
    	if (this.props.renderState === "ready") {
    		this.setState({isHovered: true});
    	}
    },
    handleMouseOut: function() {
    	console.log("onMouseOut()");
    	this.setState({isHovered: false});
    },
    reset: function() {
    	this.setState(this.getInitialState());
    },
    disable: function() {
    	this.setState({isHovered: false, clickState: "disabled"});
    },
	render: function() {
		console.log("WaitChangeButton.render(), title = " + this.props.title + " s = " + this.props.s + " renderState = " + this.props.renderState);
	    var style = this.props.s == null ? {colors: {
				                                     ready: "#eeeeee", 
				                                     depressed: "#cccccc", 
				                                     disabled: "#aaaaaa",
				                                     isHovered: "blue",
				                                     isNotHovered: "white"}} : this.props.s;
			                                          
			                                          
	    var divStyle = {
		    width: "100px",
		    margin: "10px",
		    textAlign: "center",
		    borderRadius: "10px"
		};
		
		if (this.props.containingStyle) {
			overrideProps(this.props.containingStyle, divStyle);
		}
		
        if (this.props.renderState === "disabled") {
        	divStyle['backgroundColor'] = style.colors.disabled;
        }
        else if (this.props.renderState === "depressed") {
        	divStyle['backgroundColor'] = style.colors.depressed;
        }
        else {
        	divStyle['backgroundColor'] = style.colors.ready;
        }
        
    	if (this.state.isHovered && this.props.renderState === "ready")
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