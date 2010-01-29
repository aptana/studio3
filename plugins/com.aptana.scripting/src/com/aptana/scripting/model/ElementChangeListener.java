package com.aptana.scripting.model;

public interface ElementChangeListener
{
	/**
	 * bundleAdded
	 * 
	 * @param bundle
	 */
	void bundleAdded(BundleElement bundle);

	/**
	 * bundleDeleted
	 * 
	 * @param bundle
	 */
	void bundleDeleted(BundleElement bundle);

	/**
	 * elementAdded
	 * 
	 * @param element
	 */
	void elementAdded(AbstractElement element);

	/**
	 * elementDeleted
	 * 
	 * @param element
	 */
	void elementDeleted(AbstractElement element);
}
