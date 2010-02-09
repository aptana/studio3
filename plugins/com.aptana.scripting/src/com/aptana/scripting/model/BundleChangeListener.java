package com.aptana.scripting.model;

public interface BundleChangeListener
{
	/**
	 * This event fires whenever a new bundle is added to a bundle entry, regardless
	 * of the visibility of that bundle
	 * 
	 * @param bundle
	 */
	void added(BundleElement bundle);
	
	/**
	 * This event fires whenever a bundle that was previously visible is now hidden
	 * by another bundle
	 * 
	 * @param bundle
	 */
	void becameHidden(BundleElement bundle);
	
	/**
	 * This event fires whenever a bundle that was previously hidden has become visible
	 * 
	 * @param bundle
	 */
	void becameVisible(BundleElement bundle);
	
	/**
	 * This event fires whenever a bundle has been removed from its bundle entry.
	 * 
	 * @param bundle
	 */
	void deleted(BundleElement bundle);
}
