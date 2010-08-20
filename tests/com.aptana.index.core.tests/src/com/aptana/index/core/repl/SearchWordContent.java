package com.aptana.index.core.repl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;
import com.aptana.internal.index.core.DiskIndex;

public class SearchWordContent extends GenericCommand
{
	private boolean showIndexName;

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
			DiskIndex current = repl.getCurrentIndex();

			if (current != null)
			{
				this.outputWords(repl, name, current);
			}
			else
			{
				for (DiskIndex index : repl.getIndexes())
				{
					this.outputWords(repl, name, index);
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

		aliases.add("sw");

		return aliases;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.repl.GenericCommand#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return "Find words containing the specified text. Use * to return all words. Current index and category are honored when set; otherwise, queries all indexes and/or categories";
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.repl.ICommand#getName()
	 */
	@Override
	public String getName()
	{
		return "search-word";
	}

	/**
	 * outputForCategory
	 * 
	 * @param name
	 * @param index
	 * @param category
	 */
	private void outputForCategory(String name, DiskIndex index, String category)
	{
		String[] categories = new String[] { category };
		String key = null;
		int rule = SearchPattern.PREFIX_MATCH;

		try
		{
			Map<String, QueryResult> results = index.addQueryResults(categories, key, rule, null);
			List<String> words = new ArrayList<String>();
			Collection<QueryResult> values = results.values();

			for (QueryResult result : values)
			{
				String word = result.getWord();

				if ("*".equals(name) || word.contains(name))
				{
					words.add(word);
				}
			}

			if (words.isEmpty() == false)
			{
				if (this.showIndexName)
				{
					System.out.println(index.indexFile.getName());
					this.showIndexName = false;
				}

				System.out.println("  " + category);

				for (String word : words)
				{
					System.out.println("    " + word);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * outputWords
	 * 
	 * @param name
	 * @param index
	 */
	private void outputWords(IndexREPL repl, String name, DiskIndex index)
	{
		this.showIndexName = true;
		String current = repl.getCurrentCategory();

		if (current != null && current.length() > 0)
		{
			this.outputForCategory(name, index, current);
		}
		else
		{
			for (String category : index.getCategories())
			{
				this.outputForCategory(name, index, category);
			}
		}
	}
}
