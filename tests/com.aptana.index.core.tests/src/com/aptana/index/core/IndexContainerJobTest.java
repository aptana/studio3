package com.aptana.index.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.FileUtil;
import com.aptana.index.core.build.BuildContext;

public class IndexContainerJobTest
{

	private Mockery context = new Mockery()
	{
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};
	private File tmpDir;

	@Before
	public void setUp() throws Exception
	{
		tmpDir = new File(FileUtil.getTempDirectory().toOSString(), "index_container");
		tmpDir.mkdirs();
	}

	@After
	public void tearDown() throws Exception
	{
		FileUtil.deleteRecursively(tmpDir);
	}

	@Test
	public void testNoIndexReturnsCancelStatus() throws Exception
	{
		IndexContainerJob job = new IndexContainerJob(tmpDir.toURI())
		{
			@Override
			protected Index getIndex()
			{
				return null;
			}
		};
		IStatus status = job.run(new NullProgressMonitor());
		assertFalse(status.isOK());
		assertEquals(IStatus.CANCEL, status.getSeverity());
		context.assertIsSatisfied();
	}

	@Test
	public void testTypicalIndex() throws Exception
	{
		final File indexFile = File.createTempFile("fake_indexFile", ".index");

		// Generate some structure in the tmp dir
		final File file1 = new File(tmpDir, "file1");
		file1.createNewFile();
		final File dir1 = new File(tmpDir, "dir1");
		dir1.mkdirs();
		final File file2 = new File(dir1, "file2");
		file2.createNewFile();
		final File file3 = new File(dir1, "file3");
		file3.createNewFile();
		File dir2 = new File(dir1, "dir2");
		dir2.mkdirs();
		final File file4 = new File(dir2, "file4");
		file4.createNewFile();
		final File file5 = new File(dir2, "file5");
		file5.createNewFile();
		File dir3 = new File(dir2, "dir3");
		dir3.mkdirs();

		final Index index = context.mock(Index.class);
		final IFileStoreIndexingParticipant participant = context.mock(IFileStoreIndexingParticipant.class);
		IndexContainerJob job = new IndexContainerJob(tmpDir.toURI())
		{
			@Override
			protected Index getIndex()
			{
				return index;
			}

			@Override
			protected List<IFileStoreIndexingParticipant> getIndexParticipants(IFileStore file)
			{
				return CollectionsUtil.newList(participant);
			}
		};
		context.checking(new Expectations()
		{
			{
				oneOf(index).queryDocumentNames(null);
				will(returnValue(CollectionsUtil.newSet(file1.toURI().toString(), file2.toURI().toString(), dir1
						.toURI().toString() + File.separator + "fileX", dir1.toURI().toString() + File.separator
						+ "fileY", dir1.toURI().toString() + File.separator + "fileZ")));

				oneOf(index).remove(URI.create(dir1.toURI().toString() + File.separator + "fileX"));
				oneOf(index).remove(URI.create(dir1.toURI().toString() + File.separator + "fileY"));
				oneOf(index).remove(URI.create(dir1.toURI().toString() + File.separator + "fileZ"));

				oneOf(index).getIndexFile();
				will(returnValue(indexFile));

				// We remove and index the files.
				oneOf(index).remove(URI.create(file1.toURI().toString()));
				oneOf(index).remove(URI.create(file2.toURI().toString()));
				oneOf(index).remove(URI.create(file3.toURI().toString()));
				oneOf(index).remove(URI.create(file4.toURI().toString()));
				oneOf(index).remove(URI.create(file5.toURI().toString()));
				exactly(5).of(participant).index(with(any(BuildContext.class)), with(index),
						with(any(IProgressMonitor.class)));

				// Now save the index at the end
				oneOf(index).save();
			}
		});
		job.run(new NullProgressMonitor());
		context.assertIsSatisfied();
	}
}
