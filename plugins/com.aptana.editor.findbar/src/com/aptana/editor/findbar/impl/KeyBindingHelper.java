/**
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license.txt included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.findbar.impl;

import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;

/**
 * Helper for knowing about keybindings and related actions
 * 
 * @author Fabio Zadrozny
 */
public class KeyBindingHelper
{

	/**
	 * @param event
	 *            the key event to be checked
	 * @param commandId
	 *            the command to be checked
	 * @return true if the given key event can trigger the passed command (and false otherwise).
	 */
	public static boolean matchesKeybinding(KeyEvent event, String commandId)
	{
		int keyCode = event.keyCode;
		int stateMask = event.stateMask;

		return matchesKeybinding(keyCode, stateMask, commandId);
	}

	public static boolean matchesKeybinding(int keyCode, int stateMask, String commandId)
	{
		final IBindingService bindingSvc = (IBindingService) PlatformUI.getWorkbench()
				.getAdapter(IBindingService.class);
		TriggerSequence[] activeBindingsFor = bindingSvc.getActiveBindingsFor(commandId);

		for (TriggerSequence seq : activeBindingsFor)
		{
			if (seq instanceof KeySequence)
			{
				if (matchesKeybinding(keyCode, stateMask, seq))
				{
					return true;
				}
			}
		}

		return false;
	}

	
	public static boolean matchesKeybinding(int keyCode, int stateMask, TriggerSequence seq)
	{
		int upperCase = Character.toUpperCase(keyCode);
		if(upperCase == keyCode){
			return internalMatchesKeybinding(keyCode, stateMask, seq);
		}else{
			//try both: upper and lower case.
			return internalMatchesKeybinding(upperCase, stateMask, seq) || internalMatchesKeybinding(keyCode, stateMask, seq);
		}
	}

	private static boolean internalMatchesKeybinding(int keyCode, int stateMask, TriggerSequence seq)
	{
		KeySequence keySequence = (KeySequence) seq;
		KeyStroke[] keyStrokes = keySequence.getKeyStrokes();

		if(keyStrokes.length > 1)
		{
			return false; //Only handling one step binding... the code below does not support things as "CTRL+X R" for redo.
		}
		for (KeyStroke keyStroke : keyStrokes)
		{
			if (keyStroke.getNaturalKey() == keyCode && keyStroke.getModifierKeys() == stateMask)
			{

				return true;
			}
		}
		return false;
	}

}
