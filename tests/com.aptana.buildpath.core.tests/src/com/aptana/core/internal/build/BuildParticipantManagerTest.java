package com.aptana.core.internal.build;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentType;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IBuildParticipant;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.index.core.build.BuildContext;

public class BuildParticipantManagerTest
{

	private BuildParticipantManager fManager;

//	@Override
	@Before
	public void setUp() throws Exception
	{
//		super.setUp();
		fManager = new BuildParticipantManager();
	}

//	@Override
	@After
	public void tearDown() throws Exception
	{
		fManager = null;
//		super.tearDown();
	}

	@Test
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

	@Test
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

	@Test
	public void testGetContentTypes() throws Exception
	{
		Set<IContentType> types = fManager.getContentTypes();
		assertNotNull(types);
		assertTrue("Expected to get at least the 3 fake content types registered with testing participants",
				types.size() >= 3);
		assertContains(types, "com.aptana.buildpath.core.fake_content_type1",
				"com.aptana.buildpath.core.fake_content_type2", "com.aptana.buildpath.core.fake_content_type3");
	}

	@Test
	public void testFilterParticipants() throws Exception
	{
		List<IBuildParticipant> participants = fManager.getAllBuildParticipants();
		int size = participants.size();
		assertFiltered(participants, "com.aptana.buildpath.core.fake_content_type1", 3);
		assertEquals(
				"Original list of participants changed size when filter was called. Should have operated on a copy!",
				size, participants.size());
		assertFiltered(participants, "com.aptana.buildpath.core.fake_content_type2", 2);
		assertEquals(
				"Original list of participants changed size when filter was called. Should have operated on a copy!",
				size, participants.size());
		assertFiltered(participants, "com.aptana.buildpath.core.fake_content_type3", 2);
		assertEquals(
				"Original list of participants changed size when filter was called. Should have operated on a copy!",
				size, participants.size());
		assertFiltered(participants, "com.aptana.buildpath.core.fake_content_type4", 1);
		assertEquals(
				"Original list of participants changed size when filter was called. Should have operated on a copy!",
				size, participants.size());
	}

	protected void assertFiltered(List<IBuildParticipant> participants, String contentTypeId, int min)
	{
		assertBuildParticipantMininmumAndSorted(fManager.filterParticipants(participants, contentTypeId),
				contentTypeId, min);
	}

	protected void assertContains(Set<IContentType> types, String... expectedTypes)
	{
		Set<String> expected = CollectionsUtil.newSet(expectedTypes);
		for (IContentType type : types)
		{
			String id = type.getId();
			if (expected.contains(id))
			{
				expected.remove(id);
				if (expected.isEmpty())
				{
					return;
				}
			}
		}
		if (!expected.isEmpty())
		{
			fail(MessageFormat.format("Expected, but did not find, types <{0}> in set <{1}>", expected, types));
		}
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
		assertBuildParticipantMininmumAndSorted(participants, contentTypeId, minParticipantCount);
	}

	private void assertBuildParticipantMininmumAndSorted(List<IBuildParticipant> participants, String contentTypeId,
			int minParticipantCount)
	{
		assertTrue(MessageFormat.format(
				"Did not get the minimum expected number ({0}) of participants for content type {1}",
				minParticipantCount, contentTypeId), participants.size() >= minParticipantCount);
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
