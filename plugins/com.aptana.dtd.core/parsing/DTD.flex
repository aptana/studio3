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
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private List<Symbol> tokenQueue = new ArrayList<Symbol>();
	
	private static final Pattern ENTITY = Pattern.compile("%([^; \\t\\n]+);"); //$NON-NLS-1$
	private Map<String, String> _entities;
	
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
		if (!tokenQueue.isEmpty())
		{
			return tokenQueue.remove(0);
		}

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
		tokenQueue.clear();
	}
	
	String getValue(String key)
	{
		String result = null;

		if (this._entities != null)
		{
			result = this._entities.get(key);
		}

		return result;
	}
	
	void register(String key, String value)
	{
		if (this._entities == null)
		{
			this._entities = new HashMap<String, String>();
		}

		// According to the XML 1.1 Specification in Section 4.2:
		// If the same entity is declared more than once, the first declaration encountered is binding;
		// at user option, an XML processor may issue a warning if entities are declared multiple times.
		if (this._entities.containsKey(key) == false)
		{
			this._entities.put(key, value);
		}
	}
	
	Symbol registered(String peRef) throws IOException, Exception
	{
		// grab key minus the leading '%' and trailing ';'
		String key = peRef.substring(1, peRef.length() - 1);

		// grab entity's value
		String text = getValue(key);

		if (text == null)
		{
			return newToken(Terminals.PE_REF, peRef);
		}

		// create new scanner
		// We need to continue to scan tokens until EOF, not just one token!
		DTDScanner nested = new DTDScanner();
		nested.setSource(text);
		nested._entities = _entities;
		Symbol s;
		while (true)
		{
			s = nested.nextToken();
			if (s.getId() == Terminals.EOF)
			{
				break;
			}
			s = new Symbol(s.getId(), s.getStart() + yychar, s.getEnd() + yychar, s.value);
			tokenQueue.add(s);
		}

		return tokenQueue.remove(0);
	}
	
	Symbol handleString(String text)
	{
		StringBuffer buffer = new StringBuffer();
		Matcher m = ENTITY.matcher(text);

		while (m.find())
		{
			String name = m.group(1);
			String newText = this.getValue(name);

			if (newText == null)
			{
				newText = name;
			}

			m.appendReplacement(buffer, newText);
		}

		m.appendTail(buffer);

		return newToken(Terminals.STRING, pool(buffer.toString()));
	}
%}

LineTerminator = \r|\n|\r\n
Whitespace = {LineTerminator} | [ \t\f]

NMToken = [:a-zA-Z_\-\.0-9]+
Identifier = [:a-zA-Z_][:a-zA-Z_\-\.0-9]*

//DoubleQuotedString = \"([^\\\"\r\n]|\\[^])*\"
DoubleQuotedString = "\"" ~"\""
//SingleQuotedString = '([^\\'\r\n]|\\[^])*'
SingleQuotedString = "'" ~"'"
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
	{Strings}		{ return handleString(yytext()); }

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
	{PeRef}			{  return registered(yytext()); }
	
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
