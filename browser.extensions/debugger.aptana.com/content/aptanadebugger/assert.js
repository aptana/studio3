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
