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
