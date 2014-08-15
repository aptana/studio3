// $codepro.audit.disable
/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing;

import java.io.Reader;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.List;

import beaver.Symbol;
import beaver.Scanner;

import org.eclipse.core.internal.utils.StringPool;

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
	
	private StringPool _stringPool;

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

	private Symbol newToken(JSTokenType type)
	{
		return newToken(type.getIndex(), type.getName());
	}
	
	private String pool(String value)
	{
		return _stringPool.add(value);
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

		_stringPool = new StringPool();

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
	{Number}		{ return newToken(Terminals.NUMBER, pool(yytext())); }

	// strings
	{Strings}		{ return newToken(Terminals.STRING, pool(yytext())); }

	// keywords
	"break"			{ return newToken(JSTokenType.BREAK); }
	"case"			{ return newToken(JSTokenType.CASE); }
	"catch"			{ return newToken(JSTokenType.CATCH); }
	"const"			{ return newToken(JSTokenType.VAR); }
	"continue"		{ return newToken(JSTokenType.CONTINUE); }
	"debugger"		{ return newToken(JSTokenType.DEBUGGER); }
	"default"		{ return newToken(JSTokenType.DEFAULT); }
	"delete"		{ return newToken(JSTokenType.DELETE); }
	"do"			{ return newToken(JSTokenType.DO); }
	"else"			{ return newToken(JSTokenType.ELSE); }
	"false"			{ return newToken(JSTokenType.FALSE); }
	"finally"		{ return newToken(JSTokenType.FINALLY); }
	"for"			{ return newToken(JSTokenType.FOR); }
	"function"		{ return newToken(JSTokenType.FUNCTION); }
	"if"			{ return newToken(JSTokenType.IF); }
	"instanceof"	{ return newToken(JSTokenType.INSTANCEOF); }
	"in"			{ return newToken(JSTokenType.IN); }
	"new"			{ return newToken(JSTokenType.NEW); }
	"null"			{ return newToken(JSTokenType.NULL); }
	"return"		{ return newToken(JSTokenType.RETURN); }
	"switch"		{ return newToken(JSTokenType.SWITCH); }
	"this"			{ return newToken(JSTokenType.THIS); }
	"throw"			{ return newToken(JSTokenType.THROW); }
	"true"			{ return newToken(JSTokenType.TRUE); }
	"try"			{ return newToken(JSTokenType.TRY); }
	"typeof"		{ return newToken(JSTokenType.TYPEOF); }
	"var"			{ return newToken(JSTokenType.VAR); }
	"void"			{ return newToken(JSTokenType.VOID); }
	"while"			{ return newToken(JSTokenType.WHILE); }
	"with"			{ return newToken(JSTokenType.WITH); }
	"get"			{ return newToken(JSTokenType.GET); }
	"set"			{ return newToken(JSTokenType.SET); }
	
	// Future Use Reserved Words
	"class"			{ return newToken(JSTokenType.CLASS); }
	"enum"			{ return newToken(JSTokenType.ENUM); }
	"export"		{ return newToken(JSTokenType.EXPORT); }
	"extends"		{ return newToken(JSTokenType.EXTENDS); }
	"import"		{ return newToken(JSTokenType.IMPORT); }
	"super"			{ return newToken(JSTokenType.SUPER); }
	"implements"	{ return newToken(JSTokenType.IMPLEMENTS); }
	"interface"		{ return newToken(JSTokenType.INTERFACE); }
	"let"			{ return newToken(JSTokenType.LET); }
	"package"		{ return newToken(JSTokenType.PACKAGE); }
	"private"		{ return newToken(JSTokenType.PRIVATE); }
	"protected"		{ return newToken(JSTokenType.PROTECTED); }
	"public"		{ return newToken(JSTokenType.PUBLIC); }
	"static"		{ return newToken(JSTokenType.STATIC); }
	"yield"			{ return newToken(JSTokenType.YIELD); }

	// identifiers
	{Identifier}	{ return newToken(Terminals.IDENTIFIER, pool(yytext())); }

	// operators
	">>>="			{ return newToken(JSTokenType.GREATER_GREATER_GREATER_EQUAL); }
	">>>"			{ return newToken(JSTokenType.GREATER_GREATER_GREATER); }

	"<<="			{ return newToken(JSTokenType.LESS_LESS_EQUAL); }
	"<<"			{ return newToken(JSTokenType.LESS_LESS); }
	"<="			{ return newToken(JSTokenType.LESS_EQUAL); }
	"<"				{ return newToken(JSTokenType.LESS); }

	">>="			{ return newToken(JSTokenType.GREATER_GREATER_EQUAL); }
	">>"			{ return newToken(JSTokenType.GREATER_GREATER); }
	">="			{ return newToken(JSTokenType.GREATER_EQUAL); }
	">"				{ return newToken(JSTokenType.GREATER); }

	"==="			{ return newToken(JSTokenType.EQUAL_EQUAL_EQUAL); }
	"=="			{ return newToken(JSTokenType.EQUAL_EQUAL); }
	"="				{ return newToken(JSTokenType.EQUAL); }

	"!=="			{ return newToken(JSTokenType.EXCLAMATION_EQUAL_EQUAL); }
	"!="			{ return newToken(JSTokenType.EXCLAMATION_EQUAL); }
	"!"				{ return newToken(JSTokenType.EXCLAMATION); }

	"&&"			{ return newToken(JSTokenType.AMPERSAND_AMPERSAND); }
	"&="			{ return newToken(JSTokenType.AMPERSAND_EQUAL); }
	"&"				{ return newToken(JSTokenType.AMPERSAND); }

	"||"			{ return newToken(JSTokenType.PIPE_PIPE); }
	"|="			{ return newToken(JSTokenType.PIPE_EQUAL); }
	"|"				{ return newToken(JSTokenType.PIPE); }

	"*="			{ return newToken(JSTokenType.STAR_EQUAL); }
	"*"				{ return newToken(JSTokenType.STAR); }

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

	"%="			{ return newToken(JSTokenType.PERCENT_EQUAL); }
	"%"				{ return newToken(JSTokenType.PERCENT); }

	"--"			{ return newToken(JSTokenType.MINUS_MINUS); }
	"-="			{ return newToken(JSTokenType.MINUS_EQUAL); }
	"-"				{ return newToken(JSTokenType.MINUS); }

	"++"			{ return newToken(JSTokenType.PLUS_PLUS); }
	"+="			{ return newToken(JSTokenType.PLUS_EQUAL); }
	"+"				{ return newToken(JSTokenType.PLUS); }

	"^="			{ return newToken(JSTokenType.CARET_EQUAL); }
	"^"				{ return newToken(JSTokenType.CARET); }
 
	"?"				{ return newToken(JSTokenType.QUESTION); }
	"~"				{ return newToken(JSTokenType.TILDE); }
	";"				{ return newToken(JSTokenType.SEMICOLON); }
	"("				{ return newToken(JSTokenType.LPAREN); }
	")"				{ return newToken(JSTokenType.RPAREN); }
	"["				{ return newToken(JSTokenType.LBRACKET); }
	"]"				{ return newToken(JSTokenType.RBRACKET); }
	"{"				{ return newToken(JSTokenType.LCURLY); }
	"}"				{ return newToken(JSTokenType.RCURLY); }
	","				{ return newToken(JSTokenType.COMMA); }
	":"				{ return newToken(JSTokenType.COLON); }
	"."				{ return newToken(JSTokenType.DOT); }
}

<DIVISION> {
	"/="			{
						yybegin(YYINITIAL);
						return newToken(JSTokenType.FORWARD_SLASH_EQUAL);
					}
	"/"				{
						yybegin(YYINITIAL);
						return newToken(JSTokenType.FORWARD_SLASH);
					}
}

<REGEX> {
	{Regex}			{
						yybegin(YYINITIAL);
						return newToken(Terminals.REGEX, pool(yytext()));
					}
	"/="			{
						yybegin(YYINITIAL);
						return newToken(JSTokenType.FORWARD_SLASH_EQUAL);
					}
	"/"				{
						yybegin(YYINITIAL);
						return newToken(JSTokenType.FORWARD_SLASH);
					}
}

// \"[^\"\r\n\ ]+|.

[\"']\\?|.	{
				// make sure we reset the lexer state for next (potential) scan
				yybegin(YYINITIAL);
				throw new Scanner.Exception("Unexpected character '" + yytext() + "' around offset " + yychar);
			}
