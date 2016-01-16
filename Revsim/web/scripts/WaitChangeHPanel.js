var WaitChangeHPanel = React.createClass({
	getInitialState: function() {
		if (!this.props.titles)
			throw "missing required titles prop";
		if (!this.props.clickFuncs)
			throw "missing required clickFuncs prop";
		if (this.props.titles.length != this.props.clickFuncs.length)
			throw "titles length must match clickFuncs length";
		return {waitingOn: -1};
	},
	handleButtonClick(index) {
		console.log("WaitChangeHPanel.handleButtonClick.index = " + index);
		this.setState({waitingOn: index});
		this.props.clickFuncs[index]();
	},
	reset: function() {
		console.log("WaitChangeHPanel.reset()");
	},
	componentWillReceiveProps: function(nextProps) {
		console.log("WaitChangeHPanel.componentWillReceiveProps");
		if (!nextProps.waiting) {
			this.setState({waitingOn: -1});
		}
	},
	render: function() {
		console.log("WaitChangeHPanel.render(), this.props.toDisable[0] = " + this.props.toDisable[0]);
		var that = this;
		var makeClickFunc = function(n) {
			return function() {
				that.handleButtonClick(n);
			}
		}
		
		if (this.props.containingStyle) {
			overrideProps(this.props.containingStyle, divStyle);
		}
		
		var kids = [];
		for (var i = 0; i < this.props.titles.length; i++) {
			var title = this.props.titles[i];
			var containingStyle = {
			    float: "left"
			}
			
			var renderState = "ready";
			if (this.state.waitingOn >= 0) {
				if (this.state.waitingOn === i)
					renderState = "depressed";
				else
					renderState = "disabled";
			}
			
			if (this.props.toDisable) {
				console.log("testing index " + i);
				if ($.inArray(i, this.props.toDisable) > -1)
				{
					console.log("index " + i + " is in toDisable");
					renderState = "disabled";
				}
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
window.WaitChangeHPanel = WaitChangeHPanel;