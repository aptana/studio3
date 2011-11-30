package com.aptana.core.build;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.index.core.build.BuildContext;

@SuppressWarnings("nls")
public class AbstractBuildParticipantTest extends TestCase
{

	private AbstractBuildParticipant participant;

	protected void setUp() throws Exception
	{
		super.setUp();

		participant = new AbstractBuildParticipant()
		{

			public void deleteFile(BuildContext context, IProgressMonitor monitor)
			{
			}

			public void buildFile(BuildContext context, IProgressMonitor monitor)
			{
			}
		};
	}

	protected void tearDown() throws Exception
	{
		participant = null;

		super.tearDown();
	}

	public void testGetLineNumber() throws Exception
	{
		String source = "1\n2\n3\n4\n";
		assertEquals("Offset 0 should be line 1", 1, participant.getLineNumber(0, source));
		assertEquals("Offset 1 should be line 1", 1, participant.getLineNumber(1, source));
		assertEquals("Offset 2 should be line 2", 2, participant.getLineNumber(2, source));
		assertEquals("Offset 3 should be line 2", 2, participant.getLineNumber(3, source));
		assertEquals("Offset 4 should be line 3", 3, participant.getLineNumber(4, source));
		assertEquals("Offset 5 should be line 3", 3, participant.getLineNumber(5, source));
		assertEquals("Offset 6 should be line 4", 4, participant.getLineNumber(6, source));
		assertEquals("Offset 7 should be line 4", 4, participant.getLineNumber(7, source));
		assertEquals("Offset past end of source should return -1", -1, participant.getLineNumber(8, source));
	}

}
