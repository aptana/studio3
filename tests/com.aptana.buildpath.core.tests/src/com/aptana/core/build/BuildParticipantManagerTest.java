package com.aptana.core.build;

import java.text.MessageFormat;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.core.internal.build.BuildParticipantManager;
import com.aptana.index.core.build.BuildContext;

public class BuildParticipantManagerTest extends TestCase
{

	private BuildParticipantManager fManager;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		fManager = new BuildParticipantManager();
	}

	@Override
	protected void tearDown() throws Exception
	{
		fManager = null;
		super.tearDown();
	}

	public void testGetAllBuildParticipants() throws Exception
	{
		List<IBuildParticipant> participants = fManager.getAllBuildParticipants();
		assertTrue("Should pick up at least the test build participants specified in this fragment",
				participants.size() >= 4);
		assertSortedByPriorityDescending(participants);

		for (IBuildParticipant participant : participants)
		{
			if (participant instanceof NoPrioritySpecifiedBuildParticipant)
			{
				assertEquals("Incorrect default priority when unspecified", participant.getPriority(),
						AbstractBuildParticipant.DEFAULT_PRIORITY);
				break;
			}
		}
	}

	public void testBuildParticipantsByContentType() throws Exception
	{
		// Two registered explicitly, one has no binding, so applies to all
		assertGetBuildParticipantsByContentTypeId("com.aptana.buildpath.core.fake_content_type1", 3);

		// One registered explicitly, one has no binding, so applies to all
		assertGetBuildParticipantsByContentTypeId("com.aptana.buildpath.core.fake_content_type2", 2);

		// One registered explicitly, one has no binding, so applies to all
		assertGetBuildParticipantsByContentTypeId("com.aptana.buildpath.core.fake_content_type3", 2);

		// None registered explicitly, one has no binding, so applies to all
		assertGetBuildParticipantsByContentTypeId("com.aptana.buildpath.core.fake_content_type4", 1);
	}

	/**
	 * We assert the count is at least minParticipantCount, since real participants will get loaded during testing too.
	 * 
	 * @param contentTypeId
	 * @param minParticipantCount
	 */
	protected void assertGetBuildParticipantsByContentTypeId(String contentTypeId, int minParticipantCount)
	{
		List<IBuildParticipant> participants = fManager.getBuildParticipants(contentTypeId);
		assertTrue("Did not get the minimum expected number of participants for content type",
				participants.size() >= minParticipantCount);
		assertSortedByPriorityDescending(participants);
	}

	protected static void assertSortedByPriorityDescending(List<IBuildParticipant> participants)
	{
		int priority = Integer.MAX_VALUE;
		for (IBuildParticipant participant : participants)
		{
			int participantPriority = participant.getPriority();
			assertTrue(
					MessageFormat.format(
							"Participants were not sorted in descending priority. Current participant priority = {0}, previous = {1}",
							participantPriority, priority), participantPriority <= priority);
			priority = participantPriority;
		}
	}

	/**
	 * Class used for the registered build participants, solely for testing!
	 * 
	 * @author cwilliams
	 */
	public static class TestBuildParticipant extends AbstractBuildParticipant
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

	public static class NoPrioritySpecifiedBuildParticipant extends AbstractBuildParticipant
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
