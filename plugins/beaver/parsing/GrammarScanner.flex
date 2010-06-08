/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of Beaver Parser Generator.                       *
 * Copyright (C) 2003,2005 Alexander Demenchuk <alder@softanvil.com>.  *
 * All rights reserved.                                                *
 * See the file "LICENSE" for the terms and conditions for copying,    *
 * distribution and modification of Beaver.                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package beaver.spec.parser;

import beaver.Symbol;
import beaver.Scanner;
import beaver.spec.parser.GrammarParser.Terminals;

%%

%class GrammarScanner
%public
%extends Scanner
%{
	private int token_line;
	private int token_column;

	private String matched_text;

	private Symbol newSymbol(short id)
	{
		return new Symbol(id, yyline + 1, yycolumn + 1, yylength(), yytext());
	}

	private Symbol newSymbol(short id, Object value)
	{
		return new Symbol(id, yyline + 1, yycolumn + 1, yylength(), value);
	}
%}
%unicode
%line
%column
%function nextToken
%yylexthrow Scanner.Exception
%type Symbol
%eofval{
	return newSymbol(Terminals.EOF, "end-of-file");
%eofval}

LineTerminator = \r | \n | \r\n
WhiteSpace     = {LineTerminator} | [ \t\f]
Identifier     = [:letter:] ([:letter:] | [:digit:] | "_")*

TxtChar        = [^\r\n\"]
AnyChar        = . | \n

%xstate CODE TEXT MLN_COMMENT EOL_COMMENT
%xstate CODE_END EOF_CODE EOF_MLN_COMMENT EOF_EOL_COMMENT

%%

{WhiteSpace}        { /* ignore */ }

<YYINITIAL> {
	"%header"       { return newSymbol(Terminals.HEADER    ); }
	"%package"      { return newSymbol(Terminals.PACKAGE   ); }
	"%import"       { return newSymbol(Terminals.IMPORT    ); }
	"%class"        { return newSymbol(Terminals.CLASS     ); }
	"%implements"   { return newSymbol(Terminals.IMPLEMENTS); }
	"%embed"        { return newSymbol(Terminals.EMBED     ); }
	"%init"         { return newSymbol(Terminals.INIT      ); }
	"%goal"         { return newSymbol(Terminals.GOAL      ); }

	"%terminals"    { return newSymbol(Terminals.TERMINALS ); }
	"%typeof"       { return newSymbol(Terminals.TYPEOF    ); }
	"%left"         { return newSymbol(Terminals.LEFT      ); }
	"%right"        { return newSymbol(Terminals.RIGHT     ); }
	"%nonassoc"     { return newSymbol(Terminals.NONASSOC  ); }

	","             { return newSymbol(Terminals.COMMA     ); }
	"="             { return newSymbol(Terminals.IS        ); }
	";"             { return newSymbol(Terminals.SEMI      ); }

	"@"             { return newSymbol(Terminals.AT        ); }
	"."             { return newSymbol(Terminals.DOT       ); }
	"|"             { return newSymbol(Terminals.BAR       ); }

	"?"             { return newSymbol(Terminals.QUESTION  ); }
	"+"             { return newSymbol(Terminals.PLUS      ); }
	"*"             { return newSymbol(Terminals.STAR      ); }

	"{:"            { token_line = yyline; token_column = yycolumn; yybegin(CODE); }
	"/*"            { token_line = yyline; token_column = yycolumn; yybegin(MLN_COMMENT); }
	"//"            { token_line = yyline; token_column = yycolumn; yybegin(EOL_COMMENT); }
	\"              { token_line = yyline; token_column = yycolumn; yybegin(TEXT); }

	{Identifier}    { return newSymbol(Terminals.IDENT, yytext()); }
}

<CODE> {
	~ ":}"          { yypushback(2); matched_text = yytext(); yybegin(CODE_END); }
	{AnyChar}?      { yybegin(EOF_CODE); }
}

<CODE_END> {
	":}"            { yybegin(YYINITIAL); return new Symbol(Terminals.CODE, Symbol.makePosition(token_line + 1, token_column + 1), Symbol.makePosition(yyline + 1, yycolumn + 3), matched_text); }
	{AnyChar}?      { yybegin(EOF_CODE); }
}

<EOF_CODE> {
	{AnyChar}*      { throw new Scanner.Exception(token_line + 1, token_column + 1, "end of file in Java code"); }
}

<TEXT> {
	{TxtChar}+      { matched_text = yytext(); }
	\"              { yybegin(YYINITIAL); String txt = matched_text; matched_text = null; return new Symbol(Terminals.TEXT, Symbol.makePosition(token_line + 1, token_column + 1), Symbol.makePosition(yyline + 1, yycolumn + 1), txt); }
	{AnyChar}?      { yybegin(YYINITIAL); matched_text = null; throw new Scanner.Exception(token_line + 1, token_column + 1, "unterminated string"); }
}

<MLN_COMMENT> {
	~ "*/"          { yybegin(YYINITIAL); }
	{AnyChar}?      { yybegin(EOF_MLN_COMMENT); }
}

<EOF_MLN_COMMENT> {
	{AnyChar}*      { throw new Scanner.Exception(token_line + 1, token_column + 1, "end of file in comment"); }
}

<EOL_COMMENT> {
	~ {LineTerminator}
	                { yybegin(YYINITIAL); }
	                /* if Lineterminator has not been found, then this comment is on the last, unterminated, line */
	{AnyChar}?      { yybegin(EOF_EOL_COMMENT); }
}

<EOF_EOL_COMMENT> {
	{AnyChar}*      { yybegin(YYINITIAL); }
}

{AnyChar}           { throw new Scanner.Exception(yyline + 1, yycolumn + 1, "unrecognized character '" + yytext() + "'"); }
