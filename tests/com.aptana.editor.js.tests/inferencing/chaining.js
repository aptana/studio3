var Utils = {
	create: function() {
		var utilsSelf = {
			chain: function() {
				return utilsSelf;
			}
		};
		
		return utilsSelf;
	}
};

var thing = Utils.create();

thing.chain();