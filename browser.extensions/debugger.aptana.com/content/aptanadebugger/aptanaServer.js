/* ***** BEGIN LICENSE BLOCK *****
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 * 
 * Contributor(s):
 *     Max Stepanov (Appcelerator, Inc.)
 *
 * ***** END LICENSE BLOCK ***** */

const global = this;

(function(scope) {
	const resources = [
		"chrome://aptanadebugger/content/aptanaCommon.js",
		"chrome://aptanadebugger/content/aptanaLogger.js",
		"chrome://aptanadebugger/content/aptanaUtils.js",
		"chrome://aptanadebugger/content/aptanaStrings.js",
		"chrome://aptanadebugger/content/aptanaSockets.js",
		"chrome://aptanadebugger/content/aptanaDebugAPI.js",
		"chrome://aptanadebugger/content/aptanaDebugger.js",
		"chrome://aptanadebugger/content/aptanaFBService.js",
	];

	const scriptLoader = CC["@mozilla.org/moz/jssubscript-loader;1"].getService(CI.mozIJSSubScriptLoader);

	for(var i = 0; i < resources.length; ++i) {
		scriptLoader.loadSubScript(resources[i], scope);
	}
})(this);

function AptanaDebuggerChrome() {};

(function() {

// ************************************************************************************************

// ************************************************************************************************

this.update_state = function(state)
{
	/* do nothing */
}

var enabled = false;
this.enable = function(state)
{
	/* do nothing */
}

this.close = function()
{
}

this.onShutdown = function()
{
	shutdown();
}

function initialize()
{
	if (global.launchParam) {
		AptanaLogger.init();
		AptanaDebugger.initDebugger(global.launchParam);
		delete global.launchParam;
	}
}

function shutdown()
{
	AptanaLogger.close();
	Jaxer.private.hasDebugger = false;
}

// MAIN
initialize();

// ************************************************************************************************

function ddd(text)
{
	AptanaLogger.logConsole(text);
}

// ************************************************************************************************

}).apply(AptanaDebuggerChrome);
