function abc() {
	this.property = 10;
	this.method = function() { return true; };
}

function def() {
	this.abc = 10;
	this.def = 20;
	
	function def_internal() {
		this.ghi = 10;
		this.jkl = function() {};
		this.|
	}
}
