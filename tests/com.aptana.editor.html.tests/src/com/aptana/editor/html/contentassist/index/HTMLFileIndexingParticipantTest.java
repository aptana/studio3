package com.aptana.editor.html.contentassist.index;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.aptana.core.build.IProblem;
import com.aptana.core.util.FileUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.css.core.index.CSSFileIndexingParticipant;
import com.aptana.index.core.FileStoreBuildContext;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.index.core.build.BuildContext;
import com.aptana.js.core.index.JSFileIndexingParticipant;

public class HTMLFileIndexingParticipantTest
{

	private HTMLFileIndexingParticipant indexer;
	private CSSFileIndexingParticipant cssIndexer;
	private JSFileIndexingParticipant jsIndexer;

	@Before
	public void setUp() throws Exception
	{
//		super.setUp();
		cssIndexer = new CSSFileIndexingParticipant();
		jsIndexer = new JSFileIndexingParticipant();
		indexer = new HTMLFileIndexingParticipant();
	}

	@After
	public void tearDown() throws Exception
	{
		jsIndexer = null;
		cssIndexer = null;
		indexer = null;
//		super.tearDown();
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
					problems.put(markerType, newItems);
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
