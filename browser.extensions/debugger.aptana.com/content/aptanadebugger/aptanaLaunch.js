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

(function() {

// ************************************************************************************************

const APTANA_DEBUGGER_LAUNCH_URL = new RegExp("^http://www\\.aptana\\.com/\\?debugger=true&port=((server:)?[0-9]+)$");

const nsISupports = Components.interfaces.nsISupports;
const nsIWebNavigation = Components.interfaces.nsIWebNavigation;
const nsIWebProgress = Components.interfaces.nsIWebProgress;
const nsIWebProgressListener = Components.interfaces.nsIWebProgressListener;
const nsIDOMChromeWindow = Components.interfaces.nsIDOMChromeWindow;
const nsIBrowserDOMWindow = Components.interfaces.nsIBrowserDOMWindow;
const nsIWindowMediator = Components.interfaces.nsIWindowMediator;

const NS_NOINTERFACE = Components.results.NS_NOINTERFACE;

const OPEN_NEWWINDOW = nsIBrowserDOMWindow.OPEN_NEWWINDOW;

// ************************************************************************************************

function processSpecialURLs(url)
{
	if (  APTANA_DEBUGGER_LAUNCH_URL.test(url) ) {
		var match = APTANA_DEBUGGER_LAUNCH_URL.exec(url);
		var param = match[1];
		if ( param.substr(0,7) == "server:" || parseInt(param) > 1024 ) {
			this.launchParam = param;
			preventCheckDefaultBrowserDialog();
		}
	} else {
		forceNewWindow();
	}
}

function forceNewWindow()
{
	const onLoad = function(event) {
		window.removeEventListener("load", onLoad, false);
		function wrapBrowserDOMWindow() {
			window.QueryInterface(nsIDOMChromeWindow).browserDOMWindow = {
				wrappedObject: window.QueryInterface(nsIDOMChromeWindow).browserDOMWindow,
				QueryInterface : function(aIID) {
					if (aIID.equals(nsIBrowserDOMWindow)
						|| aIID.equals(nsISupports)) {
						return this;
					}
					throw NS_NOINTERFACE;
				},
				openURI : function(aURI, aOpener, aWhere, aContext) {
					if ( aURI ) {
						var url = aURI.prePath+aURI.path;
						if ( APTANA_DEBUGGER_LAUNCH_URL.test(url) ) {
							aWhere = OPEN_NEWWINDOW;
						}
					}
					return this.wrappedObject.openURI(aURI, aOpener, aWhere, aContext);
				},
				isTabContentWindow : function(aWindow) {
					return this.wrappedObject.isTabContentWindow(aWindow);
				}
			};
		}
		if ( window.QueryInterface(nsIDOMChromeWindow).browserDOMWindow != null ) {
			wrapBrowserDOMWindow();
		} else {
			setTimeout(wrapBrowserDOMWindow,0);
		}
	};
	window.addEventListener("load", onLoad, false);
}

function preventCheckDefaultBrowserDialog()
{
	var shellService = AptanaUtils.getShellService();
	if ( shellService && shellService.shouldCheckDefaultBrowser ) {
		/* do check to get subsequest calls to
		 * nsIShellService.shouldCheckDefaultBrowser return false
		 */
		shellService.isDefaultBrowser(true);
	}
}

this.attachToParentDebugger = function()
{
	const windowManager = Components.classes['@mozilla.org/appshell/window-mediator;1']
										.getService(nsIWindowMediator);
	var enumerator = windowManager.getEnumerator(null);
	while( enumerator.hasMoreElements() ) {
		var win = enumerator.getNext();
		if ( win != window
				&& win.location.toString() == 'chrome://browser/content/browser.xul'
				&& "AptanaDebugger" in win 
				&& win.AptanaDebugger.isInitialized() ) {
			AptanaDebugger.attachToParent(win.AptanaDebugger);
		}
	}
}

this.startDebugger = function(port)
{
	const listener = new AptanaUtils.WebProgressListener();
	listener.onStateChange = function(aWebProgress, aRequest, aStateFlags, aStatus) {
			if ( aStateFlags & nsIWebProgressListener.STATE_STOP) {
				window.getBrowser().removeProgressListener(this);
				AptanaDebuggerChrome.enable(true);
				window.setTimeout(function() {
					try {
						AptanaDebugger.initDebugger(port);
					} catch(exc) {
						dd(exc,'err');
					}
				}, 0);
			}
	};
	try {
		window.getBrowser().stop(nsIWebNavigation.STOP_ALL);
		window.getBrowser().addProgressListener(listener,nsIWebProgress.NOTIFY_STATE_WINDOW);
		window.getWebNavigation().loadURI("chrome://aptanadebugger/skin/init.html",nsIWebNavigation.LOAD_FLAGS_BYPASS_HISTORY,null,null,null);
	} catch(exc) {
		dd(exc,'err');
	}
}

if ( "arguments" in window && window.arguments.length > 0 ) {
	var arg = window.arguments[0];
	if (arg && typeof(arg) == 'object' && "QueryInterface" in arg) {
		try {
			var array = arg.QueryInterface(Components.interfaces.nsISupportsArray);
			if (array) {
				arg = array.GetElementAt(0).QueryInterface(Components.interfaces.nsISupportsString);
			}
		} catch (exc) {
			dd(exc, 'err');
		}
	}
	processSpecialURLs(arg);
}

// ************************************************************************************************

function dd(message,level)
{
	if ( typeof(message) == 'object' && "fileName" in message && "lineNumber" in message ) {
		message = ""+message+" at "+message.fileName+":"+message.lineNumber;
	}
	try {
		if ( level == 'err' ) {
			AptanaLogger.logError(message);
			return;
		}
		if ( AptanaDebugger.DEBUG ) {
			AptanaLogger.log(message,level);
		}
	} catch(exc) {
	}
}

function ddd(text)
{
	AptanaLogger.logConsole(text);
}

// ************************************************************************************************

}).apply(AptanaDebuggerChrome);
