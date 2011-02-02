/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing;

import com.aptana.editor.common.parsing.CompositeTokenScanner;
import com.aptana.editor.common.parsing.IScannerSwitchStrategy;
import com.aptana.editor.common.parsing.ScannerSwitchStrategy;
import com.aptana.editor.html.parsing.lexer.HTMLTokens;

public class HTMLScanner extends CompositeTokenScanner
{

	private static final String[] CSS_ENTER_TOKENS = new String[] { HTMLTokens.getTokenName(HTMLTokens.STYLE) };
	private static final String[] CSS_EXIT_TOKENS = new String[] { HTMLTokens.getTokenName(HTMLTokens.STYLE_END) };
	private static final String[] JS_ENTER_TOKENS = new String[] { HTMLTokens.getTokenName(HTMLTokens.SCRIPT) };
	private static final String[] JS_EXIT_TOKENS = new String[] { HTMLTokens.getTokenName(HTMLTokens.SCRIPT_END) };

	private static final IScannerSwitchStrategy CSS_STRATEGY = new ScannerSwitchStrategy(CSS_ENTER_TOKENS,
			CSS_EXIT_TOKENS);
	private static final IScannerSwitchStrategy JS_STRATEGY = new ScannerSwitchStrategy(JS_ENTER_TOKENS, JS_EXIT_TOKENS);

	public HTMLScanner()
	{
		this(new HTMLTokenScanner(), new IScannerSwitchStrategy[] { CSS_STRATEGY, JS_STRATEGY });
	}

	protected HTMLScanner(HTMLTokenScanner tokenScanner, IScannerSwitchStrategy[] switchStrategies)
	{
		super(tokenScanner, switchStrategies);
	}

	public short getTokenType(Object data)
	{
		IScannerSwitchStrategy strategy = getCurrentSwitchStrategy();
		if (strategy == null)
		{
			// the primary token scanner is being used
			return HTMLTokens.getToken(data.toString());
		}
		if (strategy == CSS_STRATEGY)
		{
			return HTMLTokens.STYLE;
		}
		if (strategy == JS_STRATEGY)
		{
			return HTMLTokens.SCRIPT;
		}
		return HTMLTokens.UNKNOWN;
	}
}
