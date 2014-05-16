package com.aptana.editor.coffee.internal.index;

import java.io.File;
import java.io.FileOutputStream;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.util.FileUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.index.core.FileStoreBuildContext;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.index.core.build.BuildContext;

public class CoffeeFileIndexingParticipantTest
{
	private CoffeeFileIndexingParticipant indexer;

	@Before
	public void setUp() throws Exception
	{
		indexer = new CoffeeFileIndexingParticipant();
	}

	@After
	public void tearDown() throws Exception
	{
		indexer = null;
	}

	@Test
	public void testIndexWithNullIFileStore() throws Exception
	{
		File tmpDir = null;
		try
		{
			String src = "# TODO This is a task\n";

			// Generate some files to index!
			tmpDir = new File(FileUtil.getTempDirectory().toOSString(), "testIndexWithNullIFileStore"
					+ System.currentTimeMillis());
			tmpDir.mkdirs();

			File coffeeFile = new File(tmpDir, "index_me.coffee");
			IOUtil.write(new FileOutputStream(coffeeFile), src);

			IFileStore fileStore = EFS.getStore(coffeeFile.toURI());
			BuildContext context = new FileStoreBuildContext(fileStore);

			Index index = getIndexManager().getIndex(tmpDir.toURI());
			indexer.index(context, index, new NullProgressMonitor());
		}
		finally
		{
			// Clean up the generated files!
			FileUtil.deleteRecursively(tmpDir);
		}
	}

	protected IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
	}

	@Test
	public void testIndexWithNullIndex() throws Exception
	{
		File tmpDir = null;
		try
		{
			String src = "# TODO This is a task\n";

			// Generate some files to index!
			tmpDir = new File(FileUtil.getTempDirectory().toOSString(), "testIndexWithNullIndex"
					+ System.currentTimeMillis());
			tmpDir.mkdirs();

			File coffeeFile = new File(tmpDir, "index_me.coffee");
			IOUtil.write(new FileOutputStream(coffeeFile), src);

			IFileStore fileStore = EFS.getStore(coffeeFile.toURI());
			BuildContext context = new FileStoreBuildContext(fileStore);
			indexer.index(context, null, new NullProgressMonitor());
		}
		finally
		{
			// Clean up the generated files!
			FileUtil.deleteRecursively(tmpDir);
		}
	}

	@Test
	public void testIndexWithEmptyContent() throws Exception
	{
		File tmpDir = null;
		try
		{
			String src = "\n";

			// Generate some files to index!
			tmpDir = new File(FileUtil.getTempDirectory().toOSString(), "testIndexWithEmptyContent"
					+ System.currentTimeMillis());
			tmpDir.mkdirs();

			File coffeeFile = new File(tmpDir, "index_me.coffee");
			IOUtil.write(new FileOutputStream(coffeeFile), src);

			IFileStore fileStore = EFS.getStore(coffeeFile.toURI());
			BuildContext context = new FileStoreBuildContext(fileStore);

			IProgressMonitor monitor = new NullProgressMonitor();
			indexer.index(context, null, monitor);
		}
		finally
		{
			// Clean up the generated files!
			FileUtil.deleteRecursively(tmpDir);
		}
	}
}
