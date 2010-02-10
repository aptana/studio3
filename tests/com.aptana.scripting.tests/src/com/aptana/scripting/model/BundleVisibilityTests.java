package com.aptana.scripting.model;

import java.util.ArrayList;
import java.util.List;

public class BundleVisibilityTests extends BundleTestBase
{
	public class BundleTestListener implements BundleChangeListener
	{
		List<BundleElement> _addedBundles = new ArrayList<BundleElement>();
		List<BundleElement> _deletedBundles = new ArrayList<BundleElement>();
		List<BundleEntry> _hiddenEntries = new ArrayList<BundleEntry>();
		List<BundleEntry> _visibleEntries = new ArrayList<BundleEntry>();
		
		public void added(BundleElement bundle)
		{
			this._addedBundles.add(bundle);
		}

		public void becameHidden(BundleEntry entry)
		{
			this._hiddenEntries.add(entry);
		}

		public void becameVisible(BundleEntry entry)
		{
			this._visibleEntries.add(entry);
		}

		public void deleted(BundleElement bundle)
		{
			this._deletedBundles.add(bundle);
		}
	}
	
	private BundleTestListener _bundleListener;
	
	/**
	 * setUp
	 */
	public void setUp()
	{
		this._bundleListener = new BundleTestListener();
		
		BundleManager.getInstance().addBundleChangeListener(this._bundleListener);
	}
	
	/**
	 * testAddedBundle
	 */
	public void testAddedBundle()
	{
		BundleEntry entry = this.getBundleEntry("bundleWithCommand", BundlePrecedence.APPLICATION);
		
		BundleElement[] bundles = entry.getBundles();
		assertEquals(1, bundles.length);
		
		List<BundleElement> added = this._bundleListener._addedBundles;
		assertEquals(1, added.size());
		
		assertSame(bundles[0], added.get(0));
	}
	
	/**
	 * testDeletedBundle
	 */
	public void testDeleteBundle()
	{
		BundleEntry entry = this.getBundleEntry("bundleWithCommand", BundlePrecedence.APPLICATION);
		
		BundleElement[] bundles = entry.getBundles();
		assertEquals(1, bundles.length);
		BundleElement bundle = bundles[0];
		
		List<BundleElement> added = this._bundleListener._addedBundles;
		assertEquals(1, added.size());
		
		entry.removeBundle(bundle);
		
		List<BundleElement> deleted = this._bundleListener._deletedBundles;
		assertEquals(1, deleted.size());
		
		assertSame(bundle, deleted.get(0));
	}
	
	/**
	 * testVisibleWhenAdded
	 */
	public void testVisibleWhenAdded()
	{
		this.loadBundle("bundleWithCommand", BundlePrecedence.APPLICATION);
		
		List<BundleEntry> visible = this._bundleListener._visibleEntries;
		assertEquals(1, visible.size());
		
		BundleEntry entry = visible.get(0);
		BundleElement[] bundles = entry.getBundles();
		assertNotNull(bundles);
		assertEquals(1, bundles.length);
		
		BundleElement bundle = bundles[0];
		assertTrue(bundle.isVisible());
	}
	
	/**
	 * testHiddenWhenDeleted
	 */
	public void testHiddenWhenDeleted()
	{
		BundleEntry entry = this.getBundleEntry("bundleWithCommand", BundlePrecedence.APPLICATION);
		
		List<BundleElement> added = this._bundleListener._addedBundles;
		assertEquals(1, added.size());
		
		BundleElement bundle = added.get(0);
		entry.removeBundle(bundle);
		
		List<BundleEntry> hidden = this._bundleListener._hiddenEntries;
		assertEquals(1, hidden.size());
		
		BundleEntry entry2 = hidden.get(0);
		BundleElement[] bundles = entry2.getBundles();
		assertNotNull(bundles);
		assertEquals(1, bundles.length);
		
		BundleElement bundle2 = bundles[0];
		assertFalse(bundle2.isVisible());
	}
	
	/**
	 * testExposeBundles
	 */
	public void testExposeBundles()
	{
		
	}
	
	/**
	 * testHideBundles
	 */
	public void testHideBundles()
	{
		
	}
	
	/**
	 * testAddWithNoChange
	 */
	public void testAddWithNoChange()
	{
		
	}
}
