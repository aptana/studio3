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

import com.aptana.core.util.StringUtil;

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
	"break"			{ return newToken(Terminals.BREAK, StringUtil.EMPTY); }
	"case"			{ return newToken(Terminals.CASE, StringUtil.EMPTY); }
	"catch"			{ return newToken(Terminals.CATCH, StringUtil.EMPTY); }
	"const"			{ return newToken(Terminals.VAR, StringUtil.EMPTY); }
	"continue"		{ return newToken(Terminals.CONTINUE, StringUtil.EMPTY); }
	"default"		{ return newToken(Terminals.DEFAULT, StringUtil.EMPTY); }
	"delete"		{ return newToken(Terminals.DELETE, StringUtil.EMPTY); }
	"do"			{ return newToken(Terminals.DO, StringUtil.EMPTY); }
	"else"			{ return newToken(Terminals.ELSE, StringUtil.EMPTY); }
	"false"			{ return newToken(Terminals.FALSE, StringUtil.EMPTY); }
	"finally"		{ return newToken(Terminals.FINALLY, StringUtil.EMPTY); }
	"for"			{ return newToken(Terminals.FOR, StringUtil.EMPTY); }
	"function"		{ return newToken(Terminals.FUNCTION, StringUtil.EMPTY); }
	"if"			{ return newToken(Terminals.IF, StringUtil.EMPTY); }
	"instanceof"	{ return newToken(Terminals.INSTANCEOF, StringUtil.EMPTY); }
	"in"			{ return newToken(Terminals.IN, StringUtil.EMPTY); }
	"new"			{ return newToken(Terminals.NEW, StringUtil.EMPTY); }
	"null"			{ return newToken(Terminals.NULL, StringUtil.EMPTY); }
	"return"		{ return newToken(Terminals.RETURN, StringUtil.EMPTY); }
	"switch"		{ return newToken(Terminals.SWITCH, StringUtil.EMPTY); }
	"this"			{ return newToken(Terminals.THIS, StringUtil.EMPTY); }
	"throw"			{ return newToken(Terminals.THROW, StringUtil.EMPTY); }
	"true"			{ return newToken(Terminals.TRUE, StringUtil.EMPTY); }
	"try"			{ return newToken(Terminals.TRY, StringUtil.EMPTY); }
	"typeof"		{ return newToken(Terminals.TYPEOF, StringUtil.EMPTY); }
	"var"			{ return newToken(Terminals.VAR, StringUtil.EMPTY); }
	"void"			{ return newToken(Terminals.VOID, StringUtil.EMPTY); }
	"while"			{ return newToken(Terminals.WHILE, StringUtil.EMPTY); }
	"with"			{ return newToken(Terminals.WITH, StringUtil.EMPTY); }

	// identifiers
	{Identifier}	{ return newToken(Terminals.IDENTIFIER, yytext()); }

	// operators
	">>>="			{ return newToken(Terminals.GREATER_GREATER_GREATER_EQUAL, StringUtil.EMPTY); }
	">>>"			{ return newToken(Terminals.GREATER_GREATER_GREATER, StringUtil.EMPTY); }

	"<<="			{ return newToken(Terminals.LESS_LESS_EQUAL, null); }
	"<<"			{ return newToken(Terminals.LESS_LESS, StringUtil.EMPTY); }
	"<="			{ return newToken(Terminals.LESS_EQUAL, StringUtil.EMPTY); }
	"<"				{ return newToken(Terminals.LESS, StringUtil.EMPTY); }

	">>="			{ return newToken(Terminals.GREATER_GREATER_EQUAL, StringUtil.EMPTY); }
	">>"			{ return newToken(Terminals.GREATER_GREATER, StringUtil.EMPTY); }
	">="			{ return newToken(Terminals.GREATER_EQUAL, StringUtil.EMPTY); }
	">"				{ return newToken(Terminals.GREATER, StringUtil.EMPTY); }

	"==="			{ return newToken(Terminals.EQUAL_EQUAL_EQUAL, StringUtil.EMPTY); }
	"=="			{ return newToken(Terminals.EQUAL_EQUAL, StringUtil.EMPTY); }
	"="				{ return newToken(Terminals.EQUAL, StringUtil.EMPTY); }

	"!=="			{ return newToken(Terminals.EXCLAMATION_EQUAL_EQUAL, StringUtil.EMPTY); }
	"!="			{ return newToken(Terminals.EXCLAMATION_EQUAL, StringUtil.EMPTY); }
	"!"				{ return newToken(Terminals.EXCLAMATION, StringUtil.EMPTY); }

	"&&"			{ return newToken(Terminals.AMPERSAND_AMPERSAND, StringUtil.EMPTY); }
	"&="			{ return newToken(Terminals.AMPERSAND_EQUAL, StringUtil.EMPTY); }
	"&"				{ return newToken(Terminals.AMPERSAND, StringUtil.EMPTY); }

	"||"			{ return newToken(Terminals.PIPE_PIPE, StringUtil.EMPTY); }
	"|="			{ return newToken(Terminals.PIPE_EQUAL, StringUtil.EMPTY); }
	"|"				{ return newToken(Terminals.PIPE, StringUtil.EMPTY); }

	"*="			{ return newToken(Terminals.STAR_EQUAL, StringUtil.EMPTY); }
	"*"				{ return newToken(Terminals.STAR, StringUtil.EMPTY); }

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

	"%="			{ return newToken(Terminals.PERCENT_EQUAL, StringUtil.EMPTY); }
	"%"				{ return newToken(Terminals.PERCENT, StringUtil.EMPTY); }

	"--"			{ return newToken(Terminals.MINUS_MINUS, StringUtil.EMPTY); }
	"-="			{ return newToken(Terminals.MINUS_EQUAL, StringUtil.EMPTY); }
	"-"				{ return newToken(Terminals.MINUS, StringUtil.EMPTY); }

	"++"			{ return newToken(Terminals.PLUS_PLUS, StringUtil.EMPTY); }
	"+="			{ return newToken(Terminals.PLUS_EQUAL, StringUtil.EMPTY); }
	"+"				{ return newToken(Terminals.PLUS, StringUtil.EMPTY); }

	"^="			{ return newToken(Terminals.CARET_EQUAL, StringUtil.EMPTY); }
	"^"				{ return newToken(Terminals.CARET, StringUtil.EMPTY); }
 
	"?"				{ return newToken(Terminals.QUESTION, StringUtil.EMPTY); }
	"~"				{ return newToken(Terminals.TILDE, StringUtil.EMPTY); }
	";"				{ return newToken(Terminals.SEMICOLON, StringUtil.EMPTY); }
	"("				{ return newToken(Terminals.LPAREN, StringUtil.EMPTY); }
	")"				{ return newToken(Terminals.RPAREN, StringUtil.EMPTY); }
	"["				{ return newToken(Terminals.LBRACKET, StringUtil.EMPTY); }
	"]"				{ return newToken(Terminals.RBRACKET, StringUtil.EMPTY); }
	"{"				{ return newToken(Terminals.LCURLY, StringUtil.EMPTY); }
	"}"				{ return newToken(Terminals.RCURLY, StringUtil.EMPTY); }
	","				{ return newToken(Terminals.COMMA, StringUtil.EMPTY); }
	":"				{ return newToken(Terminals.COLON, StringUtil.EMPTY); }
	"."				{ return newToken(Terminals.DOT, StringUtil.EMPTY); }
}

<DIVISION> {
	"/="			{
						yybegin(YYINITIAL);
						return newToken(Terminals.FORWARD_SLASH_EQUAL, StringUtil.EMPTY);
					}
	"/"				{
						yybegin(YYINITIAL);
						return newToken(Terminals.FORWARD_SLASH, StringUtil.EMPTY);
					}
}

<REGEX> {
	{Regex}			{
						yybegin(YYINITIAL);
						return newToken(Terminals.REGEX, yytext());
					}
	"/="			{
						yybegin(YYINITIAL);
						return newToken(Terminals.FORWARD_SLASH_EQUAL, StringUtil.EMPTY);
					}
	"/"				{
						yybegin(YYINITIAL);
						return newToken(Terminals.FORWARD_SLASH, StringUtil.EMPTY);
					}
}

// \"[^\"\r\n\ ]+|.

[\"']\\?|.	{
				// make sure we reset the lexer state for next (potential) scan
				yybegin(YYINITIAL);
				throw new Scanner.Exception("Unexpected character '" + yytext() + "' around offset " + yychar);
			}
