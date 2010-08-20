package com.aptana.index.core.repl;

import java.util.ArrayList;
import java.util.List;

public class ReloadIndexes extends GenericCommand
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.repl.GenericCommand#execute(com.aptana.index.core.repl.IndexREPL, java.lang.String[])
	 */
	@Override
	public boolean execute(IndexREPL repl, String[] args)
	{
		repl.loadDiskIndexes();
		
		System.out.println("Indexes reloaded.");

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.repl.GenericCommand#getAlaises()
	 */
	@Override
	public List<String> getAliases()
	{
		List<String> aliases = new ArrayList<String>();

		aliases.add("r");

		return aliases;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.repl.GenericCommand#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return "Reload all indexes from disk";
	}

	@Override
	public String getName()
	{
		return "reload";
	}
}
