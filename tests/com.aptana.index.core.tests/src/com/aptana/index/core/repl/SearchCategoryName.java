package com.aptana.index.core.repl;

import java.util.ArrayList;
import java.util.List;

import com.aptana.internal.index.core.DiskIndex;

public class SearchCategoryName extends GenericCommand
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
				this.outputCategories(name, current);
			}
			else
			{
				for (DiskIndex index : repl.getIndexes())
				{
					this.outputCategories(name, index);
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

		aliases.add("sc");

		return aliases;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.repl.GenericCommand#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return "Find categories containing the specified text. Use * to return all categories. Current index is honored when set; otherwise, queries all indexes";
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.repl.ICommand#getName()
	 */
	@Override
	public String getName()
	{
		return "search-category";
	}

	/**
	 * outputCategories
	 * 
	 * @param name
	 * @param index
	 */
	private void outputCategories(String name, DiskIndex index)
	{
		List<String> categories = new ArrayList<String>();

		for (String category : index.getCategories())
		{
			if ("*".equals(name) || category.contains(name))
			{
				categories.add(category);
			}
		}

		if (categories.isEmpty() == false)
		{
			System.out.println(index.indexFile.getName());

			for (String category : categories)
			{
				System.out.println("  " + category);
			}
		}
	}
}
