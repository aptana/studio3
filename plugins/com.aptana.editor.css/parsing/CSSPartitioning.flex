// $codepro.audit.disable
/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing;

import java.io.Reader;
import java.io.StringReader;

import beaver.Symbol;
import beaver.Scanner;

import com.aptana.core.util.StringUtil;
import com.aptana.css.core.parsing.CSSTokenTypeSymbol;
import com.aptana.css.core.parsing.CSSTokenType;
import com.aptana.editor.common.parsing.ForceReturnException;

@SuppressWarnings({"unused", "nls"})

%%

%public
%class CSSPartitionFlexScanner
%extends Scanner
%type Symbol
%yylexthrow Scanner.Exception
%eofval{
	return newToken(CSSTokenType.EOF, "end-of-file");
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

	public CSSPartitionFlexScanner()
	{
		this((Reader) null);
	}

	public Symbol getLastToken()
	{
		return _lastToken;
	}


	private Symbol newToken(CSSTokenType type, Object value)
	{
		return new CSSTokenTypeSymbol(type, yychar, yychar + yylength() - 1, value);
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

                _lastToken = new CSSTokenTypeSymbol((CSSTokenType) forceReturnException.type, start, start + zzEndRead
                        - 1, StringUtil.EMPTY);
                yyclose();
            }
            else
            {
                int end = yychar + yylength() - 1;
                _lastToken = new CSSTokenTypeSymbol(CSSTokenType.EOF, yychar, end, StringUtil.EMPTY);
            }
		}

		return _lastToken;
	}


	public void setSource(String source)
	{
		yyreset(new StringReader(source));

		// clear last token
		_lastToken = null;
	}
%}

%%

<YYINITIAL> {
	"/*" ~"*/"		               { return newToken(CSSTokenType.COMMENT, StringUtil.EMPTY); }
	                                
	                                
    "/"                            {  
                        
                                        char c = '\0';
                                        try{
                                            c = yycharat(1);
                                        }catch(RuntimeException e){}
                                        
                                        // If we actually have a /* but didn't match it, this means we have a comment
                                        // until the end of the file.
                                        if(c == '*'){
                                            throw new ForceReturnException(0, 0, "Forcing COMMENT to end of document.", CSSTokenType.COMMENT);
                                            
                                        }
                                   }

	'([^\\'\r\n]|\\[^])*'		   { return newToken(CSSTokenType.SINGLE_QUOTED_STRING, StringUtil.EMPTY); }
	'([^\\'\r\n]|\\[^])*(\n)	   { return newToken(CSSTokenType.SINGLE_QUOTED_STRING, StringUtil.EMPTY); }
	
	\"([^\\\"\r\n]|\\[^])*\"	   { return newToken(CSSTokenType.DOUBLE_QUOTED_STRING, StringUtil.EMPTY); }
	\"([^\\\"\r\n]|\\[^])*(\n)	   { return newToken(CSSTokenType.DOUBLE_QUOTED_STRING, StringUtil.EMPTY); }
	
}

/* error fallback */
.|\n { /* ignore */ }