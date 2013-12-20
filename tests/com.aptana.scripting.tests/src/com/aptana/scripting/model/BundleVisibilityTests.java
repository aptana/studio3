/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

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

	@Before
	public void setUp()
	{
		this._bundleListener = new BundleTestListener();

		BundleTestBase.getBundleManagerInstance().addBundleVisibilityListener(this._bundleListener);
	}

	/**
	 * testVisibleWhenAdded
	 */
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
