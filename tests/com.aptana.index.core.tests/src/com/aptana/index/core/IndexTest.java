package com.aptana.index.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Test;

@SuppressWarnings({ "nls", "deprecation" })
public class IndexTest
{

	private Index index;
	private File indexDir;

	@After
	public void tearDown() throws Exception
	{
		if (indexDir != null)
		{
			indexDir.delete();
			indexDir = null;
		}
		if (index != null)
		{
			getIndexManager().removeIndex(index.getRoot());
			index = null;
		}

	}

	protected void createIndex(String name) throws IOException
	{
		File tmpFile = File.createTempFile(name, ".index");
		tmpFile.deleteOnExit();
		File parent = tmpFile.getParentFile();
		indexDir = new File(parent, name);
		indexDir.mkdirs();
		URI path = indexDir.toURI();
		index = getIndexManager().getIndex(path);
	}

	protected IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
	}

	/**
	 * This helps expose multithreading issues when not using locks properly. This will not always fail if locks are
	 * wrong, but will fail most of the time. It shoudl never fail if locks are done properly.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMultiThreadedRemoveAndQuery() throws Exception
	{
		createIndex("multi_threaded");

		for (int i = 0; i < 1000; i++)
		{
			index.addEntry("category", "Some" + i + "/Cryptic/Key", new URI("fake" + i + ".rb"));
		}

		final int NUM_LOOPS = 2000;
		final boolean[] failures = new boolean[1];
		failures[0] = false;
		Thread t1 = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					for (int i = 0; i < NUM_LOOPS; i++)
					{
						index.query(new String[] { "category" }, "Some", SearchPattern.PREFIX_MATCH
								| SearchPattern.CASE_SENSITIVE);
						Thread.yield();
					}
				}
				catch (ConcurrentModificationException cme)
				{
					cme.printStackTrace();
					failures[0] = true;
					return;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					fail(e.getMessage());
				}
			}
		});

		Thread t2 = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					for (int i = 0; i < NUM_LOOPS; i++)
					{
						index.remove(new URI("other" + i + ".rb"));
						Thread.yield();
					}
				}
				catch (ConcurrentModificationException cme)
				{
					cme.printStackTrace();
					failures[0] = true;
					return;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					fail(e.getMessage());
				}
			}
		});

		t1.start();
		t2.start();

		t1.join();
		t2.join();
		assertFalse("Received a ConcurrentModificationException while accessing index", failures[0]);
	}

	@Test
	public void testAddEntry() throws Exception
	{
		createIndex("add_entry");
		index.addEntry("category", "key", new URI("relative_path.rb"));

		assertEntryAdded();
	}

	protected void assertEntryAdded()
	{
		List<String> categories = index.getCategories();
		assertEquals(1, categories.size());
		assertEquals("category", categories.get(0));

		List<QueryResult> result = index.query(new String[] { "category" }, "key", SearchPattern.EXACT_MATCH
				| SearchPattern.CASE_SENSITIVE);
		assertNotNull(result);
		assertEquals(1, result.size());
		QueryResult qr = result.get(0);
		assertEquals("key", qr.getWord());

		Set<String> docs = qr.getDocuments();
		assertNotNull(docs);
		assertEquals(1, docs.size());
		assertEquals("relative_path.rb", docs.iterator().next());
	}

	@Test
	public void testQueryDocumentNames() throws Exception
	{
		createIndex("query_doc_names");
		index.addEntry("category", "key1", new URI("relative_path.rb"));
		index.addEntry("category", "key2", new URI("relate.rb"));
		index.addEntry("category", "key3", new URI("relax.rb"));
		index.addEntry("category", "key4", new URI("blah.rb"));

		Set<String> docNames = index.queryDocumentNames("rel");
		assertEquals(3, docNames.size());
		assertTrue(docNames.contains("relative_path.rb"));
		assertTrue(docNames.contains("relate.rb"));
		assertTrue(docNames.contains("relax.rb"));
		assertFalse(docNames.contains("blah.rb"));

		docNames = index.queryDocumentNames("relat");
		assertEquals(2, docNames.size());
		assertTrue(docNames.contains("relative_path.rb"));
		assertTrue(docNames.contains("relate.rb"));
		assertFalse(docNames.contains("relax.rb"));
		assertFalse(docNames.contains("blah.rb"));

		docNames = index.queryDocumentNames(null);
		assertEquals(4, docNames.size());
		assertTrue(docNames.contains("relative_path.rb"));
		assertTrue(docNames.contains("relate.rb"));
		assertTrue(docNames.contains("relax.rb"));
		assertTrue(docNames.contains("blah.rb"));
	}

	@Test
	public void testRemove() throws Exception
	{
		// add an entry...
		testAddEntry();
		// now remove it and verify...
		index.remove(new URI("relative_path.rb"));

		List<String> categories = index.getCategories();
		assertTrue(categories.isEmpty());

		List<QueryResult> result = index.query(new String[] { "category" }, "key", SearchPattern.EXACT_MATCH
				| SearchPattern.CASE_SENSITIVE);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testRemoveCategories() throws Exception
	{
		createIndex("remove_categories");
		// Add 3 keys in separate categories in first file.
		index.addEntry("category1", "key1", new URI("file1.rb"));
		index.addEntry("category2", "key1", new URI("file1.rb"));
		index.addEntry("category3", "key1", new URI("file1.rb"));

		// Add 2 keys in separate categories in second file.
		index.addEntry("category1", "key1", new URI("file2.rb"));
		index.addEntry("category3", "key1", new URI("file2.rb"));

		// Add 2 diffrent keys in separate categories in third file.
		index.addEntry("category1", "otherkey1", new URI("file3.rb"));
		index.addEntry("category2", "otherkey1", new URI("file3.rb"));

		// Now remove the categories
		index.removeCategories("category1", "category3");

		// Now verify categories were removed, but the rest of the stuff is OK

		// verify "category2" remains
		List<String> categories = index.getCategories();
		assertEquals(1, categories.size());
		assertEquals("category2", categories.get(0));

		// Search category2
		List<QueryResult> result = index.query(new String[] { "category2" }, "*key*", SearchPattern.PATTERN_MATCH
				| SearchPattern.CASE_SENSITIVE);
		assertNotNull(result);
		assertEquals(2, result.size());
		QueryResult qr = result.get(0);
		assertEquals("key1", qr.getWord());

		Set<String> docs = qr.getDocuments();
		assertNotNull(docs);
		assertEquals(1, docs.size());
		assertEquals("file1.rb", docs.iterator().next());

		qr = result.get(1);
		assertEquals("otherkey1", qr.getWord());

		docs = qr.getDocuments();
		assertNotNull(docs);
		assertEquals(1, docs.size());
		assertEquals("file3.rb", docs.iterator().next());

		// TODO Is there any way to verify that the disk index doesn't retain wasted/bad refs to old
		// categories/words/files?
	}

	@Test
	public void testSave() throws Exception
	{
		// add an entry...
		testAddEntry();

		// save...
		index.save();

		// make sure after saving we can still pull up the same results/entry
		assertEntryAdded();
	}

}
