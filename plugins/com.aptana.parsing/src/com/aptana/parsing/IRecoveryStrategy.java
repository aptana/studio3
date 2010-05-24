package com.aptana.parsing;

import java.io.IOException;

import beaver.Symbol;
import beaver.Parser.TokenStream;

public interface IRecoveryStrategy
{
	/**
	 * recover
	 * 
	 * @param parser
	 * @param token
	 * @param in
	 * @return
	 */
	boolean recover(IParser parser, Symbol token, TokenStream in) throws IOException;
}
