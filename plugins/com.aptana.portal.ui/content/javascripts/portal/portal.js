/**
 * This script manages the portal observer-observable event mechanism.
 */
// Constants
var Events = {ERROR : 'error', RECENT_FILES : 'recentFiles', PLUGINS : 'plugins', GEMS : 'gemList'};
var ConfigurationStatus = {UNKNOWN : 'unknown', OK : 'ok', PROCESSING : 'processing', ERROR : 'error'};

// A list of plugins that we display and query for.
// Every item has a display name and an array that contains: plug-in id for existance check, min version, update site url, feature id on the update site.
var pluginList  = $H({'Aptana Git': ['com.aptana.git.core', '2.0.0', 'http://download.aptana.org/tools/studio/plugin/install/studio', 'com.aptana.ide.feature.studio'],
    'Aptana JavaScript': ['com.aptana.editor.js', '1.0.0', 'http://download.aptana.org/tools/studio/plugin/install/studio', 'com.aptana.ide.feature.studio'], 
    'Eclipse PHP Developer Tools' : ['org.eclipse.php', '2.0.0', 'http://download.eclipse.org/tools/pdt/updates/2.0/', 'org.eclipse.php']
});

// Add observers to the dispatcher
eventsDispatcher.addObserver(Events.RECENT_FILES, function(e) { showRecentlyOpenedFiles($('recentFiles')); });
eventsDispatcher.addObserver(Events.GEMS, function(e) { showGems($('gems'), e); });
eventsDispatcher.addObserver(Events.PLUGINS, function(e) { showPlugins($('plugins'), e); });

/**
 * This custom error handler is needed when the Portal is viewed in the 
 * Studio internal browser.
 * The Studio is the one that makes the call to hook the window.onerror event to this handler.
 */
function customErrorHandler(desc,page,line) {
 alert(
  'A JavaScript error occurred! \n'
 +'\nError: \t'+desc
 +'\nURL:      \t'+page
 +'\nLine number:       \t'+line
 );
 // make sure we return false, so the error will also propogate to other
 // error handlers, such as firebug.
 return false;
}

function loadConfigurationPortal() {
    showRecentlyOpenedFiles($('recentFiles'));
    showGems($('gems'));
    showPlugins($('plugins'));
}

function showRecentlyOpenedFiles(parentElement) {
    // Make sure that the Studio dispatch call is available
	if (!this.dispatch) {
	  parentElement.appendChild(_studioOnlyContent());
	  return;
	}
	var recentFiles = dispatch($H({controller:"portal.recentFiles", action:"getRecentFiles"}).toJSON());
	if (recentFiles) {
		var filesJSON = recentFiles.evalJSON();
		if (_isErrorStatus(filesJSON)) {
			parentElement.appendChild(_errorStatus(filesJSON));
	  		return;
		}
		var fileNames = $H(filesJSON).keys();
		var filePaths = $H(filesJSON).values();
		var filesCount = fileNames.size();
		var items;
		with(Elements.Builder) {
			// We have to wrap the rows in a tbody, otherwise, the internal browser
			// fails to display the rows.
			var tbodyItem;
			items = table({'id' : 'recentFilesTable'}, tbodyItem = tbody());
			for( var i = 0; i < filesCount; i++ ) {
			  var openFileLink;
			  var itemRow = tr(
			    td(
				  openFileLink = a({'href' : '#', 'file' : filePaths[i]}, fileNames[i])
				)
			  );
			  // make a call to open the file in the editor
			  openFileLink.observe('click', function(event) {
     			  var fileToOpen = event.element().getAttribute('file');
				  // make sure to nest the fileToOpen string as a JSON.
				  // Another option is to pass it in an array just like that:
				  // {controller:"portal.recentFiles", action:"openRecentFiles", args:[fileToOpen].toJSON()}
				  dispatch($H({controller:"portal.recentFiles", action:"openRecentFiles", args:fileToOpen.toJSON()}).toJSON());
				  // Stop the event, otherwise we loose the eclipse BroswerFunctions!
				  event.stop();
  		      });
			  tbodyItem.appendChild(itemRow);
			}
		}
		var oldTable = $('recentFilesTable');
		if (oldTable) {
			parentElement.replaceChild(items, oldTable);
		} else {
			parentElement.appendChild(items);
		}
	} else {
		_clearDescendants(parentElement);
		parentElement.appendChild(Elements.Builder.div('No Recent Files'));
	}
}

/**
 * Get the installed gems from the Studio and show them
 * @param parentElement
 * @param data - A JSON object, or null
 */
