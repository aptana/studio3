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

function AptanaDebuggerChrome() {};

(function() {

// ************************************************************************************************

const nsIWindowWatcher = Components.interfaces.nsIWindowWatcher;
const nsIPrefBranch2 = Components.interfaces.nsIPrefBranch2;

const PREFS_DOMAIN = "extensions.aptanadebugger";
const PREF_HIDDEN = PREFS_DOMAIN+".hidden";
const PREF_ENABLELOG = PREFS_DOMAIN+".enableLog";

const prefs = Components.classes["@mozilla.org/preferences-service;1"]
								.getService(nsIPrefBranch2);

// ************************************************************************************************

this.cmd_aboutAptana = function()
{
	openDialog("chrome://aptanadebugger/content/about.xul", "", "chrome,modal");
}

this.cmd_hideAptana = function()
{
	var statusBar = document.getElementById("adStatusBar");
	statusBar.hidden = true;
	prefs.setBoolPref(PREF_HIDDEN,true);
}

this.cmd_clientAction = function(action)
{
	if ( AptanaDebugger.isInitialized() ) {
		AptanaDebugger.sendClientAction(action);	
	}
}

this.update_menuitems = function()
{
	if ( AptanaDebugger.isInitialized() ) {
		var menuItem = document.getElementById("adStatusContextMenuSuspend");
		menuItem.setAttribute("disabled", enabled && AptanaDebugger.isSuspendEnabled() ? "false" : "true");
		menuItem = document.getElementById("adStatusContextMenuStop");
		menuItem.setAttribute("disabled", enabled ? "false" : "true");
	}
}

this.update_state = function(state)
{
	var statusIcon = document.getElementById("adStatusIcon");
	statusIcon.setAttribute("state", state);
}

var enabled = false;
this.enable = function(state)
{
	var statusBar = document.getElementById("adStatusBar");
	statusBar.setAttribute("enabled", state?"true":"false");
	enabled = state;
}

this.close = function()
{
	window.close();
}

this.onShutdown = function() {}

const windowWatcher = Components.classes['@mozilla.org/embedcomp/window-watcher;1']
									.getService(nsIWindowWatcher);

const WindowWatcherObserver =
{
	observe: function(subject, topic, data)
	{
		if (topic == 'domwindowclosed' && subject == window)
			shutdown();
	}
};

function initialize()
{
	if (this.launchParam) {
		try {
			if(prefs.getBoolPref(PREF_HIDDEN))
				prefs.setBoolPref(PREF_HIDDEN,false);
		} catch(exc) {
			prefs.setBoolPref(PREF_HIDDEN,false);
		}
	}

	try {
		var hidden = prefs.getBoolPref(PREF_HIDDEN);
		if (hidden) {
			var statusBar = document.getElementById("adStatusBar");
			statusBar.hidden = true;	
		}
	} catch(exc) {
		var item = document.getElementById("adStatusContextMenuHide");
		if ( item )
			item.hidden = false;
	}

	var enableLog = false;
	try {
		enableLog = prefs.getBoolPref(PREF_ENABLELOG);
	} catch(exc) {
	}

	if (this.launchParam) {
		if (enableLog) {
			AptanaLogger.init();
		}
		AptanaDebuggerChrome.startDebugger(this.launchParam);
		delete this.launchParam;
	} else {
		AptanaDebuggerChrome.attachToParentDebugger();
	}
	windowWatcher.registerNotification(WindowWatcherObserver);
}

function shutdown()
{
	windowWatcher.unregisterNotification(WindowWatcherObserver);
//XXX: hangs on close	AptanaDebugger.shutdown();
	AptanaLogger.close();
}

// MAIN
const onLoad = function(event) {
	window.removeEventListener("load", onLoad, false);
	initialize();
};
window.addEventListener("load", onLoad, false);

// ************************************************************************************************

function ddd(text)
{
	AptanaLogger.logConsole(text);
}

// ************************************************************************************************

}).apply(AptanaDebuggerChrome);
