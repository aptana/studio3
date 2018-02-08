package com.aptana.core.build;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.buildpath.core.BuildPathCorePlugin;
import com.aptana.core.resources.TaskTag;
import com.aptana.index.core.build.BuildContext;
import com.aptana.parsing.ast.ParseNode;

@SuppressWarnings("nls")
public class AbstractBuildParticipantTest
{

	private AbstractBuildParticipant participant;

	@Before
	public void setUp() throws Exception
	{
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
			
			protected Collection<TaskTag> getTaskTags()
			{
				return Arrays.asList(new TaskTag("TODO", IMarker.PRIORITY_NORMAL));
			}
		};
	}

	@After
	public void tearDown() throws Exception
	{
		participant = null;
	}

	@Test
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

	@Test
	public void testCreateError() throws Exception
	{
		String message = "message";
		int linenumber = 1;
		int offset = 2;
		int length = 3;
		String sourcePath = "sourcePath";
		IProblem error = participant.createError(message, linenumber, offset, length, sourcePath);
		assertNotNull(error);
		assertEquals(message, error.getMessage());
		assertEquals(length, error.getLength());
		assertEquals(linenumber, error.getLineNumber());
		assertEquals(offset, error.getOffset());
		assertEquals(sourcePath, error.getSourcePath());
		assertEquals(IMarker.SEVERITY_ERROR, error.getSeverity().intValue());
		assertEquals(IMarker.PRIORITY_NORMAL, error.getPriority());
	}

	@Test
	public void testCreateWarning() throws Exception
	{
		String message = "message";
		int linenumber = 1;
		int offset = 2;
		int length = 3;
		String sourcePath = "sourcePath";
		IProblem error = participant.createWarning(message, linenumber, offset, length, sourcePath);
		assertNotNull(error);
		assertEquals(message, error.getMessage());
		assertEquals(length, error.getLength());
		assertEquals(linenumber, error.getLineNumber());
		assertEquals(offset, error.getOffset());
		assertEquals(sourcePath, error.getSourcePath());
		assertEquals(IMarker.SEVERITY_WARNING, error.getSeverity().intValue());
		assertEquals(IMarker.PRIORITY_NORMAL, error.getPriority());
	}

	@Test
	public void testCreateInfo() throws Exception
	{
		String message = "message";
		int linenumber = 1;
		int offset = 2;
		int length = 3;
		String sourcePath = "sourcePath";
		IProblem error = participant.createInfo(message, linenumber, offset, length, sourcePath);
		assertNotNull(error);
		assertEquals(message, error.getMessage());
		assertEquals(length, error.getLength());
		assertEquals(linenumber, error.getLineNumber());
		assertEquals(offset, error.getOffset());
		assertEquals(sourcePath, error.getSourcePath());
		assertEquals(IMarker.SEVERITY_INFO, error.getSeverity().intValue());
		assertEquals(IMarker.PRIORITY_NORMAL, error.getPriority());
	}

	@Test
	public void testCreateTask() throws Exception
	{
		String message = "message";
		int linenumber = 1;
		int offset = 2;
		int length = 3;
		int priority = IMarker.PRIORITY_HIGH;
		String sourcePath = "sourcePath";
		IProblem error = participant.createTask(sourcePath, message, priority, linenumber, offset, offset + length);
		assertNotNull(error);
		assertEquals(message, error.getMessage());
		assertEquals(length, error.getLength());
		assertEquals(linenumber, error.getLineNumber());
		assertEquals(offset, error.getOffset());
		assertEquals(sourcePath, error.getSourcePath());
		assertEquals(IMarker.SEVERITY_INFO, error.getSeverity().intValue());
		assertEquals(priority, error.getPriority());
	}

	@Test
	public void testProcessCommentNode() throws Exception
	{
		String filePath = "example.js";
		String source = "/*\n * TODO This is a task */\n";
		int initialOffset = 0;
		ParseNode commentNode = new ParseNode()
		{

			public String getLanguage()
			{
				return "javascript";
			}
		};
		commentNode.setLocation(0, source.length());
		String commentEnding = "*/";
		Collection<IProblem> problems = participant.processCommentNode(filePath, source, initialOffset, commentNode,
				commentEnding);
		assertEquals(1, problems.size());
	}

	@Test
	public void testProcessCommentNodeWithNullNodeReturnsEmptyCollection() throws Exception
	{
		String filePath = "example.js";
		String source = "/*\n * TODO This is a task */\n";
		int initialOffset = 0;
		String commentEnding = "*/";
		Collection<IProblem> problems = participant.processCommentNode(filePath, source, initialOffset, null,
				commentEnding);
		assertTrue(problems.isEmpty());
	}
}
