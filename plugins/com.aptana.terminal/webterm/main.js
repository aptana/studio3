var terminal;
var terminalElementID = "terminal";
var fontInfo;
var RESIZE_INTERVAL = 250;
var resizeID = null;

/**
 * Setup a timeout to force the terminal to resize to the window.
 * The actual resize will only occur after a delay. This function
 * takes care of clearing any previously queued resize and
 * reschedules a new timeout
 */
function autoSize()
{
	if (resizeID !== null)
	{
		window.clearTimeout(resizeID);
	}
	
	resizeID = window.setTimeout(resizeHelper, RESIZE_INTERVAL);
}

/**
 * Copy the currently selected text from the terminal
 */
function copy()
{
	return terminal.getSelectedText();
}

/**
 * Force the communication handler to process any pending input.
 * This is used by Eclipse when we've sent text directly to the
 * underlying shell. Without this call, there may be a delay as long
 * as 2 seconds (or whatever max interval has been set in the
 * communication handle).
 */
function getInput()
{
	terminal.getCommunicationHandler().update();
}

/**
 * Determine if there is a selection in the terminal
 * 
 * @return {Boolean}
 */
function hasSelection()
{
	return terminal.hasSelection();
}

/**
 * Initialize the terminal and its environment
 */
function init()
{
	var query = getQuery();
	
	var config = {
		id: (query !== null && query.hasOwnProperty("id")) ? query.id : null,
		autoStart: false,
		showTitle: false
	};
	
	// create terminal
	terminal = new Term(terminalElementID, 80,20, config);
	
	// save reference to fontInfo
	fontInfo = terminal.getFontInfo();
	
	// turn on tracking for mono-spaced fonts
	if (fontInfo.isMonospaced())
	{
		// get rule for div.webterm pre span.b
		var rule = getCSSRule("div.webterm pre span.b");
		
		if (rule)
		{
			// apply tracking
			rule.style.letterSpacing = fontInfo.getTracking();
			
			// and let know font info know we've done that
			fontInfo.useTracking(true);
		}
	}
	
	// resize to fit
	resizeHelper();
	
	// start processing
	terminal.toggleRunState();
}

/**
 * onThemeChange
 */
function onThemeChange()
{
	// reload CSS
	var s = document.getElementById('ss');
	var h = s.href.replace(/(&|\\?)forceReload=d /,'');
	
	s.href = h + (h.indexOf('?') >= 0 ? '&' : '?') + 'forceReload=' + (new Date().valueOf());
	
	// reset the font and terminal size after a delay to allow the
	// CSS to reload
	window.setTimeout(
		function()
		{
			// reset font info cache
			fontInfo.reset();

			// force resize						
			resizeHelper();
		},
		1000
	);
}

/**
 * Perform the actual terminal resize. This can be invoked directly
 * or will be called after a delay in autoResize
 */
function resizeHelper()
{
	// clear timeout id
	resizeID = null;
	
	// resize terminal to fill the window
	terminal.sizeToWindow();
}

/**
 * Select all of the text in the terminal
 */
function selectAll()
{
	terminal.selectAll();
}
