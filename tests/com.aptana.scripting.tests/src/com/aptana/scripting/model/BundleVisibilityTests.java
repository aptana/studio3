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
		
		public void reset()
		{
			this._addedBundles.clear();
			this._deletedBundles.clear();
			this._hiddenEntries.clear();
			this._visibleEntries.clear();
		}
	}
	
	private BundleTestListener _bundleListener;
	
	/**
	 * setUp
	 */
	public void setUp()
	{
		this._bundleListener = new BundleTestListener();
		
		BundleTestBase.getBundleManagerInstance().addBundleChangeListener(this._bundleListener);
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
		this.loadBundleEntry("bundleWithCommand", BundlePrecedence.APPLICATION);
		this.loadBundleEntry("bundleWithCommandReference", BundlePrecedence.APPLICATION);
		this.loadBundleEntry("bundleWithCommand", BundlePrecedence.USER);
		
		this._bundleListener.reset();
		
		BundleEntry entry = BundleTestBase.getBundleManagerInstance().getBundleEntry("bundleWithCommand");
		assertNotNull(entry);
		
		BundleElement[] bundles = entry.getBundles();
		assertNotNull(bundles);
		assertEquals(3, bundles.length);
		
		BundleElement lastBundle = bundles[bundles.length - 1];
		entry.removeBundle(lastBundle);
		
		List<BundleElement> deleted = this._bundleListener._deletedBundles;
		assertEquals(1, deleted.size());
		assertSame(lastBundle, deleted.get(0));
		
		List<BundleEntry> hidden = this._bundleListener._hiddenEntries;
		assertEquals(1, hidden.size());
		BundleElement[] hiddenBundles = hidden.get(0).getBundles();
		assertNotNull(hiddenBundles);
		assertEquals(1, hiddenBundles.length);
		assertSame(lastBundle, hiddenBundles[0]);
		
		List<BundleEntry> visible = this._bundleListener._visibleEntries;
		assertEquals(1, visible.size());
		BundleElement[] visibleBundles = visible.get(0).getBundles();
		assertNotNull(visibleBundles);
		assertEquals(2, visibleBundles.length);
		assertSame(bundles[0], visibleBundles[0]);
		assertSame(bundles[1], visibleBundles[1]);
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
		BundleElement[] bundles = entry.getBundles();
		assertNotNull(bundles);
		assertEquals(3, bundles.length);
		BundleElement lastBundle = bundles[bundles.length - 1];
		
		List<BundleElement> added = this._bundleListener._addedBundles;
		assertEquals(1, added.size());
		assertSame(lastBundle, added.get(0));
		
		List<BundleEntry> hidden = this._bundleListener._hiddenEntries;
		assertEquals(1, hidden.size());
		BundleElement[] hiddenBundles = hidden.get(0).getBundles();
		assertNotNull(hiddenBundles);
		assertEquals(2, hiddenBundles.length);
		assertSame(bundles[0], hiddenBundles[0]);
		assertSame(bundles[1], hiddenBundles[1]);
		
		List<BundleEntry> visible = this._bundleListener._visibleEntries;
		assertEquals(1, visible.size());
		BundleElement[] visibleBundles = visible.get(0).getBundles();
		assertNotNull(visibleBundles);
		assertEquals(1, visibleBundles.length);
		assertSame(lastBundle, visibleBundles[0]);
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
		BundleElement[] bundles = entry.getBundles();
		assertNotNull(bundles);
		assertEquals(2, bundles.length);
		BundleElement firstBundle = bundles[0];
		
		List<BundleElement> added = this._bundleListener._addedBundles;
		assertEquals(1, added.size());
		assertSame(firstBundle, added.get(0));
		
		List<BundleEntry> hidden = this._bundleListener._hiddenEntries;
		assertEquals(0, hidden.size());
		
		List<BundleEntry> visible = this._bundleListener._visibleEntries;
		assertEquals(0, visible.size());
	}
}
