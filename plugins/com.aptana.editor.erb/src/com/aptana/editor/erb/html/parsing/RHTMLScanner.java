package com.aptana.editor.erb.html.parsing;

import com.aptana.editor.common.parsing.IScannerSwitchStrategy;
import com.aptana.editor.common.parsing.ScannerSwitchStrategy;
import com.aptana.editor.erb.parsing.lexer.ERBTokens;
import com.aptana.editor.html.parsing.HTMLScanner;

public class RHTMLScanner extends HTMLScanner
{

	private static final String[] RUBY_ENTER_TOKENS = new String[] { ERBTokens.getTokenName(ERBTokens.RUBY) };
	private static final String[] RUBY_EXIT_TOKENS = new String[] { ERBTokens.getTokenName(ERBTokens.RUBY_END) };

	private static final IScannerSwitchStrategy RUBY_STRATEGY = new ScannerSwitchStrategy(RUBY_ENTER_TOKENS,
			RUBY_EXIT_TOKENS);

	private boolean isInRuby;

	public RHTMLScanner()
	{
		super(new RHTMLTokenScanner(), new IScannerSwitchStrategy[] { RUBY_STRATEGY });
	}

	public short getTokenType(Object data)
	{
		IScannerSwitchStrategy strategy = getCurrentSwitchStrategy();
		if (strategy == RUBY_STRATEGY)
		{
			if (!isInRuby)
			{
				isInRuby = true;
			}
			return ERBTokens.RUBY;
		}
		if (strategy == null && isInRuby)
		{
			isInRuby = false;
			return ERBTokens.RUBY_END;
		}
		return super.getTokenType(data);
	}
}
