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

// A XUL element with id="location" for managing
// dialog location relative to parent window
var gLocation;
var gParam;

function onLoad()
{
	gParam = window.arguments[0];
	gParam.action = '';
	
	var label = document.getElementById("ad-about-extensionVersion");
  	label.value += AptanaDebugger.VERSION;
	
	var debugButton = document.documentElement.getButton("extra1");
	if ( gParam.debug == "disabled" ) {
		debugButton.setAttribute("disabled", "true");
	} else if ( gParam.debug == "suspend" ) {
		debugButton.setAttribute("label",document.getElementById("suspendButton.label").value);		
	}
	var assertCaption = document.getElementById("ad-assert-caption");
	var assertMessage = document.getElementById("ad-assert-message");
	
	if ( gParam.caption != null ) {
		assertCaption.value = gParam.caption;
		assertCaption.style.display = "inline";
	}
	var messages = gParam.messages;
	for( var i = 0; i < messages.length-1; ++i ) {
		var elem = assertMessage.cloneNode(true);
		elem.value = messages[i];
		assertMessage.parentNode.insertBefore(elem,assertMessage);
	}
	assertMessage.value = messages[messages.length-1];
	
	restoreWindowLocation();
}

function onAction(action)
{
	gParam.action = action;
	saveWindowLocation();
	return true;
}

function restoreWindowLocation()
{
  gLocation = document.getElementById("location");
  if (gLocation)
  {
    window.screenX = Math.max(0, Math.min(window.opener.screenX + Number(gLocation.getAttribute("offsetX")),
                                          screen.availWidth - window.outerWidth));
    window.screenY = Math.max(0, Math.min(window.opener.screenY + Number(gLocation.getAttribute("offsetY")),
                                          screen.availHeight - window.outerHeight));
  }
}

function saveWindowLocation()
{
  if (gLocation)
  {
    var newOffsetX = window.screenX - window.opener.screenX;
    var newOffsetY = window.screenY - window.opener.screenY;
    gLocation.setAttribute("offsetX", window.screenX - window.opener.screenX);
    gLocation.setAttribute("offsetY", window.screenY - window.opener.screenY);
  }
}
