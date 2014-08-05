// $codepro.audit.disable
/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.xml.core.parsing;

import java.io.Reader;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.List;

import beaver.Symbol;
import beaver.Scanner;

import org.eclipse.core.internal.utils.StringPool;

%%

%public
%class XMLScanner
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

	public XMLScanner()
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
		return newToken(id, null);
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

Identifier = [:a-zA-Z_][:a-zA-Z_\-\.0-9]*
InvalidIdentifier = [:a-zA-Z_\-\.0-9]+

CharData = [^<]*

DoubleQuotedString = \"([^\\\"\r\n]|\\[^])*\"
SingleQuotedString = '([^\\'\r\n]|\\[^])*'
Strings = {DoubleQuotedString} | {SingleQuotedString}

Comment = "<!--" ~"-->"
DocType = "<!DOCTYPE" {Whitespace} {Identifier} ({Whitespace} | {Identifier} | {Strings})* ("[" [^\]]+ "]" {Whitespace}?)? ">"
CData = "<![CDATA[" ~"]]>"
Declaration = "<?xml" ~">"

%state TAG

%%

{Whitespace}+	{ /* ignore */ }

<YYINITIAL> {
    // comments
	{Comment}		{ return newToken(Terminals.COMMENT, pool(yytext())); }

	// DocType
	{DocType}		{ return newToken(Terminals.DOCTYPE, pool(yytext())); }
	
	// CData
	{CData}			{ return newToken(Terminals.CDATA, pool(yytext())); }
		
	// Declaration
	{Declaration}	{ return newToken(Terminals.DECLARATION, pool(yytext())); }
		
	"</"			{ yybegin(TAG); return newToken(Terminals.LESS_SLASH); }
	"<"				{ yybegin(TAG); return newToken(Terminals.LESS); }
		
	// Text between tags
	{CharData}		{ return newToken(Terminals.TEXT, pool(yytext())); }
}

<TAG> {
	// Strings
	{Strings}		{ return newToken(Terminals.STRING, pool(yytext())); }
	
	"="				{ return newToken(Terminals.EQUAL); }
	
	"/>"			{ yybegin(YYINITIAL); return newToken(Terminals.SLASH_GREATER); }
	">"				{ yybegin(YYINITIAL); return newToken(Terminals.GREATER); }
	
	// Identifiers
	{Identifier}	{ return newToken(Terminals.TEXT, pool(yytext())); }
	{InvalidIdentifier}	{ return newToken(Terminals.TEXT, pool(yytext())); }
}
