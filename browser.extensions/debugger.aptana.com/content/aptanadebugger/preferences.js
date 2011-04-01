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

const nsIPrefBranch2 = Components.interfaces.nsIPrefBranch2;
const prefs = Components.classes["@mozilla.org/preferences-service;1"]
								.getService(nsIPrefBranch2);

const PREFS_DOMAIN = "extensions.aptanadebugger";
const PREF_ENABLELOG = PREFS_DOMAIN+".enableLog";

function onLoad()
{
	
	var label = document.getElementById("ad-about-extensionVersion");
  	label.value += AptanaDebugger.VERSION;

	try {
		var file = AptanaLogger.getFile('log');
		var textfield = document.getElementById("ad-preferences-logFile");
		textfield.value = file.path;
		if ( !file.exists() ) {
			document.getElementById("ad-preferences-logFile.viewButton").setAttribute("disabled", "true");
		}
		
		file = AptanaLogger.getFile('err');
		var textfield = document.getElementById("ad-preferences-errlogFile");
		textfield.value = file.path;
		if ( !file.exists() ) {
			document.getElementById("ad-preferences-errlogFile.viewButton").setAttribute("disabled", "true");
		}
		
	} catch( exc ) {
	}	

	try {
		var checkbox = document.getElementById("ad-preferences-enableLog.checkbox");
		checkbox.checked = prefs.getBoolPref(PREF_ENABLELOG);
	} catch(exc) {
	}	
}

function onAction(action)
{
	if ( action == '' ) {
	}
	return true;
}

function onOpenFile(file)
{
	window.open("file://"+file,"","");
}

function onEnableLog(enable)
{
	prefs.setBoolPref(PREF_ENABLELOG,!!enable);
}
