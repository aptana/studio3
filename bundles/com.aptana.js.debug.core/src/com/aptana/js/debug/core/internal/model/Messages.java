/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal.model;

import org.eclipse.osgi.util.NLS;

public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.js.debug.core.internal.model.messages"; //$NON-NLS-1$

	private Messages() {
	}

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * WatchExpressionDelegate_ExpressionEvaluation
	 */
	public static String WatchExpressionDelegate_ExpressionEvaluation;

	public static String JSDebugThread_main_label;

	/**
	 * JSDebugThread_Suspending
	 */
	public static String JSDebugThread_Suspending;

	public static String JSDebugThread_Thread_Label;

	/**
	 * JSDebugExceptionBreakpoint_JSExceptionBreakpoint_0_1
	 */
	public static String JSDebugExceptionBreakpoint_JSExceptionBreakpoint_0_1;

	/**
	 * JSDebugExceptionBreakpoint_BreakpointMarkerCreationFailed
	 */
	public static String JSDebugExceptionBreakpoint_BreakpointMarkerCreationFailed;

	/**
	 * JSDebugLineBreakpoint_JSBreakpoint_0_1
	 */
	public static String JSDebugLineBreakpoint_JSBreakpoint_0_1;

	/**
	 * JSDebugLineBreakpoint_BreakpointMarkerCreationFailed
	 */
	public static String JSDebugLineBreakpoint_BreakpointMarkerCreationFailed;

	public static String JSDebugProcess_Terminate_Failed;

	/**
	 * JSDebugTarget_TopLevelScript
	 */
	public static String JSDebugTarget_TopLevelScript;

	/**
	 * JSDebugTarget_EvalScript
	 */
	public static String JSDebugTarget_EvalScript;

	/**
	 * JSDebugTarget_JSDebugger
	 */
	public static String JSDebugTarget_JSDebugger;

	public static String JSDebugWatchpoint_JS_Watchpoint;
}
