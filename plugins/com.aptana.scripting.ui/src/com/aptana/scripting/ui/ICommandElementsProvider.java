/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui;

import java.util.List;

import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.swt.graphics.Point;

import com.aptana.scripting.model.CommandElement;

/**
 * This interface is used to handle the command elements in the current scope.
 * 
 * @author schitale
 */
public interface ICommandElementsProvider
{
	/**
	 * Return the list of CommandElements in the current scope that are bound to the given key sequence
	 * 
	 * @param keySequence
	 *            to match
	 * @return list of CommandElements
	 */
	List<CommandElement> getCommandElements(KeySequence keySequence);

	/**
	 * Execute the specified CommandElement.
	 * 
	 * @param commandElement
	 */
	void execute(CommandElement commandElement);

	/**
	 * Return the display relative location to show the pop-up menu of Commands.
	 * 
	 * @return a suitable display relative location of pop-up. If <code>null</code> is returned the cursor location will
	 *         be used.
	 */
	Point getCommandElementsPopupLocation();
}
