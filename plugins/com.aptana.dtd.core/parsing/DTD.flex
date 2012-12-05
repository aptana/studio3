// $codepro.audit.disable
/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.dtd.core.parsing;

import java.io.Reader;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.List;

import beaver.Symbol;
import beaver.Scanner;

import org.eclipse.core.internal.utils.StringPool;

%%

%public
%class DTDScanner
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

	public DTDScanner()
	{
		this((Reader) null);
	}

	public Symbol getLastToken()
	{
		return _lastToken;
	}

	private Symbol newToken(DTDTokenType type, Object value)
	{
		return newToken(type.getIndex(), value);
	}

	private Symbol newToken(DTDTokenType type)
	{
		return newToken(type.getIndex(), null);
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

			_lastToken = new Symbol(DTDTokenType.EOF.getIndex(), yychar, end, text);
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

NMToken = [:a-zA-Z_\-\.0-9]+
Identifier = [:a-zA-Z_][:a-zA-Z_\-\.0-9]*

DoubleQuotedString = \"([^\\\"\r\n]|\\[^])*\"
SingleQuotedString = '([^\\'\r\n]|\\[^])*'
Strings = {DoubleQuotedString} | {SingleQuotedString}

Comment = "<!--" ~"-->"
Pi = "<?" ~"?>"

PeRef = [%&] {Identifier} ";"

%%

{Whitespace}+	{ /* ignore */ }

<YYINITIAL> {
    // comments
	{Comment}		{ return newToken(Terminals.COMMENT, pool(yytext())); }

	// Processing includes?
	{Pi}			{ return newToken(Terminals.PI, pool(yytext())); }

	// strings
	{Strings}		{ return newToken(Terminals.STRING, pool(yytext())); }

	// keywords
	"<!["			{ return newToken(DTDTokenType.SECTION_START); }
	"]]>"			{ return newToken(DTDTokenType.SECTION_END); }
	"<!ATTLIST"		{ return newToken(DTDTokenType.ATTLIST); }
	"<!ELEMENT"		{ return newToken(DTDTokenType.ELEMENT); }
	"<!ENTITY"		{ return newToken(DTDTokenType.ENTITY); }
	"<!NOTATION"	{ return newToken(DTDTokenType.NOTATION); }
	"#FIXED"		{ return newToken(DTDTokenType.FIXED, pool("#FIXED")); }
	"#IMPLIED"		{ return newToken(DTDTokenType.IMPLIED, pool("#IMPLIED")); }
	"#PCDATA"		{ return newToken(DTDTokenType.PCDATA, pool("#PCDATA")); }
	"#REQUIRED"		{ return newToken(DTDTokenType.REQUIRED, pool("#REQUIRED")); }
	"ANY"			{ return newToken(DTDTokenType.ANY, pool("ANY")); }
	"CDATA"			{ return newToken(DTDTokenType.CDATA_TYPE, pool("CDATA")); }
	"EMPTY"			{ return newToken(DTDTokenType.EMPTY, pool("EMPTY")); }
	"ENTITY"		{ return newToken(DTDTokenType.ENTITY_TYPE, pool("ENTITY")); }
	"ENTITIES"		{ return newToken(DTDTokenType.ENTITIES_TYPE, pool("ENTITIES")); }
	"IDREFS"		{ return newToken(DTDTokenType.IDREFS_TYPE, pool("IDREFS")); }
	"IDREF"			{ return newToken(DTDTokenType.IDREF_TYPE, pool("IDREF")); }
	"ID"			{ return newToken(DTDTokenType.ID_TYPE, pool("ID")); }
	"IGNORE"		{ return newToken(DTDTokenType.IGNORE, pool("IGNORE")); }
	"INCLUDE"		{ return newToken(DTDTokenType.INCLUDE, pool("INCLUDE")); }
	"NDATA"			{ return newToken(DTDTokenType.NDATA, pool("NDATA")); }
	"NMTOKENS"		{ return newToken(DTDTokenType.NMTOKENS_TYPE, pool("NMTOKENS")); }
	"NMTOKEN"		{ return newToken(DTDTokenType.NMTOKEN_TYPE, pool("NMTOKEN")); }
	"NOTATION"		{ return newToken(DTDTokenType.NOTATION_TYPE, pool("NOTATION")); }
	"PUBLIC"		{ return newToken(DTDTokenType.PUBLIC, pool("PUBLIC")); }
	"SYSTEM"		{ return newToken(DTDTokenType.SYSTEM, pool("SYSTEM")); }

	// Entity references
	{PeRef}			{ return newToken(DTDTokenType.PE_REF); }
	
	// characters
	">"				{ return newToken(DTDTokenType.GREATER_THAN); }
	"("				{ return newToken(DTDTokenType.LPAREN); }
	"|"				{ return newToken(DTDTokenType.PIPE); }
	")"				{ return newToken(DTDTokenType.RPAREN); }
	"?"				{ return newToken(DTDTokenType.QUESTION); }
	"*"				{ return newToken(DTDTokenType.STAR); }
	"+"				{ return newToken(DTDTokenType.PLUS); }
	","				{ return newToken(DTDTokenType.COMMA); }
	"%"				{ return newToken(DTDTokenType.PERCENT); }
	"["				{ return newToken(DTDTokenType.LBRACKET); }

	// Identifiers
	{Identifier}	{ return newToken(DTDTokenType.NAME, pool(yytext())); }

	{NMToken}		{ return newToken(DTDTokenType.NMTOKEN, pool(yytext())); }
}

// \"[^\"\r\n\ ]+|.

[\"']\\?|.	{
				// make sure we reset the lexer state for next (potential) scan
				yybegin(YYINITIAL);
				throw new Scanner.Exception("Unexpected character '" + yytext() + "' around offset " + yychar);
			}
