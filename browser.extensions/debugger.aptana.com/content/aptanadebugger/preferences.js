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
