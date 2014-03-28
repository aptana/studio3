package com.aptana.editor.html.contentassist.index;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.build.IProblem;
import com.aptana.core.util.FileUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.index.core.FileStoreBuildContext;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.index.core.build.BuildContext;

public class HTMLFileIndexingParticipantTest
{

	private HTMLFileIndexingParticipant indexer;

	@Before
	public void setUp() throws Exception
	{
		indexer = new HTMLFileIndexingParticipant();
	}

	@After
	public void tearDown() throws Exception
	{
		indexer = null;
	}

	@Test
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
			tmpDir = new File(FileUtil.getTempDirectory().toOSString(), "testIndex_" + System.currentTimeMillis());
			tmpDir.mkdirs();

			File coffeeFile = new File(tmpDir, "index_me.html");
			IOUtil.write(new FileOutputStream(coffeeFile), src);
			IFileStore fileStore = EFS.getStore(coffeeFile.toURI());

			Index index = getIndexManager().getIndex(tmpDir.toURI());
			BuildContext context = new FileStoreBuildContext(fileStore)
			{
				@Override
				public void putProblems(String markerType, Collection<IProblem> newItems)
				{
					fProblems.put(markerType, newItems);
				}
			};
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

}
