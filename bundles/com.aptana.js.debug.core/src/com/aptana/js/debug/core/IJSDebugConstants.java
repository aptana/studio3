/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core;

/**
 * JS Debug model constants
 */
public interface IJSDebugConstants {

	/**
	 * Unique identifier for the JS debug model (value <code>com.aptana.debug.core</code>).
	 */
	String ID_DEBUG_MODEL = JSDebugPlugin.PLUGIN_ID;

	/**
	 * Unique identifier for the JS breakpoints location (value <code>com.aptana.debug.core.breakpointLocation</code>).
	 */
	String BREAKPOINT_LOCATION = ID_DEBUG_MODEL + ".breakpointLocation"; //$NON-NLS-1$

	/**
	 * Unique identifier for the JS run to line breakpoints (value
	 * <code>com.aptana.debug.core.runToLineBreakpoint</code>).
	 */
	String RUN_TO_LINE = ID_DEBUG_MODEL + ".runToLineBreakpoint"; //$NON-NLS-1$

	/**
	 * Unique identifier for the JS breakpoints hit count (value <code>com.aptana.debug.core.breakpointHitCount</code>).
	 */
	String BREAKPOINT_HIT_COUNT = ID_DEBUG_MODEL + ".breakpointHitCount"; //$NON-NLS-1$

	/**
	 * Unique identifier for the JS breakpoints condition (value <code>com.aptana.debug.core.breakpointCondition</code>
	 * ).
	 */
	String BREAKPOINT_CONDITION = ID_DEBUG_MODEL + ".breakpointCondition"; //$NON-NLS-1$

	/**
	 * Unique identifier for the JS breakpoints condition enabled (value
	 * <code>com.aptana.debug.core.breakpointConditionEnabled</code>).
	 */
	String BREAKPOINT_CONDITION_ENABLED = ID_DEBUG_MODEL + ".breakpointConditionEnabled"; //$NON-NLS-1$

	/**
	 * Unique identifier for the JS breakpoints condition suspend flag (value
	 * <code>com.aptana.debug.core.breakpointConditionSuspendOnTrue</code>).
	 */
	String BREAKPOINT_CONDITION_SUSPEND_ON_TRUE = ID_DEBUG_MODEL + ".breakpointConditionSuspendOnTrue"; //$NON-NLS-1$

	/**
	 * Unique identifier for the JS breakpoints exception type name (value
	 * <code>com.aptana.debug.core.exceptionTypeName</code>).
	 */
	String EXCEPTION_TYPE_NAME = ID_DEBUG_MODEL + ".exceptionTypeName"; //$NON-NLS-1$

	/**
	 * Unique identifier for the JS watchpoint variable name (value
	 * <code>com.aptana.debug.core.watchpointVariableName</code>).
	 */
	String WATCHPOINT_VARIABLE_NAME = ID_DEBUG_MODEL + ".watchpointVariableName"; //$NON-NLS-1$

	/**
	 * Unique fill accessor for the JS watchpoint variable name (value
	 * <code>com.aptana.debug.core.watchpointVariableAccessor</code>). Internal use only.
	 */
	String WATCHPOINT_VARIABLE_ACCESSOR = ID_DEBUG_MODEL + ".watchpointVariableAccessor"; //$NON-NLS-1$

	/**
	 * Debug model-specific events
	 */
	int DEBUG_EVENT_URL_OPEN = 0x0001;

	/**
	 * DEBUG_EVENT_URL_OPENED
	 */
	int DEBUG_EVENT_URL_OPENED = 0x0002;

	/**
	 * ID_WARNING_STREAM
	 */
	String ID_WARNING_STREAM = "ID_WARNING_STREAM"; //$NON-NLS-1$

	/**
	 * PROCESS_TYPE
	 */
	String PROCESS_TYPE = "javascript"; //$NON-NLS-1$
}
