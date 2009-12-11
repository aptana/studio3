package com.aptana.scripting.model;

import java.util.ArrayList;
import java.util.List;

public class BundleList
{
	List<BundleElement> _bundles;
	
	/**
	 * BundleList
	 */
	public BundleList()
	{
		this._bundles = new ArrayList<BundleElement>();
	}
	
	/**
	 * add
	 * 
	 * @param bundle
	 */
	public void add(BundleElement bundle)
	{
		
	}
	
	/**
	 * get
	 * 
	 * @param i
	 * @return
	 */
	public BundleElement get(int i)
	{
		return this._bundles.get(i);
	}
	
	/**
	 * getList
	 * 
	 * @return
	 */
	List<BundleElement> getList()
	{
		return this._bundles;
	}
	
	/**
	 * remove
	 * 
	 * @param bundle
	 */
	public void remove(BundleElement bundle)
	{
		
	}
	
	/**
	 * size
	 */
	public int size()
	{
		return this._bundles.size();
	}
	
	/**
	 * toArray
	 * 
	 * @return
	 */
	public BundleElement[] toArray()
	{
		return this._bundles.toArray(new BundleElement[this._bundles.size()]);
	}
}