function showGems(parentElement, data) {
	if (!this.dispatch) {
	  parentElement.appendChild(_studioOnlyContent());
	  return;
	}
	// Make sure we have the data as JSON
	var gems = data;
	if (!gems) {
		gems = dispatch($H({controller:"portal.gems", action:"getInstalledGems"}).toJSON());
		if (gems) {
		  	gems = gems.evalJSON();
		}
	}
	if (gems) {
		if (_isErrorStatus(gems)) {
			_clearDescendants(parentElement);
			parentElement.appendChild(_errorStatus(gems));
	  		return;
		}
		var gemsContent;
		var status = gems.data.status;
		with(Elements.Builder) {
			// Prepare some common elements
			var checkLink;
			var tbodyItem;
			var checkDiv = div({'class' : 'check'}, checkLink = a({'href' : '#'}, 'Check for Gems'));
			gemsContent = div({'class':'test'}, table({'id' : 'installedGems'}, tbodyItem = tbody()));
			// Do an actual check for the returned JSON	
			if (status == ConfigurationStatus.UNKNOWN) {
				// Display a link to check for the installed gems
				gemsContent = checkDiv;
			} else if (status == ConfigurationStatus.PROCESSING) {
			    gemsContent = div({'class' : 'processing'}, 'Checking for Gems...');
			} else if (status == ConfigurationStatus.ERROR) {
				gemsContent = div({'class' : 'gemsError'}, 'Error while checking for gems', checkDiv);
			} else {
				// Read the result from the given JSON and list the gems in a table
				if (gems.data && gems.data.gems) {
					var gemNames = gems.data.gems.split(';');
					var gemsCount = gemNames.size();
					for( var i = 0; i < gemsCount; i++ ) {
						var itemRow = tr(
							td({'class' : 'gem'},
							  gemNames[i]
							)
						);
						tbodyItem.appendChild(itemRow);
					}
				} else {
					gemsContent.appendChild(div({'class' : 'no-gems'}, 'No gems were found'));
				}
				gemsContent.appendChild(checkDiv);
			}
			// Dispatch a check for gems when the user clicks the link
			checkLink.observe('click', function(event) {
				dispatch($H({controller:"portal.gems", action:"computeInstalledGems"}).toJSON());
				// Stop the event, otherwise we loose the eclipse BroswerFunctions!
				event.stop();
  		  });
		}
		// Clear anything that we had under this parent element before and put there the new elements
		_clearDescendants(parentElement);
		parentElement.appendChild(gemsContent);
	}
}

/**
 * Show the plugins section
 */
function showPlugins(parentElement) {
  if (!this.dispatch) {
    parentElement.appendChild(_studioOnlyContent());
    return;
  }
	// We ask for a list of all the installed plugin. This list will then be checked for some specific IDs to verify 
	// if the Studio has a specific plugin.
	var allPlugins = dispatch($H({controller:"portal.plugins", action:"getInstalledPlugins"}).toJSON());
	allPlugins = allPlugins.evalJSON();
	var status = allPlugins.data.status;
	allPlugins = $H(allPlugins.data.plugins);
	var items;
	var links;
	var installLink;
	var checkLink;
	with(Elements.Builder) {
		// We have to wrap the rows in a tbody, otherwise, the internal browser
		// fails to display the rows.
		var tbodyItem;
		items = table({'id' : 'plugins'}, tbodyItem = tbody());
		var pluginNames = pluginList.keys();
		var items_count = pluginNames.size();
		var pluginSpan;
		var actionsSpan;
    for( var i = 0; i < items_count; i++ ) {
      var plugin = pluginList.get(pluginNames[i]);
		  var itemRow = tr(
		    td(
		      pluginSpan = span({'class' : 'unknown ' + plugin[0], 'pluginId' : plugin[0], 'minVersion' : plugin[1]}, pluginNames[i]), 
		      actionsSpan = span()
		    )
		  );
		  // Check the status of the plugin. In case needed, disply a link to install/update the plugin.
		  switch(status) {
		    case ConfigurationStatus.PROCESSING:
		      pluginSpan.setAttribute('className', 'processing');
		    break;
		    case ConfigurationStatus.OK:
		      // In this case, we need to check whether this plugin needs an update/install
		      var pluginInfo = allPlugins.get(plugin[0]);
		      // In case we never dispatched the check, the pluginInfo will be undefined at this point.
		      if (pluginInfo) {
  		      if (pluginInfo.exists == "yes") {
  		        // We have this plugin installed, and we just need to check it has the right version
  		        pluginSpan.setAttribute('installedVersion', pluginInfo.version);
  		        if (pluginInfo.compatibility == 'ok') {
  		          pluginSpan.setAttribute('className', 'ok');
  		          pluginSpan.setAttribute('pluginStatus', 'ok');
  		        } else {
  		          pluginSpan.setAttribute('className', 'notice');
  		          pluginSpan.setAttribute('pluginStatus', 'update');
  		          var updateLink;
                var updateSpan = span({'class' : 'action update'},  updateLink = a({'href' : '#', 'update-site' : plugin[2], 'feature-id' : plugin[3]}, 'update'));
                actionsSpan.appendChild(updateSpan);
                updateLink.observe('click', function(event) {
                  _installPlugin(event, pluginList.values());
                  event.stop();
                  return true;
                });
  		        }
  		      } else {
  		        // We don't have this plugin installed. Display an install link.
  		        var installLink;
  		        var installSpan = span({'class' : 'action install'},  installLink = a({'href' : '#', 'update-site' : plugin[2], 'feature-id' : plugin[3]}, 'install'));
  		        pluginSpan.setAttribute('pluginStatus', 'install');
  		        pluginSpan.setAttribute('className', 'missing');
  		        actionsSpan.appendChild(installSpan);
  		        installLink.observe('click', function(event) {
                _installPlugin(event, pluginList.values());
                event.stop();
                return true;
              });
  		      }
  		    }
		    break;
		  }
		  tbodyItem.appendChild(itemRow);
		  // register the tooltips
		  pluginSpan.observe('mouseover', function(e) {
		    _showTooltip(e);
		    e.stop();
		    return true;
  		});
  		pluginSpan.observe('mouseout', function(e) {
  		  tooltip.hide();
  		  return true;
  		});
		}
		
		links = div(
		  div(installLink = a({'href':'#'}, "Install a new plug-in")),
		  div(checkLink = a({'href':'#'}, "Check for installed plug-ins"))
		);
		
		installLink.observe('click', function(event) {
		    // We pass the plugins list to this function as well. The function will re-compute the installed plugins right after the 
		    // 'New Software' dialog is closed. The first and second args are the update-site URL and the feature-id, which are not needed in this case.
        dispatch($H({controller:"portal.plugins", action:"openPluginsDialog", args : ["", "", pluginList.values()].toJSON()}).toJSON());
        // Stop the event, otherwise we loose the eclipse BroswerFunctions!
        event.stop();
    });
    checkLink.observe('click', function(event) {
        dispatch($H({controller:"portal.plugins", action:"computeInstalledPlugins", args : pluginList.values().toJSON()}).toJSON());
        // Stop the event, otherwise we loose the eclipse BroswerFunctions!
        event.stop();
    });
	}
	_clearDescendants(parentElement);
	parentElement.appendChild(items);
	parentElement.appendChild(links);
}

