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

import com.aptana.editor.js.parsing.lexer.JSTokenType;
import com.aptana.editor.js.parsing.JSTokenTypeSymbol;

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
                case STRING:
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
    "//" [^\r\n]*(\r?\n?)       {  return newToken(JSTokenType.SINGLELINE_COMMENT, "");  }
                        
    //Note: SDOC must be declared before multiline.
    
    "/**" ~"*/"         {  return newToken(JSTokenType.SDOC, "");  }
                        
    "/*" ~"*/"          {  return newToken(JSTokenType.MULTILINE_COMMENT, "");  }
                        
                        
                        

    // ---- numbers
    {Number}        { return newToken(JSTokenType.NUMBER, ""); }

    // strings
    '([^\\'\r\n]|\\[^])*'       { return newToken(JSTokenType.STRING_SINGLE, ""); }
    '([^\\'\r\n]|\\[^])*$       { return newToken(JSTokenType.STRING_SINGLE, ""); }

    
    \"([^\\\"\r\n]|\\[^])*\"    { return newToken(JSTokenType.STRING_DOUBLE, ""); }
    \"([^\\\"\r\n]|\\[^])*$     { return newToken(JSTokenType.STRING_DOUBLE, ""); }

    // identifiers
    {Identifier}    { return newToken(JSTokenType.IDENTIFIER, ""); }

    // operators
    ">>>="          { return newToken(JSTokenType.GREATER_GREATER_GREATER_EQUAL, ""); }
    ">>>"           { return newToken(JSTokenType.GREATER_GREATER_GREATER, ""); }

    "<<="           { return newToken(JSTokenType.LESS_LESS_EQUAL, ""); }
    "<<"            { return newToken(JSTokenType.LESS_LESS, ""); }
    "<="            { return newToken(JSTokenType.LESS_EQUAL, ""); }
    "<"             { return newToken(JSTokenType.LESS, ""); }

    ">>="           { return newToken(JSTokenType.GREATER_GREATER_EQUAL, ""); }
    ">>"            { return newToken(JSTokenType.GREATER_GREATER, ""); }
    ">="            { return newToken(JSTokenType.GREATER_EQUAL, ""); }
    ">"             { return newToken(JSTokenType.GREATER, ""); }

    "==="           { return newToken(JSTokenType.EQUAL_EQUAL_EQUAL, ""); }
    "=="            { return newToken(JSTokenType.EQUAL_EQUAL, ""); }
    "="             { return newToken(JSTokenType.EQUAL, ""); }

    "!=="           { return newToken(JSTokenType.EXCLAMATION_EQUAL_EQUAL, ""); }
    "!="            { return newToken(JSTokenType.EXCLAMATION_EQUAL, ""); }
    "!"             { return newToken(JSTokenType.EXCLAMATION, ""); }

    "&&"            { return newToken(JSTokenType.AMPERSAND_AMPERSAND, ""); }
    "&="            { return newToken(JSTokenType.AMPERSAND_EQUAL, ""); }
    "&"             { return newToken(JSTokenType.AMPERSAND, ""); }

    "||"            { return newToken(JSTokenType.PIPE_PIPE, ""); }
    "|="            { return newToken(JSTokenType.PIPE_EQUAL, ""); }
    "|"             { return newToken(JSTokenType.PIPE, ""); }

    "*="            { return newToken(JSTokenType.STAR_EQUAL, ""); }
    "*"             { return newToken(JSTokenType.STAR, ""); }

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

    "%="            { return newToken(JSTokenType.PERCENT_EQUAL, ""); }
    "%"             { return newToken(JSTokenType.PERCENT, ""); }

    "--"            { return newToken(JSTokenType.MINUS_MINUS, ""); }
    "-="            { return newToken(JSTokenType.MINUS_EQUAL, ""); }
    "-"             { return newToken(JSTokenType.MINUS, ""); }

    "++"            { return newToken(JSTokenType.PLUS_PLUS, ""); }
    "+="            { return newToken(JSTokenType.PLUS_EQUAL, ""); }
    "+"             { return newToken(JSTokenType.PLUS, ""); }

    "^="            { return newToken(JSTokenType.CARET_EQUAL, ""); }
    "^"             { return newToken(JSTokenType.CARET, ""); }
 
    "?"             { return newToken(JSTokenType.QUESTION, ""); }
    "~"             { return newToken(JSTokenType.TILDE, ""); }
    ";"             { return newToken(JSTokenType.SEMICOLON, ""); }
    "("             { return newToken(JSTokenType.LPAREN, ""); }
    ")"             { return newToken(JSTokenType.RPAREN, ""); }
    "["             { return newToken(JSTokenType.LBRACKET, ""); }
    "]"             { return newToken(JSTokenType.RBRACKET, ""); }
    "{"             { return newToken(JSTokenType.LCURLY, ""); }
    "}"             { return newToken(JSTokenType.RCURLY, ""); }
    ","             { return newToken(JSTokenType.COMMA, ""); }
    ":"             { return newToken(JSTokenType.COLON, ""); }
    "."             { return newToken(JSTokenType.DOT, ""); }
}

<DIVISION> {
    "/="            {
                        yybegin(YYINITIAL);
                        return newToken(JSTokenType.FORWARD_SLASH_EQUAL, "");
                    }
    "/"             {
                        yybegin(YYINITIAL);
                        return newToken(JSTokenType.FORWARD_SLASH, "");
                    }
}

<REGEX> {
    {Regex}         {
                        yybegin(YYINITIAL);
                        return newToken(JSTokenType.REGEX, "");
                    }
    "/="            {
                        yybegin(YYINITIAL);
                        return newToken(JSTokenType.FORWARD_SLASH_EQUAL, "");
                    }
    "/"             {
                        yybegin(YYINITIAL);
                        return newToken(JSTokenType.FORWARD_SLASH, "");
                    }
}


// \"[^\"\r\n\ ]+|.

[\"']\\?|.  {


                // make sure we reset the lexer state for next (potential) scan
                yybegin(YYINITIAL);
                throw new Scanner.Exception("Unexpected character '" + "" + "' around offset " + yychar);
            }
