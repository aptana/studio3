package com.aptana.editor.html.internal.build;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.build.IProblem;
import com.aptana.core.resources.IMarkerConstants;
import com.aptana.core.util.FileUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.index.core.FileStoreBuildContext;
import com.aptana.index.core.build.BuildContext;

public class HTMLTaskDetectorTest
{

	private HTMLTaskDetector taskDetector;

	@Before
	public void setUp() throws Exception
	{
		taskDetector = new HTMLTaskDetector();
	}

	@After
	public void tearDown() throws Exception
	{
		taskDetector = null;
	}

	@Test
	public void testDetectTaskTagWithUnicodeCharactersInCSSHTMLAndJS() throws Exception
	{
		File tmpDir = null;
		try
		{
			// @formatter:off
			String src = 
			"<html>\n" +
			"<head>\n" +
			"<style>\n" +
			"body {\n" +
			"	/* TODO CSS Comment: Привет */\n" +
			"}\n" +
			"</style>\n" +
			"<script>\n" +
			"  /* TODO JS Comment: Привет */\n" +
			"</script>\n" +
			"</head>\n" +
			"<body>\n" +
			"<!-- TODO HTML comment: Привет -->\n" + 
			"</body>\n" +
			"</html>\n";
			// @formatter:on

			// Generate some files to index!
			tmpDir = new File(FileUtil.getTempDirectory().toOSString(), "testIndex_" + System.currentTimeMillis());
			tmpDir.mkdirs();

			File coffeeFile = new File(tmpDir, "index_me.html");
			IOUtil.write(new FileOutputStream(coffeeFile), src);
			IFileStore fileStore = EFS.getStore(coffeeFile.toURI());

			BuildContext context = new FileStoreBuildContext(fileStore)
			{
				@Override
				public void putProblems(String markerType, Collection<IProblem> newItems)
				{
					fProblems.put(markerType, newItems);
				}
			};
			taskDetector.buildFile(context, new NullProgressMonitor());

			Map<String, Collection<IProblem>> problems = context.getProblems();
			assertTrue(problems.containsKey(IMarkerConstants.TASK_MARKER));
			Collection<IProblem> tasks = problems.get(IMarkerConstants.TASK_MARKER);
			assertEquals(3, tasks.size());
			Iterator<IProblem> iter = tasks.iterator();
			IProblem task = iter.next();
			assertEquals("TODO CSS Comment: Привет", task.getMessage());
			assertEquals(5, task.getLineNumber());
			assertEquals(33, task.getOffset());
			assertEquals(24, task.getLength());
			assertEquals(IMarker.PRIORITY_NORMAL, task.getPriority());
			assertEquals(coffeeFile.toURI().toString(), task.getSourcePath());

			task = iter.next();
			assertEquals("TODO JS Comment: Привет", task.getMessage());
			assertEquals(9, task.getLineNumber());
			assertEquals(86, task.getOffset());
			assertEquals(23, task.getLength());
			assertEquals(IMarker.PRIORITY_NORMAL, task.getPriority());
			assertEquals(coffeeFile.toURI().toString(), task.getSourcePath());

			task = iter.next();
			assertEquals("TODO HTML comment: Привет", task.getMessage());
			assertEquals(13, task.getLineNumber());
			assertEquals(143, task.getOffset());
			assertEquals(25, task.getLength());
			assertEquals(IMarker.PRIORITY_NORMAL, task.getPriority());
			assertEquals(coffeeFile.toURI().toString(), task.getSourcePath());
		}
		finally
		{
			// Clean up the generated files!
			FileUtil.deleteRecursively(tmpDir);
		}
	}
}
