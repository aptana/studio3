/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
	 * Returns this breakpoint's hit count or, -1 if this breakpoint does not have a hit count.
	 * 
	 * @return this breakpoint's hit count, or -1
	 * @exception CoreException
	 *                if unable to access the property from this breakpoint's underlying marker
	 */
	int getHitCount() throws CoreException;

	/**
	 * Sets the hit count attribute of this breakpoint. If this breakpoint is currently disabled and the hit count is
	 * set greater than -1, this breakpoint is automatically enabled.
	 * 
	 * @param count
	 *            the new hit count
	 * @exception CoreException
	 *                if unable to set the property on this breakpoint's underlying marker
	 */
	void setHitCount(int count) throws CoreException;

	/**
	 * Returns the conditional expression associated with this breakpoint, or <code>null</code> if this breakpoint does
	 * not have a condition.
	 * 
	 * @return this breakpoint's conditional expression, or <code>null</code>
	 * @exception CoreException
	 *                if unable to access the property on this breakpoint's underlying marker
	 */
	String getCondition() throws CoreException;

	/**
	 * Sets the condition associated with this breakpoint. When the condition is enabled, this breakpoint will only
	 * suspend execution when the given condition evaluates to <code>true</code>. Setting the condition to
	 * <code>null</code> or an empty string removes the condition.
	 * <p>
	 * If this breakpoint does not support conditions, setting the condition has no effect.
	 * </p>
	 * 
	 * @param condition
	 *            conditional expression
	 * @exception CoreException
	 *                if unable to set the property on this breakpoint's underlying marker
	 */
	void setCondition(String condition) throws CoreException;

	/**
	 * Returns whether the condition on this breakpoint is enabled.
	 * 
	 * @return whether this breakpoint's condition is enabled
	 * @exception CoreException
	 *                if unable to access the property on this breakpoint's underlying marker
	 */
	boolean isConditionEnabled() throws CoreException;

	/**
	 * Sets the enabled state of this breakpoint's condition to the given state. When enabled, this breakpoint will only
	 * suspend when its condition evaluates to true. When disabled, this breakpoint will suspend as it would with no
	 * condition defined.
	 * 
	 * @param enabled
	 * @exception CoreException
	 *                if unable to set the property on this breakpoint's underlying marker
	 */
	void setConditionEnabled(boolean enabled) throws CoreException;

	/**
	 * Returns whether the breakpoint suspends when the value of the condition is <code>true</code> or when the value of
	 * the condition changes.
	 * 
	 * @return <code>true</code> if this breakpoint suspends when the value of the condition is <code>true</code>,
	 *         <code>false</code> if this breakpoint suspends when the value of the condition changes.
	 * @exception CoreException
	 *                if unable to access the property on this breakpoint's underlying marker
	 */
	boolean isConditionSuspendOnTrue() throws CoreException;

	/**
	 * Set the suspend state of this breakpoint's condition. If the value is <code>true</code>, the breakpoint will stop
	 * when the value of the condition is <code>true</code>. If the value is <code>false</code>, the breakpoint will
	 * stop when the value of the condition changes.
	 * 
	 * @param suspendOnTrue
	 * @exception CoreException
	 *                if unable to access the property on this breakpoint's underlying marker
	 */
	void setConditionSuspendOnTrue(boolean suspendOnTrue) throws CoreException;

}
