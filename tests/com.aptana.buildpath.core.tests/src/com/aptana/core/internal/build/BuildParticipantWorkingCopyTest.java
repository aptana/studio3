package com.aptana.core.internal.build;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.buildpath.core.BuildPathCorePlugin;
import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IBuildParticipant.BuildType;
import com.aptana.core.build.IBuildParticipantWorkingCopy;
import com.aptana.index.core.build.BuildContext;

@SuppressWarnings("nls")
public class BuildParticipantWorkingCopyTest
{

	private AbstractBuildParticipant participant;

	@Before
	public void setUp() throws Exception
	{
//		super.setUp();

		participant = new AbstractBuildParticipant()
		{

			@Override
			protected String getPreferenceNode()
			{
				return BuildPathCorePlugin.PLUGIN_ID;
			}

			public void deleteFile(BuildContext context, IProgressMonitor monitor)
			{
			}

			public void buildFile(BuildContext context, IProgressMonitor monitor)
			{
			}
		};
	}

	@After
	public void tearDown() throws Exception
	{
		participant = null;

//		super.tearDown();
	}

	@Test
	public void testCanToggleEnablement() throws Exception
	{
		assertFalse(participant.isRequired());
		assertFalse(participant.isEnabled(BuildType.BUILD));
		assertFalse(participant.isEnabled(BuildType.RECONCILE));
		setEnabled(participant, BuildType.BUILD, true);
		assertTrue(participant.isEnabled(BuildType.BUILD));
		assertFalse(participant.isEnabled(BuildType.RECONCILE));
		setEnabled(participant, BuildType.RECONCILE, true);
		assertTrue(participant.isEnabled(BuildType.BUILD));
		assertTrue(participant.isEnabled(BuildType.RECONCILE));
		setEnabled(participant, BuildType.RECONCILE, false);
		assertTrue(participant.isEnabled(BuildType.BUILD));
		assertFalse(participant.isEnabled(BuildType.RECONCILE));
		participant.restoreDefaults();
		assertFalse(participant.isEnabled(BuildType.BUILD));
		assertFalse(participant.isEnabled(BuildType.RECONCILE));
	}

	private void setEnabled(AbstractBuildParticipant participant, BuildType type, boolean value)
	{
		IBuildParticipantWorkingCopy wc = participant.getWorkingCopy();
		wc.setEnabled(type, value);
		wc.doSave();
	}

	@Test
	public void testChangeFilters() throws Exception
	{
		assertEquals(Collections.emptyList(), participant.getFilters());

		assertSetFilters(".*-me-.*", ".*-webkit-.*");
		assertSetFilters(".*-webkit-.*");

		participant.restoreDefaults();
		assertEquals(Collections.emptyList(), participant.getFilters());

		assertSetFilters(".*-me-.*", ".*-webkit-.*");
	}

	protected void assertSetFilters(String... filters)
	{
		IBuildParticipantWorkingCopy wc = participant.getWorkingCopy();
		wc.setFilters(filters);
		wc.doSave();
		List<String> filtersList = participant.getFilters();
		assertEquals(filters.length, filtersList.size());
		for (int i = 0; i < filters.length; i++)
		{
			assertEquals(filters[i], filtersList.get(i));
		}
	}
}
