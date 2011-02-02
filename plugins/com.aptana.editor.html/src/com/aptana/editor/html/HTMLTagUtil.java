/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

import com.aptana.editor.html.parsing.lexer.HTMLTokenType;
import com.aptana.parsing.lexer.Lexeme;

public class HTMLTagUtil
{
	/**
	 * Is the current Lexeme a HTML tag
	 * 
	 * @param lexeme
	 * @return
	 */
	public static boolean isTag(Lexeme<HTMLTokenType> lexeme)
	{
		if (lexeme != null)
		{
			HTMLTokenType type = lexeme.getType();
			return HTMLTokenType.STRUCTURE_TAG.equals(type) || HTMLTokenType.BLOCK_TAG.equals(type)
					|| HTMLTokenType.INLINE_TAG.equals(type);
		}
		else
		{
			return false;
		}
	}
}
