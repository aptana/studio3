package com.aptana.js.core.build;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Iterator;
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
import com.aptana.js.core.build.JSTaskDetector;

public class JSTaskDetectorTest
{

	private JSTaskDetector indexer;

	@Before
	public void setUp() throws Exception
	{
//		super.setUp();
		indexer = new JSTaskDetector();
	}

	@After
	public void tearDown() throws Exception
	{
		indexer = null;
//		super.tearDown();
	}

	@Test
	public void testDetectTaskTagWithUnicodeCharacters() throws Exception
	{
		File tmpDir = null;
		try
		{
			// @formatter:off
			String src = "  /* TODO JS Comment: Привет */\n";
			// @formatter:on

			// Generate some files to index!
			tmpDir = new File(FileUtil.getTempDirectory().toOSString(), "testJSTask_" + System.currentTimeMillis());
			tmpDir.mkdirs();

			File coffeeFile = new File(tmpDir, "index_me.js");
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
			indexer.buildFile(context, new NullProgressMonitor());

			Map<String, Collection<IProblem>> problems = context.getProblems();
			assertTrue(problems.containsKey(IMarkerConstants.TASK_MARKER));
			Collection<IProblem> tasks = problems.get(IMarkerConstants.TASK_MARKER);
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
