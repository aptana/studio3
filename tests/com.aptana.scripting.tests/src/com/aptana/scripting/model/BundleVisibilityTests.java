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

import java.util.ArrayList;
import java.util.List;

public class BundleVisibilityTests extends BundleTestBase
{
	public class BundleTestListener implements BundleVisibilityListener
	{
		List<BundleEntry> hiddenEntries = new ArrayList<BundleEntry>();
		List<BundleEntry> visibleEntries = new ArrayList<BundleEntry>();
		
		public void bundlesBecameHidden(BundleEntry entry)
		{
			hiddenEntries.add(entry);
		}

		public void bundlesBecameVisible(BundleEntry entry)
		{
			visibleEntries.add(entry);
		}

		public void reset()
		{
			hiddenEntries.clear();
			visibleEntries.clear();
		}
	}
	
	private BundleTestListener _bundleListener;
	
	/**
	 * setUp
	 */
	public void setUp()
	{
		this._bundleListener = new BundleTestListener();
		
		BundleTestBase.getBundleManagerInstance().addBundleVisibilityListener(this._bundleListener);
	}
	
	/**
	 * testVisibleWhenAdded
	 */
	public void testVisibleWhenAdded()
	{
		this.loadBundle("bundleWithCommand", BundlePrecedence.APPLICATION);
		
		List<BundleEntry> visible = this._bundleListener.visibleEntries;
		assertEquals(1, visible.size());
		
		BundleEntry entry = visible.get(0);
		List<BundleElement> bundles = entry.getBundles();
		assertNotNull(bundles);
		assertEquals(1, bundles.size());
		
		BundleElement bundle = bundles.get(0);
		assertTrue(bundle.isVisible());
	}
	
	/**
	 * testHiddenWhenDeleted
	 */
	public void testHiddenWhenDeleted()
	{
		BundleEntry entry = this.getBundleEntry("bundleWithCommand", BundlePrecedence.APPLICATION);
		
		BundleElement bundle = entry.getBundles().get(0);
		entry.removeBundle(bundle);
		
		List<BundleEntry> hidden = this._bundleListener.hiddenEntries;
		assertEquals(1, hidden.size());
		
		BundleEntry entry2 = hidden.get(0);
		List<BundleElement> bundles = entry2.getBundles();
		assertNotNull(bundles);
		assertEquals(1, bundles.size());
		
		BundleElement bundle2 = bundles.get(0);
		assertFalse(bundle2.isVisible());
	}
	
	/**
	 * testExposeBundles
	 */
	public void testExposeBundles()
	{
		this.loadBundleEntry("bundleWithCommand", BundlePrecedence.APPLICATION);
		this.loadBundleEntry("bundleWithCommandReference", BundlePrecedence.APPLICATION);
		this.loadBundleEntry("bundleWithCommand", BundlePrecedence.USER);
		
		this._bundleListener.reset();
		
		BundleEntry entry = BundleTestBase.getBundleManagerInstance().getBundleEntry("bundleWithCommand");
		assertNotNull(entry);
		
		List<BundleElement> bundles = entry.getBundles();
		assertNotNull(bundles);
		assertEquals(3, bundles.size());
		
		BundleElement lastBundle = bundles.get(2);
		entry.removeBundle(lastBundle);
		
		List<BundleEntry> hidden = this._bundleListener.hiddenEntries;
		assertEquals(1, hidden.size());
		List<BundleElement> hiddenBundles = hidden.get(0).getBundles();
		assertNotNull(hiddenBundles);
		assertEquals(1, hiddenBundles.size());
		assertSame(lastBundle, hiddenBundles.get(0));
		
		List<BundleEntry> visible = this._bundleListener.visibleEntries;
		assertEquals(1, visible.size());
		List<BundleElement> visibleBundles = visible.get(0).getBundles();
		assertNotNull(visibleBundles);
		assertEquals(2, visibleBundles.size());
		assertSame(bundles.get(0), visibleBundles.get(0));
		assertSame(bundles.get(1), visibleBundles.get(1));
	}
	
	/**
	 * testHideBundles
	 */
	public void testHideBundles()
	{
		this.loadBundleEntry("bundleWithCommand", BundlePrecedence.APPLICATION);
		this.loadBundleEntry("bundleWithCommandReference", BundlePrecedence.APPLICATION);
		
		this._bundleListener.reset();
		
		this.loadBundleEntry("bundleWithCommand", BundlePrecedence.USER);
		BundleEntry entry = BundleTestBase.getBundleManagerInstance().getBundleEntry("bundleWithCommand");
		assertNotNull(entry);
		List<BundleElement> bundles = entry.getBundles();
		assertNotNull(bundles);
		assertEquals(3, bundles.size());
		BundleElement lastBundle = bundles.get(2);
		
		List<BundleEntry> hidden = this._bundleListener.hiddenEntries;
		assertEquals(1, hidden.size());
		List<BundleElement> hiddenBundles = hidden.get(0).getBundles();
		assertNotNull(hiddenBundles);
		assertEquals(2, hiddenBundles.size());
		assertSame(bundles.get(0), hiddenBundles.get(0));
		assertSame(bundles.get(1), hiddenBundles.get(1));
		
		List<BundleEntry> visible = this._bundleListener.visibleEntries;
		assertEquals(1, visible.size());
		List<BundleElement> visibleBundles = visible.get(0).getBundles();
		assertNotNull(visibleBundles);
		assertEquals(1, visibleBundles.size());
		assertSame(lastBundle, visibleBundles.get(0));
	}
	
	/**
	 * testAddWithNoChange
	 */
	public void testAddWithNoChange()
	{
		this.loadBundleEntry("bundleWithCommand", BundlePrecedence.USER);
		
		this._bundleListener.reset();
		
		this.loadBundleEntry("bundleWithCommand", BundlePrecedence.APPLICATION);
		BundleEntry entry = BundleTestBase.getBundleManagerInstance().getBundleEntry("bundleWithCommand");
		assertNotNull(entry);
		List<BundleElement> bundles = entry.getBundles();
		assertNotNull(bundles);
		assertEquals(2, bundles.size());
		
		List<BundleEntry> hidden = this._bundleListener.hiddenEntries;
		assertEquals(0, hidden.size());
		
		List<BundleEntry> visible = this._bundleListener.visibleEntries;
		assertEquals(0, visible.size());
	}
}
