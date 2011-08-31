/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.configurations.processor;

import java.util.Set;

import org.eclipse.core.expressions.Expression;
import org.eclipse.core.runtime.IPath;

/**
 * A base interface for configuration processor delegates.<br>
 * These delegates usually support a specific set of commands, and this class can be queried about those commands, along
 * with some basic functionalities such as installation capabilities, version reading etc.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public interface IConfigurationProcessorDelegate
{
	/**
	 * Version retrieving command type
	 */
	public static final String VERSION_COMMAND = "version"; //$NON-NLS-1$
	/**
	 * Install command type
	 */
	public static final String INSTALL_COMMAND = "install"; //$NON-NLS-1$

	/**
	 * Uninstall command type
	 */
	public static final String UNINSTALL_COMMAND = "uninstall"; //$NON-NLS-1$

	/**
	 * Returns <code>true</code> if this delegate is enabled; <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if this delegate is enabled; <code>false</code> otherwise.
	 */
	public boolean isEnabled();

	/**
	 * Set the delegate's enablement {@link Expression}.
	 * 
	 * @param enablementExpression
	 *            An {@link Expression} that will be evaluated to determine the enablement of this delegate. May be
	 *            <code>null</code> to indicate that the delegate is always enabled.
	 */
	public void setEnablement(Expression enablementExpression);

	/**
	 * Returns a lower-case name of the application that this delegate supports.
	 * 
	 * @return
	 */
	public String getSupportedApplication();

	/**
	 * Returns the commands that this delegate support.
	 * 
	 * @return A supported commands set.
	 */
	public Set<String> getSupportedCommands();

	/**
	 * Runs a command.
	 * 
	 * @param commandType
	 *            One of the commands that was returned in the {@link #getSupportedCommands()} as available for this
	 *            delegate.
	 * @param workingDir
	 *            The work directory to run the command from (can be null)
	 * @return An object representing the result of this command (can be null).
	 */
	public Object runCommand(String commandType, IPath workingDir);
}
