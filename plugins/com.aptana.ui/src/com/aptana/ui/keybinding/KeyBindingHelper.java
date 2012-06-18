/**
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license.txt included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.keybinding;

import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.SWTKeySupport;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.keys.BindingService;
import org.eclipse.ui.keys.IBindingService;

/**
 * Helper for knowing about keybindings and related actions
 * 
 * @author Fabio Zadrozny
 */
@SuppressWarnings("restriction")
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
		if (upperCase == keyCode)
		{
			return internalMatchesKeybinding(keyCode, stateMask, seq);
		}
		else
		{
			// try both: upper and lower case.
			return internalMatchesKeybinding(upperCase, stateMask, seq)
					|| internalMatchesKeybinding(keyCode, stateMask, seq);
		}
	}

	private static boolean internalMatchesKeybinding(int keyCode, int stateMask, TriggerSequence seq)
	{
		KeySequence keySequence = (KeySequence) seq;
		KeyStroke[] keyStrokes = keySequence.getKeyStrokes();

		if (keyStrokes.length > 1)
		{
			return false; // Only handling one step binding... the code below does not support things as "CTRL+X R" for
							// redo.
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

	public static boolean isKeyEventComplete(Event event)
	{
		// Is this a complete KeyStroke
		return SWTKeySupport.convertAcceleratorToKeyStroke(SWTKeySupport.convertEventToUnmodifiedAccelerator(event))
				.isComplete();
	}

	public static void handleEvent(Event e)
	{
		IBindingService bindingService = (IBindingService) PlatformUI.getWorkbench().getAdapter(IBindingService.class);

		Listener keyDownFilter = ((BindingService) bindingService).getKeyboard().getKeyDownFilter();
		boolean enabled = bindingService.isKeyFilterEnabled();
		Control focusControl = e.display.getFocusControl();
		try
		{
			bindingService.setKeyFilterEnabled(true);
			keyDownFilter.handleEvent(e);
		}
		finally
		{
			if (focusControl == e.display.getFocusControl()) // $codepro.audit.disable useEquals
			{
				bindingService.setKeyFilterEnabled(enabled);
			}
		}
	}

}
