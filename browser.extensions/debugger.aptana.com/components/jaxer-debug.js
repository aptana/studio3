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

const CC = Components.classes;
const CI = Components.interfaces;
const CR = Components.results;
const CU = Components.utils;

CU.import("resource://gre/modules/XPCOMUtils.jsm");

const EVENT_RequestStart = "jaxerEvent.RequestStart";
const EVENT_HTMLParseStart = "jaxerEvent.HTMLParseStart";
const EVENT_RequestComplete = "jaxerEvent.RequestComplete";
const EVENT_ScriptCompile = "jaxerEvent.ScriptCompile";

const DEBUGGER_PORT_EXPRESSION = new RegExp("([0-2]?[0-9]?[0-9]\\.[0-2]?[0-9]?[0-9]\\.[0-2]?[0-9]?[0-9]\\.[0-2]?[0-9]?[0-9]:)?([0-9]+)");
const FILENAME_EXPRESSION = new RegExp("^/\\* (.+\\.js) \\*/");

try {
	const Jaxer = CC['@aptana.com/jaxer/global;1'].getService(CI.aptIJaxerGlobal).getObject();
	const Log = CC['@aptana.com/log;1'].getService(CI.aptICoreLog);

	const eINFO = CI.aptICoreLog.eINFO;
	const eWARN = CI.aptICoreLog.eWARN;
	const eERROR = CI.aptICoreLog.eERROR;
	const eFATAL = CI.aptICoreLog.eFATAL;
	const eNOTICE = CI.aptICoreLog.eNOTICE;

} catch(exc) {
	/* not Jaxer */
}

const scope = {};
var logListener;
var lastEvalScriptId = 0;

function initDebugger(param) {
	scope.launchParam = param;
	try {
		CC["@mozilla.org/moz/jssubscript-loader;1"]
			.getService(CI.mozIJSSubScriptLoader)
			.loadSubScript("chrome://aptanadebugger/content/aptanaServer.js", scope);
		Jaxer.private.hasDebugger = true;
		scope.Jaxer = Jaxer;
		
		logListener = {
			observe: function(type, message) {
				if (type >= eINFO && Jaxer.private.hasDebugger && scope.AptanaDebugger) {
					var logType = "out";
					if (type == eWARN) {
						logType = "warn";
					} else if (type == eERROR || type == eFATAL) {
						logType = "err";
					}
					scope.AptanaDebugger.log(logType, message);
				}
			}
		};
		Log.registerListener(logListener);
	} catch(exc) {
		var message;
		if ( typeof(exc) == 'object' && "fileName" in exc && "lineNumber" in exc ) {
			message = ""+exc+" at "+exc.fileName+":"+exc.lineNumber;
		} else {
			message = exc.toString();
		}
		Log.error("Error while loading debugger: " + message);
	}
}

function terminateJaxer() {
	Log.unregisterListener(logListener);
	CC["@aptana.com/httpdocumentfetcher;1"]
		.getService(CI.aptIDocumentFetcherService)
			.exit();
}

function JaxerDebugger() { }

