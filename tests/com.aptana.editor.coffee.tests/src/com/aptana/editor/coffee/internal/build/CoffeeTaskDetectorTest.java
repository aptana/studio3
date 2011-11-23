package com.aptana.editor.coffee.internal.build;

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
import com.aptana.core.util.FileUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.index.core.FileStoreBuildContext;
import com.aptana.index.core.build.BuildContext;

public class CoffeeTaskDetectorTest extends TestCase
{
	private CoffeeTaskDetector taskDetector;

	protected void setUp() throws Exception
	{
		super.setUp();
		taskDetector = new CoffeeTaskDetector();
	}

	protected void tearDown() throws Exception
	{
		taskDetector = null;
		super.tearDown();
	}

	public void testDetectTaskTag() throws Exception
	{
		File tmpDir = null;
		try
		{
			String src = "# TODO This is a task\n";

			// Generate some files to index!
			tmpDir = new File(System.getProperty("java.io.tmpdir"), "testCoffeeTask_" + System.currentTimeMillis());
			tmpDir.mkdirs();

			File coffeeFile = new File(tmpDir, "index_me.coffee");
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
			taskDetector.buildFile(context, new NullProgressMonitor());

			Map<String, Collection<IProblem>> problems = context.getProblems();
			assertTrue(problems.containsKey(IMarker.TASK));
			Collection<IProblem> tasks = problems.get(IMarker.TASK);
			assertEquals(1, tasks.size());
			IProblem task = tasks.iterator().next();
			assertEquals("TODO This is a task", task.getMessage());
			assertEquals(1, task.getLineNumber());
			assertEquals(2, task.getOffset());
			assertEquals(19, task.getLength());
			assertEquals(IMarker.PRIORITY_NORMAL, task.getPriority());
			assertEquals(IMarker.SEVERITY_INFO, task.getSeverity());
			assertEquals(coffeeFile.toURI().toString(), task.getSourcePath());
		}
		finally
		{
			// Clean up the generated files!
			FileUtil.deleteRecursively(tmpDir);
		}
	}

	public void testDetectWithEmptyContent() throws Exception
	{
		File tmpDir = null;
		try
		{
			String src = "\n";

			// Generate some files to index!
			tmpDir = new File(System.getProperty("java.io.tmpdir"), "testCoffeeTaskWithEmptyContent"
					+ System.currentTimeMillis());
			tmpDir.mkdirs();

			File coffeeFile = new File(tmpDir, "index_me.coffee");
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

			taskDetector.buildFile(context, new NullProgressMonitor());
			
			Map<String, Collection<IProblem>> problems = context.getProblems();
			assertTrue(problems.containsKey(IMarker.TASK));
			Collection<IProblem> tasks = problems.get(IMarker.TASK);
			assertEquals(0, tasks.size());
		}
		finally
		{
			// Clean up the generated files!
			FileUtil.deleteRecursively(tmpDir);
		}
	}
}
