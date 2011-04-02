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

var FirebugServiceDebugger;

(function() {

// ************************************************************************************************

const nsISupports = Components.interfaces.nsISupports;
const nsIConsoleService = Components.interfaces.nsIConsoleService;
const nsIObserverService = Components.interfaces.nsIObserverService;
const nsIHttpChannel = Components.interfaces.nsIHttpChannel;
const nsIPromptService = Components.interfaces.nsIPromptService;
const nsIStringInputStream = Components.interfaces.nsIStringInputStream;
const nsIWebNavigation = Components.interfaces.nsIWebNavigation;
const nsIScriptError = Components.interfaces.nsIScriptError;
const jsdIDebuggerService = Components.interfaces.jsdIDebuggerService;
const jsdIExecutionHook = Components.interfaces.jsdIExecutionHook;
const jsdIErrorHook = Components.interfaces.jsdIErrorHook;
const jsdIValue		= Components.interfaces.jsdIValue;
const jsdIProperty	= Components.interfaces.jsdIProperty;

const TYPE_INTERRUPTED = jsdIExecutionHook.TYPE_INTERRUPTED;
const TYPE_BREAKPOINT = jsdIExecutionHook.TYPE_BREAKPOINT;
const TYPE_DEBUG_REQUESTED = jsdIExecutionHook.TYPE_DEBUG_REQUESTED;
const TYPE_DEBUGGER_KEYWORD = jsdIExecutionHook.TYPE_DEBUGGER_KEYWORD;
const TYPE_THROW = jsdIExecutionHook.TYPE_THROW;

const RETURN_CONTINUE = jsdIExecutionHook.RETURN_CONTINUE;
const RETURN_CONTINUE_THROW = jsdIExecutionHook.RETURN_CONTINUE_THROW;
const RETURN_RET_WITH_VAL = jsdIExecutionHook.RETURN_RET_WITH_VAL;
const RETURN_THROW_WITH_VAL = jsdIExecutionHook.RETURN_THROW_WITH_VAL;

const REPORT_ERROR = jsdIErrorHook.REPORT_ERROR;
const REPORT_EXCEPTION = jsdIErrorHook.REPORT_EXCEPTION;
const REPORT_WARNING = jsdIErrorHook.REPORT_WARNING;

const NS_NOINTERFACE = Components.results.NS_NOINTERFACE;

const TYPE_VOID		= jsdIValue.TYPE_VOID;
const TYPE_NULL		= jsdIValue.TYPE_NULL;
const TYPE_BOOLEAN	= jsdIValue.TYPE_BOOLEAN;
const TYPE_INT		= jsdIValue.TYPE_INT;
const TYPE_DOUBLE	= jsdIValue.TYPE_DOUBLE;
const TYPE_STRING	= jsdIValue.TYPE_STRING;
const TYPE_FUNCTION	= jsdIValue.TYPE_FUNCTION;
const TYPE_OBJECT	= jsdIValue.TYPE_OBJECT;

const PROP_ENUMERATE	= jsdIProperty.FLAG_ENUMERATE;
const PROP_READONLY		= jsdIProperty.FLAG_READONLY;
const PROP_PERMANENT	= jsdIProperty.FLAG_PERMANENT;
const PROP_ALIAS		= jsdIProperty.FLAG_ALIAS;
const PROP_ARGUMENT		= jsdIProperty.FLAG_ARGUMENT;
const PROP_VARIABLE		= jsdIProperty.FLAG_VARIABLE;
const PROP_EXCEPTION	= jsdIProperty.FLAG_EXCEPTION;
const PROP_ERROR		= jsdIProperty.FLAG_ERROR;
const PROP_HINTED		= jsdIProperty.FLAG_HINTED;
const PROP_CONST		= 0x8000;

const CONSTANTS_FILTER = new RegExp("^[A-Z][A-Z_]*$");
const FUNCTION_NAME_GUESS_PATTERN = new RegExp("\"?(\\w+)\"?\\s*[:=(]\\s*$");
const JAVA_OBJECT_PATTERN = new RegExp("^Java(Array|Member|Object|Package)$");
const XPCONNECT_PATTERN = new RegExp("^\\[xpconnect wrapped ([^\\]]*)\\]$");
const HTMLWRAP_MATCHER = new RegExp("\\.js$");
const FILE_URL_PATTERN = new RegExp("^file:/([^/].*)$");

const PROTOCOL_VERSION = "1";

// ************************************************************************************************

const isClientDebugger = (typeof(document) != "undefined");
const ff3 = !isClientDebugger || (AptanaUtils.compareVersion(AptanaUtils.getAppVersion(), "3.0*") >= 0);
const ff35 = (AptanaUtils.compareVersion(AptanaUtils.getAppVersion(), "3.5*") >= 0);

var hooks = {};
var debugging = false;
var started = false;
var parentDebugger;
var childDebuggers = [];

var options = {
	showFunctions: false,
	showConstants: true,
	monitorXHR: false,
	bypassConstructors: false,
	stepFiltersEnabled: false,
	suspendOnFirstLine: false,
	suspendOnExceptions: false,
	suspendOnErrors: false,
	suspendOnKeywords: true,
	suspendOnAssert: false,
	suspendOnRequest: false,
	suspendOnCallback: false
};
var strings;

var jsd;
var fbs;
var consoleService;
var observerService;

var socket = null;
var reqid;

var hookReturn;
var dbgstate = {};

// ************************************************************************************************

const debuggr =
{
	// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
	// nsISupports
	
	QueryInterface : function(iid)
	{
		if (iid.equals(nsISupports)) {
			return this;
		}
		throw NS_NOINTERFACE;
	},

	debuggerName: "AptanaDebugger",
	activeContexts: [], // FB@explore only
	
	// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
	// nsIFireBugNetworkDebugger
	
	suspendActivity: function()
	{
		if ( socket )
			socket.stopProcessing();
	},
	
	resumeActivity: function()
	{
		if ( socket )
			socket.startProcessing();
	},

	// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
	// nsIFireBugScriptListener

	onScriptCreated: function(script, url, lineNo)
	{
		if ( !enabled )
			return;
		var fileName = script.fileName;
		if ((fileName == '[Eval-script]') || (fileName.substr(0,11) == 'javascript:')
			|| fileName.substr(0,15) == 'resource://gre/'
			|| script.functionName == '_FirebugConsole'
			|| script.functionName == '_createFirebugConsole') {
			return;
		}
		var scriptKey = ""+fileName+":"+script.baseLineNumber+":"+script.functionName;
		if ( scriptKey in loadedScripts ) {
			return;
		}
		if (fileName && !(fileName in translateHrefs)) {
			translateHrefs[fileName] = fileName;
			var i = fileName.indexOf("?");
			if (i != -1) {
				var href = fileName.substr(0, i);
				translateHrefs[href] = fileName;
				if (href in breakpoints) {
					var list = AptanaUtils.cloneArray(breakpoints[href]);
					for( var i = 0; i < list.length; ++i ) {
						var line = list[i];
						if (breakpointProps[href+":"+line]) {
							setBreakpoint(fileName, line, breakpointProps[href+":"+line]);
						}
					}
				}
			}
		}


		loadedScripts[scriptKey] = true;
		collectScriptInfo(script);
		sendSocketData("scripts*created*"+packageScriptReport([script])[0]);
	},

	onScriptDestroyed: function(script)
	{
	},
	
	// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
	// nsIFireBugDebugger
	
	supportsWindow: function(win)
	{
		return false;
	},
		
	supportsGlobal: function(global, frame)
	{
		return false;
	},
	
	onLock: function(state)
	{
	},
	
	onBreak: function(frame, type)
	{
		return RETURN_CONTINUE;
	},
	
	onHalt: function(frame)
	{
		return RETURN_CONTINUE;
	},
	
	onThrow: function(frame, rv)
	{
		return RETURN_CONTINUE;
	},
	
	onCall: function(frame)
	{
	},
	
	onError: function(frame, error)
	{
		return -2;
	},

	onEvalScriptCreated: function(frame, outerScript, innerScripts)
	{
		return null;
	},

	onEventScriptCreated: function(frame, outerScript, innerScripts)
	{
		return null;
	},

	onTopLevelScriptCreated: function(frame, outerScript, innerScripts)
	{
		return null;
	},
		
	onToggleBreakpoint: function(url, lineNo, isSet, props)
	{
		if ( skipToggleBreakpoint )
			return;
		var data = "";
		var cmd = "change";
		if (isSet) {
			data = [props.disabled?"0":"1", props.hitCount, encodeData(props.condition), props.onTrue?"1":"0"].join("*");
			if ( addBreakpoint(url,lineNo) )
				cmd = "create";
		} else {
			if ( !removeBreakpoint(url,lineNo) )
				return;
			cmd = "remove";
		}
		sendSocketData(["breakpoint", cmd, encodeData(AptanaUtils.denormalizeHref(url)), lineNo, data].join("*"));
	},
	
	onToggleErrorBreakpoint: function(url, lineNo, isSet)
	{
	},
	
	onToggleMonitor: function(url, lineNo, isSet)
	{
	}
	
};
debuggr.wrappedJSObject = debuggr;
FirebugServiceDebugger = debuggr;

// ************************************************************************************************

this.initDebugger = function(port)
{
	if (isClientDebugger) {
		strings = document.getElementById("strings_aptanadebugger");
		AptanaUtils.initStringBundle(strings.stringBundle,this);
	} else {
		AptanaUtils.initStringBundle(AptanaUtils.createStringBundle("chrome://aptanadebugger/locale/aptanadebugger.properties"),this);
	}
	
	jsd = Components.classes['@mozilla.org/js/jsd/debugger-service;1']
						.getService(jsdIDebuggerService);
	if (FBL.fbs) {
		fbs = FBL.fbs;
	} else {
	  fbs = Components.classes['@joehewitt.com/firebug;1']
								.getService(nsISupports).wrappedJSObject;
	}							
	consoleService = Components.classes['@mozilla.org/consoleservice;1']
								.getService(nsIConsoleService);
	observerService = Components.classes["@mozilla.org/observer-service;1"]
								.getService(nsIObserverService);
	if (isClientDebugger) {
		window.onclose = AptanaUtils.bindFunction(onTryCloseWindow,this,window.onclose);
	}
	
	const socketListener = {
		onPacket: function(packet) {
			try {
				onPacketReceived(packet);
			} catch (exc) {
				dd(exc,'err');
			}
		},
		onClose: function() {
			AptanaDebugger.shutdown();	
		}
	};
	
	var host = "localhost";
	if ( port.substr(0,7) == "server:" ) {
		host = "*";
		port = port.substr(7);
	}
	try {
		socket = this.createSocket(host,port,socketListener);
	} catch(exc) { ddd(exc);}
			
	fbs.registerDebugger(debuggr);
	consoleListener.register();
	httpRequestObserver.register();

	fbs.trackThrowCatch = true;
	fbs.filterSystemURLs = true;
	fbs.showStackTrace = true;
	fbs.breakOnErrors = true;
	
	hook("init", [this]);	
}

this.attachToParent = function(parent)
{
	parentDebugger = parent;
	hook("init", [parent]);
	if ( parent.isEnabled() )
		hook("enable");
}

this.detachParent = function()
{
	parentDebugger = null;
}

this.initChild = function()
{
	AptanaDebuggerChrome.enable(true);
	this.chrome = AptanaDebuggerChrome;
	parentDebugger.registerChild(this);
}

this.registerChild = function(child)
{
	childDebuggers.push(child);
}

function unregisterChild(child)
{
	AptanaUtils.removeFromArray(childDebuggers,child);
}

this.isInitialized = function()
{
	if ( parentDebugger ) {
		return parentDebugger.isInitialized();
	}
	return socket != null;
}

this.isEnabled = function()
{
	return enabled;
}

this.isSuspendEnabled = function()
{
	if ( parentDebugger )
		return parentDebugger.isSuspendEnabled();
	return enabled && !stepping && !debugging;
}

this.sendClientAction = function(action)
{
	if ( parentDebugger )
		return parentDebugger.sendClientAction(action);
	sendSocketData("client*"+action);
}

// ************************************************************************************************

function updateUI()
{
	var state = debugging?"suspended":(stepping?"stepping":"");
	try {
		AptanaDebuggerChrome.update_state(state);
	} catch(exc) {
		/* ignore */
	}
	for( var i = 0; i < childDebuggers.length; ++i ) {
		try {
			childDebuggers[i].chrome.update_state(state);
		} catch(exc) {
			/* ignore */
		}
	}
}

function hook(name,args)
{
	if ( debuggerContext )
		return debuggerContext.contextHook(name,args);
	if ( name in hooks ) {
		return hooks[name].apply(this,args);
	}
}
this.contextHook = hook;

this.setHook = function(name,hook)
{
	hooks[name] = hook;
}

this.shutdown = function()
{
	this.shutdownInProgress = true;
	if ( !jsd )
		return;
	if ( !hook("shutdown") )
		return;
	if ( parentDebugger )
		return;
	disable();
		
	for( var i = childDebuggers.length-1; i >= 0; --i ) {
		childDebuggers[i].chrome.close();
	}
	childDebuggers = [];
	
	if ( socket ) {
		socket.close();
		socket = null;
	}
	consoleListener.unregister();
	httpRequestObserver.unregister();
	if ( fbs ) {
		fbs.unregisterDebugger(debuggr);
	}
	consoleService = null;
	fbs = null;
	jsd = null;
	AptanaDebuggerChrome.onShutdown();
}

function onTryCloseWindow(event,prevOnClose)
{
	if ( childDebuggers.length > 0 ) {
		const promptService = Components.classes["@mozilla.org/embedcomp/prompt-service;1"]
										.getService(nsIPromptService);
		var buttonPressed = promptService.confirmEx(window,
								strings.getString('closeWarningTitle'),
								strings.getFormattedString("closeWarning", [childDebuggers.length+1]),
								(promptService.BUTTON_TITLE_IS_STRING * promptService.BUTTON_POS_0)
									+ (promptService.BUTTON_TITLE_CANCEL * promptService.BUTTON_POS_1),
								strings.getString('closeButton'),
								null, null, null, {} );
		return (buttonPressed == 0);
	}
	if ( typeof(prevOnClose) == 'function' ) {
		return prevOnClose(event);
	}
	return true;
}

this.openInEditor = function(href)
{
	sendSocketData(["client", "open", encodeData(href)].join("*"));
}

// ************************************************************************************************

var serverRequest;
var sourceMap;

this.onRequestStart = function(request)
{
	serverRequest = request;
	sourceMap = {};
	if (request.isHandler /* TODO: test URL for callback */) {
		if (options.suspendOnCallback)
			suspendOnTopLevel = "callbackStart";
	} else {
		if (options.suspendOnRequest)
			suspendOnTopLevel = "requestStart";
	}
	sendSocketData("requestStart*"+encodeData(request.documentURL.spec));
}

this.onRequestComplete = function(response)
{
	/* TODO: use response status code/etc */
	sendSocketData("requestComplete");
	//serverRequest = null;
	//sourceMap = null;
}

this.onScriptCompile = function(scriptURI, lineNo, scriptText)
{
	if (lineNo == 1) {
		sourceMap[scriptURI] = scriptText;
	}
}
// ************************************************************************************************

function sendSocketData(data)
{
	if (socket)
		socket.send(data);
}

function sendResponse(data)
{
	if ( data != null ) {
		socket.send(reqid+"*"+data);
	} else {
		socket.send(reqid);
	}
}

function onPacketReceived(packet)
{
	var command = packet.split("*");
	reqid =  command[0];
	
	if ( command.length < 2 ) {
		sendResponse('!no command specified');
		return;
	}
	switch(command[1])
	{
		case "openUrl":
			onOpenUrl(command[2], command[3]);
			break;

		case "getSource":
			onGetSource(command[2]);
			break;

		case "enable":
			enable();
			var scripts = enumerateScripts();
			if ( scripts.length > 0 )
				sendSocketData("scripts*created*"+packageScriptReport(scripts).join('*'));
			sendResponse(null);
			break;

		case "disable":
			disable();
			sendResponse(null);
			break;

		case "option":
			if ( command[2] in options ) {
				if ( typeof(options[command[2]]) == 'boolean' ) {
					options[command[2]] =  (command[3] == 'true');
				} else {
					options[command[2]] =  command[3];
				}
				sendResponse(null);
			} else {
				sendResponse("!unknown option <"+command[2]+">");
			}
			break;

		case "suspend":
			if ( debugging ) {
				dd("!invalid state (suspended)");
				return;
			}
			suspend(command[1]);
			break;
			
		case "resume":
			if ( !debugging ) {
				dd("!invalid state (running)");
				return;
			}
			resume(command[1]);
			break;

		case "stepInto":
		case "stepOver":
		case "stepReturn":
		case "stepToFrame":
			if ( !debugging ) {
				dd("!invalid state (running)");
				return;
			}
			dbgstate.resumeReason = dbgstate.suspendReason = command[1];
			stepping = true;
			switch(command[1])
			{
				case "stepInto":
				case "stepOver":
				case "stepReturn":
					hook(command[1]);
					break;
				case "stepToFrame":
					var frameId = parseInt(command[2]);
					if ( frameId == 0 ) {
						hook("stepOver");
					} else {
						hook(command[1],[dbgstate.frames[frameId-1]]);
					}
					break;
			}
			break;
						
		case "breakpoint": {
				if ( command.length >= 9 )
				{
					var props = {
						disabled: command[5]=="0",
						condition: decodeData(command[7]),
						onTrue: command[8]=="1",
						hitCount: parseInt(command[6])
					};
				}
				onSetBreakpoint(command[2],decodeData(command[3]),parseInt(command[4]),props);
			}
			break;

		case "exception":
			onSetException(command[2],command[3]);
			break;

		case "ibreakpoint":
			onSetInstanceBreakpoint(command[2],command[3]);
			break;

		case "stepFilters":
			onSetStepFilters(command[2],command[3]);
			break;

		case "detailFormatters":
			command.splice(0,2);
			onSetDetailFormatters(command);
			break;

		case "frames":
			onGetFrames();
			break;
		
		case "variables":
			onGetVariables(command[2]);
			break;

		case "details":
			onGetDetails(command[2]);
			break;

		case "eval":
			onEval(command[2],command[3]);
			break;

		case "setValue":
			onSetValue(command[2],command[3]);
			break;
		
		case "setHttpHeaders":
			command.splice(0,2);
			onSetHttpHeaders(command);
			break;

		case "terminate":
			terminate();
			break;

		case "version":
			sendResponse(PROTOCOL_VERSION+"*"+AptanaDebugger.VERSION);
			break;

		case "update":
			updateExtension();
			sendResponse(null);
			break;

		default:
			sendResponse('!unsupported command <'+command[1]+'>');
			break;
	}	
}

// ************************************************************************************************

var enabled = false;
var stepping = false;
var suspendOnTopLevel;


function enable()
{
	if (enabled)
		return;
	enabled = true;
	if (options.suspendOnFirstLine)
		suspendOnTopLevel = "firstLine";
	
	// unregister console listener, now we handle errors in debugger
	consoleListener.unregister();

	hook("enable");
}

function disable()
{
	if (!enabled)
		return;
	enabled = false;

	// register console listener
	consoleListener.register();

	clearAllBreakpoints();
	hook("disable");
}

function suspend(reason)
{
	if ( reason )
		dbgstate.suspendReason = reason;
	stepping = true;
	updateUI();
	hook("suspend");
}

function resume(reason)
{
	if ( reason )
		dbgstate.resumeReason = reason;
	hook("resume");
}

function terminate()
{
	const shutdownFunc = function() {
		disable();
		AptanaDebugger.shutdown();
		if (isClientDebugger) {
			window.close();
		} else {
			terminateJaxer();
		}
	};

	if (debugging) {
		dbgstate.shutdownHook = shutdownFunc;
		dbgstate.resumeReason = "abort";
		hook("abort");
	} else {
		shutdownFunc();
	}
}

// ************************************************************************************************

var debuggerContext;

this.onStop = function(debuggr,frame,type,rv)
{
    if (debugging)
        return RETURN_CONTINUE;
        
    hookReturn = RETURN_CONTINUE;
	switch( type )
	{
		case TYPE_INTERRUPTED:
			if ( frame.isConstructing && options.bypassConstructors )
				return hookReturn;
			break;
		case TYPE_BREAKPOINT:
			dbgstate.suspendReason = "breakpoint";
			break;
		case TYPE_DEBUG_REQUESTED:
			if ( dbgstate.currentException ) {
				delete dbgstate.currentException;
				return hookReturn;
			}
			dbgstate.suspendReason = "requested";
			break;
		case TYPE_DEBUGGER_KEYWORD:
			if ( !options.suspendOnKeywords )
				return hookReturn;
			dbgstate.suspendReason = "keyword";
			break;
		case TYPE_THROW:
			hookReturn = RETURN_CONTINUE_THROW;
			dbgstate.suspendReason = "exception";
			dbgstate.currentException = rv.value;
			break;
	}
	dbgstate.hookReturnValue = rv;
	debugging = true;
	stepping = false;
	if ( debuggr != this )
		debuggerContext = debuggr;
	updateUI();

    dbgstate.frames = new Array();
    var prevFrame = frame;    
    while (prevFrame)
    {
		var fileName = prevFrame.script.fileName;
		if ( fileName.indexOf("chrome:") != 0 ) {
			dbgstate.frames.push(prevFrame);	
		}
        prevFrame = prevFrame.callingFrame;
    }

	sendSocketData(["suspended", ""+dbgstate.suspendReason, encodeData(frame.script.fileName), frame.line].join("*"));
	delete dbgstate.suspendReason;
}

this.onResume = function()
{
	if ( dbgstate.currentException && (hookReturn == RETURN_CONTINUE_THROW) ) {
		if ( dbgstate.hookReturnValue.value != dbgstate.currentException ) {
		    dbgstate.hookReturnValue.value = dbgstate.currentException;
			if ( dbgstate.currentException.getWrappedValue() == null ) {
				hookReturn = RETURN_RET_WITH_VAL;
				dbgstate.hookReturnValue.value = null;
			} else {
				hookReturn = RETURN_THROW_WITH_VAL;
			}
		}
	}
	/* TODO: move code above to FB */	
	
	delete dbgstate.hookReturnValue;
	delete dbgstate.evalResults;
	delete dbgstate.frames;
	delete dbgstate.currentException;
	
	sendSocketData("resumed*"+dbgstate.resumeReason);
	if ( typeof(dbgstate.resumeReason) == "undefined" )
		delete dbgstate.suspendReason;
	delete dbgstate.resumeReason;
	
	debuggerContext = null;
	debugging = false;
	updateUI();
	
	if ( dbgstate.shutdownHook ) {
		dbgstate.shutdownHook();
		delete dbgstate.shutdownHook;
	}
	
	return hookReturn;
}

this.onError = function(frame, error)
{
	var logType = "out";
	var message = error.message;
	if ( error.flags & REPORT_ERROR ) {
		logType = "err";
	} else if ( error.flags & REPORT_EXCEPTION ) {
		logType = "exception";
	} else if ( error.flags & REPORT_WARNING ) {
		logType = "warn";
	}
	if ( error.exc ) {
		message = getExceptionTypeName(error.exc)+": "+message;
	}
	if ( frame ) {
		var frames = []; 
		for (; frame; frame = frame.callingFrame) {
			var fileName = frame.script.fileName;
			if ( fileName.indexOf("chrome:") != 0 ) {
				frames.push({
					functionName: getFunctionName(frame.script),
					fileName: fileName,
					lineNumber: frame.line,
					functionArguments: packageFrameArguments(frame)
				});
			}
		}
		log(logType,message,frames);
	} else {
		log(logType,message, { fileName: error.fileName, lineNumber: error.line } );
	}
	if ( options.suspendOnErrors )		
		return -1;
	if ( error.exc ) {
		if ( hasExceptionFilter(error.exc) ) {
			dbgstate.currentException = error.exc; 
			return -1;	
		}		
	}
	return RETURN_CONTINUE;
}

this.onThrow = function(frame, rv)
{
	var needSuspend = options.suspendOnExceptions;
	if ( hasExceptionFilter(rv.value) )
		needSuspend = true;
	return needSuspend;
}

this.onTopLevel = function(frame, href)
{
	resolveAnonymousFunctionsNames();
	loadedHrefs.push(href);		
	if (suspendOnTopLevel) {
		suspend(suspendOnTopLevel);
		suspendOnTopLevel = false;
	}
}

this.onInit = function(debuggr)
{
	if ( this == debuggr ) {
		started = true;
		restoreBreakpoints();
		sendSocketData("resumed*start");
	}
}

this.onLoaded = function(url)
{
	resolveAnonymousFunctionsNames();
	sendSocketData("opened*"+encodeData(url));
}

this.acceptWindow = function(debuggr,win)
{
	if ( this != debuggr ) {
		if ( !win.opener ) {
			debuggr.detachParent();
			return false;
		}
		debuggr.initChild();
	}
	return true;
}

var attachedWindows = [];

this.attachToWindow = function(win)
{
	if ( AptanaUtils.findInArray(attachedWindows,win) != null ) {
		return;
	}
	attachedWindows.push(win);

	if ( !win.wrappedJSObject.aptana ) {
		win.wrappedJSObject.aptana = new AptanaDebugAPI(AptanaDebugger);
		win.wrappedJSObject.dump = function(text) {
			log("out",text);
		};
	}
	if ( wrappedURL ) {
		var script = win.document.createElement("script");
		script.setAttribute("src",wrappedURL);
		var heads = win.document.getElementsByTagName("head");
		if (heads.length)
		{
			heads[0].appendChild(script);
		}
		wrappedURL = null;
	}

	
	var self = this;
	var onUnload = function(event) {
		self.detachFromWindow(win);
	}
	win.addEventListener("unload", onUnload, true);

}

this.detachFromWindow = function(win)
{
	if ( !AptanaUtils.removeFromArray(attachedWindows,win) )
		return;

	if ( !win.wrappedJSObject.aptana ) {
		delete win.wrappedJSObject.aptana;
	}
}

this.onDestroy = function(debuggr)
{
	if ( !debuggr.shutdownInProgress && !ff35) {
		if (debugging) {
			dbgstate.resumeReason = "abort";
			hook("abort");
		}
		return;
	}
	if ( this != debuggr ) {
		unregisterChild(debuggr);
		if (debugging) {
			dbgstate.resumeReason = "abort";
			hook("abort");
		}
	} else {
		const shutdownFunc = function() {
			try {
				disable();
			} catch(exc) {
			}
			debuggr.shutdown(); /* eq this */
		};
	
		if (debugging && !ff3) {
			dbgstate.shutdownHook = shutdownFunc;
			dbgstate.resumeReason = "abort";
			hook("abort");
		} else {
			shutdownFunc();
		}
	}
}

this.onShow = function(show)
{
	AptanaDebuggerChrome.enable(show);	
}

// ************************************************************************************************

function packHTTPHeaders(headers)
{
	var out = [];
	for( var i = 0; i < headers.length; ++i )
		out.push(headers[i].name+": "+headers[i].value);
	return out.join("\n");
}

this.xhrStart = function(id, method, url, postText, requestHeaders)
{
	sendSocketData(["xhr", id, "start", method, encodeData(url), encodeData(packHTTPHeaders(requestHeaders)), encodeData(postText)].join("*"));
}

this.xhrLoad = function(id, statusCode, statusText, responseText, responseHeaders)
{
	sendSocketData(["xhr", id, "load", statusCode, encodeData(statusText), encodeData(packHTTPHeaders(responseHeaders)), encodeData(responseText)].join("*"));
}

// ************************************************************************************************

var wrappedURL;

function onOpenUrl(url, headers)
{
	url = decodeData(url);
	if (headers) {
		headers = headers.split("|");
		for (var i in headers) {
			headers[i] = decodeData(headers[i]);
		}
	} else {
		headers = [];
	}
	if ( HTMLWRAP_MATCHER.test(url) ) {
		wrappedURL = url;
		url = 'chrome://aptanadebugger/skin/main.html';
	}
	hook("openURL", [url]);
	if (isClientDebugger) {
		var headersData = "Cache-Control: no-cache, must-revalidate\r\nPragma: no-cache\r\n"+headers.join("\r\n");
		var headersStream = Components.classes["@mozilla.org/io/string-input-stream;1"]
									.createInstance(nsIStringInputStream);
		headersStream.setData(headersData, headersData.length);
		window.getWebNavigation().loadURI(url,nsIWebNavigation.LOAD_FLAGS_BYPASS_PROXY|nsIWebNavigation.LOAD_FLAGS_BYPASS_CACHE,null,null,headersStream);
	}
	sendResponse(null);
}

function onGetSource(url)
{
	url = decodeData(url);
	if (isClientDebugger) {
		AptanaUtils.loadURLAsync(url, AptanaUtils.bindFunction(onGetSourceComplete,this,reqid));
	} else {
		var data;
		if (serverRequest && serverRequest.documentURL.spec === url) {
			data = AptanaUtils.loadStreamData(serverRequest.getDocumentInputStream());
		} else if (sourceMap) {
			data = sourceMap[url];
		}
		onGetSourceComplete(data, url, !!data, reqid);	
	}
}

function onGetSourceComplete(data,url,succeeds,reqid)
{
	if ( succeeds ) {
		sendSocketData(reqid+"*success*"+encodeData(data));	
	} else {
		sendSocketData(reqid+"*failure");
	}
}

// ************************************************************************************************

const consoleListener = {
	observe : function(object) {
		try {
			if (object instanceof nsIScriptError) {
				if ( object.sourceName && object.sourceName.substr(0,7) == "chrome:" ) {
					var logType = "out";
					if (object.flags & nsIScriptError.warningFlag) {
						logType = "warn";
					} else {
						logType = "err";
					}
					log(logType,object.errorMessage, { fileName: object.sourceName, lineNumber: object.lineNumber });
				}
			} else {
				dd(object.message);
			}
		} catch (exc) {
			dd(exc,'err');
		}
	},
	
	register: function() {
		if ( consoleService ) {
			consoleService.registerListener(this);
		}
	},
	
	unregister: function() {
		if ( consoleService ) {
			consoleService.unregisterListener(this);
		}
	}
};

function log(level,message,source)
{
	var srcInfo = "";
	if ( source && source.constructor == Array ) {
		srcInfo = "*trace"; 
		for ( var i = 0; i < source.length; ++i ) {
			srcInfo += "*"+[encodeData(source[i].functionName), encodeData(source[i].functionArguments), encodeData(source[i].fileName), source[i].lineNumber].join("|");
		}
	} else if ( source ) {
		srcInfo = "*src*"+encodeData(source.fileName)+"*"+source.lineNumber;
	}
	sendSocketData("log*"+level+"*"+encodeData(message)+srcInfo);
}
this.log = log;

this.onAssert = function(messages, caption)
{
	var param = {
		messages: messages,
		caption: AptanaUtils.format(caption),
		debug: socket.closed ? "disabled" : (enabled ? "suspend" : "enabled")
		};
	if ( param.debug == 'suspend' && options.suspendOnAssert ) {
		log('warn',param.caption);
		param.action = 'debug';
	} else if (isClientDebugger) {
		window.getAttention();
		openDialog("chrome://aptanadebugger/content/assert.xul", "", "chrome,modal,resizable=no", param);
	}
	if ( param.action == 'stop' ){
		terminate();
	} else if ( param.action == 'debug' ) {
		if ( !enabled ) {
			this.sendClientAction("attach");
		} else if ( !stepping ) {
			suspend("suspend");
		}
	}
}

// ************************************************************************************************

var httpHeaders = [];

const httpRequestObserver = {
	observe: function(subject, topic, data) {
		if (topic == "http-on-modify-request") {
			var httpChannel = subject.QueryInterface(nsIHttpChannel);
			for (var i in httpHeaders) {
				httpChannel.setRequestHeader(httpHeaders[i].name, httpHeaders[i].value, false);
			}
		}
	},
	
	register: function() {
		if (observerService) {
			try {
				observerService.addObserver(this, "http-on-modify-request", false);
			} catch(e) {}
		}
	},
	
	unregister: function() {
		if (observerService) {
			try {
				observerService.removeObserver(this, "http-on-modify-request");
			} catch(e) {}
		}
	}
};

function onSetHttpHeaders(args)
{
	var headers = [];
	for( var i = 0; i < args.length; ++i ) {
		var subargs = args[i].split("|");
		headers.push({ name: decodeData(subargs[0]), value: decodeData(subargs[1]) });
	}
	httpHeaders = headers;
	sendResponse(null);
}

// ************************************************************************************************

var breakpoints = {};
var breakpointProps = {};
var deferredBreakpoints = [];
var skipToggleBreakpoint = false;

function onSetBreakpoint(cmd,href,line,props)
{
	var result = null;
	if (FILE_URL_PATTERN.test(href)) {
		var match = FILE_URL_PATTERN.exec(href);
		href = "file:///"+match[1];
	}
	if (href in translateHrefs)
		href = translateHrefs[href];

	if ( cmd == "create" || cmd == "change" )
	{
		if ( cmd == "change" && hasBreakpoint(href,line) )
		{
			clearBreakpoint(href,line);
		}
		if (setBreakpoint(href,line,props))
		{
			result = "created";
			if ( cmd == "change" )
			{
				result = "changed";
			}
		}
	} else if ( cmd == "remove" ) {
		if (clearBreakpoint(href,line) )
			result = "removed";
	}
	sendResponse(result);
}

function hasBreakpoint(href,line)
{
	if ( href in breakpoints ) {
		var list = breakpoints[href];
		for( var i = 0; i < list.length; ++i ) {
			if ( list[i] == line )
				return true;
		}
	}
	return false;
}

function clearBreakpoint(href,line)
{
	if ( removeBreakpoint(href,line) ) {
		skipToggleBreakpoint = true;
		fbs.clearBreakpoint(href,line);
		delete breakpointProps[href+":"+line];
		skipToggleBreakpoint = false;
		return true;
	}
	return false;
}

function clearAllBreakpoints()
{
	var hrefs = [];
	for( var href in breakpoints ) {
		if ( breakpoints[href].length > 0 )
			hrefs.push(href);
	}
	skipToggleBreakpoint = true;
	hook("clearAllBreakpoints", [hrefs]);
	breakpointProps = {};
	skipToggleBreakpoint = false;
}

function setBreakpoint(href, line, props)
{
	if (!started) {
		deferredBreakpoints.push({ href: href, line: line, props: props });
		return true;
	}
	skipToggleBreakpoint = true;
	if ( hook("setBreakpoint", [href, line, props]) ) {
		breakpointProps[href+":"+line] = props;
		skipToggleBreakpoint = false;
		addBreakpoint(href,line);
		return true;
	}
	skipToggleBreakpoint = false;
	return false;
}

function addBreakpoint(href,line)
{
	if ( href in breakpoints ) {
		var list = breakpoints[href];
		for( var i = 0; i < list.length; ++i ) {
			if ( list[i] == line )
				return false;
		}
		breakpoints[href].push(line);
	} else {
		breakpoints[href] = [line];
	}
	return true;
}

function removeBreakpoint(href,line)
{
	if ( href in breakpoints ) {
		var list = breakpoints[href];
		for( var i = 0; i < list.length; ++i ) {
			if ( list[i] == line ) {
				if ( list.length > 1 ) {
					list.slice(i,1);
				} else {
					delete breakpoints[href];
				}
				return true;
			}
		}
	}
	return false;
}

function restoreBreakpoints()
{
	if (!started) {
		return;
	}
	for (var i = 0; i < deferredBreakpoints.length; ++i) {
		var bp = deferredBreakpoints[i];
		setBreakpoint(bp.href, bp.line, bp.props);
	}
}
// ************************************************************************************************

var exceptionFilters = {};

function onSetException(cmd,exceptionType)
{
	var result = null;
	var exceptionKey = ":"+exceptionType; 
	if ( cmd == "create" || cmd == "change" )
	{
		result = "created";
		if ( !(exceptionKey in exceptionFilters) ) {
			exceptionFilters[exceptionKey] = { exceptionType: exceptionType };
		}
		if ( cmd == "change" )
			result = "changed";
	} else if ( cmd == "remove" ) {
		if ( exceptionKey in exceptionFilters ) {
			delete exceptionFilters[exceptionKey];
			result = "removed";
		}
	}
	sendResponse(result);
}

function getExceptionTypeName(exc)
{
	if ( exc.jsType == TYPE_STRING ) {
		return "String";
	}else if ( exc.jsType == TYPE_OBJECT ) {
		var type = exc.jsClassName;
		if ( exc.isNative && type == "Error" )
			type = exc.jsConstructor.jsFunctionName;
		return type;	
	}
	dd("Exception type="+exc.jsType+" className="+exc.jsClassName);
	return "";
}

function hasExceptionFilter(exc)
{
	var exceptionType = ':'+getExceptionTypeName(exc);
	if ( exceptionType in exceptionFilters ) {
		return true;
	}
	/* Search hierarhy */
	if ( exc.jsType == TYPE_OBJECT ) {
		exceptionType = ':'+exc.jsClassName;
		if ( exceptionType in exceptionFilters ) {
			return true;
		}
	}
	return false;
}

// ************************************************************************************************

var instanceBreakpoints = {};

function onSetInstanceBreakpoint(cmd,instanceType)
{
	var result = null;
	var typeKey = ":"+instanceType; 
	if ( cmd == "create" || cmd == "change" )
	{
		result = "created";
		if ( !(typeKey in instanceBreakpoints) ) {
			instanceBreakpoints[typeKey] = { instanceType: instanceType };
		}
		if ( cmd == "change" )
			result = "changed";
	} else if ( cmd == "remove" ) {
		if ( typeKey in instanceBreakpoints ) {
			delete instanceBreakpoints[typeKey];
			result = "removed";
		}
	}
	sendResponse(result);
}

// ************************************************************************************************

var stepFilters = [];

function onSetStepFilters(cmd,arg)
{
	stepFilters = arg.split("|");
	sendResponse(null);
}

// ************************************************************************************************

function onGetFrames()
{
	var result = null;
    if ( debugging && dbgstate.frames.length > 0 ) {
    	var names = [];
	    for (var i = 0; i < dbgstate.frames.length; ++i) {
		    	var frame = dbgstate.frames[i];
		    	names.push([i, encodeData(getFunctionName(frame.script)), encodeData(packageFrameArguments(frame)), encodeData(frame.script.fileName), frame.line, frame.isNative, frame.pc, frame.script.tag].join("|"));
		}
		if(names.length > 0)
	    	result = names.join("*");	
    }
	sendResponse(result);
}

function packageFrameArguments(frame)
{
	var args = [];
	var listValue = {value: null}, lengthValue = {value: 0};
	frame.scope.getProperties(listValue, lengthValue);
	for (var j = 0; j < lengthValue.value; ++j) {
		if ( (listValue.value[j].flags & PROP_ARGUMENT) )
		{
			var props = getPropertyValue(listValue.value[j].value);
			args.push(props.displayType);
		}
	}
	return args.join(", ");
}

// ************************************************************************************************

var loadedHrefs = [];
var functionsNameMap = {};
var anonymousFunctions = [];
var translateHrefs = {};
var loadedScripts = {};

function enumerateScripts()
{
	var scripts = fbs.enumerateScripts({});
	for( var i = 0; i < scripts.length; ++i )
	{
		var scriptKey = ""+scripts[i].fileName+":"+scripts[i].baseLineNumber+":"+scripts[i].functionName;
		var fileName = scripts[i].fileName;
		if ( (scriptKey in loadedScripts) || (fileName == '[Eval-script]') || (fileName.substr(0,11) == 'javascript:')
			|| fileName.substr(0,15) == 'resource://gre/')
		{
			scripts.splice(i, 1);
			--i;
			continue;
		}
		collectScriptInfo(scripts[i]);
	}
	return scripts;
}

function packageScriptReport(scripts)
{
	var names = [];
	for (var i = 0; i < scripts.length; ++i)
	{
		var script = scripts[i];
		names.push([script.tag, encodeData(script.fileName), encodeData(getFunctionName(script)), script.baseLineNumber, script.lineExtent].join("|"));
	}
	return names;	
}

function collectScriptInfo(script)
{
	if ( script.functionName == 'anonymous' ) {
		var key = "tag:"+script.tag;
		if ( key in functionsNameMap ) {
			return;
		}
		anonymousFunctions.push({
			tag: script.tag,
			fileName: AptanaUtils.normalizeHref(script.fileName),
			baseLineNumber: script.baseLineNumber,
			lineExtent: script.lineExtent
		});			
	}
}

function resolveAnonymousFunctionsNames()
{
	for (var i = 0; i < loadedHrefs.length; ++i) {
		resolveAnonymousFunctionsNamesForFile(loadedHrefs[i]);
	}
	loadedHrefs = [];
}

function resolveAnonymousFunctionsNamesForFile(href)
{
	var source = false;
	var resolved = [];
	if ( anonymousFunctions.length > 0 ) {
		for (var i = 0; i < anonymousFunctions.length; ++i) {
			var af = anonymousFunctions[i];
			if ( af.fileName == href ) {
				if ( !source ) {
					source = hook("getSourceLines", [href]);
				}
				var baseLine = af.baseLineNumber-1;
				var text = "";
				for( var j = 0; !FUNCTION_NAME_GUESS_PATTERN.test(text) && j < 5; ++j ) {
					text = source[baseLine-j]+text;
					var pos = text.lastIndexOf ('function');
					if ( pos >= 0 )
						text = text.substring(0, pos);
				}
				if ( FUNCTION_NAME_GUESS_PATTERN.test(text) ) {
					var match = FUNCTION_NAME_GUESS_PATTERN.exec(text);
					functionsNameMap["tag:"+af.tag] = match[1];
					resolved.push(""+af.tag+"|"+encodeData(match[1]));
					anonymousFunctions.splice(i, 1);
					--i;
				}
			}
		}
	}
	if ( resolved.length > 0 ) {
		sendSocketData("scripts*resolved*"+resolved.join('*'));
	}
}
	
function getFunctionName(script)
{
	var functionName = script.functionName;
	if ( !functionName )
		functionName = '';
	if ( functionName == 'anonymous' ) {
		var key = "tag:"+script.tag;
		if ( key in functionsNameMap ) {
			functionName = functionsNameMap[key];
		}
	}
	return functionName;
}

// ************************************************************************************************

function onGetVariables(variableName)
{
   	var result = null;
   	if ( !debugging ) {
   		sendResponse(result);
   		return;
   	}
	variableName = decodeData(variableName);
	if ( variableName.indexOf("frame[") == 0 && (dbgstate.frames.length > 0) )
	{
		var i = variableName.indexOf("]");
		var frameIndex = parseInt(variableName.substring(variableName.indexOf("[")+1,i));
		var frame = dbgstate.frames[frameIndex];
		variableName = variableName.substring(i+1);
		if ( variableName.indexOf(".") == 0 )
			variableName = variableName.substring(1);
	} else if ( variableName.indexOf("eval[") == 0 && dbgstate.evalResults)
	{
		var i = variableName.indexOf("]");
		var _eval = dbgstate.evalResults[''+parseInt(variableName.substring(variableName.indexOf("[")+1,i))].value;
		variableName = "eval"+variableName.substring(i+1);
	} else {
   		sendResponse(result);
   		return;
	}
	
	switch(variableName)
	{
		case "":
			if ( frame )		
			{
		    	var names = [];
				var rval = frame.thisValue;
				var name = "this";
				var val = getPropertyValue(rval);
				names.push([encodeData(name), val.displayType, val.flags, encodeData(val.displayValue)].join("|"));

				if(dbgstate.currentException && frameIndex == 0 )
				{
					rval = dbgstate.currentException;
					name = "exception";
					val = getPropertyValue(rval);
					names.push([encodeData(name), val.displayType, val.flags + "we", encodeData(val.displayValue)].join("|"));
				}

				if(frame.scope)
				{
					var scopeNames = packageReport(frame.scope,"l");
					for( var i = 0; i < scopeNames.length; ++i )
						names.push(scopeNames[i]);
				}
				if(names.length > 0)
					result = names.join("*");
		    }
	        break;

		case "this":
			if( frame && frame.thisValue)
			{
				var names = packageReport(frame.thisValue);
				if(names.length > 0)
					result = names.join("*");
			}			
	        break;

		case "exception":		
			if(dbgstate.currentException)
			{
				var names = packageReport(dbgstate.currentException);
				if(names.length > 0)
					result = names.join("*");
		    }
	        break;

		case "eval":		
			if(_eval)
			{
				var names = packageReport(_eval);
				if(names.length > 0)
					result = names.join("*");
		    }
	        break;

	    default:
	    	{
	    		var value = null;

	    		if(variableName.indexOf("this") == 0)
	    		{
	    			if ( !frame || !frame.thisValue ) {
	    				break;
	    			}
	    			variableName = variableName.substring(5);
	    			value = findVariable(frame.thisValue,variableName);
			} else if(variableName.indexOf("eval") == 0)
		    	{
		    		if ( !_eval ) {
		    			break;
		    		}
		    		variableName = variableName.substring(5);
		    		value = findVariable(_eval,variableName);
			} else if(variableName.indexOf("exception") == 0)
		    	{
		    		if ( !dbgstate.currentException && !(frameIndex == 0) ) {
		    			break;
		    		}
		    		variableName = variableName.substring(10);
		    		value = findVariable(dbgstate.currentException,variableName);
	    		} else if( frame && frame.scope)
	    		{
					value = findVariable(frame.scope,variableName);
		    	}
				    		
			if(value != null)
   			{
   				var names = packageReport(value);
   				if(names.length > 0)
					result = names.join("*");
   			}
		}
		break;
	}
	sendResponse(result);
}

function packageReport(value,flags)
{
	var props = listProperties(value);
	var names = [];
	if ( typeof flags == "undefined" )
		flags = "";
	
	for (var i = 0; i < props.length; ++i)
	{
		var name = props[i].name;
		try {
			var val = getPropertyValue(props[i].value);
			names.push([encodeData(name), val.displayType, flags+val.flags+convertFlags(props[i].flags), encodeData(val.displayValue)].join("|")); 
		} catch (exc) {
			dd(exc,'err');
		}
	}
	return names;	
}

function findVariable(parent, variableName)
{
	var nameParts = variableName.split(".");
	var obj = parent;
	for(var i = 0; obj != null && i < nameParts.length; ++i)
	{
		var part = nameParts[i];
		var prop = obj.getProperty(part);
		if ( prop != null ) {
			obj = prop.value;
		} else {
			var jsobj = obj.getWrappedValue();
			obj = null;
			if ( part in jsobj ) {
				obj = jsd.wrapValue(jsobj[part]);
			}
		}
	}
	return obj;
}

function listProperties(variable)
{
	var props = {};
	var value;
	var flags;
	
	// get the enumerable properties
	var jsvalue = variable.getWrappedValue();
	for( var name in jsvalue )
	{
		flags = PROP_ENUMERATE | PROP_HINTED;
		if ( CONSTANTS_FILTER.test(name) ) {
			if ( !options.showConstants )
				continue;
			flags |= PROP_CONST;
		}
		try {
			try {
				value = jsvalue[name];	
			} catch( exc ) {
				value = null;
			}
			value = jsd.wrapValue(value);
			if ( (value.jsType == TYPE_FUNCTION) && !options.showFunctions )
				continue;
			if ( (value.jsType == TYPE_OBJECT) && filterObjectValue(value) )
				continue;
				
			if ( name == "length" && jsvalue instanceof Array )
				flags |= PROP_READONLY;				
			
			props[':'+name] = ({
				name: name,
				value: value,
				flags: flags
			});			
		} catch( exc ) {
			dd(exc,'err');
		}
	}
	
	// get the local properties, may or may not be enumerable
	var listValue = {value: null}, lengthValue = {value: 0};
	variable.getProperties(listValue, lengthValue);
	for (var i = 0; i < lengthValue.value; ++i)
	{
		var prop = listValue.value[i];
		flags = prop.flags;
		var name = prop.name.stringValue;
		if ( (prop.value.jsType == TYPE_FUNCTION) && !options.showFunctions )
			continue;
		if ( (prop.value.jsType == TYPE_OBJECT) && filterObjectValue(prop.value) )
			continue;
		if ( CONSTANTS_FILTER.test(name) )
		{
			if ( !options.showConstants )
			{
				if ( props[':'+name] )
					delete props[':'+name];
				continue;				
			}
			flags |= PROP_CONST;
		}
		
		props[':'+name] = ({
			name: name,
			value: prop.value,
			flags: flags
		});
	}
	
	// sort the property list
	var nameList = AptanaUtils.keys(props);
	nameList.sort();
	var propList = [];
	for (i = 0; i < nameList.length; ++i)
	{
		var name = nameList[i];
		if ( name == ':__aptanaXHRSpy__'
			|| name.substr(0,9).toLowerCase() == ':_firebug')
			continue;
		propList.push(props[name]);			
	}

	return propList;	
}

function filterObjectValue(value)
{
	var className = value.jsClassName;
	return className == 'Constructor' || className == 'nsXPCComponents'
		|| className == 'XULControllers' || className.substr(0,9) == 'chrome://';
}

function getPropertyValue(value,detail)
{ with (AptanaDebugger) {

	var val = new Object();
	val.flags = "";
	var strval;
	if ( typeof detail == "undefined" )
		detail = false;
	
	var jsType = value.jsType;	
	switch (jsType)
	{
		case TYPE_VOID:
			val.displayType  = CONST_TYPE_VOID;
			val.displayValue = CONST_TYPE_VOID
			break;
		case TYPE_NULL:
			val.displayType  = CONST_TYPE_NULL;
			val.displayValue = CONST_TYPE_NULL;
			break;
		case TYPE_BOOLEAN:
			val.displayType  = CONST_TYPE_BOOLEAN;
			val.displayValue = value.stringValue;
			break;
		case TYPE_INT:
			val.displayType  = CONST_TYPE_INT;
			val.displayValue = value.intValue;
			break;
		case TYPE_DOUBLE:
			val.displayType  = CONST_TYPE_DOUBLE;
			val.displayValue = value.doubleValue;
			break;
		case TYPE_STRING:
			val.displayType  = CONST_TYPE_STRING;
			strval = value.stringValue;
			val.displayValue = strval.quote();
			break;
		case TYPE_FUNCTION:
		case TYPE_OBJECT:
			val.displayType = value.jsClassName;

			var ctor = value.jsClassName;
			if ( !JAVA_OBJECT_PATTERN.test(ctor) ) {
				val.flags = "o";
			}
			switch (ctor)
			{
				case "Function":
					val.displayType  = CONST_TYPE_FUNCTION;
					val.displayValue = (value.isNative ? CONST_CLASS_NATIVE_FUN : CONST_CLASS_SCRIPT_FUN);
					if ( detail && !value.isNative )
					{
						val.detailValue = getValueDetail(val.displayType,value);						
					}
					break;
				
				case "Object":
					val.displayType  = CONST_TYPE_OBJECT;
					if (value.jsConstructor) {
						val.displayType = value.jsConstructor.jsFunctionName;
					}
					val.displayValue = value.stringValue;
					if ( val.displayValue == 'null' ) {
						dd('!null value was found','err');
						val.flags = "";
					}
					if ( detail ) {
						val.detailValue = getObjectDetail(val.displayType,value);
					}
					break;
				
				case "XPCWrappedNative_NoHelper":
					val.displayValue = CONST_CLASS_CONST_XPCOBJ;
					break;
				
				case "XPC_WN_ModsAllowed_Proto_JSClass":
					val.displayValue = CONST_CLASS_XPCOBJ;
					break;
				
				case "String":
					val.flags = "";
					strval = value.stringValue;
					val.displayValue = strval.quote();
					break;

				case "Call":
					val.displayValue = "["+val.displayType+"]";
					break;
				
				case "Date":
					if ( detail )
					{
						val.detailValue = getValueDetail(val.displayType,value);						
					}
				case "Number":
				case "Boolean":
					val.flags = "";
					val.displayValue = value.stringValue;
					break;
				
				default:
					val.displayValue = value.stringValue;
					if ( val.displayValue == 'null' ) {
						dd('!null value was found','err');
						val.flags = "";
					}
					if ( detail )
					{
						val.detailValue = getValueDetail(val.displayType,value);						
					}
					break;
			}
			break;
	}
	if ( detail && typeof(val.detailValue) == "undefined" )
	{
		val.detailValue = val.displayValue;
	}
		
	return val;
} }

function convertFlags(flags)
{
	var output = ( flags & PROP_READONLY ) ? "" : "w";
	if ( flags & PROP_CONST )
		output = output + "c";
	if ( flags & PROP_ENUMERATE )
		output = output + "n";
	if ( flags & PROP_PERMANENT )
		output = output + "p";
	if ( flags & PROP_ARGUMENT )
		output = output + "a";
	if ( flags & PROP_VARIABLE )
		output = output + "v";
	if ( flags & PROP_EXCEPTION )
		output = output + "e";
	if ( flags & PROP_ERROR )
		output = output + "r";
	return output;
}

// ************************************************************************************************

var evalResultsLastId = 0;

function onEval(variableName,expression)
{
   	var result = null;
   	if ( !debugging ) {
   		sendResponse(result);
   		return;
   	}
	variableName = decodeData(variableName);
	expression = decodeData(expression);
	if ( variableName.indexOf("frame[") == 0 && (dbgstate.frames.length > 0) )
	{
		var i = variableName.indexOf("]");
		var frame = dbgstate.frames[parseInt(variableName.substring(variableName.indexOf("[")+1,i))];
		variableName = variableName.substring(i+1);
		if ( variableName.indexOf(".") == 0 )
			variableName = variableName.substring(1);
	} else {
		sendResponse(result);
		return;
	}
	// simple implementation for frames only
	var rval = new Object();
	if ( !dbgstate.evalResults ) {
		dbgstate.evalResults = {};
	}
	if ( frame.eval(expression,"[Eval-script]",1,rval) )
	{
		var id = evalResultsLastId++;
		result = "result*"+id;
		var prop = rval;
		rval = ("value" in rval) ? rval.value : null; 
		dbgstate.evalResults[''+id] = { expr: expression, value: rval};
		var val = getPropertyValue(rval);
		result += "*" + [val.displayType, val.flags + convertFlags(prop.flags), encodeData(val.displayValue)].join("|");
	} else {
		var exc = rval.value.getWrappedValue();
		result = "exception*"+("message" in exc ? encodeData(exc.message) : "");
	}
	sendResponse(result);
}

// ************************************************************************************************

function onSetValue(variableName,valueRef)
{
   	var result = null;
   	if ( !debugging ) {
   		sendResponse(result);
   		return;
   	}
	var obj = null;
	var newValue = null;
	variableName = decodeData(variableName);
	if ( variableName.indexOf("frame[") == 0 && (dbgstate.frames.length > 0) )
	{
		var i = variableName.indexOf("]");
		var frameIndex = parseInt(variableName.substring(variableName.indexOf("[")+1,i));
		var obj = dbgstate.frames[frameIndex];
		variableName = variableName.substring(i+1);
		if ( variableName.indexOf(".") == 0 )
			variableName = variableName.substring(1);
		var nameParts = variableName.split(".");
		var propertyName = nameParts[nameParts.length-1];
		if ( nameParts.length == 1 ) {
			if ( propertyName == "exception" && dbgstate.currentException && frameIndex == 0 ) {
				obj = this;
				propertyName = "dbgstate.currentException";
			} else {
				obj = obj.scope;
			}
		} else {
			nameParts.splice(nameParts.length-1,1);
			if( nameParts[0] == "this" ) {
				obj = obj.thisValue;
				nameParts.splice(0,1);
			} else if ( nameParts[0] == "exception" && dbgstate.currentException && frameIndex == 0 ) {
				obj = dbgstate.currentException;
				nameParts.splice(0,1);
			} else /* scope */ {
				obj = obj.scope;
			}
			if ( nameParts.length != 0 ) {
				variableName = nameParts.join('.');
				obj = findVariable(obj,variableName);				
			}
		}
	}
	if ( valueRef.indexOf("eval[") == 0 && dbgstate.evalResults)
	{
		var i = valueRef.indexOf("]");
		newValue = dbgstate.evalResults[''+parseInt(valueRef.substring(valueRef.indexOf("[")+1,i))].value;
	}
	if ( obj != null && newValue != null && propertyName )
	{
		try {
			if ( obj != this ) {
				var jsobj = obj.getWrappedValue();
				jsobj[propertyName] = newValue.getWrappedValue();
				var prop = obj.getProperty(propertyName);
			} else if ( propertyName == "dbgstate.currentException" ) {
				dbgstate.currentException = newValue;
				prop = { value: newValue, flags: 0 };
			}
			if ( !prop ) {
				prop = { value: jsd.wrapValue(jsobj[propertyName]), flags: 0};
			}
			var rval = prop.value;
			var val = getPropertyValue(rval);
			result = "result*" + [val.displayType, val.flags + convertFlags(prop.flags), encodeData(val.displayValue)].join("|");
		} catch( ex ) {
			result = "exception*"+encodeData(ex.message);		
		}
	}
	
	sendResponse(result);	
}

// ************************************************************************************************

var detailFormatters = {};

function onSetDetailFormatters(args)
{
	var formatters = {};
	for( var i = 0; i < args.length; ++i )
	{
		var subargs = args[i].split("|");
		formatters[":"+subargs[0]] = preprocessDetailFormatter(decodeData(subargs[1]));
	}
	detailFormatters = formatters;
	sendResponse(null);
}

function onGetDetails(variableName)
{
   	var result = null;
   	if ( !debugging ) {
   		sendResponse(result);
   		return;
   	}
	variableName = decodeData(variableName);
	if ( variableName.indexOf("frame[") == 0 && (dbgstate.frames.length > 0) )
	{
		var i = variableName.indexOf("]");
		var frameIndex = parseInt(variableName.substring(variableName.indexOf("[")+1,i));
		var frame = dbgstate.frames[frameIndex];
		variableName = variableName.substring(i+1);
		if ( variableName.indexOf(".") == 0 )
			variableName = variableName.substring(1);
	} else if ( variableName.indexOf("eval[") == 0 && dbgstate.evalResults )
	{
		var i = variableName.indexOf("]");
		var _eval = dbgstate.evalResults[''+parseInt(variableName.substring(variableName.indexOf("[")+1,i))].value;
		variableName = "eval"+variableName.substring(i+1);
	} else {
   		sendResponse(result);
   		return;
   	}

	var value = null;
	
	switch(variableName)
	{
		case "this":
			if(frame.thisValue)
			{
				value = frame.thisValue;
			}			
	        break;

		case "exception":		
			if(dbgstate.currentException)
			{
				value = dbgstate.currentException;
		    }
	        break;

		case "eval":		
			if(_eval)
			{
				value = _eval;
		    }
	        break;

	    default:
	    		if(variableName.indexOf("this") == 0)
	    		{
	    			if ( !frame || !frame.thisValue ) {
	    				break;
	    			}
	    			variableName = variableName.substring(5);
	    			value = findVariable(frame.thisValue,variableName);
			} else if(variableName.indexOf("eval") == 0)
		    	{
		    		if ( !_eval ) {
		    			break;
		    		}
		    		variableName = variableName.substring(5);
		    		value = findVariable(_eval,variableName);
			} else if(variableName.indexOf("exception") == 0)
		    	{
		    		if ( !dbgstate.currentException || !(frameIndex == 0) ) {
		    			break;
		    		}
		    		variableName = variableName.substring(10);
		    		value = findVariable(dbgstate.currentException,variableName);
	    		} else if( frame && frame.scope)
	    		{
				value = findVariable(frame.scope,variableName);
		    	}
			break;
	}
	if(value != null)
	{
		var val = getPropertyValue(value,true);
		result = "result*" + encodeData(val.detailValue);
	}

	sendResponse(result);
}

function getObjectDetail(type,value)
{ with(AptanaDebugger) {

	if ( hasDetailFormatter(type) ) {
		return getValueDetail(type,value);
	} else {
		var ctor = value.jsConstructor;
		while( ctor && ctor.jsPrototype ) {
			var jsType = ctor.jsFunctionName;
			if ( hasDetailFormatter(jsType) ) {
				return getValueDetail(jsType,value);
			}
			ctor = null; /* TODO: class hierarhy */
		}
	}
	if ( hasDetailFormatter(CONST_TYPE_OBJECT) ) {
		return getValueDetail(CONST_TYPE_OBJECT,value);
	}

} }

function hasDetailFormatter(type)
{
	type = ":"+type;
	return ( type in detailFormatters );
}

function getValueDetail(type,value)
{
	var detail;
	type = ":"+type;
	if ( type in detailFormatters )
	{
		var win = attachedWindows[0];
		try {
			var formatter = detailFormatters[type];
			var sandbox = new Components.utils.Sandbox(win.location.href);
			sandbox.__this__ = value.getWrappedValue();
			detail = Components.utils.evalInSandbox(formatter,sandbox);
		} catch(exc) {
			detail = exc;
		}
	}
	return detail;
}

function preprocessDetailFormatter(snippet)
{
	snippet =  snippet.replace(/(\s|^|\+|-)this\b/g, "$1__this__");
	if ( /\breturn\s.*;/.test(snippet) ) {
		snippet = "function aptanaDetails(){\n"+snippet+"\n}; aptanaDetails();";
	}
	/* TODO: use function.apply(__this__) */
	return snippet;
}

// ************************************************************************************************

function updateExtension()
{
	var updateListener = {
		updateFound: function(addon) {
			if ( addon ) {
				try {
					AptanaUtils.installExtension(addon);
				} catch(exc) {
					dd(exc,'err');
				}
			}
		}
	};
	if ( !isClientDebugger || !AptanaUtils.findExtensionUpdate(AptanaDebugger.EXTENSION_ID,updateListener) ) {
	}
}

// ************************************************************************************************

function encodeData(data)
{
	if( typeof data != "string")
		data = ""+data;
    return data.replace(/#/g, "#0").replace(/\|/g, "#1").replace(/\*/g, "#2");
}

function decodeData(data)
{
    return data.replace(/#2/g, "*").replace(/#1/g, "|").replace(/#0/g, "#");
}

// ************************************************************************************************

function ddd(text)
{
	try {
		AptanaLogger.logConsole(text);
	} catch(exc) {
	}
}

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
		if (typeof(AptanaDebugger) != "undefined" && AptanaDebugger.DEBUG ) {
			AptanaLogger.log(message,level);
		}
	} catch(exc) {
	}
}

// ************************************************************************************************

}).apply(AptanaDebugger);
