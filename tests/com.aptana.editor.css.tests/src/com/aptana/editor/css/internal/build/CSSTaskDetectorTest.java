package com.aptana.editor.css.internal.build;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.aptana.core.build.IProblem;
import com.aptana.core.resources.IMarkerConstants;
import com.aptana.core.util.FileUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.index.core.FileStoreBuildContext;
import com.aptana.index.core.build.BuildContext;

public class CSSTaskDetectorTest extends TestCase
{
	private CSSTaskDetector taskDetector;

	protected void setUp() throws Exception
	{
		super.setUp();
		taskDetector = new CSSTaskDetector();
	}

	protected void tearDown() throws Exception
	{
		taskDetector = null;
		super.tearDown();
	}

	public void testDetectTaskTagWithUnicodeCharacters() throws Exception
	{
		File tmpDir = null;
		try
		{
			// @formatter:off
			String src = 
			"body {\n" +
			"	/* TODO: Привет */\n" +
			"}\n";
			// @formatter:on

			// Generate some files to index!
			tmpDir = new File(System.getProperty("java.io.tmpdir"), "testIndex_" + System.currentTimeMillis());
			tmpDir.mkdirs();

			File coffeeFile = new File(tmpDir, "index_me.css");
			IOUtil.write(new FileOutputStream(coffeeFile), src, "UTF-8");
			IFileStore fileStore = EFS.getStore(coffeeFile.toURI());

			BuildContext context = new FileStoreBuildContext(fileStore)
			{
				@Override
				public void putProblems(String markerType, Collection<IProblem> newItems)
				{
					problems.put(markerType, newItems);
				}
			};
			taskDetector.buildFile(context, new NullProgressMonitor());

			Map<String, Collection<IProblem>> problems = context.getProblems();
			assertTrue(problems.containsKey(IMarkerConstants.TASK_MARKER));
			Collection<IProblem> tasks = problems.get(IMarkerConstants.TASK_MARKER);
			assertEquals(1, tasks.size());
			IProblem task = tasks.iterator().next();
			assertEquals("TODO: Привет", task.getMessage());
			assertEquals(2, task.getLineNumber());
			assertEquals(11, task.getOffset());
			assertEquals(12, task.getLength());
			assertEquals(IMarker.PRIORITY_NORMAL, task.getPriority());
			assertEquals(IMarker.SEVERITY_INFO, task.getSeverity());
			assertEquals(coffeeFile.toURI().toString(), task.getSourcePath());

			// Now "delete" the file, make sure it wipes the tasks.
			taskDetector.deleteFile(context, new NullProgressMonitor());
			problems = context.getProblems();
			assertTrue(problems.isEmpty());
		}
		finally
		{
			// Clean up the generated files!
			FileUtil.deleteRecursively(tmpDir);
		}
	}

	public void testDeleteFileNullContext() throws Exception
	{
		taskDetector.deleteFile(null, null);
		assertTrue(true);
	}
}
