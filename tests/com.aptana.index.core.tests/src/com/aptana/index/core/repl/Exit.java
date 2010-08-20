package com.aptana.index.core.repl;

import java.util.ArrayList;
import java.util.List;

public class Exit extends GenericCommand
{
	
	@Override
	public boolean execute(IndexREPL repl, String[] args)
	{
		System.out.println("Exiting...");
		
		return false;
	}

	/* (non-Javadoc)
	 * @see com.aptana.index.core.repl.GenericCommand#getAlaises()
	 */
	@Override
	public List<String> getAliases()
	{
		List<String> result = new ArrayList<String>();
		
		result.add("bye");
		result.add("quit");
		result.add("e");
		result.add("b");
		result.add("q");
		
		return result;
	}

	@Override
	public String getDescription()
	{
		return "Exit this application";
	}

	@Override
	public String getName()
	{
		return "exit";
	}
}
