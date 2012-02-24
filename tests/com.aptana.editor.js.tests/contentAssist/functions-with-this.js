function abc() {
	this.property = 10;
	this.method = function() { return true; };
	this.|
}

function def() {
	this.abc = 10;
	this.def = 20;
}
