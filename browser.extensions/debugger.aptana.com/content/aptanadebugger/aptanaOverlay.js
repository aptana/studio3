/* ***** BEGIN LICENSE BLOCK *****
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain Eclipse Public Licensed code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 * 
 * Contributor(s):
 *     Max Stepanov (Aptana, Inc.)
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
