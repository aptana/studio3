/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

public interface BundleVisibilityListener
{
	/**
	 * This event fires whenever one or more bundles that were previously visible have become hidden by another bundle.
	 * All bundles that have changed state are members of the specified bundle entry. The bundle entry can then be used
	 * to calculate bundle properties following the bundle precedence rules. This can be done either via the helper
	 * methods on BundleEntry or through {@link BundleEntry#processBundles(BundleProcessor)}. Note that a bundle will be
	 * included in the BundleEntry if it has been deleted or if it has been added but is not visible due to bundle
	 * precedence.
	 * 
	 * @param entry
	 */
	void bundlesBecameHidden(BundleEntry entry);

	/**
	 * This event fires whenever one or more bundles that were previously hidden have become visible by the deletion of
	 * another bundle. All bundles that have changed state are members of the specified bundle entry. The bundle entry
	 * can then be used to calculate bundle properties following the bundle precedence rules. This can be done either
	 * via the helper methods on BundleEntry or through {@link BundleEntry#processBundles(BundleProcessor)}. Note that a
	 * bundle will be included in the BundleEntry if it has been added and is visible or if another bundle has been
	 * deleted thus exposing it due to bundle precedence.
	 * 
	 * @param entry
	 */
	void bundlesBecameVisible(BundleEntry entry);
}
