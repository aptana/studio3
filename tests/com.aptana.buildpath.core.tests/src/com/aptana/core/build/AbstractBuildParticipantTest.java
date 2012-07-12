package com.aptana.core.build;

import java.util.Collection;

import junit.framework.TestCase;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.buildpath.core.BuildPathCorePlugin;
import com.aptana.index.core.build.BuildContext;
import com.aptana.parsing.ast.ParseNode;

@SuppressWarnings("nls")
public class AbstractBuildParticipantTest extends TestCase
{

	private AbstractBuildParticipant participant;

	protected void setUp() throws Exception
	{
		super.setUp();

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
		assertEquals(IMarker.SEVERITY_ERROR, error.getSeverity());
		assertEquals(IMarker.PRIORITY_NORMAL, error.getPriority());
	}

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
		assertEquals(IMarker.SEVERITY_WARNING, error.getSeverity());
		assertEquals(IMarker.PRIORITY_NORMAL, error.getPriority());
	}

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
		assertEquals(IMarker.SEVERITY_INFO, error.getSeverity());
		assertEquals(IMarker.PRIORITY_NORMAL, error.getPriority());
	}

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
		assertEquals(IMarker.SEVERITY_INFO, error.getSeverity());
		assertEquals(priority, error.getPriority());
	}

	public void testProcessCommentNode() throws Exception
	{
		String filePath = "example.js";
		String source = "/*\n * TODO This is a task */\n";
		int initialOffset = 0;
		ParseNode commentNode = new ParseNode("javascript");
		commentNode.setLocation(0, source.length());
		String commentEnding = "*/";
		Collection<IProblem> problems = participant.processCommentNode(filePath, source, initialOffset, commentNode,
				commentEnding);
		assertEquals(1, problems.size());
	}

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
