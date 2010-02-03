package com.aptana.editor.html.parsing;

import org.eclipse.jface.text.rules.IToken;

import beaver.Symbol;

import com.aptana.editor.common.parsing.CompositeParserScanner;
import com.aptana.editor.common.parsing.IScannerSwitchStrategy;
import com.aptana.editor.common.parsing.ScannerSwitchStrategy;
import com.aptana.editor.html.parsing.lexer.HTMLTokens;

public class HTMLParserScanner extends CompositeParserScanner
{

	private static final String[] CSS_ENTER_TOKENS = new String[] { HTMLTokens.getTokenName(HTMLTokens.STYLE) };
	private static final String[] CSS_EXIT_SEQUENCES = new String[] { "</style>" }; //$NON-NLS-1$
	private static final String[] JS_ENTER_TOKENS = new String[] { HTMLTokens.getTokenName(HTMLTokens.SCRIPT) };
	private static final String[] JS_EXIT_SEQUENCES = new String[] { "</script>" }; //$NON-NLS-1$

	private static final short[] TOKEN_TYPES = new short[] { HTMLTokens.STYLE, HTMLTokens.SCRIPT };

	public HTMLParserScanner()
	{
		super(new HTMLTokenScanner(), new IScannerSwitchStrategy[] {
				new ScannerSwitchStrategy(CSS_ENTER_TOKENS, CSS_EXIT_SEQUENCES),
				new ScannerSwitchStrategy(JS_ENTER_TOKENS, JS_EXIT_SEQUENCES) });
	}

	@Override
	protected Symbol createSymbol(int start, int end, String text, IToken token)
	{
		short type = HTMLTokens.EOF;
		Object data = token.getData();
		if (data != null)
		{
			int index = getCurrentStrategyIndex();
			if (index == DEFAULT_INDEX)
			{
				type = HTMLTokens.getToken(data.toString());
			}
			else
			{
				type = TOKEN_TYPES[index];
			}
		}
		if (type == HTMLTokens.START_TAG && text.endsWith("/>")) //$NON-NLS-1$
		{
			// self closing
			type = HTMLTokens.SELF_CLOSING;
		}
		return new Symbol(type, start, end, text);
	}

	@Override
	protected boolean isIgnored(IToken token)
	{
		if (super.isIgnored(token))
		{
			return true;
		}
		Object data = token.getData();
		if (data == null)
		{
			return false;
		}
		// ignores comments and doctype declaration
		return data.equals(HTMLTokens.getTokenName(HTMLTokens.COMMENT))
				|| data.equals(HTMLTokens.getTokenName(HTMLTokens.DOCTYPE));
	}
}
