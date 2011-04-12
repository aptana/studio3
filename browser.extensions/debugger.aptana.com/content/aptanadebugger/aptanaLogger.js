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
