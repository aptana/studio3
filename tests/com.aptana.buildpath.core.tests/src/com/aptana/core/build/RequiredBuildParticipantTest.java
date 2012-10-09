package com.aptana.core.build;

import java.util.Collections;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.buildpath.core.BuildPathCorePlugin;
import com.aptana.core.build.IBuildParticipant.BuildType;
import com.aptana.index.core.build.BuildContext;

public class RequiredBuildParticipantTest extends TestCase
{

	private RequiredBuildParticipant participant;

	protected void setUp() throws Exception
	{
		super.setUp();
		participant = new TestParticipant();
	}

	protected void tearDown() throws Exception
	{
		participant = null;
		super.tearDown();
	}

	public void testRequiredParticipantCantBeDisabled() throws Exception
	{
		assertTrue(participant.isRequired());
		assertTrue(participant.isEnabled(BuildType.BUILD));
		assertTrue(participant.isEnabled(BuildType.RECONCILE));

		IBuildParticipantWorkingCopy wc = participant.getWorkingCopy();
		wc.setEnabled(BuildType.BUILD, false);
		wc.setEnabled(BuildType.RECONCILE, false);
		wc.doSave();

		assertTrue(participant.isEnabled(BuildType.BUILD));
		assertTrue(participant.isEnabled(BuildType.RECONCILE));
	}

	public void testRequiredParticipantCantSetFilters() throws Exception
	{
		assertTrue(participant.isRequired());
		assertEquals(Collections.emptyList(), participant.getFilters());

		IBuildParticipantWorkingCopy wc = participant.getWorkingCopy();
		String[] filters = new String[] { ".*-wbkit-.*" };
		wc.setFilters(filters);
		wc.doSave();

		assertEquals(Collections.emptyList(), participant.getFilters());
	}

	private static final class TestParticipant extends RequiredBuildParticipant
	{
		protected String getPreferenceNode()
		{
			return BuildPathCorePlugin.PLUGIN_ID;
		}

		public void buildFile(BuildContext context, IProgressMonitor monitor)
		{
			// no-op
		}

		public void deleteFile(BuildContext context, IProgressMonitor monitor)
		{
			// no-op
		}
	}
}
