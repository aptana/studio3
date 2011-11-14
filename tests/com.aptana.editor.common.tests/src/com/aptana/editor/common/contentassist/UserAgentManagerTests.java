/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.core.util.ResourceUtil;
import com.aptana.editor.common.contentassist.UserAgentManager.UserAgent;
import com.aptana.ui.epl.UIEplPlugin;

/**
 * UserAgentManagerTests
 */
public class UserAgentManagerTests extends TestCase
{
	/**
	 * 
	 */
	private static final String UNKNOWN_NATURE_ID = "some.other.unknown.nature.id";
	private UserAgentManager manager;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		manager = UserAgentManager.getInstance();

		restoreDefaultUserAgents();
	}

	private void restoreDefaultUserAgents()
	{
		// Set defaults for all Aptana natures
		for (String natureID : ResourceUtil.getAptanaNaturesMap().values())
		{
			String[] userAgentIDs = manager.getDefaultUserAgentIDs(natureID);

			manager.setActiveUserAgents(natureID, userAgentIDs);
		}

		// Add one unknown nature
		String[] userAgentIDs = manager.getDefaultUserAgentIDs(UNKNOWN_NATURE_ID);

		manager.setActiveUserAgents(UNKNOWN_NATURE_ID, userAgentIDs);
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		manager = null;

		super.tearDown();
	}

	public void assertIDs(String natureID, String... expectedUserAgentIDs)
	{
		UserAgent[] userAgents = manager.getActiveUserAgents(natureID);

		assertNotNull(userAgents);

		// create expected set
		Set<String> expected = new HashSet<String>();
		for (String id : expectedUserAgentIDs)
		{
			expected.add(id);
		}

		// create actual set
		Set<String> actual = new HashSet<String>();
		for (UserAgent userAgent : userAgents)
		{
			actual.add(userAgent.ID);
		}

		assertEquals(expected, actual);
	}

	public void testWebDefaults()
	{
		// @formatter:off
		assertIDs(
			"com.aptana.projects.webnature",
			"IE", "Mozilla", "Chrome"
		);
		// @formatter:on
	}

	public void testOtherDefaults()
	{
		// @formatter:off
		assertIDs(
			UNKNOWN_NATURE_ID,
			"IE", "Mozilla", "Chrome"
		);
		// @formatter:on
	}

	public void testPreferenceMigration()
	{
		// set preference to older format
		IPreferenceStore prefs = UIEplPlugin.getDefault().getPreferenceStore();
		prefs.setValue(IPreferenceConstants.USER_AGENT_PREFERENCE, "IE,Safari");

		// force reload of preference key
		manager.loadPreference();

		// now check that all natures are set to the list we specified above
		for (String natureID : ResourceUtil.getAptanaNaturesMap().values())
		{
			// @formatter:off
			assertIDs(
				natureID,
				"IE", "Safari"
			);
			// @formatter:on
		}
	}
}
