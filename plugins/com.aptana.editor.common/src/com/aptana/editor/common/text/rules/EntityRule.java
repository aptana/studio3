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
