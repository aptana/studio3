package com.aptana.index.core.repl;

import java.util.ArrayList;
import java.util.List;

import com.aptana.internal.index.core.DiskIndex;

public class SearchIndexName extends GenericCommand
{
	/* (non-Javadoc)
	 * @see com.aptana.index.core.repl.GenericCommand#getAlaises()
	 */
	@Override
	public List<String> getAliases()
	{
		List<String> aliases = new ArrayList<String>();
		
		aliases.add("si");
		
		return aliases;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.repl.GenericCommand#execute(com.aptana.index.core.repl.IndexREPL, java.lang.String[])
	 */
	@Override
	public boolean execute(IndexREPL repl, String[] args)
	{
		if (args != null)
		{
			String name = (args.length > 1) ? args[1] : "*";

			for (DiskIndex index : repl.getIndexes())
			{
				String indexName = index.indexFile.getName();

				if ("*".equals(name) || indexName.contains(name))
				{
					System.out.println(indexName);
				}
			}
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.repl.GenericCommand#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return "Return a list of indexes that match the specified pattern";
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.repl.ICommand#getName()
	 */
	@Override
	public String getName()
	{
		return "search-index";
	}
}
