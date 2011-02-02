/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing;

import com.aptana.editor.css.parsing.lexer.CSSTokenType;

public class CSSIdentifierTest extends CSSTokensTest
{

	public void testSimpleIdentifier()
	{
		assertToken("abc", CSSTokenType.IDENTIFIER, 0, 3); //$NON-NLS-1$
	}

	public void testIdentifierWithNumber()
	{
		assertToken("abc0", CSSTokenType.IDENTIFIER, 0, 4); //$NON-NLS-1$
	}

	public void testIdentifierWithHyphen()
	{
		assertToken("abc-def", CSSTokenType.IDENTIFIER, 0, 7); //$NON-NLS-1$
	}

	public void testIdentifierWithLeadingHyphen()
	{
		assertToken("-abc", CSSTokenType.IDENTIFIER, 0, 4); //$NON-NLS-1$
	}

	public void testIdentifierWithUnderscore()
	{
		assertToken("abc_def", CSSTokenType.IDENTIFIER, 0, 7); //$NON-NLS-1$
	}

	public void testIdentifierWithLeadingUnderscore()
	{
		assertToken("_abc", CSSTokenType.IDENTIFIER, 0, 4); //$NON-NLS-1$
	}
}
