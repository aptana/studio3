package com.aptana.index.core.repl;

import java.util.ArrayList;
import java.util.List;

import com.aptana.internal.index.core.DiskIndex;

public class SetCategoryName extends GenericCommand
{
	/* (non-Javadoc)
	 * @see com.aptana.index.core.repl.GenericCommand#getAlaises()
	 */
	@Override
	public List<String> getAliases()
	{
		List<String> aliases = new ArrayList<String>();
		
		aliases.add("c=");
		
		return aliases;
	}
	
	@Override
	public boolean execute(IREPL repl, String[] args)
	{
		if (args != null && args.length > 1)
		{
			String name = args[1];
			DiskIndex index = repl.getCurrentIndex();
			
			if (index != null)
			{
				boolean set = false;
				
				for (String category : index.getCategories())
				{
					if (category.contains(name))
					{
						repl.setCurrentCategory(category);
						System.out.println("Category set to " + category);
						set = true;
						break;
					}
				}
				
				if (set == false)
				{
					System.out.println("Unable to locate a category match for '" + name + "'");
				}
			}
			else
			{
				System.out.println("No current index");
			}
		}
		else
		{
			repl.setCurrentCategory(null);
		}
		
		return true;
	}

	@Override
	public String getDescription()
	{
		return "Set current category to first one that contains specified text. If no args are passed in, it clears current category";
	}

	@Override
	public String getName()
	{
		return "set-category";
	}
}
