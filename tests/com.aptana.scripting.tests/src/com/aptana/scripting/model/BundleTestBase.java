package com.aptana.scripting.model;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;

import com.aptana.scripting.TestUtils;

public abstract class BundleTestBase extends TestCase
{
	private static final String APPLICATION_BUNDLES = TestUtils.getFile(new Path("application-bundles")).getAbsolutePath();
	private static final String USER_BUNDLES = TestUtils.getFile(new Path("user-bundles")).getAbsolutePath();
	private static final String PROJECT_BUNDLES = TestUtils.getFile(new Path("project-bundles")).getAbsolutePath();

	/**
	 * getBundleEntry
	 * 
	 * @param bundleName
	 * @param precedence
	 * @return
	 */
	protected BundleEntry getBundleEntry(String bundleName, BundlePrecedence precedence)
	{
		this.loadBundleEntry(bundleName, precedence);

		// get bundle entry
		BundleEntry entry = getBundleManagerInstance().getBundleEntry(bundleName);
		assertNotNull(entry);

		return entry;
	}

	/**
	 * loadBundle
	 * 
	 * @param bundleName
	 * @param precedence
	 * @return
	 */
	protected BundleElement loadBundle(String bundleName, BundlePrecedence precedence)
	{
		BundleEntry entry = this.getBundleEntry(bundleName, precedence);

		BundleElement[] bundles = entry.getBundles();
		assertEquals(1, bundles.length);

		return bundles[0];
	}

	/**
	 * loadBundleEntry
	 * 
	 * @param bundleName
	 * @param precedence
	 */
	protected void loadBundleEntry(String bundleName, BundlePrecedence precedence)
	{
		BundleManager manager = getBundleManagerInstance();
		String baseDirectory = null;

		// make sure we have a test bundle
		switch (precedence)
		{
			case APPLICATION:
				baseDirectory = APPLICATION_BUNDLES;
				break;

			case PROJECT:
				baseDirectory = PROJECT_BUNDLES;
				break;

			case USER:
				baseDirectory = USER_BUNDLES;
				break;

			default:
				fail("Unrecognized bundle scope: " + precedence);
		}

		File bundleFile = new File(baseDirectory + File.separator + bundleName);
		assertTrue(bundleFile.exists());

		// load bundle
		manager.loadBundle(bundleFile, false);
	}

	/**
	 * getBundleManagerInstance
	 * 
	 * @return
	 */
	public static BundleManager getBundleManagerInstance()
	{
		return BundleManager.getInstance(APPLICATION_BUNDLES, USER_BUNDLES);
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		BundleManager.getInstance().reset();

		super.tearDown();
	}
}
