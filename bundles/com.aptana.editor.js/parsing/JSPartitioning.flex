// $codepro.audit.disable
/**
 * Aptana Studio
 * Copyright (c) 2005-2017 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text;

import java.io.Reader;
import java.io.StringReader;

import beaver.Symbol;
import beaver.Scanner;

import com.aptana.core.util.StringUtil;
import com.aptana.js.core.parsing.JSTokenType;
import com.aptana.editor.common.parsing.ForceReturnException;

%%

%public
%class JSPartitioningFlexScanner
%extends Scanner
%type Symbol
%yylexthrow Scanner.Exception
%eofval{
    return newToken(JSTokenType.EOF, "end-of-file");
%eofval}
%unicode
%char

//%switch
//%table
//%pack

%{
    // last token used for look behind. Also needed when implementing the ITokenScanner interface
    private Symbol _lastToken;

    public JSPartitioningFlexScanner()
    {
        this((Reader) null);
    }

    public Symbol getLastToken()
    {
        return _lastToken;
    }

    private Symbol newToken(JSTokenType id, Object value)
    {
        return new JSTokenTypeSymbol(id, yychar, yychar + yylength() - 1, value);
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
            if (e instanceof ForceReturnException)
            {
                //Ok, we have a 'forced return', meaning we should consume everything until the end
                //of the file and return the token that was forced.
                ForceReturnException forceReturnException = (ForceReturnException) e;
                int start = yychar;
                boolean eof;
                do
                {
                    eof = zzRefill();
                }
                while (!eof);

                _lastToken = new JSTokenTypeSymbol((JSTokenType) forceReturnException.type, start, start + zzEndRead
                        - 1, "");
                yyclose();
            }
            else
            {
                int end = yychar + yylength() - 1;
                _lastToken = new JSTokenTypeSymbol(JSTokenType.EOF, yychar, end, "");
            }
        }

        return _lastToken;
    }

    private boolean isValidDivisionStart()
    {
        if (_lastToken != null)
        {
            switch (((JSTokenTypeSymbol) _lastToken).token)
            {
                case IDENTIFIER:
                case NUMBER:
                case REGEX:
                case STRING_SINGLE:
                case STRING_DOUBLE:
                case RPAREN:
                case PLUS_PLUS:
                case MINUS_MINUS:
                case RBRACKET:
                case RCURLY:
                case FALSE:
                case NULL:
                case THIS:
                case TRUE:
                    return true;
            }
        }

        return false;
    }


    public void setSource(String source)
    {
        yyreset(new StringReader(source));

        // clear last token
        _lastToken = null;
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


// Templates
TemplateMiddle = "}" ~"${"
TemplateTail = "}" ~"`"
TemplateHead = "`" ~"${"
NoSubstitutionTemplate = "`" ~"`"

CharClass = "[" ([^\]\\\r\n]|\\[^\r\n])* "]"
Character = ([^\[\\\/\r\n]|\\[^\r\n])+
Regex = "/" ({CharClass}|{Character})+ "/" [a-z]*

//Note: tried creating more lexer states for multiline-comments and this made the parser MUCH slower
//so, reverted that change.
%state DIVISION, REGEX

%%

{Whitespace}+   { /* ignore */ }

