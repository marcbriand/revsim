window.overrideProps = function(src, target) {
	for (var k in src) {
		target[k] = src[k];
	}
}