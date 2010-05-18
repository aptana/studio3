package com.aptana.parsing;

import java.io.IOException;

import beaver.Symbol;
import beaver.Parser.Events;
import beaver.Parser.TokenStream;

public interface IRecoveryStrategy
{
	/**
	 * recover
	 * 
	 * @param token
	 * @param in
	 * @param report
	 * @return
	 */
	boolean recover(Symbol token, TokenStream in, Symbol lastSymbol, Events report) throws IOException;
}
