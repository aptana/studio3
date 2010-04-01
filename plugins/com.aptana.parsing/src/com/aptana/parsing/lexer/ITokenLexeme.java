package com.aptana.parsing.lexer;

import org.eclipse.jface.text.rules.IToken;

public interface ITokenLexeme extends ILexeme
{
	IToken getToken();
}
