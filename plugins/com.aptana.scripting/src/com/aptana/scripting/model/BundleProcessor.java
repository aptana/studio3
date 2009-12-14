package com.aptana.scripting.model;

public interface BundleProcessor
{
	/**
	 * processBundle
	 * 
	 * @param bundle
	 * @return Returns true if processing should continue
	 */
	public boolean processBundle(BundleEntry entry, BundleElement bundle);
}
