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
