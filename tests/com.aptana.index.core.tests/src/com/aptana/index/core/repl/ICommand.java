package com.aptana.index.core.repl;

import java.util.List;

public interface ICommand
{
	/**
	 * execute
	 * 
	 * @param repl
	 * @param args
	 * @return
	 */
	boolean execute(IREPL repl, String[] args);

	/**
	 * getAliases
	 * 
	 * @return
	 */
	List<String> getAliases();
	
	/**
	 * getDescription
	 * 
	 * @return
	 */
	String getDescription();

	/**
	 * getName
	 * 
	 * @return
	 */
	String getName();
}
