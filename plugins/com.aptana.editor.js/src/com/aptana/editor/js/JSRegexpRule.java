package com.aptana.editor.js;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.common.text.rules.RegexpRule;

public class JSRegexpRule extends RegexpRule
{

	public JSRegexpRule(IToken successToken)
	{
		super("/([^/]|\\\\/)*?([^/\\\\]+|\\\\\\\\|\\\\/)/[igm]*", successToken, true); //$NON-NLS-1$
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume)
	{
		if (scanner instanceof IJSTokenScanner)
		{
			if (((IJSTokenScanner) scanner).hasDivisionStart())
			{
				return Token.UNDEFINED;
			}
		}
		return super.evaluate(scanner, resume);
	}
}
