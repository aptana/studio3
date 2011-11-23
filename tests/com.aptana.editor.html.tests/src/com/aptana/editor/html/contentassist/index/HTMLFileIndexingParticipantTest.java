package com.aptana.editor.html.contentassist.index;

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
import com.aptana.editor.css.contentassist.index.CSSFileIndexingParticipant;
import com.aptana.editor.js.contentassist.index.JSFileIndexingParticipant;
import com.aptana.index.core.FileStoreBuildContext;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.build.BuildContext;

public class HTMLFileIndexingParticipantTest extends TestCase
{

	private HTMLFileIndexingParticipant indexer;
	private CSSFileIndexingParticipant cssIndexer;
	private JSFileIndexingParticipant jsIndexer;

	protected void setUp() throws Exception
	{
		super.setUp();
		cssIndexer = new CSSFileIndexingParticipant();
		jsIndexer = new JSFileIndexingParticipant();
		indexer = new HTMLFileIndexingParticipant();
	}

	protected void tearDown() throws Exception
	{
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
			IFileStore fileStore = EFS.getStore(coffeeFile.toURI());

			Index index = IndexManager.getInstance().getIndex(tmpDir.toURI());
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

}
