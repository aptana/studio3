package com.aptana.scripting.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BundleEntry
{
	private String _name;
	private List<BundleElement> _bundles;

	/**
	 * BundleEntry
	 * 
	 * @param name
	 */
	public BundleEntry(String name)
	{
		this._name = name;
		this._bundles = new ArrayList<BundleElement>();
	}

	/**
	 * add
	 * 
	 * @param bundle
	 */
	public void addBundle(BundleElement bundle)
	{
		this._bundles.add(bundle);
		
		// keep bundles in canonical order
		Collections.sort(this._bundles, new Comparator<BundleElement>()
		{
			public int compare(BundleElement o1, BundleElement o2)
			{
				int result = o1.getBundleScope().compareTo(o2.getBundleScope());
				
				if (result == 0)
				{
					if (o1.isReference() == o2.isReference())
					{
						result = o1.getPath().compareTo(o2.getPath());
					}
					else
					{
						result = (o1.isReference()) ? -1 : 1;
					}
				}
				
				return result;
			}
		});
	}
	
	/**
	 * getActiveScope
	 * 
	 * @return
	 */
	public BundleScope getActiveScope()
	{
		BundleScope result = BundleScope.UNKNOWN;
		int size = this._bundles.size();
		
		if (size > 0)
		{
			result = this._bundles.get(size - 1).getBundleScope();
		}
		
		return result;
	}
	
	/**
	 * getBundles
	 * 
	 * @return
	 */
	public BundleElement[] getBundles()
	{
		return this._bundles.toArray(new BundleElement[this._bundles.size()]);
	}

	/**
	 * getCommands
	 * 
	 * @return
	 */
	public CommandElement[] getCommands()
	{
		final Set<String> names = new HashSet<String>();
		final List<CommandElement> result = new ArrayList<CommandElement>();
		
		this.processMembers(new BundleProcessor()
		{
			public boolean processBundle(BundleEntry entry, BundleElement bundle)
			{
				for (CommandElement command : bundle.getCommands())
				{
					String name = command.getDisplayName();
					
					if (names.contains(name) == false)
					{
						names.add(name);
						result.add(command);
					}
				}
				
				return true;
			}
		});
		
		return result.toArray(new CommandElement[result.size()]);
	}

	/**
	 * geMenus
	 * 
	 * @return
	 */
	public MenuElement[] getMenus()
	{
		final Set<String> names = new HashSet<String>();
		final List<MenuElement> result = new ArrayList<MenuElement>();
		
		this.processMembers(new BundleProcessor()
		{
			public boolean processBundle(BundleEntry entry, BundleElement bundle)
			{
				for (MenuElement menu : bundle.getMenus())
				{
					String name = menu.getDisplayName();
					
					if (names.contains(name) == false)
					{
						names.add(name);
						result.add(menu);
					}
				}
				
				return true;
			}
		});
		
		return result.toArray(new MenuElement[result.size()]);
	}
	
	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return this._name;
	}

	/**
	 * getSnippets
	 * 
	 * @return
	 */
	public SnippetElement[] getSnippets()
	{
		final Set<String> names = new HashSet<String>();
		final List<SnippetElement> result = new ArrayList<SnippetElement>();
		
		this.processMembers(new BundleProcessor()
		{
			public boolean processBundle(BundleEntry entry, BundleElement bundle)
			{
				for (SnippetElement snippet : bundle.getSnippets())
				{
					String name = snippet.getDisplayName();
					
					if (names.contains(name) == false)
					{
						names.add(name);
						result.add(snippet);
					}
				}
				
				return true;
			}
			
		});
		
		return result.toArray(new SnippetElement[result.size()]);
	}
	
	/**
	 * processMembers
	 * 
	 * @param processor
	 */
	protected void processMembers(BundleProcessor processor)
	{
		BundleScope activeScope = this.getActiveScope();
		boolean processingReferences = true;
		
		// walk the list of bundles from highest bundle scope precedence to lowest
		for (int i = this._bundles.size() - 1; i >= 0; i--)
		{
			BundleElement bundle = this._bundles.get(i);
		
			// we're done processing if we've left the active scope or if we've processed all
			// bundle references and one bundle
			if (bundle.getBundleScope() != activeScope || processingReferences == false)
			{
				break;
			}
			
			// go ahead and process this first non-ref bundle, but turn on a
			// flag to let us know to quit processing next time around
			if (bundle.isReference() == false)
			{
				processingReferences = false;
			}
			
			// do something with the bundle and exit if told to do so
			if (processor.processBundle(this, bundle) == false)
			{
				break;
			}
		}
	}
	
	/**
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean removeBundle(BundleElement bundle)
	{
		return this._bundles.remove(bundle);
	}
}
