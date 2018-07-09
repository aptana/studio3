/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing;

import java.util.List;

import beaver.Symbol;

public class JSFlexScannerTest extends JSLexerTest
{

	@Override
	protected IJSScanner createScanner()
	{
		final JSFlexScanner scanner = new JSFlexScanner();
		scanner.setCollectComments(true);
		return new IJSScanner()
		{

			public void setSource(String source)
			{
				scanner.setSource(source);
			}

			public Symbol nextToken() throws Exception
			{
				return scanner.nextToken();
			}

			public List<Symbol> getVSDocComments()
			{
				return scanner.getVSDocComments();
			}

			public List<Symbol> getSingleLineComments()
			{
				return scanner.getSingleLineComments();
			}

			public List<Symbol> getSDocComments()
			{
				return scanner.getSDocComments();
			}

			public List<Symbol> getMultiLineComments()
			{
				return scanner.getMultiLineComments();
			}
		};
	}
}