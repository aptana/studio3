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
package com.aptana.scripting.keybindings.internal;

import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * This is based on <code>org.eclipse.ui.internal.keys.KeyBindingState</code>.
 *
 * @author schitale
 *
 */
class KeyBindingState
{

	/**
	 * The workbench window associated with this state. The state can only exist for one window. When the focus leaves
	 * this window then the state must automatically be reset.
	 */
	private IWorkbenchWindow associatedWindow;

	/**
	 * This is the current extent of the sequence entered by the user. If there are multi-stroke key bindings, this is the sequence
	 * entered by the user that partially matches another key bindings.
	 */
	private KeySequence currentSequence;

	/**
	 * The workbench that should be notified of changes to the key binding state. This is done by updating one of the
	 * contribution items on the status line.
	 */
	private final IWorkbench workbench;

	/**
	 * Constructs a new instance of <code>KeyBindingState</code> with an empty key sequence, set to reset fully.
	 *
	 * @param workbenchToNotify
	 *            The workbench that this state should keep advised of changes to the key binding state; must not be
	 *            <code>null</code>.
	 */
	KeyBindingState(IWorkbench workbenchToNotify)
	{
		currentSequence = KeySequence.getInstance();
		workbench = workbenchToNotify;
		associatedWindow = workbench.getActiveWorkbenchWindow();
	}

	/**
	 * An accessor for the workbench window associated with this state. This should never be <code>null</code>, as the
	 * setting follows the last workbench window to have focus.
	 *
	 * @return The workbench window to which the key binding architecture is currently attached; should never be
	 *         <code>null</code>.
	 */
	IWorkbenchWindow getAssociatedWindow()
	{
		return associatedWindow;
	}

	/**
	 * An accessor for the current key sequence waiting for completion.
	 *
	 * @return The current incomplete key sequence; never <code>null</code>, but may be empty.
	 */
	KeySequence getCurrentSequence()
	{
		return currentSequence;
	}

	/**
	 * <p>
	 * Resets the state based on the current properties. If the state is to collapse fully or if there are no key
	 * strokes, then it sets the state to have an empty key sequence. Otherwise, it leaves the first key stroke in the
	 * sequence.
	 * </p>
	 * <p>
	 * The workbench's status lines are updated, if appropriate.
	 * </p>
	 */
	void reset()
	{
		currentSequence = KeySequence.getInstance();
	}

	/**
	 * A mutator for the workbench window to which this state is associated.
	 *
	 * @param window
	 *            The workbench window to associated; should never be <code>null</code>.
	 */
	void setAssociatedWindow(IWorkbenchWindow window)
	{
		associatedWindow = window;
	}

	/**
	 * A mutator for the partial sequence entered by the user.
	 *
	 * @param sequence
	 *            The current key sequence; should not be <code>null</code>, but may be empty.
	 */
	void setCurrentSequence(KeySequence sequence)
	{
		currentSequence = sequence;
	}

}
