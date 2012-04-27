// $codepro.audit.disable
/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.parsing;

import java.io.Reader;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.List;

import beaver.Symbol;
import beaver.Scanner;

import com.aptana.editor.js.parsing.lexer.JSTokenType;

%%

%public
%class JSFlexScanner
%extends Scanner
%type Symbol
%yylexthrow Scanner.Exception
%eofval{
	return newToken(Terminals.EOF, "end-of-file");
%eofval}
%unicode
%char

//%switch
//%table
//%pack

%{
	// last token used for look behind. Also needed when implementing the ITokenScanner interface
	private Symbol _lastToken;

	// flag indicating if we should collect comments or not
	private boolean _collectComments = true;

	// accumulator of consecutive vsdoc lines, later added to vsdocComments as a single entity
	private List<Symbol> _vsdocAccumulator = new ArrayList<Symbol>();

	// comment collections, by type
	private List<Symbol> _sdocComments = new ArrayList<Symbol>();
	private List<Symbol> _vsdocComments = new ArrayList<Symbol>();
	private List<Symbol> _singleLineComments = new ArrayList<Symbol>();
	private List<Symbol> _multiLineComments = new ArrayList<Symbol>();

	public JSFlexScanner()
	{
		this((Reader) null);
	}

	public Symbol getLastToken()
	{
		return _lastToken;
	}

	public List<Symbol> getSDocComments()
	{
		return _sdocComments;
	}

	public List<Symbol> getVSDocComments()
	{
		return _vsdocComments;
	}

	public List<Symbol> getSingleLineComments()
	{
		return _singleLineComments;
	}

	public List<Symbol> getMultiLineComments()
	{
		return _multiLineComments;
	}

	private Symbol newToken(JSTokenType type, Object value)
	{
		return newToken(type.getIndex(), value);
	}

	private Symbol newToken(short id, Object value)
	{
		return new Symbol(id, yychar, yychar + yylength() - 1, value);
	}

	public Symbol nextToken() throws java.io.IOException, Scanner.Exception
	{
		// clear accumulators
		_vsdocAccumulator.clear();

		try
		{
			// get next token
			_lastToken = yylex();
		} 
		catch (Scanner.Exception e)
		{
			// create default token type
			String text = yytext();
			int end = yychar + text.length() - 1;

			_lastToken = new Symbol(JSTokenType.EOF.getIndex(), yychar, end, text);
		}
		finally
		{
			// process any accumulated vsdoc lines
			if (!_vsdocAccumulator.isEmpty())
			{
				Symbol vsdoc = newToken(JSTokenType.VSDOC, new ArrayList<Symbol>(_vsdocAccumulator));

				_vsdocComments.add(vsdoc);
			}
		}

		return _lastToken;
	}

	private boolean isValidDivisionStart()
	{
		if (_lastToken != null)
		{
			switch (_lastToken.getId())
			{
				case Terminals.IDENTIFIER:
				case Terminals.NUMBER:
				case Terminals.REGEX:
				case Terminals.STRING:
				case Terminals.RPAREN:
				case Terminals.PLUS_PLUS:
				case Terminals.MINUS_MINUS:
				case Terminals.RBRACKET:
				case Terminals.RCURLY:
				case Terminals.FALSE:
				case Terminals.NULL:
				case Terminals.THIS:
				case Terminals.TRUE:
					return true;
			}
		}

		return false;
	}

	public void setCollectComments(boolean flag)
	{
		_collectComments = flag;
	}

	public void setSource(String source)
	{
		yyreset(new StringReader(source));

		// clear last token
		_lastToken = null;

		// reset comment collection lists
		_singleLineComments.clear();
		_multiLineComments.clear();
		_sdocComments.clear();
		_vsdocComments.clear();
	}
%}

LineTerminator = \r|\n|\r\n
RubyBlock = "<%" ~"%>"
PHPBlock = "<?" ~"?>"
DjangoBlock = "{%" ~"%}"
Whitespace = {LineTerminator} | [ \t\f] | {RubyBlock} | {PHPBlock} | {DjangoBlock}

//Identifier = [a-zA-Z_$][a-zA-Z0-9_$]*
Identifier = ([:jletter:]|\$)([:jletterdigit:]|\$)*

Integer = [:digit:][:digit:]*
Hex = "0" [xX] [a-fA-F0-9]+
Float = ({Integer} \.[:digit:]*) | (\.[:digit:]+)
Scientific = ({Integer} | {Float}) [eE][-+]?[:digit:]+
Number = {Integer} | {Hex} | {Float} | {Scientific}

