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

function AptanaLogger() {}

(function() {

// ************************************************************************************************

const NS_OS_TEMP_DIR = "TmpD"

const nsIProperties = Components.interfaces.nsIProperties;
const nsIFileOutputStream = Components.interfaces.nsIFileOutputStream;
const nsIXULAppInfo = Components.interfaces.nsIXULAppInfo;
const nsIXULRuntime = Components.interfaces.nsIXULRuntime;
const nsIConsoleService = Components.interfaces.nsIConsoleService;
const nsIFile = Components.interfaces.nsIFile;

const isClientDebugger = (typeof(document) != "undefined");

// ************************************************************************************************

var fos;

this.init = function()
{
	if ( fos ) {
		return;
	}
	try {
		const file = this.getFile('log');
		fos = Components.classes["@mozilla.org/network/file-output-stream;1"]
								.createInstance(nsIFileOutputStream);
		fos.init(file, -1, -1, 0);
		const app = Components.classes["@mozilla.org/xre/app-info;1"]
								.getService(nsIXULAppInfo);
		const os = app.QueryInterface(nsIXULRuntime);
		const greeting = "Logger initialized ("+new Date()+")\n"
						+ "Platform: "+app.name+" v"+app.version+" ("+os.OS+")\n"
						+ "Debugger: v"+AptanaDebugger.VERSION+"\n";
		fos.write(greeting,greeting.length);		
	} catch( exc ) {
		fos = null;
		this.logConsole(""+exc);
	}	
}

this.getFile = function(type)
{
	const dirServiceProvider = Components.classes["@mozilla.org/file/directory_service;1"]
											.getService(nsIProperties);
	var file = dirServiceProvider.get(NS_OS_TEMP_DIR, nsIFile);
	if (isClientDebugger) {
		file.append('aptana');
	}
	if ( !file.exists() ) {
		file.create(nsIFile.DIRECTORY_TYPE, 0776);
	}
	file.append((isClientDebugger ? 'aptanadebugger' : 'jaxerdebugger')+'.'+type);
	return file;
}

this.log = function(message,level)
{
	if ( fos ) {
		var d = new Date();
		var prefix = "["+d.getMinutes()+":"+d.getSeconds()+"."+d.getMilliseconds()+"] ";
		if ( message.length > 1024 ) {
			message = message.substring(0,1024);
		}
		message = prefix + message + "\n";
		fos.write(message,message.length);
	}
}

this.close = function()
{
	if ( fos ) {
		try {
			const ending = "######## End of log ########\n";
			fos.write(ending,ending.length);
			fos.close();
			fos = null;
		} catch( exc ) {
			this.logConsole(""+exc);
		}
	}
}

this.logError = function(message)
{
	message = "["+new Date()+"] "+message+"\n";
	try {
		const file = this.getFile('err');
		var fout = Components.classes["@mozilla.org/network/file-output-stream;1"]
									.createInstance(nsIFileOutputStream);
		fout.init(file, 0x1A, -1, 0); //append
		fout.write(message,message.length);
		fout.close();
	} catch( exc ) {
		this.logConsole(""+exc);
	}
}

// ************************************************************************************************

var consoleService;

this.logConsole = function(message)
{
	if ( !consoleService ) {
		consoleService = Components.classes['@mozilla.org/consoleservice;1']
										.getService(nsIConsoleService);
	}
	consoleService.logStringMessage(message+"");
}

// ************************************************************************************************

}).apply(AptanaLogger);
