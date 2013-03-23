/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStepFilters;
import org.eclipse.debug.core.model.IValue;

/**
 * @author Max Stepanov
 */
public interface IJSDebugTarget extends IDebugTarget, IStepFilters
{
	/**
	 * getTopScriptElements
	 * 
	 * @return IJSScriptElement[]
	 */
	IJSScriptElement[] getTopScriptElements();

	/**
	 * computeValueDetails
	 * 
	 * @param value
	 * @return String
	 * @throws DebugException
	 */
	String computeValueDetails(IValue value) throws DebugException;

	/**
	 * Returns whether this target supports instance breakpoints.
	 * 
	 * @return whether this target supports instance breakpoints
	 */
	// public boolean supportsInstanceBreakpoints();

	/**
	 * Returns whether constructors are filtered when stepping, if step filters are enabled.
	 * 
	 * @return whether constructors are filtered when stepping
	 */
	boolean isFilterConstructors();

	/**
	 * Sets whether to filter constructors when stepping.
	 * 
	 * @param filter
	 *            whether to filter constructors when stepping
	 */
	void setFilterConstructors(boolean filter);

	/**
	 * Returns the list of active step filters in this target. The list is a collection of Strings. Each string is the
	 * fully qualified name/pattern of a type/package to filter when stepping. For example <code>java.lang.*</code> or
	 * <code>java.lang.String</code>.
	 * 
	 * @return the list of active step filters, or <code>null</code>
	 */
	String[] getStepFilters();

	/**
	 * Sets the list of active step filters in this target. The list is a collection of Strings. Each string is the
	 * fully qualified name/pattern of a type/package to filter when stepping. For example <code>java.lang.*</code> or
	 * <code>java.lang.String</code>.
	 * 
	 * @param list
	 *            active step filters, or <code>null</code>
	 */
	void setStepFilters(String[] list);

	/**
	 * Sets the value of a client defined attribute.
	 * 
	 * @param key
	 *            the attribute key
	 * @param value
	 *            the attribute value
	 */
	void setAttribute(String key, String value);

	/**
	 * Returns the value of a client defined attribute.
	 * 
	 * @param key
	 *            the attribute key
	 * @return value the attribute value, or <code>null</code> if undefined
	 */
	String getAttribute(String key);

	/**
	 * Returns the {@link IJSConnection} attached to this target.
	 * 
	 * @return An {@link IJSConnection}
	 */
	IJSConnection getConnection();
}
