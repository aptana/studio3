/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.js.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStepFilters;
import org.eclipse.debug.core.model.IValue;

/**
 * @author Max Stepanov
 */
public interface IJSDebugTarget extends IDebugTarget, IStepFilters {
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
	 * Returns whether constructors are filtered when stepping, if step filters
	 * are enabled.
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
	 * Returns the list of active step filters in this target. The list is a
	 * collection of Strings. Each string is the fully qualified name/pattern of
	 * a type/package to filter when stepping. For example
	 * <code>java.lang.*</code> or <code>java.lang.String</code>.
	 * 
	 * @return the list of active step filters, or <code>null</code>
	 */
	String[] getStepFilters();

	/**
	 * Sets the list of active step filters in this target. The list is a
	 * collection of Strings. Each string is the fully qualified name/pattern of
	 * a type/package to filter when stepping. For example
	 * <code>java.lang.*</code> or <code>java.lang.String</code>.
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
}
