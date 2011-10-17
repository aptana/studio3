package com.aptana.editor.css.contentassist.index;

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
import org.eclipse.core.runtime.NullProgressMonitor;

import com.aptana.core.util.FileUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

public class CSSFileIndexingParticipantTest extends TestCase
{

	private CSSFileIndexingParticipant indexer;
	private List<Task> tasks;

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

	protected void setUp() throws Exception
	{
		super.setUp();
		tasks = new ArrayList<Task>();
		indexer = new CSSFileIndexingParticipant()
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
			IOUtil.write(new FileOutputStream(coffeeFile), src);

			Set<IFileStore> files = new HashSet<IFileStore>();
			IFileStore fileStore = EFS.getStore(coffeeFile.toURI());
			files.add(fileStore);
			// Also add in a null to make sure we handle it
			files.add(null);

			Index index = IndexManager.getInstance().getIndex(tmpDir.toURI());
			indexer.index(files, index, new NullProgressMonitor());

			assertEquals(1, tasks.size());
			Task task = tasks.get(0);
			assertEquals("TODO: Привет", task.message);
			assertEquals(2, task.line);
			assertEquals(11, task.start);
			assertEquals(23, task.end);
			assertEquals(IMarker.PRIORITY_NORMAL, task.priority);
			assertEquals(fileStore, task.store);
		}
		finally
		{
			// Clean up the generated files!
			FileUtil.deleteRecursively(tmpDir);
		}
	}
}
