package com.aptana.scripting.model;

import java.util.ArrayList;
import java.util.List;

public class BundleEntry
{
	private String _name;
	private BundleList[] _scopedBundleLists;

	/**
	 * BundleEntry
	 * 
	 * @param name
	 */
	public BundleEntry(String name)
	{
		this._name = name;
		this._scopedBundleLists = new BundleList[BundleScope.getMaxValue() + 1];
	}

	/**
	 * add
	 * 
	 * @param bundle
	 */
	public void addBundle(BundleElement bundle)
	{
		BundleScope scope = bundle.getBundleScope();
		int index = scope.getIndex();
		BundleList list = this._scopedBundleLists[index];
		
		if (list == null)
		{
			list = new BundleList();
			
			this._scopedBundleLists[index] = list;
		}
		
		list.add(bundle);
	}
	
	/**
	 * getBundles
	 * 
	 * @return
	 */
	public BundleElement[] getBundles()
	{
		List<BundleElement> result = new ArrayList<BundleElement>();
		
		for (int i = 0; i < this._scopedBundleLists.length; i++)
		{
			BundleList list = this._scopedBundleLists[i];
			
			if (list != null)
			{
				result.addAll(list.getList());
			}
		}
		
		return result.toArray(new BundleElement[result.size()]);
	}
	
	/**
	 * getBundlesForScope
	 * 
	 * @param scope
	 * @return
	 */
	public BundleElement[] getBundlesForScope(BundleScope scope)
	{
		return this._scopedBundleLists[scope.getIndex()].toArray();
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
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return this._name;
	}

	/**
	 * processMembers
	 * 
	 * @param processor
	 */
	protected void processMembers(BundleProcessor processor)
	{
		for (int i = this._scopedBundleLists.length - 1; i >= 0; i--)
		{
			BundleList list = this._scopedBundleLists[i];

			for (int j = list.size(); j >= 0; j--)
			{
				BundleElement bundle = list.get(j);

				processor.processBundle(bundle);
			}
		}
	}
}
