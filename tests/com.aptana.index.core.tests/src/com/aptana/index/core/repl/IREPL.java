/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core.repl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.aptana.internal.index.core.DiskIndex;

/**
 * @author klindsey
 */
public class IREPL
{
	private static final Map<String, ICommand> COMMAND_MAP;
	private List<DiskIndex> indexes;
	private DiskIndex currentIndex;
	private String currentCategory;
	private boolean helpedOnce = false;

	static
	{
		// TODO: use reflection, if possible, to find all ICommands
		ICommand[] commands = new ICommand[] { //
			new Exit(), //
			new Help(), //
			new ReloadIndexes(), //
			
			new SearchCategoryName(), //
			new SearchDocumentName(), //
			new SearchIndexName(), //
			new SearchWordContent(), //
			
			new SetCategoryName(), //
			new SetIndex() //
		};

		COMMAND_MAP = new HashMap<String, ICommand>();

		for (ICommand command : commands)
		{
			COMMAND_MAP.put(command.getName(), command);
			
			List<String> aliases = command.getAliases();
			
			if (aliases != null)
			{
				for (String alias : aliases)
				{
					COMMAND_MAP.put(alias, command);
				}
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		IREPL repl = new IREPL();

		repl.run();
	}

	/**
	 * getCurrentCategory
	 * 
	 * @return
	 */
	public String getCurrentCategory()
	{
		return this.currentCategory;
	}
	
	/**
	 * getCommand
	 * 
	 * @param name
	 * @return
	 */
	public ICommand getCommand(String name)
	{
		return COMMAND_MAP.get(name);
	}

	/**
	 * getCommandsNames
	 * 
	 * @return
	 */
	public List<String> getCommandNames()
	{
		List<String> result = new ArrayList<String>(COMMAND_MAP.keySet());

		Collections.sort(result);

		return result;
	}

	/**
	 * getCurrentIndex
	 * 
	 * @return
	 */
	public DiskIndex getCurrentIndex()
	{
		return this.currentIndex;
	}

	/**
	 * getIndex
	 * 
	 * @param name
	 * @return
	 */
	public DiskIndex getIndex(String name)
	{
		DiskIndex result = null;
		
		for (DiskIndex index : this.getIndexes())
		{
			if (index.indexFile.getName().contains(name))
			{
				result = index;
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * getIndexes
	 * 
	 * @return
	 */
	public List<DiskIndex> getIndexes()
	{
		return this.indexes;
	}

	/**
	 * loadDiskIndex
	 */
	public void loadDiskIndexes()
	{
		indexes = new ArrayList<DiskIndex>();

		File indexHome = new File("/Users/klindsey/Documents/Workspaces/runtime-red.product/.metadata/.plugins/com.aptana.index.core");
		FileFilter filter = new FileFilter()
		{
			@Override
			public boolean accept(File pathname)
			{
				return pathname.getName().endsWith(".index");
			}
		};

		for (File indexFile : indexHome.listFiles(filter))
		{
			try
			{
				DiskIndex index = new DiskIndex(indexFile.getAbsolutePath());
				index.initialize(true);
				indexes.add(index);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * prompt
	 */
	protected void prompt()
	{
		System.out.print("> ");
	}

	/**
	 * run
	 */
	public void run()
	{
		System.out.println("IREPL v0.1 - A simple read-execute-print loop to poke around disk indexes");
		System.out.println("Type help for more info");
		System.out.println();
		
		// pre-load current set of indexes
		this.loadDiskIndexes();

		// wrap stdin with a scanner
		Scanner scanner = new Scanner(System.in);

		// start loop until we exit
		while (true)
		{
			// show prompt
			this.prompt();
			
			// grab (trimmed) user input
			String input = scanner.nextLine().trim();
			
			// process input if we got something
			if (input.length() > 0)
			{
				// simple arg splitting
				String[] args = input.split("\\s+");
				
				// first arg is the command
				String name = args[0];
	
				// process command or show help
				if (COMMAND_MAP.containsKey(name))
				{
					ICommand command = COMMAND_MAP.get(name);
	
					try
					{
						if (command.execute(this, args) == false)
						{
							break;
						}
					}
					catch (Throwable t)
					{
						t.printStackTrace();
					}
				}
				else
				{
					System.out.println("Unrecognized command: " + name);
					
					if (this.helpedOnce == false)
					{
						System.out.println();
						COMMAND_MAP.get("help").execute(this, null);
						this.helpedOnce = true;
					}
				}
			}
		}
	}

	/**
	 * setCurrentCategory
	 * 
	 * @param category
	 */
	public void setCurrentCategory(String category)
	{
		this.currentCategory = category;
	}
	
	/**
	 * setCurrentIndex
	 * 
	 * @param index
	 */
	public void setCurrentIndex(DiskIndex index)
	{
		this.currentIndex = index;
	}
}
