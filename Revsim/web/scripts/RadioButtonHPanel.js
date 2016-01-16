var RadioButtonHPanel = React.createClass({
	getInitialState: function() {
		if (!this.props.titles)
			throw "missing required titles prop";
		if (!this.props.clickFuncs)
			throw "missing required clickFuncs prop";
		if (this.props.titles.length != this.props.clickFuncs.length)
			throw "titles length must match clickFuncs length";
		var curr = -1;
		if (this.props.selectionIndex)
			curr = parseInt(this.props.selectionIndex);
		return {currentlyDepressed: curr};
	},
	handleButtonClick(index) {
		console.log("RadioButtonHPanel.handleButtonClick.index = " + index);
		if (this.state.currentlyDepressed != index) {
		    this.setState({currentlyDepressed: index});
		    this.props.clickFuncs[index]();
		}
	},
	render: function() {
		var that = this;
		var makeClickFunc = function(n) {
			return function() {
				that.handleButtonClick(n);
			}
		}
		
		var kids = [];
		for (var i = 0; i < this.props.titles.length; i++) {
			var title = this.props.titles[i];
			var containingStyle = {
			    float: "left"
			}
			
			var renderState = "ready";
			if (this.state.currentlyDepressed >= 0) {
				if (this.state.currentlyDepressed === i)
					renderState = "depressed";
			}
			
			var el = React.createElement(SimpleMouseoverButton, 
					                     {
				                          key: i,
				                          s: {colors: ButtonStyles.lightBlue}, 
				                          title:title, 
				                          containingStyle: containingStyle,
				                          onClick: makeClickFunc(i),
				                          renderState: renderState
				                         }, 
					                     null);
			kids.push(el);
		}
		return React.createElement('div', null, kids);
	}
});
window.RadioButtonHPanel = RadioButtonHPanel;