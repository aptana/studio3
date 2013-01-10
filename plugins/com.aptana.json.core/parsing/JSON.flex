// $codepro.audit.disable
/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json.core.parsing;

import java.io.Reader;
import java.io.StringReader;

import beaver.Symbol;
import beaver.Scanner;

import org.eclipse.core.internal.utils.StringPool;

%%

%public
%class JSONFlexScanner
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


	public JSONFlexScanner()
	{
		this((Reader) null);
	}

	public Symbol getLastToken()
	{
		return _lastToken;
	}
	
	private String pool(String value)
	{
		return _stringPool.add(value);
	}
	
	private Symbol newToken(short id)
	{
		return new Symbol(id, yychar, yychar + yylength() - 1, null);
	}

	private Symbol newToken(short id, Object value)
	{
		return new Symbol(id, yychar, yychar + yylength() - 1, value);
	}

	public Symbol nextToken() throws java.io.IOException, Scanner.Exception
	{
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

			_lastToken = new Symbol(Terminals.EOF, yychar, end, text);
		}

		return _lastToken;
	}

	public void setSource(String source)
	{
		yyreset(new StringReader(source));

		_stringPool = new StringPool();

		// clear last token
		_lastToken = null;
	}
%}

LineTerminator = \r|\n|\r\n
Whitespace = {LineTerminator} | [ \t\f]

Integer = [:digit:][:digit:]*
Hex = "0" [xX] [a-fA-F0-9]+
Float = ({Integer} \.[:digit:]*) | (\.[:digit:]+)
Scientific = ({Integer} | {Float}) [eE][-+]?[:digit:]+
Number = {Integer} | {Hex} | {Float} | {Scientific}

DoubleQuotedString = \"([^\\\"\r\n]|\\[^])*\"
SingleQuotedString = '([^\\'\r\n]|\\[^])*'
Strings = {DoubleQuotedString} | {SingleQuotedString}
%%

{Whitespace}+	{ /* ignore */ }

<YYINITIAL> {
	// strings
	{Strings}		{ return newToken(Terminals.STRING, pool(yytext())); }

	// keywords
	"false"			{ return newToken(Terminals.FALSE); }
	"null"			{ return newToken(Terminals.NULL); }
	"true"			{ return newToken(Terminals.TRUE); }

	// numbers
	{Number}		{ return newToken(Terminals.NUMBER, pool(yytext())); }
	
	// operators
	"{"				{ return newToken(Terminals.LCURLY); }
	"}"				{ return newToken(Terminals.RCURLY); }
	"["				{ return newToken(Terminals.LBRACKET); }
	"]"				{ return newToken(Terminals.RBRACKET); }
	","				{ return newToken(Terminals.COMMA); }
	":"				{ return newToken(Terminals.COLON); }
}

[\"']\\?|.	{
				// make sure we reset the lexer state for next (potential) scan
				yybegin(YYINITIAL);
				throw new Scanner.Exception("Unexpected character '" + yytext() + "' around offset " + yychar);
			}
