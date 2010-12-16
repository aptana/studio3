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
package com.aptana.js.debug.core.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.ILineBreakpoint;

/**
 * @author Max Stepanov
 */
public interface IJSLineBreakpoint extends ILineBreakpoint {
	
	/**
	 * Returns if this breakpoint is run-to-line temporary breakpoint
	 * 
	 * @return boolean
	 * @throws CoreException
	 */
	boolean isRunToLine() throws CoreException;

	/**
	 * Returns this breakpoint's hit count or, -1 if this breakpoint does not
	 * have a hit count.
	 * 
	 * @return this breakpoint's hit count, or -1
	 * @exception CoreException
	 *                if unable to access the property from this breakpoint's
	 *                underlying marker
	 */
	int getHitCount() throws CoreException;

	/**
	 * Sets the hit count attribute of this breakpoint. If this breakpoint is
	 * currently disabled and the hit count is set greater than -1, this
	 * breakpoint is automatically enabled.
	 * 
	 * @param count
	 *            the new hit count
	 * @exception CoreException
	 *                if unable to set the property on this breakpoint's
	 *                underlying marker
	 */
	void setHitCount(int count) throws CoreException;

	/**
	 * Returns the conditional expression associated with this breakpoint, or
	 * <code>null</code> if this breakpoint does not have a condition.
	 * 
	 * @return this breakpoint's conditional expression, or <code>null</code>
	 * @exception CoreException
	 *                if unable to access the property on this breakpoint's
	 *                underlying marker
	 */
	String getCondition() throws CoreException;

	/**
	 * Sets the condition associated with this breakpoint. When the condition is
	 * enabled, this breakpoint will only suspend execution when the given
	 * condition evaluates to <code>true</code>. Setting the condition to
	 * <code>null</code> or an empty string removes the condition.
	 * <p>
	 * If this breakpoint does not support conditions, setting the condition has
	 * no effect.
	 * </p>
	 * 
	 * @param condition
	 *            conditional expression
	 * @exception CoreException
	 *                if unable to set the property on this breakpoint's
	 *                underlying marker
	 */
	void setCondition(String condition) throws CoreException;

	/**
	 * Returns whether the condition on this breakpoint is enabled.
	 * 
	 * @return whether this breakpoint's condition is enabled
	 * @exception CoreException
	 *                if unable to access the property on this breakpoint's
	 *                underlying marker
	 */
	boolean isConditionEnabled() throws CoreException;

	/**
	 * Sets the enabled state of this breakpoint's condition to the given state.
	 * When enabled, this breakpoint will only suspend when its condition
	 * evaluates to true. When disabled, this breakpoint will suspend as it
	 * would with no condition defined.
	 * 
	 * @param enabled
	 * @exception CoreException
	 *                if unable to set the property on this breakpoint's
	 *                underlying marker
	 */
	void setConditionEnabled(boolean enabled) throws CoreException;

	/**
	 * Returns whether the breakpoint suspends when the value of the condition
	 * is <code>true</code> or when the value of the condition changes.
	 * 
	 * @return <code>true</code> if this breakpoint suspends when the value of
	 *         the condition is <code>true</code>, <code>false</code> if this
	 *         breakpoint suspends when the value of the condition changes.
	 * @exception CoreException
	 *                if unable to access the property on this breakpoint's
	 *                underlying marker
	 */
	boolean isConditionSuspendOnTrue() throws CoreException;

	/**
	 * Set the suspend state of this breakpoint's condition. If the value is
	 * <code>true</code>, the breakpoint will stop when the value of the
	 * condition is <code>true</code>. If the value is <code>false</code>, the
	 * breakpoint will stop when the value of the condition changes.
	 * 
	 * @param suspendOnTrue
	 * @exception CoreException
	 *                if unable to access the property on this breakpoint's
	 *                underlying marker
	 */
	void setConditionSuspendOnTrue(boolean suspendOnTrue) throws CoreException;

}
