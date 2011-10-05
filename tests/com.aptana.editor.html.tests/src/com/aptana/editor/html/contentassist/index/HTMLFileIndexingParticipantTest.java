package com.aptana.editor.html.contentassist.index;

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
import com.aptana.editor.css.contentassist.index.CSSFileIndexingParticipant;
import com.aptana.editor.js.contentassist.index.JSFileIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

public class HTMLFileIndexingParticipantTest extends TestCase
{

	private HTMLFileIndexingParticipant indexer;
	private List<Task> tasks;
	private CSSFileIndexingParticipant cssIndexer;
	private JSFileIndexingParticipant jsIndexer;

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
		cssIndexer = new CSSFileIndexingParticipant()
		{
			@Override
			protected void createTask(IFileStore store, String message, int priority, int line, int start, int end)
			{
				tasks.add(new Task(store, message, priority, line, start, end));
			}
		};
		jsIndexer = new JSFileIndexingParticipant()
		{
			@Override
			protected void createTask(IFileStore store, String message, int priority, int line, int start, int end)
			{
				tasks.add(new Task(store, message, priority, line, start, end));
			}
		};
		indexer = new HTMLFileIndexingParticipant()
		{
			@Override
			protected void createTask(IFileStore store, String message, int priority, int line, int start, int end)
			{
				tasks.add(new Task(store, message, priority, line, start, end));
			}

			@Override
			protected CSSFileIndexingParticipant createCSSIndexer()
			{
				return cssIndexer;
			}

			@Override
			protected JSFileIndexingParticipant createJSIndexer()
			{
				return jsIndexer;
			}
		};
	}

	protected void tearDown() throws Exception
	{
		tasks = null;
		jsIndexer = null;
		cssIndexer = null;
		indexer = null;
		super.tearDown();
	}

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
			tmpDir = new File(System.getProperty("java.io.tmpdir"), "testIndex_" + System.currentTimeMillis());
			tmpDir.mkdirs();

			File coffeeFile = new File(tmpDir, "index_me.html");
			IOUtil.write(new FileOutputStream(coffeeFile), src);

			Set<IFileStore> files = new HashSet<IFileStore>();
			IFileStore fileStore = EFS.getStore(coffeeFile.toURI());
			files.add(fileStore);
			// Also add in a null to make sure we handle it
			files.add(null);

			Index index = IndexManager.getInstance().getIndex(tmpDir.toURI());
			indexer.index(files, index, new NullProgressMonitor());

			assertEquals(3, tasks.size());
			Task task = tasks.get(0);
			assertEquals("TODO CSS Comment: Привет", task.message);
			assertEquals(5, task.line);
			assertEquals(33, task.start);
			assertEquals(57, task.end);
			assertEquals(IMarker.PRIORITY_NORMAL, task.priority);
			assertEquals(fileStore, task.store);

			task = tasks.get(1);
			assertEquals("TODO JS Comment: Привет", task.message);
			assertEquals(9, task.line);
			assertEquals(86, task.start);
			assertEquals(109, task.end);
			assertEquals(IMarker.PRIORITY_NORMAL, task.priority);
			assertEquals(fileStore, task.store);

			task = tasks.get(2);
			assertEquals("TODO HTML comment: Привет", task.message);
			assertEquals(13, task.line);
			assertEquals(143, task.start);
			assertEquals(168, task.end);
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
