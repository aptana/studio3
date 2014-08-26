// $codepro.audit.disable
/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing;

import java.io.Reader;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.List;

import beaver.Symbol;
import beaver.Scanner;

@SuppressWarnings({"unused", "nls"})

%%

%class CSSFlexScanner
%extends Scanner
%type Symbol
%yylexthrow Scanner.Exception
%eofval{
	return newToken(Terminals.EOF, "end-of-file");
%eofval}
%unicode
%ignorecase
%char

//%switch
//%table
//%pack

%{
	// last token used for look behind. Also needed when implementing the ITokenScanner interface
	private Symbol _lastToken;

	// flag indicating if we should collect comments or not
	private boolean _collectComments = true;

	// comment collections, by type
	private List<Symbol> _comments = new ArrayList<Symbol>();

	// curly brace nesting level
	private int _nestingLevel;

	// a flag indicating we're inside of a @media block
	private boolean _inMedia;

	public CSSFlexScanner()
	{
		this((Reader) null);
	}

	public Symbol getLastToken()
	{
		return _lastToken;
	}

	public List<Symbol> getComments()
	{
		return _comments;
	}

	private Symbol newToken(CSSTokenType type, Object value)
	{
		return newToken(type.getIndex(), value);
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

			_lastToken = new Symbol(CSSTokenType.EOF.getIndex(), yychar, end, text);
		}

		return _lastToken;
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

		// reset comment collection list
		_comments.clear();

		// reset nesting level
		_nestingLevel = 0;

		// reset media flag
		_inMedia = false;
	}
%}

hex							= [0-9a-fA-F]
nonascii					= [\240-\4177777]
unicode						= \\{hex}{1,6}
escape						= {unicode}|\\[^\r\n\f0-9a-fA-F]
nmstart						= [-_a-zA-Z]|{nonascii}|{escape}
nmchar						= [-_a-zA-Z0-9]|{nonascii}|{escape}
double_quoted_string		= \"([^\n\r\f\"]|\\{nl}|{escape})*\"
single_quoted_string		= \'([^\n\r\f\']|\\{nl}|{escape})*\'
bad_double_quoted_string	= \"([^\n\r\f\"]|\\{nl}|{escape})*\\?
bad_single_quoted_string	= \'([^\n\r\f\']|\\{nl}|{escape})*\\?
comment						= "/*" ~"*/"
base_identifier				= -?{nmstart}{nmchar}*
ms_identifier				= "progid:"{base_identifier}(\.{base_identifier})*
identifier					= {base_identifier}|{ms_identifier}
name						= {nmchar}+
num							= [-+]?([0-9]+|[0-9]*\.[0-9]+)
s							= [ \t\r\n\f]+
nl							= \r|\n|\r\n|\f

%%

