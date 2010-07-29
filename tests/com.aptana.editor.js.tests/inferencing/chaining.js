var Utils = {
	create: function() {
		var self = {
			chain: function() {
				return self;
			}
		};
		
		return self;
	}
};

var thing = Utils.create();

thing.chain();