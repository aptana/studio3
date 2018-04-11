/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.bindings.keys.IKeyLookup;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ArrayUtil;
import com.aptana.scripting.ScriptLogger;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.Messages;

public class KeyBindingUtil
{
	private static final KeySequence[] NO_BINDINGS = new KeySequence[0];

	private static final Pattern CONTROL_PLUS = Pattern.compile(
			"control" + Pattern.quote(KeyStroke.KEY_DELIMITER), Pattern.CASE_INSENSITIVE); //$NON-NLS-1$
	private static final String CTRL_PLUS = Matcher.quoteReplacement(IKeyLookup.CTRL_NAME + KeyStroke.KEY_DELIMITER);
	private static final Pattern OPTION_PLUS = Pattern.compile(
			"option" + Pattern.quote(KeyStroke.KEY_DELIMITER), Pattern.CASE_INSENSITIVE); //$NON-NLS-1$
	private static final String ALT_PLUS = Matcher.quoteReplacement(IKeyLookup.ALT_NAME + KeyStroke.KEY_DELIMITER);

	/**
	 * getKeySequence
	 * 
	 * @return
	 */
	public static KeySequence[] getKeySequences(CommandElement command)
	{
		String[] bindings = command.getKeyBindings();
		if (ArrayUtil.isEmpty(bindings))
		{
			return NO_BINDINGS;
		}

		List<KeySequence> result = new ArrayList<KeySequence>(bindings.length);
		for (String binding : bindings)
		{
			try
			{
				// Need to convert the format
				String normalizedKeyBinding = normalizeKeyBinding(binding);
				KeySequence sequence = KeySequence.getInstance(normalizedKeyBinding);

				result.add(sequence);
			}
			catch (ParseException e)
			{
				String message = MessageFormat.format(Messages.CommandElement_Invalid_Key_Binding, new Object[] {
						binding, command.getDisplayName(), command.getPath(), e.getMessage() });
				// Log to scripting console
				ScriptLogger.logError(message);
				IdeLog.logError(ScriptingUIPlugin.getDefault(), message);
			}
		}

		return result.toArray(new KeySequence[result.size()]);
	}

	/**
	 * Normalize the keyBinding string.
	 * <p>
	 * Convert control+ to CTRL+ Convert option+ to ALT+
	 * 
	 * @param keyBinding
	 * @return
	 */
	private static String normalizeKeyBinding(String keyBinding)
	{
		String result = null;

		if (keyBinding != null)
		{
			result = CONTROL_PLUS.matcher(keyBinding).replaceAll(CTRL_PLUS); // Convert control+ to CTRL+
			result = OPTION_PLUS.matcher(result).replaceAll(ALT_PLUS); // Convert option+ to ALT+
		}

		return result;
	}
}
