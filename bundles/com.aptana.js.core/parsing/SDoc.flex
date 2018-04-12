package com.aptana.js.internal.core.parsing.sdoc;

import java.io.Reader;
import java.io.StringReader;

import beaver.Symbol;
import beaver.Scanner;

import com.aptana.core.util.StringUtil;

%%

%class SDocFlexScanner
%extends Scanner
%type Symbol
%yylexthrow Scanner.Exception
%eofval{
	return newToken(Terminals.EOF, "end-of-file");
%eofval}
%unicode
%char

%{
	private int _offset;

	public SDocFlexScanner()
	{
		this((Reader) null);
	}

	private Symbol newToken(SDocTokenType type, Object value)
	{
		//System.out.println(type + ":~" + yytext() + "~");
		return newToken(type.getIndex(), value);
	}

	private Symbol newToken(short id, Object value)
	{
		int start = yychar + _offset;
		int end = start + yylength() - 1;

		return new Symbol(id, start, end, value);
	}

	public Symbol nextToken() throws java.io.IOException, Scanner.Exception
	{
		Symbol result;

		try
		{
			// get next token
			result = yylex();
		} 
		catch (Scanner.Exception e)
		{
			// create default token type
			String text = yytext();
			int end = yychar + text.length() - 1;

			result = new Symbol(SDocTokenType.EOF.getIndex(), yychar, end, text);
		}

		return result;
	}

	public void setOffset(int offset)
	{
		_offset = offset;
	}

	public void setSource(String source)
	{
		yyreset(new StringReader(source));
	}
%}

LineTerminator = \r|\n|\r\n
Whitespace = [ \t\f]

Identifier = ([:jletter:]|\$)([:jletterdigit:]|[$.])*

%state TYPES

%%

<YYINITIAL> {
	// comment prefix and other whitespace
	^[ \t]* "*" [ \t]* /[^/]	{ /* ignore */ }
	{Whitespace}+				{ /* ignore */ }
	{LineTerminator}+			{ /* ignore */ }

	// keywords
	"@advanced"			{ return newToken(SDocTokenType.ADVANCED, StringUtil.EMPTY); }
	"@alias"			{ return newToken(SDocTokenType.ALIAS, StringUtil.EMPTY); }
	"@author"			{ return newToken(SDocTokenType.AUTHOR, StringUtil.EMPTY); }
	"@classDescription"	{ return newToken(SDocTokenType.CLASS_DESCRIPTION, StringUtil.EMPTY); }
	"@constructor"		{ return newToken(SDocTokenType.CONSTRUCTOR, StringUtil.EMPTY); }
	"@example"			{ return newToken(SDocTokenType.EXAMPLE, StringUtil.EMPTY); }
	"@exception"		{ return newToken(SDocTokenType.EXCEPTION, StringUtil.EMPTY); }
	"@extends"			{ return newToken(SDocTokenType.EXTENDS, StringUtil.EMPTY); }
	"@internal"			{ return newToken(SDocTokenType.INTERNAL, StringUtil.EMPTY); }
	"@method"			{ return newToken(SDocTokenType.METHOD, StringUtil.EMPTY); }
	"@module"			{ return newToken(SDocTokenType.MODULE, StringUtil.EMPTY); }
	"@namespace"		{ return newToken(SDocTokenType.NAMESPACE, StringUtil.EMPTY); }
	"@overview"			{ return newToken(SDocTokenType.OVERVIEW, StringUtil.EMPTY); }
	"@param"			{ return newToken(SDocTokenType.PARAM, StringUtil.EMPTY); }
	"@private"			{ return newToken(SDocTokenType.PRIVATE, StringUtil.EMPTY); }
	"@property"			{ return newToken(SDocTokenType.PROPERTY, StringUtil.EMPTY); }
	"@return"			{ return newToken(SDocTokenType.RETURN, StringUtil.EMPTY); }
	"@see"				{ return newToken(SDocTokenType.SEE, StringUtil.EMPTY); }
	"@type"				{ return newToken(SDocTokenType.TYPE, StringUtil.EMPTY); }

	"@"[:letter:]*		{ return newToken(SDocTokenType.UNKNOWN, yytext()); }

	// operators and punctuators
	"#"					{ return newToken(SDocTokenType.POUND, StringUtil.EMPTY); }
	"["					{ return newToken(SDocTokenType.LBRACKET, StringUtil.EMPTY); }
	"]"					{ return newToken(SDocTokenType.RBRACKET, StringUtil.EMPTY); }
	"{"					{ yybegin(TYPES); return newToken(SDocTokenType.LCURLY, StringUtil.EMPTY); }
	"}"					{ return newToken(SDocTokenType.RCURLY, StringUtil.EMPTY); }
	"/**"				{ return newToken(SDocTokenType.START_DOCUMENTATION, StringUtil.EMPTY); }
	"*/"				{ return newToken(SDocTokenType.END_DOCUMENTATION, StringUtil.EMPTY); }

	// text
	[^ \t\r\n{\[\]#]+	{ return newToken(SDocTokenType.TEXT, yytext()); }
}

<TYPES> {
	// whitespace
	{Whitespace}+		{ /* ignore */ }
	{LineTerminator}+	{ /* ignore */ }

	// keywords
	Array			{ return newToken(SDocTokenType.ARRAY, StringUtil.EMPTY); }
	Function		{ return newToken(SDocTokenType.FUNCTION, StringUtil.EMPTY); }
	Class			{ return newToken(SDocTokenType.CLASS, StringUtil.EMPTY); }

	// identifiers
	{Identifier}	{ return newToken(SDocTokenType.IDENTIFIER, yytext()); }

	// operators and punctuation
	"("				{ return newToken(SDocTokenType.LPAREN, StringUtil.EMPTY); }
	")"				{ return newToken(SDocTokenType.RPAREN, StringUtil.EMPTY); }
	"{"				{ return newToken(SDocTokenType.LCURLY, StringUtil.EMPTY); }
	"}"				{ yybegin(YYINITIAL); return newToken(SDocTokenType.RCURLY, StringUtil.EMPTY); }
	"["				{ return newToken(SDocTokenType.LBRACKET, StringUtil.EMPTY); }
	"]"				{ return newToken(SDocTokenType.RBRACKET, StringUtil.EMPTY); }
	"<"				{ return newToken(SDocTokenType.LESS_THAN, StringUtil.EMPTY); }
	">"				{ return newToken(SDocTokenType.GREATER_THAN, StringUtil.EMPTY); }
	":"				{ return newToken(SDocTokenType.COLON, StringUtil.EMPTY); }
	","				{ return newToken(SDocTokenType.COMMA, StringUtil.EMPTY); }
	"|"				{ return newToken(SDocTokenType.PIPE, StringUtil.EMPTY); }
	"..."			{ return newToken(SDocTokenType.ELLIPSIS, StringUtil.EMPTY); }
	"->"			{ return newToken(SDocTokenType.ARROW, StringUtil.EMPTY); }
}

.|\n			{ return newToken(SDocTokenType.ERROR, yytext()); }
