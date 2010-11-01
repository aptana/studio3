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
package com.aptana.configurations.processor;

import java.util.Set;

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