// Returns an element that contains informative text about running this portal outside the studio
function _studioOnlyContent() {
	return Elements.Builder.div({'class' : 'unavailable'}, 'Content only available inside Aptana Studio');
}

// Remove all the descendants from the parent element
function _clearDescendants(parentElement) {
	if (parentElement) {
		var descendants = parentElement.descendants();
		var items_count = descendants.size();
        for( var i = 0; i < items_count; i++ ) {
			descendants[i].remove();
		}
	}
}
function _isErrorStatus(jsonContent) {
	return jsonContent.event == Events.ERROR;
}

/**
 * Returns a DIV with an error details. 
 * The error details will appear only if the jsonError contains an errorDetails in its data hash.
 */
function _errorStatus(jsonError) {
	var d;
	with(Elements.Builder) {
		d = div({'class' : 'errorStatus'}, div({'class' : 'errorTitle'}, 'An error occured'));
		if (jsonError.data.errorDetails) {
			d.appendChild(div({'class' : 'errorDetails'}, jsonError.data.errorDetails));
		}
	}
	return d;
}

/**
 * Show a tooltip for the plug-in details.
 */
function _showTooltip(event) {
  var pid = event.target.getAttribute('pluginId');
  var pMinVersion = event.target.getAttribute('minVersion');
  var pInstalledVersion = event.target.getAttribute('installedVersion');
  var pStatus = event.target.getAttribute('pluginStatus');
  var ttHtml = '<div><b>Plugin-ID:</b> ' + pid + '</div>';
  if (pStatus) {
    switch (pStatus) {
      case 'install':
        ttHtml += '<div><b>Version:</b> ' + pMinVersion + '</div><div><b>Status:</b> This plugins is not installed</div>';
      break;
      case 'ok':
        ttHtml += '<div><b>Version:</b> ' + pInstalledVersion + '</div><div><b>Status:</b> This plugins is installed</div>';
      break;
      case 'update':
        ttHtml += '<div><b>Version:</b> ' + pInstalledVersion + '</div><div><b>Status:</b> This plugins is installed but needs to be updated to version '+pMinVersion+'</div>';
      break;
    }
  }
  tooltip.show(ttHtml);
}

/**
 * Dispatch an event that will trigger the eclipse 'Install New Software' dialog to appear, preloaded with a specific update site.
 * The update site is drawn from the event's target 'update-site' attribute. 
 * 
 * @param event The event that triggered this call.
 * @param postVerificationPlugins A JSON representation of the plugins we want to check right after the installation is done. 
 */
 function _installPlugin(event, postVerificationPlugins) {
   var siteURL = event.target.getAttribute('update-site');
   var featureId = event.target.getAttribute('feature-id');
   if (!siteURL || !featureId) {
     alert("There was a problem retrieving the update site for this plugin. Please try to refresh the page.\nIf that did not resolve the issue, contact Aptana's support.");
     return false;
   }
   // We pass the plugins list to this function as well. The function will re-compute the installed plugins right after the 
   // 'New Software' dialog is closed. 
   // The first arg param is the update-site URL.
   // The second arg param is the feature-id that we want to install.
   // The third arg param is the postVerificationPlugins JSON
   dispatch($H({controller:"portal.plugins", action:"openPluginsDialog", args : [siteURL, featureId, postVerificationPlugins].toJSON()}).toJSON());
 }
 
 