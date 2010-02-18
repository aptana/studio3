package com.aptana.scripting.model;

public interface BundleChangeListener
{
	/**
	 * This event fires whenever a new bundle is added to its bundle entry. The event fires after the becameHidden
	 * becameVisible events fire. When this event fires, the bundle elements visibility flag has been set.
	 * 
	 * @param bundle
	 */
	void added(BundleElement bundle);

	/**
	 * This event fires whenever one or more bundles that were previously visible have become hidden by another bundle.
	 * All bundles that have changed state are members of the specified bundle entry. The bundle entry can then be used
	 * to calculate bundle properties following the bundle precedence rules. This can be done either via the helper
	 * methods on BundleEntry or through {@link BundleEntry#processBundles(BundleProcessor)}
	 * 
	 * @param entry
	 */
	void becameHidden(BundleEntry entry);

	/**
	 * This event fires whenever one or more bundles that were previously hidden have become visible by the deletion of
	 * another bundle. All bundles that have changed state are members of the specified bundle entry. The bundle entry
	 * can then be used to calculate bundle properties following the bundle precedence rules. This can be done either
	 * via the helper methods on BundleEntry or through {@link BundleEntry#processBundles(BundleProcessor)}
	 * 
	 * @param entry
	 */
	void becameVisible(BundleEntry entry);

	/**
	 * This event fires whenever a bundle is removed from its bundle entry. The event fires before the becameHidden
	 * becameVisible events fire. When this event fires, the bundle element's visibility flag reflects its state before
	 * being deleted since all deleted bundle elements become hidden after deletion.
	 * 
	 * @param bundle
	 */
	void deleted(BundleElement bundle);
}
