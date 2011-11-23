package com.aptana.editor.js.internal.build;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.aptana.core.build.IProblem;
import com.aptana.core.util.FileUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.index.core.FileStoreBuildContext;
import com.aptana.index.core.build.BuildContext;

import junit.framework.TestCase;

public class JSTaskDetectorTest extends TestCase
{

	private JSTaskDetector indexer;

	protected void setUp() throws Exception
	{
		super.setUp();
		indexer = new JSTaskDetector();
	}

	protected void tearDown() throws Exception
	{
		indexer = null;
		super.tearDown();
	}

	public void testDetectTaskTagWithUnicodeCharacters() throws Exception
	{
		File tmpDir = null;
		try
		{
			// @formatter:off
			String src = "  /* TODO JS Comment: Привет */\n";
			// @formatter:on

			// Generate some files to index!
			tmpDir = new File(System.getProperty("java.io.tmpdir"), "testJSTask_" + System.currentTimeMillis());
			tmpDir.mkdirs();

			File coffeeFile = new File(tmpDir, "index_me.js");
			IOUtil.write(new FileOutputStream(coffeeFile), src);
			IFileStore fileStore = EFS.getStore(coffeeFile.toURI());
			
			BuildContext context = new FileStoreBuildContext(fileStore)
			{
				@Override
				public void putProblems(String markerType, Collection<IProblem> newItems)
				{
					problems.put(markerType, newItems);
				}
			};
			indexer.buildFile(context, new NullProgressMonitor());

			Map<String, Collection<IProblem>> problems = context.getProblems();
			assertTrue(problems.containsKey(IMarker.TASK));
			Collection<IProblem> tasks = problems.get(IMarker.TASK);			
			assertEquals(1, tasks.size());
			Iterator<IProblem> iter = tasks.iterator();
			IProblem task = iter.next();
			assertEquals("TODO JS Comment: Привет", task.getMessage());
			assertEquals(1, task.getLineNumber());
			assertEquals(5, task.getOffset());
			assertEquals(23, task.getLength());
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
