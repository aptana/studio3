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