DoubleQuotedString = \"([^\\\"\r\n]|\\[^])*\"
SingleQuotedString = '([^\\'\r\n]|\\[^])*'
Strings = {DoubleQuotedString} | {SingleQuotedString}

SingleLineComment = "//" [^\r\n]*
MultiLineComment = "/*" ~"*/"
SDocComment = "/**" ~"*/"
VSDocComment = "///" [^\r\n]*

CharClass = "[" ([^\]\\\r\n]|\\[^\r\n])* "]"
Character = ([^\[\\\/\r\n]|\\[^\r\n])+
Regex = "/" ({CharClass}|{Character})+ "/" [a-z]*

%state DIVISION, REGEX

%%

{Whitespace}+	{ /* ignore */ }

<YYINITIAL> {
	// comments
	{VSDocComment}		{
							if (_collectComments)
							{
								_vsdocAccumulator.add(newToken(JSTokenType.VSDOC, yytext()));
							}
						}
	{SDocComment}		{
							if (_collectComments)
							{
								_sdocComments.add(newToken(JSTokenType.SDOC, yytext()));
							}
						}
	{SingleLineComment}	{
							if (_collectComments)
							{
								_singleLineComments.add(newToken(JSTokenType.SINGLELINE_COMMENT, yytext()));
							}
						}
	{MultiLineComment}	{
							if (_collectComments)
							{
								_multiLineComments.add(newToken(JSTokenType.MULTILINE_COMMENT, yytext()));
							}
						}

	// numbers
	{Number}		{ return newToken(Terminals.NUMBER, yytext()); }

	// strings
	{Strings}		{ return newToken(Terminals.STRING, yytext()); }

	// keywords
	"break"			{ return newToken(Terminals.BREAK, yytext()); }
	"case"			{ return newToken(Terminals.CASE, yytext()); }
	"catch"			{ return newToken(Terminals.CATCH, yytext()); }
	"const"			{ return newToken(Terminals.VAR, yytext()); }
	"continue"		{ return newToken(Terminals.CONTINUE, yytext()); }
	"default"		{ return newToken(Terminals.DEFAULT, yytext()); }
	"delete"		{ return newToken(Terminals.DELETE, yytext()); }
	"do"			{ return newToken(Terminals.DO, yytext()); }
	"else"			{ return newToken(Terminals.ELSE, yytext()); }
	"false"			{ return newToken(Terminals.FALSE, yytext()); }
	"finally"		{ return newToken(Terminals.FINALLY, yytext()); }
	"for"			{ return newToken(Terminals.FOR, yytext()); }
	"function"		{ return newToken(Terminals.FUNCTION, yytext()); }
	"if"			{ return newToken(Terminals.IF, yytext()); }
	"instanceof"	{ return newToken(Terminals.INSTANCEOF, yytext()); }
	"in"			{ return newToken(Terminals.IN, yytext()); }
	"new"			{ return newToken(Terminals.NEW, yytext()); }
	"null"			{ return newToken(Terminals.NULL, yytext()); }
	"return"		{ return newToken(Terminals.RETURN, yytext()); }
	"switch"		{ return newToken(Terminals.SWITCH, yytext()); }
	"this"			{ return newToken(Terminals.THIS, yytext()); }
	"throw"			{ return newToken(Terminals.THROW, yytext()); }
	"true"			{ return newToken(Terminals.TRUE, yytext()); }
	"try"			{ return newToken(Terminals.TRY, yytext()); }
	"typeof"		{ return newToken(Terminals.TYPEOF, yytext()); }
	"var"			{ return newToken(Terminals.VAR, yytext()); }
	"void"			{ return newToken(Terminals.VOID, yytext()); }
	"while"			{ return newToken(Terminals.WHILE, yytext()); }
	"with"			{ return newToken(Terminals.WITH, yytext()); }

	// identifiers
	{Identifier}	{ return newToken(Terminals.IDENTIFIER, yytext()); }

	// operators
	">>>="			{ return newToken(Terminals.GREATER_GREATER_GREATER_EQUAL, yytext()); }
	">>>"			{ return newToken(Terminals.GREATER_GREATER_GREATER, yytext()); }

	"<<="			{ return newToken(Terminals.LESS_LESS_EQUAL, yytext()); }
	"<<"			{ return newToken(Terminals.LESS_LESS, yytext()); }
	"<="			{ return newToken(Terminals.LESS_EQUAL, yytext()); }
	"<"				{ return newToken(Terminals.LESS, yytext()); }

	">>="			{ return newToken(Terminals.GREATER_GREATER_EQUAL, yytext()); }
	">>"			{ return newToken(Terminals.GREATER_GREATER, yytext()); }
	">="			{ return newToken(Terminals.GREATER_EQUAL, yytext()); }
	">"				{ return newToken(Terminals.GREATER, yytext()); }

	"==="			{ return newToken(Terminals.EQUAL_EQUAL_EQUAL, yytext()); }
	"=="			{ return newToken(Terminals.EQUAL_EQUAL, yytext()); }
	"="				{ return newToken(Terminals.EQUAL, yytext()); }

	"!=="			{ return newToken(Terminals.EXCLAMATION_EQUAL_EQUAL, yytext()); }
	"!="			{ return newToken(Terminals.EXCLAMATION_EQUAL, yytext()); }
	"!"				{ return newToken(Terminals.EXCLAMATION, yytext()); }

	"&&"			{ return newToken(Terminals.AMPERSAND_AMPERSAND, yytext()); }
	"&="			{ return newToken(Terminals.AMPERSAND_EQUAL, yytext()); }
	"&"				{ return newToken(Terminals.AMPERSAND, yytext()); }

	"||"			{ return newToken(Terminals.PIPE_PIPE, yytext()); }
	"|="			{ return newToken(Terminals.PIPE_EQUAL, yytext()); }
	"|"				{ return newToken(Terminals.PIPE, yytext()); }

	"*="			{ return newToken(Terminals.STAR_EQUAL, yytext()); }
	"*"				{ return newToken(Terminals.STAR, yytext()); }

	"/"				{
						yypushback(1);
						if (isValidDivisionStart())
						{
							yybegin(DIVISION);
						}
						else
						{
							yybegin(REGEX);
						}
					}

	"%="			{ return newToken(Terminals.PERCENT_EQUAL, yytext()); }
	"%"				{ return newToken(Terminals.PERCENT, yytext()); }

	"--"			{ return newToken(Terminals.MINUS_MINUS, yytext()); }
	"-="			{ return newToken(Terminals.MINUS_EQUAL, yytext()); }
	"-"				{ return newToken(Terminals.MINUS, yytext()); }

	"++"			{ return newToken(Terminals.PLUS_PLUS, yytext()); }
	"+="			{ return newToken(Terminals.PLUS_EQUAL, yytext()); }
	"+"				{ return newToken(Terminals.PLUS, yytext()); }

	"^="			{ return newToken(Terminals.CARET_EQUAL, yytext()); }
	"^"				{ return newToken(Terminals.CARET, yytext()); }
 
	"?"				{ return newToken(Terminals.QUESTION, yytext()); }
	"~"				{ return newToken(Terminals.TILDE, yytext()); }
	";"				{ return newToken(Terminals.SEMICOLON, yytext()); }
	"("				{ return newToken(Terminals.LPAREN, yytext()); }
	")"				{ return newToken(Terminals.RPAREN, yytext()); }
	"["				{ return newToken(Terminals.LBRACKET, yytext()); }
	"]"				{ return newToken(Terminals.RBRACKET, yytext()); }
	"{"				{ return newToken(Terminals.LCURLY, yytext()); }
	"}"				{ return newToken(Terminals.RCURLY, yytext()); }
	","				{ return newToken(Terminals.COMMA, yytext()); }
	":"				{ return newToken(Terminals.COLON, yytext()); }
	"."				{ return newToken(Terminals.DOT, yytext()); }
}

<DIVISION> {
	"/="			{
						yybegin(YYINITIAL);
						return newToken(Terminals.FORWARD_SLASH_EQUAL, yytext());
					}
	"/"				{
						yybegin(YYINITIAL);
						return newToken(Terminals.FORWARD_SLASH, yytext());
					}
}

<REGEX> {
	{Regex}			{
						yybegin(YYINITIAL);
						return newToken(Terminals.REGEX, yytext());
					}
	"/="			{
						yybegin(YYINITIAL);
						return newToken(Terminals.FORWARD_SLASH_EQUAL, yytext());
					}
	"/"				{
						yybegin(YYINITIAL);
						return newToken(Terminals.FORWARD_SLASH, yytext());
					}
}

// \"[^\"\r\n\ ]+|.

[\"']\\?|.	{
				// make sure we reset the lexer state for next (potential) scan
				yybegin(YYINITIAL);
				throw new Scanner.Exception("Unexpected character '" + yytext() + "' around offset " + yychar);
			}
