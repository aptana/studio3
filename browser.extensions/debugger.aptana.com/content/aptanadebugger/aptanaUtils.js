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

function AptanaUtils() {}

(function() {

// ************************************************************************************************

const nsIIOService = Components.interfaces.nsIIOService;
const nsIRequest = Components.interfaces.nsIRequest;
const nsICachingChannel = Components.interfaces.nsICachingChannel;
const nsIScriptableInputStream = Components.interfaces.nsIScriptableInputStream;
const nsIXULRuntime = Components.interfaces.nsIXULRuntime;
const nsIXULAppInfo = Components.interfaces.nsIXULAppInfo;
const nsIShellService = Components.interfaces.nsIShellService;
const nsIWebProgressListener = Components.interfaces.nsIWebProgressListener;
const nsISupportsWeakReference = Components.interfaces.nsISupportsWeakReference;
const nsISupports = Components.interfaces.nsISupports;
const nsIExtensionManager = Components.interfaces.nsIExtensionManager;
const nsIAddonUpdateCheckListener = Components.interfaces.nsIAddonUpdateCheckListener;

const NS_NOINTERFACE = Components.results.NS_NOINTERFACE;

// ************************************************************************************************

this.keys = function(o)
{
	var rv = new Array();
	for (var p in o)
		rv.push(p);
	return rv;  
}

this.sliceArray = function(array, index)
{
	var slice = [];
	for (var i = index; i < array.length; ++i)
		slice.push(array[i]);
	return slice;
}

this.removeFromArray = function(array, item)
{
	for (var i = 0; i < array.length; ++i) {
		if (array[i] == item)
		{
			array.splice(i, 1);
			return true;
		}
	}
	return false;
}

this.findInArray = function(array, item)
{
	for (var i = 0; i < array.length; ++i) {
		if (array[i] == item) {
			return array[i];
		}
	}
	return null;
}

this.cloneArray = function(array, fn)
{
	var newArray = [];
	if (fn) {
		for (var i = 0; i < array.length; ++i)
			newArray.push(fn(array[i]));
	} else {
		for (var i = 0; i < array.length; ++i)
			newArray.push(array[i]);
	}
	return newArray;
}

this.arrayInsert = function(array, index, other)
{
	for (var i = 0; i < other.length; ++i)
		array.splice(i+index, 0, other[i]);
	return array;
}

this.arrayMerge = function(array, index, other)
{
	return this.arrayInsert(this.cloneArray(array),index,other);
}

this.normalizeHref = function(href)
{
    // For some reason, JSDS reports file URLs like "file:/" instead of "file:///", so they
    // don't match up with the URLs we get back from the DOM
    return href.replace(/file:\/([^/])/g, "file:///$1");
}

this.denormalizeHref = function(href)
{
    return href ? href.replace(/file:\/\/\//, "file:/") : "";
}

this.bindFunction = function()
{
	const self = this;
    var args = this.cloneArray(arguments), fn = args.shift(), object = args.shift();
    return function() { return fn.apply(object, self.arrayMerge(args, 0, arguments)); }
}

this.genUID = function()
{
	var d = new Date();
	return Date.UTC(d.getUTCFullYear(),d.getUTCMonth(),d.getUTCDate(),d.getUTCHours(),d.getUTCMinutes(),d.getUTCSeconds(),d.getUTCMilliseconds());
}

this.loadURLAsync = function(url, onComplete)
{
	var service = Components.classes["@mozilla.org/network/io-service;1"].
						getService(nsIIOService);

	var channel = service.newChannel(url, null, null);
	channel.loadFlags |= nsIRequest.LOAD_FROM_CACHE | nsIRequest.VALIDATE_NEVER
							| nsICachingChannel.LOAD_ONLY_FROM_CACHE;
	
	var listener = {
		data: "",
		onStartRequest: function(request, context) {},
		onStopRequest: function(request, context, status)
		{
			onComplete(this.data, url, Components.isSuccessCode(status));
		},
		onDataAvailable: function(request, context, inStr, sourceOffset, count)
		{
			var stream = Components.classes["@mozilla.org/scriptableinputstream;1"].
											createInstance(nsIScriptableInputStream);
			stream.init(inStr);
			this.data += stream.read(count);
		}		
	};
	return channel.asyncOpen (listener, null);
}

this.loadURLSync = function(url)
{
	var service = Components.classes["@mozilla.org/network/io-service;1"].
							getService(nsIIOService);

	var channel = service.newChannel(url, null, null);
	channel.loadFlags |= nsIRequest.LOAD_FROM_CACHE | nsIRequest.VALIDATE_NEVER
							| nsICachingChannel.LOAD_ONLY_FROM_CACHE;
	
	return loadStreamData(channel.open());
}

this.loadStreamData = function(stream)
{
	var instream = Components.classes["@mozilla.org/scriptableinputstream;1"].
							createInstance(nsIScriptableInputStream);
	instream.init(stream);
	
	var data = "";
	var count;
	while ((count = instream.available()) > 0) {
		data += instream.read(count);
	}
	return data;
}

this.getPlatform = function()
{
	return Components.classes["@mozilla.org/xre/app-info;1"]
								.getService(nsIXULRuntime).OS;
}

this.getAppVersion = function()
{
	return Components.classes["@mozilla.org/xre/app-info;1"]
								.getService(nsIXULAppInfo).version;
}

this.compareVersion = function(v1, v2)
{
	const versionChecker = Components.classes["@mozilla.org/xpcom/version-comparator;1"]
						.getService(Components.interfaces.nsIVersionComparator);
	return versionChecker.compare(v1, v2);
}

this.getShellService = function()
{
	var rv;
	try {
		rv = Components.classes["@mozilla.org/browser/shell-service;1"]
							.getService(nsIShellService);
	} catch(exc) {
        rv = null;
	}
	return rv;
}

this.findExtensionUpdate = function(extensionId,listener)
{
	// Firefox 4.0 implements new AddonManager. In case of Firefox 3.6 the module
	// is not avaialble and there is an exception.
	try {
		Components.utils.import("resource://gre/modules/AddonManager.jsm");
	} catch(e) {
	} 
	const extensionManager = Components.classes["@mozilla.org/extensions/manager;1"]
											.getService(nsIExtensionManager);
	var item = extensionManager.getItemForID(extensionId);
	if ( item ) {
		var updateCheckListener = {
			// nsIAddonUpdateCheckListener
			onUpdateStarted: function() {
			},
			onUpdateEnded: function() {
			},
			onAddonUpdateStarted: function(addon) {
			},
			onAddonUpdateEnded: function(addon, status) {
				if ( status == nsIAddonUpdateCheckListener.STATUS_UPDATE ) {
					listener.updateFound(addon);
				} else {
					listener.updateFound(null);
				}
			}
			,
			// nsISupports
			QueryInterface: function(iid) {
				if (!iid.equals(nsIAddonUpdateCheckListener) && 
						!iid.equals(nsISupports)) {
					throw NS_ERROR_NO_INTERFACE;
				}
				return this;
			}
		};
		extensionManager.update([item], 1, false, updateCheckListener);
		return true;
	}
	return false;
}

this.installExtension = function(item)
{
	const extensionManager = Components.classes["@mozilla.org/extensions/manager;1"]
											.getService(nsIExtensionManager);
	extensionManager.addDownloads([item], 1, null);
}

this.getBrowserByWindow = function(win)
{
	var doc = document;
	if ( arguments.length > 1 && arguments[1] ) {
		doc = arguments[1];
	}
	var tabBrowser = doc.getElementById("content");
	for (var i = 0; i < tabBrowser.browsers.length; ++i) {
		var browser = tabBrowser.browsers[i];
		if (browser.contentWindow == win) {
			return browser;
		}
	}
	return null;
}

this.WebProgressListener = function() {};
this.WebProgressListener.prototype = {
    QueryInterface : function(iid)
    {
        if (iid.equals(nsIWebProgressListener) ||
            iid.equals(nsISupportsWeakReference) ||
            iid.equals(nsISupports))
        {
            return this;
        }
        
        throw NS_NOINTERFACE;
    },
	onStateChange: function(aWebProgress, aRequest, aStateFlags, aStatus) {},
	onLocationChange: function(aWebProgress, aRequest, aLocation) {},
	onStatusChange: function(aWebProgress, aRequest, aStatus, aMessage) {},
	onProgressChange: function(aWebProgress, aRequest, aCurSelfProgress, aMaxSelfProgress, aCurTotalProgress, aMaxTotalProgress) {},
	onSecurityChange: function(aWebProgress, aRequest, aState) {}
};

this.stackTrace = function(frame)                                                                                          
{
	var frames = [];                                                                                               
	
	for (; frame; frame = frame.caller)
	{
		if (frame.languageName == "JavaScript" && !(frame.filename && frame.filename.indexOf("chrome:") == 0))
		{
			var functionName = frame.name;
			if ( functionName == "null" )
				functionName = "";
			frames.push({
				functionName: functionName,
				fileName: frame.filename,
				lineNumber: frame.lineNumber,
				functionArguments: "..."
			});
		}
	}
	return frames;
}

this.format = function(objects)
{
	var output = "";
    if (!objects || !objects.length)
        return output;
    
    var format = objects[0];
    var objIndex = 0;

    if (typeof(format) != "string")
    {
        format = "";
        objIndex = -1;
    }
    
    var formatParts = parseFormat(format);
    for (var i = 0; i < formatParts.length; ++i)
    {
        var formatPart = formatParts[i];
        if (formatPart && typeof(formatPart) == "object")
        {
            var object = objects[++objIndex];
            output += formatPart.func(object, formatPart.precision);
        }
        else
            output += formatPart;
    }
	return output;
}

function parseFormat(format)
{
    var formatParts = [];
    
    var reg = /((^%|[^\\]%)(\d+)?(\.)([a-zA-Z]))|((^%|[^\\]%)([a-zA-Z]))/;
    var index = 0;
    
    for (var m = reg.exec(format); m; m = reg.exec(format))
    {
        var type = m[8] ? m[8] : m[5];
        var precision = m[3] ? parseInt(m[3]) : (m[4] == "." ? -1 : 0);
        
        var func = null;
        switch (type)
        {
            case "s":
            case "f":
            case "i":
            case "d":
                func = function(obj) { return ""+obj; };
                break;
            case "o":
                func = function(obj) { return ""+obj; };
                break;
            case "x":
                func = function(obj) { return ""+obj; };
                break;
        }
        
        formatParts.push(format.substr(0, m[0][0] == "%" ? m.index : m.index+1));
        formatParts.push({func: func, precision: precision});
        
        format = format.substr(m.index+m[0].length);
    }
    
    formatParts.push(format);
    
    return formatParts;
}

this.debugStackTrace = function()
{
	var frame = Components.stack.caller;
	var output = [];
	for (; frame; frame = frame.caller) {
		output.push(frame.name+"(),"+frame.filename+":"+frame.lineNumber);
	}
	return output.join("\n");
}

// ************************************************************************************************

}).apply(AptanaUtils);


