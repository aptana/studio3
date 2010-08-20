package com.aptana.index.core.repl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SetIndexDirectory extends GenericCommand
{
	/* (non-Javadoc)
	 * @see com.aptana.index.core.repl.GenericCommand#getAlaises()
	 */
	@Override
	public List<String> getAliases()
	{
		List<String> aliases = new ArrayList<String>();
		
		aliases.add("d=");
		
		return aliases;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.repl.GenericCommand#execute(com.aptana.index.core.repl.IndexREPL, java.lang.String[])
	 */
	@Override
	public boolean execute(IREPL repl, String[] args)
	{
		if (args != null && args.length > 1)
		{
			String name = args[1];
			File file = new File(name);
			
			if (file.canRead())
			{
				repl.setIndexDirectory(name);
			}
			else
			{
				System.out.println("Directory does not exist or is not readable: " + name);
				System.out.println("Index directory not changed");
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
		return "Set the directory used to load disk index files. Note that you will need to use 'reload' to load the new indexes";
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.repl.ICommand#getName()
	 */
	@Override
	public String getName()
	{
		return "set-directory";
	}
}
