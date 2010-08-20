package com.aptana.index.core.repl;

import java.util.Collections;
import java.util.List;

public abstract class GenericCommand implements ICommand
{
	@Override
	public boolean execute(IndexREPL repl, String[] args)
	{
		return true;
	}

	@Override
	public List<String> getAliases()
	{
		return Collections.emptyList();
	}

	@Override
	public String getDescription()
	{
		return "No description";
	}
}
