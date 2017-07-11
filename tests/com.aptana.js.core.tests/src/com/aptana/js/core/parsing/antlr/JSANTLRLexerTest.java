package com.aptana.js.core.parsing.antlr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.junit.Test;

import com.aptana.js.core.JSLanguageConstants;
import com.aptana.js.core.parsing.JSLexerTest;
import com.aptana.js.core.parsing.JSTokenType;
import com.aptana.js.core.tests.ITestFiles;

import beaver.Symbol;

public class JSANTLRLexerTest extends JSLexerTest
{
	@Override
	protected IJSScanner createScanner()
	{
		return new IJSScanner()
		{
			private JSLexer lexer;
			private List<Symbol> singleLineComments;
			private List<Symbol> multiLineComments;
			private List<Symbol> vsdocComments;
			private List<Symbol> sdocComments;

			public void setSource(String source)
			{
				CharStream input = CharStreams.fromString(source);
				lexer = new JSLexer(input);
				singleLineComments = new ArrayList<Symbol>();
				multiLineComments = new ArrayList<Symbol>();
				vsdocComments = new ArrayList<Symbol>();
				sdocComments = new ArrayList<Symbol>();
			}

			public Symbol nextToken() throws Exception
			{
				Token t = lexer.nextToken();
				// if (t.getChannel() == JSLexer.HIDDEN)
				// {
				// // skip!
				// return nextToken();
				// }
				JSTokenType tt;

				switch (t.getType())
				{
					case JSParser.LineTerminator:
					case JSParser.WhiteSpaces:
						// skip newlines and whitespace
						return nextToken();
					case Token.EOF:
						tt = JSTokenType.EOF;
						break;
					case JSLexer.Identifier:
						// If is get or set, treat differently
						if (JSLanguageConstants.GET.equals(t.getText()))
						{
							tt = JSTokenType.GET;
						}
						else if (JSLanguageConstants.SET.equals(t.getText()))
						{
							tt = JSTokenType.SET;
						}
						else
						{
							tt = JSTokenType.IDENTIFIER;
						}
						break;
					case JSLexer.StringLiteral:
						tt = JSTokenType.STRING;
						break;
					case JSLexer.NoSubstitutionTemplate:
						tt = JSTokenType.NO_SUB_TEMPLATE;
						break;
					case JSLexer.RegularExpressionLiteral:
						tt = JSTokenType.REGEX;
						break;
					case JSLexer.MultiLineComment:
						tt = JSTokenType.MULTILINE_COMMENT;
						break;
					case JSLexer.SingleLineComment:
						tt = JSTokenType.SINGLELINE_COMMENT;
						break;
					case JSLexer.BinaryIntegerLiteral:
					case JSLexer.DecimalLiteral:
					case JSLexer.OctalIntegerLiteral:
					case JSLexer.HexIntegerLiteral:
						tt = JSTokenType.NUMBER;
						break;
					case JSLexer.Comma:
						tt = JSTokenType.COMMA;
						break;
					default:
						tt = JSTokenType.get(t.getText());
						break;
				}

				Symbol s = new Symbol(tt.getIndex(), t.getStartIndex(), t.getStopIndex(), t.getText());
				switch (tt)
				{
					case SINGLELINE_COMMENT:
						// Sniff what type it really is? If /// count as "VSdoc"
						if (((String) s.value).startsWith("///"))
						{
							vsdocComments.add(s);
						}
						else
						{
							singleLineComments.add(s);
						}
						break;
					case MULTILINE_COMMENT:
						// Sniff what type it really is? If /** count as "Sdoc"
						if (((String) s.value).startsWith("/**"))
						{
							sdocComments.add(s);
						}
						else
						{
							multiLineComments.add(s);
						}
						break;
					default:
						break;
				}
				return s;
			}

			public List<Symbol> getVSDocComments()
			{
				return vsdocComments;
			}

			public List<Symbol> getSingleLineComments()
			{
				return singleLineComments;
			}

			public List<Symbol> getSDocComments()
			{
				return sdocComments;
			}

			public List<Symbol> getMultiLineComments()
			{
				return multiLineComments;
			}
		};
	}

	@Test
	public void testTiMobile2() throws IOException
	{
		String src = getSource(ITestFiles.TIMOBILE_FILES[0]);
		CharStream input = CharStreams.fromString(src);
		JSLexer lexer = new JSLexer(input);
		List<? extends Token> tokens = lexer.getAllTokens();

		assertTokenTypes(getSource(ITestFiles.TIMOBILE_FILES[0]), JSTokenType.MULTILINE_COMMENT, JSTokenType.FUNCTION,
				JSTokenType.IDENTIFIER, JSTokenType.LPAREN, JSTokenType.RPAREN, JSTokenType.LCURLY,
				JSTokenType.MULTILINE_COMMENT, JSTokenType.THIS, JSTokenType.DOT, JSTokenType.IDENTIFIER,
				JSTokenType.EQUAL, JSTokenType.LCURLY, JSTokenType.RCURLY);
	}

}
