function MobilewareSdk() {
	this.name = "customerevent";
	this.version = "0.0.0";
}

MobilewareSdk.prototype.api = function(name) {
	var apiObj = this.apis[name];
	if (!apiObj) {
		throw new Error('unknown api "' + name + '"');
	}
	return apiObj;
};

MobilewareSdk.prototype.toString = function() {
	return JSON.stringify(this, function(key, value) {
		if (typeof value === 'function') {
			var func = value.toString();
			return func.substring(0, func.indexOf(')') + 1);
		}
		return value;
	}, '  ');
};

new MobilewareSdk();