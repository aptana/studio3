package com.aptana.scripting.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BundleEntry
{
	private String _name;
	private List<BundleElement> _bundles;
	private boolean _sorted;

	/**
	 * BundleEntry
	 * 
	 * @param name
	 */
	public BundleEntry(String name)
	{
		this._name = name;
		this._bundles = new ArrayList<BundleElement>();
		this._sorted = true;
	}

	/**
	 * add
	 * 
	 * @param bundle
	 */
	public void addBundle(BundleElement bundle)
	{
		this._bundles.add(bundle);
		this._sorted = false;
	}
	
	/**
	 * getBundles
	 * 
	 * @return
	 */
	public BundleElement[] getBundles()
	{
		this.sortBundles();
		
		return this._bundles.toArray(new BundleElement[this._bundles.size()]);
	}

	/**
	 * getCommands
	 * 
	 * @return
	 */
	public CommandElement[] getCommands()
	{
		CommandElement[] result = BundleManager.NO_COMMANDS;

		return result;
	}

	/**
	 * geMenus
	 * 
	 * @return
	 */
	public MenuElement[] getMenus()
	{
		MenuElement[] result = BundleManager.NO_MENUS;
		
		return result;
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
		SnippetElement[] result = BundleManager.NO_SNIPPETS;
		
		return result;
	}
	
	/**
	 * processMembers
	 * 
	 * @param processor
	 */
	protected void processMembers(BundleProcessor processor)
	{
		for (int i = this._bundles.size() - 1; i >= 0; i--)
		{
			BundleElement bundle = this._bundles.get(i);
			
			processor.processBundle(bundle);
		}
	}
	
	/**
	 * sortBundles
	 */
	protected void sortBundles()
	{
		if (this._sorted == false)
		{
			Collections.sort(this._bundles, new Comparator<BundleElement>()
			{
				public int compare(BundleElement o1, BundleElement o2)
				{
					// TODO Auto-generated method stub
					return 0;
				}
			});
			
			this._sorted = true;
		}
	}
}
