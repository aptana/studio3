package com.aptana.editor.js.contentassist;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.jobs.Job;

import com.aptana.core.util.FileUtil;
import com.aptana.editor.js.JSMetadataLoader;
import com.aptana.editor.js.contentassist.index.JSIndexWriter;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;

public class JSIndexQueryHelperTest extends TestCase
{

	private Index index;
	private IndexManager manager;
	private JSIndexQueryHelper helper;

	protected void setUp() throws Exception
	{
		super.setUp();

		helper = new JSIndexQueryHelper();
		manager = IndexPlugin.getDefault().getIndexManager();

		File dir = new File(FileUtil.getTempDirectory().toFile(), "index_helper");
		dir.mkdir();
		dir.deleteOnExit();

		index = manager.getIndex(dir.toURI());
	}

	protected void tearDown() throws Exception
	{
		manager.removeIndex(index.getRoot());
		index = null;
		helper = null;
		manager = null;
		super.tearDown();
	}

	public void testGetTypeAncestorNames() throws Exception
	{
		TypeElement type = new TypeElement();
		type.setName("MadeUpType");
		type.addParentType("Array<String>");

		JSIndexWriter writer = new JSIndexWriter();
		writer.writeType(index, type);

		Job job = new JSMetadataLoader();
		job.schedule();
		job.join();

		List<String> ancestors = helper.getTypeAncestorNames(index, type.getName());
		assertEquals("ancestors size", 2, ancestors.size());
		assertTrue("ancestors contains Array<String>", ancestors.contains("Array<String>"));
		assertTrue("ancestors contains Object", ancestors.contains("Object"));
	}

}
