Samples = Class.create({
	/**
	 * Render the items that will import a sample project.
	 */
	render : function() {
		// Get the div for the Open-View example
		projectSamplesDiv = $('projectSamples');
		with(Elements.Builder) {
			var sampleList = [];
			if( typeof (console) !== 'undefined' && typeof (dispatch) !== 'undefined') {
				console.log("Dispatching the 'getSamples' action on the 'portal.samples' controller...");
				sampleList = dispatch($H({
					controller : 'portal.samples',
					action : "getSamples"
				}).toJSON()).evalJSON();
			}
			samplesTable = table({
				"border" : "1",
				"style" : "border-collapse:collapse"
			}, tableBody = tbody(tr(th("Category"), th("Snippet"), th("Description"))));
			// Create the rows in the sample-table.
			// Each sample in the samples list contains these attributes:
			// 1. category
			// 2. name
			// 3. id
			// 4. description
			// 5. image (currently, empty)
			for(var i = 0; i < sampleList.length; i++) {
				sampleItem = sampleList[i];
				row = tr(td(sampleItem["category"]), td( snippetLink = a({'href' : '#', "snippetID" : sampleItem["id"]}, sampleItem["name"])), td(sampleItem["description"]));
				tableBody.appendChild(row);
				snippetLink.observe('click', this.importSample);
			}
			projectSamplesDiv.appendChild(samplesTable);
		}
	},
	
	importSample: function(e) {
    if( typeof (console) !== 'undefined' && typeof (dispatch) !== 'undefined') {
      snippetID = e.element().getAttribute("snippetID");
			console.log("Dispatching the 'importSample' action on the 'portal.samples' controller...");
			sampleList = dispatch($H({
				controller : 'portal.samples',
				action : "importSample", 
				args: [snippetID].toJSON()
			}).toJSON());
		}
    return false;
  },
  
  // Accepts an update that was triggered by a browser-notification when a sample
  // was loaded (added) or unloaded (deleted).
  // The event holds the given information for the nature of the notification (added/deleted), 
  // and hold the Sample-Info in its 'data' mapping.
  // The Sample in the 'data' will hold these attributes:
	// 1. category
	// 2. name
	// 3. id
	// 4. description
	// 5. image (currently, empty)
  update : function(e) {
  	if( typeof (console) !== 'undefined') {
  		console.log("A Sample was " + e.eventType);
  	}
  }
});