<YYINITIAL> {
    // ---- comments
    
    
    //Note: we want to add the \r\n as being in this context too...
    "//" [^\r\n]*(\r?\n?)       {  return newToken(JSTokenType.SINGLELINE_COMMENT, StringUtil.EMPTY);  }
                        
    //Note: SDOC must be declared before multiline.
    
    "/**" ~"*/"         {  return newToken(JSTokenType.SDOC, StringUtil.EMPTY);  }
                        
    "/*" ~"*/"          {  return newToken(JSTokenType.MULTILINE_COMMENT, StringUtil.EMPTY);  }
                        
                        
                        

    // ---- numbers
    {Number}        { return newToken(JSTokenType.NUMBER, StringUtil.EMPTY); }

    // strings
    '([^\\'\r\n]|\\[^])*'       { return newToken(JSTokenType.STRING_SINGLE, StringUtil.EMPTY); }
    '([^\\'\r\n]|\\[^])*$       { return newToken(JSTokenType.STRING_SINGLE, StringUtil.EMPTY); }

    
    \"([^\\\"\r\n]|\\[^])*\"    { return newToken(JSTokenType.STRING_DOUBLE, StringUtil.EMPTY); }
    \"([^\\\"\r\n]|\\[^])*$     { return newToken(JSTokenType.STRING_DOUBLE, StringUtil.EMPTY); }

	// keywords
	"break"			{ return newToken(JSTokenType.BREAK, StringUtil.EMPTY); }
	"case"			{ return newToken(JSTokenType.CASE, StringUtil.EMPTY); }
	"catch"			{ return newToken(JSTokenType.CATCH, StringUtil.EMPTY); }
	"continue"		{ return newToken(JSTokenType.CONTINUE, StringUtil.EMPTY); }
	"default"		{ return newToken(JSTokenType.DEFAULT, StringUtil.EMPTY); }
	"delete"		{ return newToken(JSTokenType.DELETE, StringUtil.EMPTY); }
	"do"			{ return newToken(JSTokenType.DO, StringUtil.EMPTY); }
	"else"			{ return newToken(JSTokenType.ELSE, StringUtil.EMPTY); }
	"false"			{ return newToken(JSTokenType.FALSE, StringUtil.EMPTY); }
	"finally"		{ return newToken(JSTokenType.FINALLY, StringUtil.EMPTY); }
	"for"			{ return newToken(JSTokenType.FOR, StringUtil.EMPTY); }
	"function"		{ return newToken(JSTokenType.FUNCTION, StringUtil.EMPTY); }
	"if"			{ return newToken(JSTokenType.IF, StringUtil.EMPTY); }
	"instanceof"	{ return newToken(JSTokenType.INSTANCEOF, StringUtil.EMPTY); }
	"in"			{ return newToken(JSTokenType.IN, StringUtil.EMPTY); }
	"new"			{ return newToken(JSTokenType.NEW, StringUtil.EMPTY); }
	"null"			{ return newToken(JSTokenType.NULL, StringUtil.EMPTY); }
	"return"		{ return newToken(JSTokenType.RETURN, StringUtil.EMPTY); }
	"switch"		{ return newToken(JSTokenType.SWITCH, StringUtil.EMPTY); }
	"this"			{ return newToken(JSTokenType.THIS, StringUtil.EMPTY); }
	"throw"			{ return newToken(JSTokenType.THROW, StringUtil.EMPTY); }
	"true"			{ return newToken(JSTokenType.TRUE, StringUtil.EMPTY); }
	"try"			{ return newToken(JSTokenType.TRY, StringUtil.EMPTY); }
	"typeof"		{ return newToken(JSTokenType.TYPEOF, StringUtil.EMPTY); }
	"var"			{ return newToken(JSTokenType.VAR, StringUtil.EMPTY); }
	"void"			{ return newToken(JSTokenType.VOID, StringUtil.EMPTY); }
	"while"			{ return newToken(JSTokenType.WHILE, StringUtil.EMPTY); }
	"with"			{ return newToken(JSTokenType.WITH, StringUtil.EMPTY); }
	"get"			{ return newToken(JSTokenType.GET, StringUtil.EMPTY); }
	"set"			{ return newToken(JSTokenType.SET, StringUtil.EMPTY); }
	
	// ES6
	"class"			{ return newToken(JSTokenType.CLASS, StringUtil.EMPTY); }
	"const"			{ return newToken(JSTokenType.CONST, StringUtil.EMPTY); }
	"export"		{ return newToken(JSTokenType.EXPORT, StringUtil.EMPTY); }
	"extends"		{ return newToken(JSTokenType.EXTENDS, StringUtil.EMPTY); }
	"import"		{ return newToken(JSTokenType.IMPORT, StringUtil.EMPTY); }
	"let"			{ return newToken(JSTokenType.LET, StringUtil.EMPTY); }
	"of"			{ return newToken(JSTokenType.OF, StringUtil.EMPTY); }
	"static"		{ return newToken(JSTokenType.STATIC, StringUtil.EMPTY); }
	"super"			{ return newToken(JSTokenType.SUPER, StringUtil.EMPTY); }
	"target"		{ return newToken(JSTokenType.TARGET, StringUtil.EMPTY); }
	"yield"			{ return newToken(JSTokenType.YIELD, StringUtil.EMPTY); }
	
	// Future Use Reserved Words
	"await"			{ return newToken(JSTokenType.AWAIT, StringUtil.EMPTY); }
	
	// ES6 Templates
	{TemplateHead}				{ return newToken(JSTokenType.TEMPLATE_HEAD, StringUtil.EMPTY); }
	// FIXME The lexer prefers longest sequence, so will grab _all_ of the template literal together in preference to the head/middle/tail!
	{NoSubstitutionTemplate}	{ return newToken(JSTokenType.NO_SUB_TEMPLATE, StringUtil.EMPTY); }
	{TemplateMiddle}			{ return newToken(JSTokenType.TEMPLATE_MIDDLE, StringUtil.EMPTY); }
	{TemplateTail}				{ return newToken(JSTokenType.TEMPLATE_TAIL, StringUtil.EMPTY); }

    // identifiers
    {Identifier}    { return newToken(JSTokenType.IDENTIFIER, StringUtil.EMPTY); }

    // operators
    ">>>="          { return newToken(JSTokenType.GREATER_GREATER_GREATER_EQUAL, StringUtil.EMPTY); }
    ">>>"           { return newToken(JSTokenType.GREATER_GREATER_GREATER, StringUtil.EMPTY); }

    "<<="           { return newToken(JSTokenType.LESS_LESS_EQUAL, StringUtil.EMPTY); }
    "<<"            { return newToken(JSTokenType.LESS_LESS, StringUtil.EMPTY); }
    "<="            { return newToken(JSTokenType.LESS_EQUAL, StringUtil.EMPTY); }
    "<"             { return newToken(JSTokenType.LESS, StringUtil.EMPTY); }

    ">>="           { return newToken(JSTokenType.GREATER_GREATER_EQUAL, StringUtil.EMPTY); }
    ">>"            { return newToken(JSTokenType.GREATER_GREATER, StringUtil.EMPTY); }
    ">="            { return newToken(JSTokenType.GREATER_EQUAL, StringUtil.EMPTY); }
    ">"             { return newToken(JSTokenType.GREATER, StringUtil.EMPTY); }

    "==="           { return newToken(JSTokenType.EQUAL_EQUAL_EQUAL, StringUtil.EMPTY); }
    "=="            { return newToken(JSTokenType.EQUAL_EQUAL, StringUtil.EMPTY); }
    "="             { return newToken(JSTokenType.EQUAL, StringUtil.EMPTY); }

    "!=="           { return newToken(JSTokenType.EXCLAMATION_EQUAL_EQUAL, StringUtil.EMPTY); }
    "!="            { return newToken(JSTokenType.EXCLAMATION_EQUAL, StringUtil.EMPTY); }
    "!"             { return newToken(JSTokenType.EXCLAMATION, StringUtil.EMPTY); }

    "&&"            { return newToken(JSTokenType.AMPERSAND_AMPERSAND, StringUtil.EMPTY); }
    "&="            { return newToken(JSTokenType.AMPERSAND_EQUAL, StringUtil.EMPTY); }
    "&"             { return newToken(JSTokenType.AMPERSAND, StringUtil.EMPTY); }

    "||"            { return newToken(JSTokenType.PIPE_PIPE, StringUtil.EMPTY); }
    "|="            { return newToken(JSTokenType.PIPE_EQUAL, StringUtil.EMPTY); }
    "|"             { return newToken(JSTokenType.PIPE, StringUtil.EMPTY); }

    "*="            { return newToken(JSTokenType.STAR_EQUAL, StringUtil.EMPTY); }
    "*"             { return newToken(JSTokenType.STAR, StringUtil.EMPTY); }

    "/"             {
                        
                        char c = '\0';
                        char c2 = '\0';
                        try{
                            c = yycharat(1);
                        }catch(RuntimeException e){}
                        
                        try{
                            c2 = yycharat(2);
                        }catch(RuntimeException e){}
                        
                        // If we actually have a /* but didn't match it, this means we have a comment
                        // until the end of the file.
                        if(c == '*'){
                            if(c2 == '*')
                            {
                                throw new ForceReturnException(0, 0, "Forcing SDOC to end of document.", JSTokenType.SDOC);
                            }
                            else
                            {
                                throw new ForceReturnException(0, 0, "Forcing MULTILINE_COMMENT to end of document.", JSTokenType.MULTILINE_COMMENT);
                            }
                            
                        }else{
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
                        
                    }

    "%="            { return newToken(JSTokenType.PERCENT_EQUAL, StringUtil.EMPTY); }
    "%"             { return newToken(JSTokenType.PERCENT, StringUtil.EMPTY); }

    "--"            { return newToken(JSTokenType.MINUS_MINUS, StringUtil.EMPTY); }
    "-="            { return newToken(JSTokenType.MINUS_EQUAL, StringUtil.EMPTY); }
    "-"             { return newToken(JSTokenType.MINUS, StringUtil.EMPTY); }

    "++"            { return newToken(JSTokenType.PLUS_PLUS, StringUtil.EMPTY); }
    "+="            { return newToken(JSTokenType.PLUS_EQUAL, StringUtil.EMPTY); }
    "+"             { return newToken(JSTokenType.PLUS, StringUtil.EMPTY); }

    "^="            { return newToken(JSTokenType.CARET_EQUAL, StringUtil.EMPTY); }
    "^"             { return newToken(JSTokenType.CARET, StringUtil.EMPTY); }
 
    "?"             { return newToken(JSTokenType.QUESTION, StringUtil.EMPTY); }
    "~"             { return newToken(JSTokenType.TILDE, StringUtil.EMPTY); }
    ";"             { return newToken(JSTokenType.SEMICOLON, StringUtil.EMPTY); }
    "("             { return newToken(JSTokenType.LPAREN, StringUtil.EMPTY); }
    ")"             { return newToken(JSTokenType.RPAREN, StringUtil.EMPTY); }
    "["             { return newToken(JSTokenType.LBRACKET, StringUtil.EMPTY); }
    "]"             { return newToken(JSTokenType.RBRACKET, StringUtil.EMPTY); }
    "{"             { return newToken(JSTokenType.LCURLY, StringUtil.EMPTY); }
    "}"             { return newToken(JSTokenType.RCURLY, StringUtil.EMPTY); }
    ","             { return newToken(JSTokenType.COMMA, StringUtil.EMPTY); }
    ":"             { return newToken(JSTokenType.COLON, StringUtil.EMPTY); }
    "."             { return newToken(JSTokenType.DOT, StringUtil.EMPTY); }
}

<DIVISION> {
    "/="            {
                        yybegin(YYINITIAL);
                        return newToken(JSTokenType.FORWARD_SLASH_EQUAL, StringUtil.EMPTY);
                    }
    "/"             {
                        yybegin(YYINITIAL);
                        return newToken(JSTokenType.FORWARD_SLASH, StringUtil.EMPTY);
                    }
}

<REGEX> {
    {Regex}         {
                        yybegin(YYINITIAL);
                        return newToken(JSTokenType.REGEX, StringUtil.EMPTY);
                    }
    "/="            {
                        yybegin(YYINITIAL);
                        return newToken(JSTokenType.FORWARD_SLASH_EQUAL, StringUtil.EMPTY);
                    }
    "/"             {
                        yybegin(YYINITIAL);
                        return newToken(JSTokenType.FORWARD_SLASH, StringUtil.EMPTY);
                    }
}


// \"[^\"\r\n\ ]+|.

[\"']\\?|.  {


                // make sure we reset the lexer state for next (potential) scan
                yybegin(YYINITIAL);
                throw new Scanner.Exception("Unexpected character '" + "" + "' around offset " + yychar);
            }
