package com.aptana.js.core.parsing.antlr;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.TokenFactory;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Pair;
import org.eclipse.core.internal.utils.StringPool;

import com.aptana.core.util.StringUtil;

/**
 * This token factory will set the text value for certain token types by copying it out of the CharStream. It will use a
 * {@link org.eclipse.core.internal.utils.StringPool} to reduce duplicated strings.
 * 
 * @author cwilliams
 */
public class PoolingTokenFactory implements TokenFactory<CommonToken>
{

	private StringPool _stringPool;

	public PoolingTokenFactory()
	{
		this._stringPool = new StringPool();
	}

	@Override
	public CommonToken create(Pair<TokenSource, CharStream> source, int type, String text, int channel, int start,
			int stop, int line, int charPositionInLine)
	{
		CommonToken t = new CommonToken(source, type, channel, start, stop);
		t.setLine(line);
		t.setCharPositionInLine(charPositionInLine);
		text = copyText(source, type, start, stop);
		t.setText(_stringPool.add(text));
		return t;
	}

	private String copyText(Pair<TokenSource, CharStream> source, int type, int start, int stop)
	{
		switch (type)
		{
			// copy and pool the text for identifiers, strings, numbers, booleans, regexp, multiline comments
			case JSParser.Identifier:
			case JSParser.StringLiteral:
			case JSParser.DecimalLiteral:
			case JSParser.OctalIntegerLiteral:
			case JSParser.BinaryIntegerLiteral:
			case JSParser.HexIntegerLiteral:
			case JSParser.MultiLineComment:
			case JSParser.BooleanLiteral:
			case JSParser.RegularExpressionLiteral:
				return source.b.getText(Interval.of(start, stop));
			// Stuff empty string into single line comments
			case JSParser.SingleLineComment:
				return StringUtil.EMPTY;
			// Push single space for whitespaces
			case JSParser.WhiteSpaces:
				return " "; //$NON-NLS-1$
			default:
				return null;
		}
	}

	@Override
	public CommonToken create(int type, String text)
	{
		return new CommonToken(type, text);
	}
}
