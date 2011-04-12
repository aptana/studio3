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

(function() {

// ************************************************************************************************

const nsISupports = Components.interfaces.nsISupports;
const jsdIExecutionHook = Components.interfaces.jsdIExecutionHook;

const TYPE_INTERRUPTED = jsdIExecutionHook.TYPE_INTERRUPTED;
const TYPE_BREAKPOINT = jsdIExecutionHook.TYPE_BREAKPOINT;
const TYPE_DEBUG_REQUESTED = jsdIExecutionHook.TYPE_DEBUG_REQUESTED;
const TYPE_DEBUGGER_KEYWORD = jsdIExecutionHook.TYPE_DEBUGGER_KEYWORD;
const TYPE_THROW = jsdIExecutionHook.TYPE_THROW;

const RETURN_CONTINUE = jsdIExecutionHook.RETURN_CONTINUE;
const RETURN_CONTINUE_THROW = jsdIExecutionHook.RETURN_CONTINUE_THROW;
const RETURN_RET_WITH_VAL = jsdIExecutionHook.RETURN_RET_WITH_VAL;
const RETURN_THROW_WITH_VAL = jsdIExecutionHook.RETURN_THROW_WITH_VAL;

const STEP_OVER = 1;
const STEP_INTO = 2;
const STEP_OUT = 3;

const NS_NOINTERFACE = Components.results.NS_NOINTERFACE;

// ************************************************************************************************


const self = this;
var fbs;
var debuggerClient;
var defaultContext;

this.setHook("init",function(debuggr)
{
	fbs = Components.classes['@joehewitt.com/firebug;1'].getService(nsISupports).wrappedJSObject;							
	debuggerClient =
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

		debuggerName: "JaxerDebugger",
		activeContexts: [], // FB@explore only
		
		// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
		// nsIFireBugDebugger
		
		supportsWindow: function(win)
		{
			this.breakContext = defaultContext;
			return true;
		},

		supportsGlobal: function(global)
		{
			this.breakContext = defaultContext;
			return true;
		},
		
		onLock: function(state)
		{
		},
		
		onBreak: function(frame, type)
		{
			return this.stop(defaultContext, frame, type);
		},
		
		onHalt: function(frame)
		{
			return RETURN_CONTINUE;
		},
		
		onThrow: function(frame, rv)
		{
			if (debuggr.onThrow(frame, rv))
				return this.stop(defaultContext, frame, TYPE_THROW, rv);
			return RETURN_CONTINUE_THROW;
		},
		
		onCall: function(frame)
		{
		},
		
		onError: function(frame, error)
		{
			var hookReturn = debuggr.onError(frame, error);
			if (hookReturn)
				return hookReturn;
			return -2; /* let firebug service decide to break or not */
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
            debuggr.onTopLevel(frame);
			return frame.script.fileName
		},
				
		onToggleBreakpoint: function(url, lineNo, isSet, props)
		{
			/* do nothing */
		},
		
		onToggleErrorBreakpoint: function(url, lineNo, isSet)
		{
			/* do nothing */
		},
		
		onToggleMonitor: function(url, lineNo, isSet)
		{
			/* do nothing */
		},

		init: function()
		{
			debuggr.onInit(self);
		},
		
		stop: function(context, frame, type, rv)
		{
			var executionContext;
			try {
				executionContext = frame.executionContext;
			} catch (exc) {
				// Can't proceed with an execution context - it happens sometimes.
				return RETURN_CONTINUE;
			}

			context.debugFrame = frame;
			context.stopped = true;

			var hookReturn = debuggr.onStop(self, frame, type, rv);
			if (hookReturn >= 0) {
				delete context.stopped;
	            delete context.debugFrame;
				delete context;
				return hookReturn;
			}

			executionContext.scriptsEnabled = false;
			try {
				// We will pause here until resume is called
				fbs.enterNestedEventLoop({onNest: function() {} });
			}
			catch (exc) {
				// Just ignore exceptions that happened while in the nested loop
				ddd("debugger exception in nested event loop: "+exc+"\n");
			}
			executionContext.scriptsEnabled = true;

			debuggr.onResume();

			if (this.aborted) {
				delete this.aborted;
				return RETURN_ABORT;
			} else
				return RETURN_CONTINUE;
		},

		resume: function(context)
		{
			if (!context.stopped)
				return;

			delete context.stopped;
	        delete context.debugFrame;
			delete context;

			fbs.exitNestedEventLoop();
		},

		abort: function(context)
		{
			if (context.stopped) {
				context.aborted = true;
				this.resume(context);
			}
		},

		stepOver: function(context)
		{
			if (!isValidFrame(context.debugFrame))
				return;

			fbs.step(STEP_OVER, context.debugFrame);
			this.resume(context);
		},

		stepInto: function(context)
		{
			if (!isValidFrame(context.debugFrame))
				return;

			fbs.step(STEP_INTO, context.debugFrame);
			this.resume(context);
		},

		stepOut: function(context)
		{
			if (!isValidFrame(context.debugFrame))
				return;

			fbs.step(STEP_OUT, context.debugFrame);
			this.resume(context);
		},

		suspend: function(context)
		{
			if (context.stopped)
				return;
			fbs.suspend();
		}
	};
	debuggerClient.wrappedJSObject = debuggerClient;
	defaultContext = {};
	fbs.registerDebugger(debuggerClient);
});


this.setHook("shutdown",function()
{
	return true;
});

this.setHook("enable",function()
{
	debuggerClient.init(self);
});

this.setHook("disable",function()
{
	fbs.unregisterDebugger(debuggerClient);
});

this.setHook("suspend",function()
{
	debuggerClient.suspend(defaultContext);
});

this.setHook("resume",function()
{
	debuggerClient.resume(defaultContext);
});

this.setHook("abort",function()
{
	debuggerClient.abort(defaultContext);
});

this.setHook("stepInto",function()
{
	debuggerClient.stepInto(defaultContext);
});

this.setHook("stepOver",function()
{
	debuggerClient.stepOver(defaultContext);
});

this.setHook("stepReturn",function()
{
	debuggerClient.stepOut(defaultContext);
});

this.setHook("stepToFrame",function(frame)
{
	defaultContext.debugFrame = frame;
	debuggerClient.stepOut(defaultContext);
});

function isValidFrame(frame)
{
    try {
        frame.script.fileName;
        return true;
    } catch (exc) {
        return false;
    }
}

// ************************************************************************************************

function ddd(text)
{
	AptanaLogger.logError(text);
}

// ************************************************************************************************

}).apply(AptanaDebugger);
