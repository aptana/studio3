/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import static org.junit.Assert.fail;

import java.net.URI;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.contentassist.IPreferenceConstants;
import com.aptana.editor.common.contentassist.UserAgentFilterType;
import com.aptana.editor.js.tests.JSEditorBasedTestCase;
import com.aptana.index.core.FileStoreBuildContext;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.index.core.build.BuildContext;
import com.aptana.js.core.index.SDocMLFileIndexingParticipant;

/**
 * JSUserAgentFiltering
 */
public class JSUserAgentFilteringTest extends JSEditorBasedTestCase
{
	class Indexer extends SDocMLFileIndexingParticipant
	{
		public void index(Index index, IFileStore file) throws CoreException
		{
			BuildContext context = new FileStoreBuildContext(file);
			index(context, index, new NullProgressMonitor());
		}
	}

	private static final String NO_USER_AGENTS = StringUtil.EMPTY;
	private static final String ONE_USER_AGENT = "IE";
	private static final String TWO_USER_AGENTS = "IE,Safari";
	private static final String THREE_USER_AGENTS = "IE,Safari,Firefox";

	private IEclipsePreferences prefs;

	protected void indexAndCheckProposals(String preference, String indexResource, String fileResource,
			String... proposals)
	{
		URI uri = null;

		// set active user agents
		setActiveUserAgents(preference);

		try
		{
			// create IFileStore for indexing
			IFileStore indexFile = getFileStore(indexResource);

			// grab source file URI
			IFileStore sourceFile = getFileStore(fileResource);
			uri = sourceFile.toURI();

			// create index for file
			Index index = getIndexManager().getIndex(uri);
			Indexer indexer = new Indexer();

			// index file
			indexer.index(index, indexFile);

			// check proposals
			checkProposals(fileResource, proposals);
		}
		catch (Throwable t)
		{
			fail(t.getMessage());
		}
		finally
		{
			if (uri != null)
			{
				getIndexManager().removeIndex(uri);
			}
		}
	}

