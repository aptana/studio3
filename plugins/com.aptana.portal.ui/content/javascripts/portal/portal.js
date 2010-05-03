/**
 * This script manages the portal observer-observable event mechanism.
 */
// Constants
var Events = {ERROR : 'error', RECENT_FILES : 'recentFiles', PLUGINS : 'plugins', GEMS : 'gemList', APP_VERSIONS : 'app-versions'};
var ConfigurationStatus = {UNKNOWN : 'unknown', OK : 'ok', PROCESSING : 'processing', ERROR : 'error'};

/**
 * The Portal class
 */
var Portal = Class.create({
  initialize: function() {
    this.plugins = new Plugins();
    this.files   = new Files();
    this.gems   = new Gems();
    this.configurations   = new Configurations();
    this.plugins.render($('plugins'), 'configurations');
    this.files.render($('recentFiles'));
    this.gems.render($('gems'));
    this.configurations.render($('app-versions'));
  }, 
  refreshAll: function() {
    this.plugins.dispatchCheck();
    this.gems.dispatchCheck();
    // TODO - dispatch a configuration check?
  }
});

var portal;

// Add observers to the dispatcher
eventsDispatcher.addObserver(Events.RECENT_FILES, function(e) { portal.files.render($('recentFiles')); });
eventsDispatcher.addObserver(Events.GEMS, function(e) { portal.gems.render($('gems'), e); });
eventsDispatcher.addObserver(Events.PLUGINS, function(e) { portal.plugins.render($('plugins'), 'configurations', e); });
eventsDispatcher.addObserver(Events.APP_VERSIONS, function(e) { portal.configurations.render($('app-versions'), e); });

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

function loadPortal() {
  if (!portal) {
    portal = new Portal();
    portal.refreshAll();
  }
}

/**
 * Open a light-box error notification with the given message and title.
 */
function showError(title, msg) {
    DialogHelper.createInfoBox(title, msg, "back to portal");
}

// Returns an element that contains informative text about running this portal outside the studio
function _studioOnlyContent() {
	var resultDiv;
	with(Elements.Builder) {
	   var showInfo;
	   resultDiv = div(
	     div({'class' : 'unavailable'}, 'Content Unavailable'),
	     div(showInfo = a({'href' : '#'}, 'about'))
	   );
	   showInfo.observe('click', function(event) {
	     DialogHelper.createInfoBox("Content Unavailable", "This portal element interacts with Aptana Studio and only available when running within the Studio", "back to portal");
	     event.stop();
	   });
	}
	return resultDiv;
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
 * Finds and return the OS name that the portal is running on.
 * The returned values are 'windows', 'macosx' and 'linux'. We treat any unknown system as 'linux' to simplify things.
 */
function getOS() {
  var OSName="linux";
  if (navigator.appVersion.indexOf("Win")!=-1) OSName="windows";
  if (navigator.appVersion.indexOf("Mac")!=-1) OSName="macosx";
  return OSName;
}

 