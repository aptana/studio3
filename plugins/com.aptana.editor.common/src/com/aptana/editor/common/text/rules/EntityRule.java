/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.rules;

import java.util.regex.Pattern;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

/**
 * Specialized rule to match HTML/XML entities.
 * 
 * @author cwilliams
 */
public class EntityRule extends SingleLineRule
{
	private static final Pattern ENTITY_PATTERN = Pattern.compile("^&([a-zA-Z0-9]+|#[0-9]+|#x[0-9a-fA-F]+);\\z"); //$NON-NLS-1$

	public EntityRule(IToken token)
	{
		super("&", ";", token); //$NON-NLS-1$//$NON-NLS-2$
	}

	protected IToken doEvaluate(ICharacterScanner scanner, boolean resume)
	{
		int column = scanner.getColumn();
		IToken token = super.doEvaluate(scanner, resume);
		if (token.isUndefined())
			return token;

		// Make sure whole thing matches pattern
		int read = scanner.getColumn() - column;
		for (int i = 0; i < read; i++)
		{
			scanner.unread();
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < read; i++)
		{
			builder.append((char) scanner.read());
		}
		String word = builder.toString();
		if (word.length() > 2 && ENTITY_PATTERN.matcher(word).find())
		{
			return token;
		}
		for (int i = 0; i < read; i++)
		{
			scanner.unread();
		}
		return Token.UNDEFINED;
	}
}
