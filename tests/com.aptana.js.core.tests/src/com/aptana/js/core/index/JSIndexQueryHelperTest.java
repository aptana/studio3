package com.aptana.js.core.index;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.jobs.Job;

import com.aptana.core.tests.TestProject;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.js.core.model.TypeElement;
import com.aptana.js.internal.core.index.JSIndexWriter;
import com.aptana.js.internal.core.index.JSMetadataLoader;

public class JSIndexQueryHelperTest
{

	private Index index;
	private IndexManager manager;
	private JSIndexQueryHelper helper;
	private TestProject project;

	@Before
	public void setUp() throws Exception
	{
//		super.setUp();

		project = new TestProject("index_helper", new String[] { "com.aptana.projects..webnature" });

		helper = new JSIndexQueryHelper(project.getInnerProject());
		manager = IndexPlugin.getDefault().getIndexManager();

		index = manager.getIndex(project.getURI());
	}

	@After
	public void tearDown() throws Exception
	{
		manager.removeIndex(index.getRoot());
		index = null;
		helper = null;
		manager = null;
		project.delete();
		project = null;
//		super.tearDown();
	}

	@Test
	public void testGetTypeAncestorNames() throws Exception
	{
		TypeElement type = new TypeElement();
		type.setName("MadeUpType");
		type.addParentType("Array<String>");

		JSIndexWriter writer = new JSIndexWriter();
		writer.writeType(index, type);

		Job job = new JSMetadataLoader()
		{
			protected void postRebuild()
			{
				// do nothing
			}
		};
		job.schedule();
		job.join();

		List<String> ancestors = helper.getTypeAncestorNames(type.getName());
		assertEquals("ancestors size", 2, ancestors.size());
		assertTrue("ancestors contains Array<String>", ancestors.contains("Array<String>"));
		assertTrue("ancestors contains Object", ancestors.contains("Object"));
	}

}
