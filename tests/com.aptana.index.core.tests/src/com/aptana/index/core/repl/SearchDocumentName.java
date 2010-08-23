package com.aptana.index.core.repl;

import java.util.ArrayList;
import java.util.List;

import com.aptana.internal.index.core.DiskIndex;

public class SearchDocumentName extends GenericCommand
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.repl.GenericCommand#execute(com.aptana.index.core.repl.IndexREPL, java.lang.String[])
	 */
	@Override
	public boolean execute(IREPL repl, String[] args)
	{
		if (args != null)
		{
			String name = (args.length > 1) ? args[1] : "*";
			DiskIndex current = repl.getCurrentIndex();

			if (current != null)
			{
				this.outputDocuments(name, current);
			}
			else
			{
				for (DiskIndex index : repl.getIndexes())
				{
					this.outputDocuments(name, index);
				}
			}
		}

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

		aliases.add("sd");

		return aliases;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.repl.GenericCommand#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return "Find documents containing the specified text. Use * to return all documents. Current index is honored when set; otherwise, queries all indexes";
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.repl.ICommand#getName()
	 */
	@Override
	public String getName()
	{
		return "search-document";
	}

	/**
	 * outptDocuments
	 * 
	 * @param name
	 * @param index
	 */
	private void outputDocuments(String name, DiskIndex index)
	{
		List<String> documents = new ArrayList<String>();

		for (String document : index.getDocuments())
		{
			if ("*".equals(name) || document.contains(name))
			{
				documents.add(document);
			}
		}

		if (documents.isEmpty() == false)
		{
			System.out.println(index.indexFile.getName());

			for (String document : documents)
			{
				System.out.println("  " + document);
			}
		}
	}
}
