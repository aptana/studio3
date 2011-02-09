/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
 * with certain other free and open source software ("FOSS") code and certain additional terms
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
 */
package com.aptana.js.debug.core;

/**
 * JS Debug model constants
 */
public interface IJSDebugConstants {

	/**
	 * Unique identifier for the JS debug model (value
	 * <code>com.aptana.debug.core</code>).
	 */
	String ID_DEBUG_MODEL = JSDebugPlugin.PLUGIN_ID;

	/**
	 * Unique identifier for the JS line breakpoint markers (value
	 * <code>com.aptana.debug.core.lineBreakpointMarker</code>).
	 */
	String ID_LINE_BREAKPOINT_MARKER = ID_DEBUG_MODEL + ".lineBreakpointMarker"; //$NON-NLS-1$

	/**
	 * Unique identifier for the JS exception breakpoint markers (value
	 * <code>com.aptana.debug.core.exceptionBreakpointMarker</code>).
	 */
	String ID_EXCEPTION_BREAKPOINT_MARKER = ID_DEBUG_MODEL + ".exceptionBreakpointMarker"; //$NON-NLS-1$

	/**
	 * Unique identifier for the JS watchpoint markers (value
	 * <code>com.aptana.debug.core.watchpointMarker</code>).
	 */
	String ID_WATCHPOINT_MARKER = ID_DEBUG_MODEL + ".watchpointMarker"; //$NON-NLS-1$

	/**
	 * Unique identifier for the JS breakpoints location (value
	 * <code>com.aptana.debug.core.breakpointLocation</code>).
	 */
	String BREAKPOINT_LOCATION = ID_DEBUG_MODEL + ".breakpointLocation"; //$NON-NLS-1$

	/**
	 * Unique identifier for the JS run to line breakpoints (value
	 * <code>com.aptana.debug.core.runToLineBreakpoint</code>).
	 */
	String RUN_TO_LINE = ID_DEBUG_MODEL + ".runToLineBreakpoint"; //$NON-NLS-1$

	/**
	 * Unique identifier for the JS breakpoints hit count (value
	 * <code>com.aptana.debug.core.breakpointHitCount</code>).
	 */
	String BREAKPOINT_HIT_COUNT = ID_DEBUG_MODEL + ".breakpointHitCount"; //$NON-NLS-1$

	/**
	 * Unique identifier for the JS breakpoints condition (value
	 * <code>com.aptana.debug.core.breakpointCondition</code>).
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
	 * <code>com.aptana.debug.core.watchpointVariableAccessor</code>).
	 * Internal use only.
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

}
