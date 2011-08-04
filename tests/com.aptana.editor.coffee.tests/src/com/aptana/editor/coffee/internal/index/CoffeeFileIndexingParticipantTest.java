package com.aptana.editor.coffee.internal.index;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.aptana.core.util.FileUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

public class CoffeeFileIndexingParticipantTest extends TestCase
{
	private CoffeeFileIndexingParticipant indexer;
	private List<Task> tasks;

	protected void setUp() throws Exception
	{
		super.setUp();
		tasks = new ArrayList<Task>();
		indexer = new CoffeeFileIndexingParticipant()
		{
			@Override
			protected void createTask(IFileStore store, String message, int priority, int line, int start, int end)
			{
				tasks.add(new Task(store, message, priority, line, start, end));
			}
		};
	}

	protected void tearDown() throws Exception
	{
		tasks = null;
		indexer = null;
		super.tearDown();
	}

	public void testDetectTaskTag() throws Exception
	{
		File tmpDir = null;
		try
		{
			String src = "# TODO This is a task\n";

			// Generate some files to index!
			tmpDir = new File(System.getProperty("java.io.tmpdir"), "testIndex_" + System.currentTimeMillis());
			tmpDir.mkdirs();

			File coffeeFile = new File(tmpDir, "index_me.coffee");
			IOUtil.write(new FileOutputStream(coffeeFile), src);

			Set<IFileStore> files = new HashSet<IFileStore>();
			IFileStore fileStore = EFS.getStore(coffeeFile.toURI());
			files.add(fileStore);
			// Also add in a null to make sure we handle it
			files.add(null);

			Index index = IndexManager.getInstance().getIndex(tmpDir.toURI());
			IProgressMonitor monitor = new NullProgressMonitor();
			indexer.index(files, index, monitor);

			assertEquals(1, tasks.size());
			Task task = tasks.get(0);
			assertEquals("TODO This is a task", task.message);
			assertEquals(1, task.line);
			assertEquals(2, task.start);
			assertEquals(21, task.end);
			assertEquals(IMarker.PRIORITY_NORMAL, task.priority);
			assertEquals(fileStore, task.store);
		}
		finally
		{
			// Clean up the generated files!
			FileUtil.deleteRecursively(tmpDir);
		}
	}

	public void testIndexWithNullIFileStore() throws Exception
	{
		File tmpDir = null;
		try
		{
			String src = "# TODO This is a task\n";

			// Generate some files to index!
			tmpDir = new File(System.getProperty("java.io.tmpdir"), "testIndexWithNullIFileStore"
					+ System.currentTimeMillis());
			tmpDir.mkdirs();

			File coffeeFile = new File(tmpDir, "index_me.coffee");
			IOUtil.write(new FileOutputStream(coffeeFile), src);

			Set<IFileStore> files = new HashSet<IFileStore>();
			IFileStore fileStore = EFS.getStore(coffeeFile.toURI());
			// add in a null to make sure we handle it and move on
			files.add(null);
			files.add(fileStore);
			files.add(null);

			Index index = IndexManager.getInstance().getIndex(tmpDir.toURI());
			IProgressMonitor monitor = new NullProgressMonitor();
			indexer.index(files, index, monitor);

			assertEquals(1, tasks.size());
			Task task = tasks.get(0);
			assertEquals("TODO This is a task", task.message);
			assertEquals(1, task.line);
			assertEquals(2, task.start);
			assertEquals(21, task.end);
			assertEquals(IMarker.PRIORITY_NORMAL, task.priority);
			assertEquals(fileStore, task.store);
		}
		finally
		{
			// Clean up the generated files!
			FileUtil.deleteRecursively(tmpDir);
		}
	}

	public void testIndexWithNullIndex() throws Exception
	{
		File tmpDir = null;
		try
		{
			String src = "# TODO This is a task\n";

			// Generate some files to index!
			tmpDir = new File(System.getProperty("java.io.tmpdir"), "testIndexWithNullIndex"
					+ System.currentTimeMillis());
			tmpDir.mkdirs();

			File coffeeFile = new File(tmpDir, "index_me.coffee");
			IOUtil.write(new FileOutputStream(coffeeFile), src);

			Set<IFileStore> files = new HashSet<IFileStore>();
			IFileStore fileStore = EFS.getStore(coffeeFile.toURI());
			files.add(fileStore);

			IProgressMonitor monitor = new NullProgressMonitor();
			indexer.index(files, null, monitor);

			assertEquals(1, tasks.size());
			Task task = tasks.get(0);
			assertEquals("TODO This is a task", task.message);
			assertEquals(1, task.line);
			assertEquals(2, task.start);
			assertEquals(21, task.end);
			assertEquals(IMarker.PRIORITY_NORMAL, task.priority);
			assertEquals(fileStore, task.store);
		}
		finally
		{
			// Clean up the generated files!
			FileUtil.deleteRecursively(tmpDir);
		}
	}

	public void testIndexWithEmptyContent() throws Exception
	{
		File tmpDir = null;
		try
		{
			String src = "\n";

			// Generate some files to index!
			tmpDir = new File(System.getProperty("java.io.tmpdir"), "testIndexWithEmptyContent"
					+ System.currentTimeMillis());
			tmpDir.mkdirs();

			File coffeeFile = new File(tmpDir, "index_me.coffee");
			IOUtil.write(new FileOutputStream(coffeeFile), src);

			Set<IFileStore> files = new HashSet<IFileStore>();
			IFileStore fileStore = EFS.getStore(coffeeFile.toURI());
			files.add(fileStore);

			IProgressMonitor monitor = new NullProgressMonitor();
			indexer.index(files, null, monitor);

			assertEquals(0, tasks.size());
		}
		finally
		{
			// Clean up the generated files!
			FileUtil.deleteRecursively(tmpDir);
		}
	}

	// TODO Test cancelling progress monitor in middle of indexing!

	private static class Task
	{

		IFileStore store;
		String message;
		int priority;
		int line;
		int start;
		int end;

		Task(IFileStore store, String message, int priority, int line, int start, int end)
		{
			this.store = store;
			this.message = message;
			this.priority = priority;
			this.line = line;
			this.start = start;
			this.end = end;
		}

	}
}