<YYINITIAL> {
	{s}							{ /* ignore */ }
	{comment}					{ _comments.add(newToken(CSSTokenType.COMMENT, yytext())); }

	{single_quoted_string}		{ return newToken(CSSTokenType.SINGLE_QUOTED_STRING, yytext()); }
	{double_quoted_string}		{ return newToken(CSSTokenType.DOUBLE_QUOTED_STRING, yytext()); }
	{bad_single_quoted_string}	{ return newToken(CSSTokenType.SINGLE_QUOTED_STRING, yytext()); }
	{bad_double_quoted_string}	{ return newToken(CSSTokenType.DOUBLE_QUOTED_STRING, yytext()); }

	"not"					    { return newToken(CSSTokenType.NOT, yytext()); }
	
	{num}"em"					{ return newToken(CSSTokenType.EMS, yytext()); }
	{num}"ex"					{ return newToken(CSSTokenType.EXS, yytext()); }
	{num}"px"					{ return newToken(CSSTokenType.LENGTH, yytext()); }
	{num}"cm"					{ return newToken(CSSTokenType.LENGTH, yytext()); }
	{num}"mm"					{ return newToken(CSSTokenType.LENGTH, yytext()); }
	{num}"in"					{ return newToken(CSSTokenType.LENGTH, yytext()); }
	{num}"pt"					{ return newToken(CSSTokenType.LENGTH, yytext()); }
	{num}"pc"					{ return newToken(CSSTokenType.LENGTH, yytext()); }
	{num}"deg"					{ return newToken(CSSTokenType.ANGLE, yytext()); }
	{num}"rad"					{ return newToken(CSSTokenType.ANGLE, yytext()); }
	{num}"grad"					{ return newToken(CSSTokenType.ANGLE, yytext()); }
	{num}"ms"					{ return newToken(CSSTokenType.TIME, yytext()); }
	{num}"s"					{ return newToken(CSSTokenType.TIME, yytext()); }
	{num}"hz"					{ return newToken(CSSTokenType.FREQUENCY, yytext()); }
	{num}"khz"					{ return newToken(CSSTokenType.FREQUENCY, yytext()); }
//	{num}{identifier}			{ return newToken(CSSTokenType.DIMENSION, yytext()); }
	{num}%						{ return newToken(CSSTokenType.PERCENTAGE, yytext()); }
	{num}						{ return newToken(CSSTokenType.NUMBER, yytext()); }
	
	"."{name}					{
									CSSTokenType type;

									if ((_inMedia && _nestingLevel == 1) || _nestingLevel <= 0)
									{
										type = CSSTokenType.CLASS;
									}
									else
									{
										boolean numbers = true;
										String text = yytext();

										for (int i = 1; i < text.length(); i++)
										{
											char c = text.charAt(i);

											if (c < '0' || '9' < c)
											{
												numbers = false;
												break;
											}
										}

										type = (numbers) ? CSSTokenType.NUMBER : CSSTokenType.CLASS;
									}

									return newToken(type, yytext());
								}
	"#"{name}					{
									CSSTokenType type;

									if ((_inMedia && _nestingLevel == 1) || _nestingLevel <= 0)
									{
										type = CSSTokenType.ID;
									}
									else
									{
										boolean numbers = true;
										String text = yytext();

										for (int i = 1; i < text.length(); i++)
										{
											char c = text.charAt(i);

											if (!('0' <= c && c <= '9' || 'a' <= c && c <= 'f' || 'A' <= c && c <= 'F'))
											{
												numbers = false;
												break;
											}
										}

										type = (numbers) ? CSSTokenType.RGB : CSSTokenType.ID;
									}

									return newToken(type, yytext());
								}

	"@import"					{ return newToken(CSSTokenType.IMPORT, yytext()); }
	"@page"						{ return newToken(CSSTokenType.PAGE, yytext()); }
	"@media"					{ _inMedia = true; return newToken(CSSTokenType.MEDIA_KEYWORD, yytext()); }
	"@charset"					{ return newToken(CSSTokenType.CHARSET, yytext()); }
	"@font-face"				{ return newToken(CSSTokenType.FONTFACE, yytext()); }
	"@namespace"				{ return newToken(CSSTokenType.NAMESPACE, yytext()); }
	"@-moz-document"			{ return newToken(CSSTokenType.MOZ_DOCUMENT, yytext()); }
	"@-ms-viewport"				{ return newToken(CSSTokenType.MS_VIEWPORT, yytext()); }
	"@"{name}					{ return newToken(CSSTokenType.AT_RULE, yytext()); }

	"!"({s}|{comment})*"important"	{ return newToken(CSSTokenType.IMPORTANT, yytext()); }

//	"<!--"						{ return newToken(CSSTokenType.CDO, yytext()); }
//	"-->"						{ return newToken(CSSTokenType.CDC, yytext()); }
	"~="						{ return newToken(CSSTokenType.INCLUDES, yytext()); }
	"|="						{ return newToken(CSSTokenType.DASHMATCH, yytext()); }
	"^="						{ return newToken(CSSTokenType.BEGINS_WITH, yytext()); }
	"$="						{ return newToken(CSSTokenType.ENDS_WITH, yytext()); }

	":"							{ return newToken(CSSTokenType.COLON, yytext()); }
	";"							{ return newToken(CSSTokenType.SEMICOLON, yytext()); }
	"{"							{
									_nestingLevel++;

									return newToken(CSSTokenType.LCURLY, yytext());
								}
	"}"							{
									_nestingLevel--;

									if (_nestingLevel == 0)
									{
										// reset (possibly set) media flag
										_inMedia = false;
									}

									return newToken(CSSTokenType.RCURLY, yytext());
								}
	"("							{ return newToken(CSSTokenType.LPAREN, yytext()); }
	")"							{ return newToken(CSSTokenType.RPAREN, yytext()); }
	"%"							{ return newToken(CSSTokenType.PERCENTAGE, yytext()); }
	"["							{ return newToken(CSSTokenType.LBRACKET, yytext()); }
	"]"							{ return newToken(CSSTokenType.RBRACKET, yytext()); }
	","							{ return newToken(CSSTokenType.COMMA, yytext()); }
	"+"							{ return newToken(CSSTokenType.PLUS, yytext()); }
	"*"							{ return newToken(CSSTokenType.STAR, yytext()); }
	">"							{ return newToken(CSSTokenType.GREATER, yytext()); }
	"/"							{ return newToken(CSSTokenType.SLASH, yytext()); }
	"="							{ return newToken(CSSTokenType.EQUAL, yytext()); }
	"-"							{ return newToken(CSSTokenType.MINUS, yytext()); }

	"url("[^)]*")"				{ return newToken(CSSTokenType.URL, yytext()); }

	{identifier}				{ return newToken(CSSTokenType.IDENTIFIER, yytext()); }
}

.|\n	{ return newToken(CSSTokenType.ERROR, yytext()); }