	protected IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
	}

	@Before
	public void setUp() throws Exception
	{
		prefs = InstanceScope.INSTANCE.getNode(CommonEditorPlugin.PLUGIN_ID);
	}

	@Override
	public void tearDown() throws Exception
	{
		try
		{
			prefs.remove(IPreferenceConstants.USER_AGENT_PREFERENCE);
			prefs.remove(IPreferenceConstants.CONTENT_ASSIST_USER_AGENT_FILTER_TYPE);
		}
		finally
		{
			prefs = null;
			super.tearDown();
		}
	}

	/**
	 * setActiveUserAgents
	 * 
	 * @param ids
	 */
	protected void setActiveUserAgents(String ids)
	{
		prefs.put(IPreferenceConstants.USER_AGENT_PREFERENCE, ids);
	}

	@Test
	public void testNoFilterNoUserAgentsActive()
	{
		// set filter preference
		prefs.put(IPreferenceConstants.CONTENT_ASSIST_USER_AGENT_FILTER_TYPE, UserAgentFilterType.NO_FILTER.getText());

		// @formatter:off
		indexAndCheckProposals(
			NO_USER_AGENTS,
			"sdocml/userAgents.sdocml",
			"contentAssist/empty.js",
			"NoUserAgents",
			"OneUserAgent",
			"OneUserAgentOutlier",
			"TwoUserAgents",
			"TwoUserAgentsOutlier",
			"ThreeUserAgents",
			"ThreeUserAgentsOutlier",
			"FourUserAgents",
			"FourUserAgentsOutlier"
		);
		// @formatter:on
	}

	@Test
	public void testNoFilterOneUserAgentActive()
	{
		// set filter preference
		prefs.put(IPreferenceConstants.CONTENT_ASSIST_USER_AGENT_FILTER_TYPE, UserAgentFilterType.NO_FILTER.getText());

		// @formatter:off
		indexAndCheckProposals(
			ONE_USER_AGENT,
			"sdocml/userAgents.sdocml",
			"contentAssist/empty.js",
			"NoUserAgents",
			"OneUserAgent",
			"OneUserAgentOutlier",
			"TwoUserAgents",
			"TwoUserAgentsOutlier",
			"ThreeUserAgents",
			"ThreeUserAgentsOutlier",
			"FourUserAgents",
			"FourUserAgentsOutlier"
		);
		// @formatter:on
	}

	@Test
	public void testNoFilterTwoUserAgentsActive()
	{
		// set filter preference
		prefs.put(IPreferenceConstants.CONTENT_ASSIST_USER_AGENT_FILTER_TYPE, UserAgentFilterType.NO_FILTER.getText());

		// @formatter:off
		indexAndCheckProposals(
			TWO_USER_AGENTS,
			"sdocml/userAgents.sdocml",
			"contentAssist/empty.js",
			"NoUserAgents",
			"OneUserAgent",
			"OneUserAgentOutlier",
			"TwoUserAgents",
			"TwoUserAgentsOutlier",
			"ThreeUserAgents",
			"ThreeUserAgentsOutlier",
			"FourUserAgents",
			"FourUserAgentsOutlier"
		);
		// @formatter:on
	}

	@Test
	public void testNoFilterThreeUserAgentsActive()
	{
		// set filter preference
		prefs.put(IPreferenceConstants.CONTENT_ASSIST_USER_AGENT_FILTER_TYPE, UserAgentFilterType.NO_FILTER.getText());

		// @formatter:off
		indexAndCheckProposals(
			THREE_USER_AGENTS,
			"sdocml/userAgents.sdocml",
			"contentAssist/empty.js",
			"NoUserAgents",
			"OneUserAgent",
			"OneUserAgentOutlier",
			"TwoUserAgents",
			"TwoUserAgentsOutlier",
			"ThreeUserAgents",
			"ThreeUserAgentsOutlier",
			"FourUserAgents",
			"FourUserAgentsOutlier"
		);
		// @formatter:on
	}

	@Test
	public void testSomeFilterNoUserAgentsActive()
	{
		// set filter preference
		prefs.put(IPreferenceConstants.CONTENT_ASSIST_USER_AGENT_FILTER_TYPE, UserAgentFilterType.NO_FILTER.getText());

		// @formatter:off
		indexAndCheckProposals(
			NO_USER_AGENTS,
			"sdocml/userAgents.sdocml",
			"contentAssist/empty.js",
			"NoUserAgents",
			"OneUserAgent",
			"OneUserAgentOutlier",
			"TwoUserAgents",
			"TwoUserAgentsOutlier",
			"ThreeUserAgents",
			"ThreeUserAgentsOutlier",
			"FourUserAgents",
			"FourUserAgentsOutlier"
		);
		// @formatter:on
	}

	@Test
	public void testSomeFilterOneUserAgentActive()
	{
		// set filter preference
		prefs.put(IPreferenceConstants.CONTENT_ASSIST_USER_AGENT_FILTER_TYPE, UserAgentFilterType.ONE_OR_MORE.getText());

		// @formatter:off
		indexAndCheckProposals(
			ONE_USER_AGENT,
			"sdocml/userAgents.sdocml",
			"contentAssist/empty.js",
			"NoUserAgents",
			"OneUserAgent",
			"TwoUserAgents",
			"TwoUserAgentsOutlier",
			"ThreeUserAgents",
			"ThreeUserAgentsOutlier",
			"FourUserAgents",
			"FourUserAgentsOutlier"
		);
		// @formatter:on
	}

	@Test
	public void testSomeFilterTwoUserAgentsActive()
	{
		// set filter preference
		prefs.put(IPreferenceConstants.CONTENT_ASSIST_USER_AGENT_FILTER_TYPE, UserAgentFilterType.ONE_OR_MORE.getText());

		// @formatter:off
		indexAndCheckProposals(
			TWO_USER_AGENTS,
			"sdocml/userAgents.sdocml",
			"contentAssist/empty.js",
			"NoUserAgents",
			"OneUserAgent",
			"TwoUserAgents",
			"TwoUserAgentsOutlier",
			"ThreeUserAgents",
			"ThreeUserAgentsOutlier",
			"FourUserAgents",
			"FourUserAgentsOutlier"
		);
		// @formatter:on
	}

	@Test
	public void testSomeFilterThreeUserAgentsActive()
	{
		// set filter preference
		prefs.put(IPreferenceConstants.CONTENT_ASSIST_USER_AGENT_FILTER_TYPE, UserAgentFilterType.ONE_OR_MORE.getText());

		// @formatter:off
		indexAndCheckProposals(
			THREE_USER_AGENTS,
			"sdocml/userAgents.sdocml",
			"contentAssist/empty.js",
			"NoUserAgents",
			"OneUserAgent",
			"TwoUserAgents",
			"TwoUserAgentsOutlier",
			"ThreeUserAgents",
			"ThreeUserAgentsOutlier",
			"FourUserAgents",
			"FourUserAgentsOutlier"
		);
		// @formatter:on
	}

	@Test
	public void testAllFilterNoUserAgentsActive()
	{
		// set filter preference
		prefs.put(IPreferenceConstants.CONTENT_ASSIST_USER_AGENT_FILTER_TYPE, UserAgentFilterType.ALL.getText());

		// @formatter:off
		indexAndCheckProposals(
			NO_USER_AGENTS,
			"sdocml/userAgents.sdocml",
			"contentAssist/empty.js",
			"NoUserAgents",
			"OneUserAgent",
			"OneUserAgentOutlier",
			"TwoUserAgents",
			"TwoUserAgentsOutlier",
			"ThreeUserAgents",
			"ThreeUserAgentsOutlier",
			"FourUserAgents",
			"FourUserAgentsOutlier"
		);
		// @formatter:on
	}

	@Test
	public void testAllFilterOneUserAgentActive()
	{
		// set filter preference
		prefs.put(IPreferenceConstants.CONTENT_ASSIST_USER_AGENT_FILTER_TYPE, UserAgentFilterType.ALL.getText());

		// @formatter:off
		indexAndCheckProposals(
			ONE_USER_AGENT,
			"sdocml/userAgents.sdocml",
			"contentAssist/empty.js",
			"NoUserAgents",
			"OneUserAgent",
			"TwoUserAgents",
			"TwoUserAgentsOutlier",
			"ThreeUserAgents",
			"ThreeUserAgentsOutlier",
			"FourUserAgents",
			"FourUserAgentsOutlier"
		);
		// @formatter:on
	}

	@Test
	public void testAllFilterTwoUserAgentsActive()
	{
		// set filter preference
		prefs.put(IPreferenceConstants.CONTENT_ASSIST_USER_AGENT_FILTER_TYPE, UserAgentFilterType.ALL.getText());

		// @formatter:off
		indexAndCheckProposals(
			TWO_USER_AGENTS,
			"sdocml/userAgents.sdocml",
			"contentAssist/empty.js",
			"NoUserAgents",
			"TwoUserAgents",
			"ThreeUserAgents",
			"FourUserAgents",
			"FourUserAgentsOutlier"
		);
		// @formatter:on
	}

	@Test
	public void testAllFilterThreeUserAgentsActive()
	{
		// set filter preference
		prefs.put(IPreferenceConstants.CONTENT_ASSIST_USER_AGENT_FILTER_TYPE, UserAgentFilterType.ALL.getText());

		// @formatter:off
		indexAndCheckProposals(
			THREE_USER_AGENTS,
			"sdocml/userAgents.sdocml",
			"contentAssist/empty.js",
			"NoUserAgents",
			"ThreeUserAgents",
			"FourUserAgents"
		);
		// @formatter:on
	}
}
