// SDK CLASS
function MobilewareSdk() {
	this.name = "customerevent";
	this.version = "0.0.0";
	this.organization = "appcelerator__inc";
	this.url = "https://f1d859c3876f9053f435dfe3271da0428c5aecfd.cloudapp-enterprise-preprod.appcelerator.com";
	this.apis = {};

	// API: customerevent
	this.apis["customerevent"] = new function() {
		var self = this,
			namespaces = {
				"account": {}
			};

		// create API namespace structure
		Object.keys(namespaces).forEach(function(key) {
			self[key] = namespaces[key];
		});
		this.name = 'customerevent';
		this.version = '0.0.0';

		// API implementation
		this.login = function(model, opts, callback) {
			callback = util.maybeCallback(arguments[arguments.length - 1]);
			if (!opts || util.isFunction(opts)) {
				opts = {};
			}

			makeRequest('login', 'POST', model, opts, function(err, result) {
				if (result && result.connection && result.connection.accessToken) {
					self.accessToken = result.connection.accessToken;
				}
				return callback(err, result);
			});

		};
		this.account.readAll = function(opts, callback) {
			callback = util.maybeCallback(arguments[arguments.length - 1]);
			if (!opts || util.isFunction(opts)) {
				opts = {};
			}

			makeRequest('account' + '?accessToken=' + (self.accessToken || ''), 'GET', null, opts, callback);

		};
		this.account.create = function(model, opts, callback) {
			callback = util.maybeCallback(arguments[arguments.length - 1]);
			if (!opts || util.isFunction(opts)) {
				opts = {};
			}

			makeRequest('account' + '?accessToken=' + (self.accessToken || ''), 'PUT', model, opts, callback);

		};
		this.account.delete = function(id, opts, callback) {
			callback = util.maybeCallback(arguments[arguments.length - 1]);
			if (!opts || util.isFunction(opts)) {
				opts = {};
			}

			makeRequest('account' + '/' + id + '?accessToken=' + (self.accessToken || ''), 'DELETE', null, opts, callback);

		};
		this.account.readOne = function(id, opts, callback) {
			callback = util.maybeCallback(arguments[arguments.length - 1]);
			if (!opts || util.isFunction(opts)) {
				opts = {};
			}

			makeRequest('account' + '/' + id + '?accessToken=' + (self.accessToken || ''), 'GET', null, opts, callback);

		};
		this.account = function(id, model, opts, callback) {
			callback = util.maybeCallback(arguments[arguments.length - 1]);
			if (!opts || util.isFunction(opts)) {
				opts = {};
			}

			makeRequest('account' + '/' + id + '?accessToken=' + (self.accessToken || ''), 'POST', model, opts, callback);

		};
		// Backbone sync
		this.sync = function sync(method, model, options) {
			console.warn('warning: sync not implemented, but ' + method + ' was requested');
		};
	}();
}

MobilewareSdk.prototype.api = function api(name) {
	var apiObj = this.apis[name];
	if (!apiObj) {
		throw new Error('unknown api "' + name + '"');
	}
	return apiObj;
};

MobilewareSdk.prototype.toString = function toJson() {
	return JSON.stringify(this, function(key, value) {
		if (typeof value === 'function') {
			var func = value.toString();
			return func.substring(0, func.indexOf(')') + 1);
		}
		return value;
	}, '  ');
};

new MobilewareSdk();