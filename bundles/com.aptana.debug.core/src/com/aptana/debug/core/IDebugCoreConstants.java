/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.core;

/**
 * Core Debug constants
 */
public interface IDebugCoreConstants
{

	String PREFIX = DebugCorePlugin.PLUGIN_ID;

	/**
	 * Unique identifier for the JS line breakpoint markers (value
	 * <code>com.aptana.debug.core.lineBreakpointMarker</code>).
	 */
	String ID_LINE_BREAKPOINT_MARKER = PREFIX + ".lineBreakpointMarker"; //$NON-NLS-1$

	/**
	 * Unique identifier for the JS exception breakpoint markers (value
	 * <code>com.aptana.debug.core.exceptionBreakpointMarker</code>).
	 */
	String ID_EXCEPTION_BREAKPOINT_MARKER = PREFIX + ".exceptionBreakpointMarker"; //$NON-NLS-1$

	/**
	 * Unique identifier for the JS watchpoint markers (value <code>com.aptana.debug.core.watchpointMarker</code>).
	 */
	String ID_WATCHPOINT_MARKER = PREFIX + ".watchpointMarker"; //$NON-NLS-1$

	/**
	 * Unique identifier for the JS breakpoints location (value <code>com.aptana.debug.core.breakpointLocation</code>).
	 */
	String BREAKPOINT_LOCATION = PREFIX + ".breakpointLocation"; //$NON-NLS-1$

	/**
	 * Unique identifier for the JS breakpoints hit count (value <code>com.aptana.debug.core.breakpointHitCount</code>).
	 */
	String BREAKPOINT_HIT_COUNT = PREFIX + ".breakpointHitCount"; //$NON-NLS-1$

	/**
	 * Unique identifier for the JS breakpoints condition (value <code>com.aptana.debug.core.breakpointCondition</code>
	 * ).
	 */
	String BREAKPOINT_CONDITION = PREFIX + ".breakpointCondition"; //$NON-NLS-1$

	/**
	 * Unique identifier for the JS breakpoints condition enabled (value
	 * <code>com.aptana.debug.core.breakpointConditionEnabled</code>).
	 */
	String BREAKPOINT_CONDITION_ENABLED = PREFIX + ".breakpointConditionEnabled"; //$NON-NLS-1$

	/**
	 * Unique identifier for the JS breakpoints condition suspend flag (value
	 * <code>com.aptana.debug.core.breakpointConditionSuspendOnTrue</code>).
	 */
	String BREAKPOINT_CONDITION_SUSPEND_ON_TRUE = PREFIX + ".breakpointConditionSuspendOnTrue"; //$NON-NLS-1$

	/**
	 * Unique identifier for the JS breakpoints exception type name (value
	 * <code>com.aptana.debug.core.exceptionTypeName</code>).
	 */
	String EXCEPTION_TYPE_NAME = PREFIX + ".exceptionTypeName"; //$NON-NLS-1$

	/**
	 * Unique identifier for the JS watchpoint variable name (value
	 * <code>com.aptana.debug.core.watchpointVariableName</code>).
	 */
	String WATCHPOINT_VARIABLE_NAME = PREFIX + ".watchpointVariableName"; //$NON-NLS-1$

	/**
	 * Unique fill accessor for the JS watchpoint variable name (value
	 * <code>com.aptana.debug.core.watchpointVariableAccessor</code>). Internal use only.
	 */
	String WATCHPOINT_VARIABLE_ACCESSOR = PREFIX + ".watchpointVariableAccessor"; //$NON-NLS-1$

	/**
	 * ID_STANDARD_ERROR_STREAM
	 */
	String ID_STANDARD_ERROR_STREAM = "ID_STANDARD_ERROR_STREAM"; //$NON-NLS-1$

	/**
	 * ID_STANDARD_OUTPUT_STREAM
	 */
	String ID_STANDARD_OUTPUT_STREAM = "ID_STANDARD_OUTPUT_STREAM"; //$NON-NLS-1$

}