JaxerDebugger.prototype = {
	// properties required for XPCOM registration:
	classDescription: "Jaxer Debugger",
	classID:          Components.ID("{C747E37D-91AF-4769-A444-206DE1712F38}"),
	contractID:       "@aptana.com/jaxer-debugger;1",
	
	_xpcom_categories: [
		{ category: EVENT_RequestStart },
		{ category: EVENT_HTMLParseStart },
		{ category: EVENT_RequestComplete },
		{ category: EVENT_ScriptCompile }
	],

	_xpcom_factory: {
		_instance: null,
		createInstance: function (outer, iid) {
			if (outer != null)
				throw CR.NS_ERROR_NO_AGGREGATION;
			if (this._instance == null)
				this._instance = new JaxerDebugger();
			return this._instance.QueryInterface(iid);
		}
	},

	// nsISupports
	QueryInterface: XPCOMUtils.generateQI([
						CI.nsIObserver
	]),
		
	// nsIObserver
    observe: function (aEventObj, aTopic, aData) 
    {
		switch(aTopic) {
		/* Ordered by the frequency of appearance */
		case EVENT_ScriptCompile:
			var event = aEventObj.QueryInterface(CI.aptIEventScriptCompile);
			if (Jaxer.private.hasDebugger && scope.AptanaDebugger) {
				event.scriptURI = normalizeScriptURI(event.scriptURI, event.scriptText);
				scope.AptanaDebugger.onScriptCompile(event.scriptURI, event.lineNo, event.scriptText);
			}
			break;
		case EVENT_RequestStart:
			var event = aEventObj.QueryInterface(CI.aptIEventRequestStart);
			var req = event.Request;
			var method = req.method.toUpperCase();
			if (method == "OPTIONS") {
				handleOPTIONS(req, event.Response);
			}
			break;
		case EVENT_HTMLParseStart:
			var event = aEventObj.QueryInterface(CI.aptIEventHTMLParseStart);
			if (Jaxer.private.hasDebugger && scope.AptanaDebugger) {
				scope.AptanaDebugger.onRequestStart(event.Request);
			}
			break;
		case EVENT_RequestComplete:
			var event = aEventObj.QueryInterface(CI.aptIEventRequestComplete);
			if (Jaxer.private.hasDebugger && scope.AptanaDebugger) {
				scope.AptanaDebugger.onRequestComplete(event.Response);
			}
			break;
		}
    }
};

function handleOPTIONS(req, resp)
{
	try {
		var headerCount = req.GetHeaderCount();
		for (var i = 0; i < headerCount; ++i) {
			var name = req.GetHeaderName(i);
			if (name == "Compliance") {
				var value = req.GetValueByOrd(i);
				if (value.match(/^server=jaxer;debug$/)) {
					resp.addHeader(name, value, false);
				}
			} else if (name == "Jaxer-Debug") {
				var value = req.GetValueByOrd(i);
				if (!Jaxer.private.hasDebugger && DEBUGGER_PORT_EXPRESSION.test(value)) {
					var match = DEBUGGER_PORT_EXPRESSION.exec(value);
					var debugPort = match[0];
					/* TODO: override with server fixed port if required */

					var jaxerId = "";
					var managerCmdService = CC["@aptana.com/managercmdservice;1"]
													.getService(CI.aptIManagerCmdService);
					managerCmdService.execNeedRespCmd("getme jaxerdebugid",
						function responseCmdCallback(resp) {
							if (!resp.failed) {
								while(resp.hasMore) {
									jaxerId += resp.data;
								}
							} else {
								Log.error("execNeedRespCmd(getme jaxerdebugid): ERROR_CODE=" + resp.errorCode + " ERROR:" +  resp.errorText);
							}
						}, false);
					if (jaxerId.match(/^[-0-9]+$/)) {
						initDebugger(debugPort);
						resp.addHeader(name, value, false);
						resp.addHeader("Jaxer-ID", jaxerId, false);
					}
				}
			}
		}
	} catch (e) {
		Log.warning("Error handling OPTIONS request: " + e);
	}
}

function normalizeScriptURI(scriptURI, scriptText)
{
	if (scriptURI === '<JaxerEval>' || scriptURI.substr(0,11) == 'javascript:') {
		if (FILENAME_EXPRESSION.test(scriptText)) {
			var match = FILENAME_EXPRESSION.exec(scriptText);
			scriptURI = 'dbgsource://'+match[1];
		} else {
			scriptURI = 'dbgsource://eval'+(++lastEvalScriptId)+'.js';
		}
	}
	return scriptURI;
}

if (typeof(Jaxer) != 'undefined') {
	NSGetModule = XPCOMUtils.generateNSGetModule([JaxerDebugger]);
}
