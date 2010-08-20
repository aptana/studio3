package com.aptana.index.core.repl;

import java.util.ArrayList;
import java.util.List;

import com.aptana.internal.index.core.DiskIndex;

public class SetIndex extends GenericCommand
{
	/* (non-Javadoc)
	 * @see com.aptana.index.core.repl.GenericCommand#getAlaises()
	 */
	@Override
	public List<String> getAliases()
	{
		List<String> aliases = new ArrayList<String>();
		
		aliases.add("i=");
		
		return aliases;
	}

	@Override
	public boolean execute(IREPL repl, String[] args)
	{
		if (args != null && args.length > 1)
		{
			boolean set = false;
			String name = args[1];
			
			for (DiskIndex index : repl.getIndexes())
			{
				String indexName = index.indexFile.getName();
				
				if (indexName.contains(name))
				{
					repl.setCurrentIndex(index);
					System.out.println("Index set to " + index.indexFile.getName());
					set = true;
					break;
				}
			}
			
			if (set == false)
			{
				System.out.println("Unable to locate an index for '" + name + "'");
			}
		}
		else
		{
			repl.setCurrentIndex(null);
		}
		
		return true;
	}

	@Override
	public String getDescription()
	{
		return "Set current index to first one that contains specified text. If no args are passed in, it clears current index";
	}

	@Override
	public String getName()
	{
		return "set-index";
	}
}
