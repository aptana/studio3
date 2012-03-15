package com.aptana.core.build;

import java.util.Collections;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.core.build.IBuildParticipant.BuildType;
import com.aptana.core.util.EclipseUtil;
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
		participant.setEnabled(BuildType.BUILD, false);
		participant.setEnabled(BuildType.RECONCILE, false);
		assertTrue(participant.isEnabled(BuildType.BUILD));
		assertTrue(participant.isEnabled(BuildType.RECONCILE));
	}

	public void testRequiredParticipantCantSetFilters() throws Exception
	{
		assertTrue(participant.isRequired());
		assertEquals(Collections.emptyList(), participant.getFilters());

		String[] filters = new String[] { ".*-wbkit-.*" };
		participant.setFilters(EclipseUtil.instanceScope(), filters);
		assertEquals(Collections.emptyList(), participant.getFilters());
	}

	private static final class TestParticipant extends RequiredBuildParticipant
	{
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
