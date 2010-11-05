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
package com.aptana.portal.ui.dispatch;

/**
 * An action controller interface for contributed controllers that will be invoked when a JavaScript request is coming
 * from the internal browser (Portal). Note that there is no consideration here for the arguments that method can
 * accept. The concrete class should not overload methods, and the methods names should be unique.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public interface IActionController
{
	/**
	 * Returns the available actions (methods) for this controller.
	 * 
	 * @return A list of available actions (methods)
	 */
	public String[] getActions();

	/**
	 * Returns true if this controller has an action with the given name. The action name is translated to a method name
	 * on the concrete class, and in this method will return true if a method exists. Note that there is no
	 * consideration here for the arguments that method can accept. The concrete class should not overload methods, and
	 * the methods names should be unique.
	 * 
	 * @param action
	 * @return True if this controller has the action (method); False, otherwise.
	 */
	public boolean hasAction(String action);

	/**
	 * Invoke an action (method) with the given parameters.
	 * 
	 * @param action
	 *            The action name
	 * @param args
	 *            The action arguments. Can be a null, Map, Object array or primitive array parsed from the JSON
	 * @return The invocation result
	 */
	public Object invokeAction(String action, Object args);

	/**
	 * Assign an ID of an {@link IConfigurationProcessor} that can be used by this action controller when needed.
	 * 
	 * @param id
	 *            The configuration processor ID, as defined in its contributing extension.
	 */
	public void setConfigurationProcessorId(String id);

	/**
	 * Returns the {@link IConfigurationProcessor} that was assigned to this action controller.<br>
	 * This ID can then be used to retrieve an instance of an IConfigurationProcessor from the
	 * {@link ConfigurationProcessorsRegistry} class.
	 * 
	 * @return The ID of the configuration processor, or null if none was ever set.
	 */
	public String getConfigurationProcessorId();
}
