function MobilewareSdk() {
	this.customerevent = new function() {
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

		}();
	};
}

new MobilewareSdk();