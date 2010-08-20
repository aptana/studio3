package com.aptana.index.core.repl;

import java.util.ArrayList;
import java.util.List;

public class Help extends GenericCommand
{
	/* (non-Javadoc)
	 * @see com.aptana.index.core.repl.GenericCommand#getAlaises()
	 */
	@Override
	public List<String> getAliases()
	{
		List<String> aliases = new ArrayList<String>();
		
		aliases.add("h");
		aliases.add("?");
		
		return aliases;
	}
	
	@Override
	public boolean execute(IndexREPL repl, String[] args)
	{
		for (String name : repl.getCommandNames())
		{
			ICommand command = repl.getCommand(name);
			
			if (command.getName().equals(name))
			{
				System.out.print("  " + name);
				
				List<String> aliases = command.getAliases();
				
				if (aliases != null && aliases.isEmpty() == false)
				{
					for (String alias : aliases)
					{
						System.out.print(", " + alias);
					}
				}
				
				System.out.println(" - " + command.getDescription());
			}
		}
		
		return true;
	}

	@Override
	public String getDescription()
	{
		return "Show all commands with brief descriptions";
	}

	@Override
	public String getName()
	{
		return "help";
	}
}
