/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.junit.experimental.categories.Category;

import com.aptana.core.tests.GlobalTimePerformanceTestCase;
import com.aptana.core.util.IOUtil;
import com.aptana.index.core.FileStoreBuildContext;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.index.JSFileIndexingParticipant;
import com.aptana.js.core.parsing.GraalJSParser;
import com.aptana.js.core.parsing.ast.JSParseRootNode;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.ParseResult;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.testing.categories.PerformanceTests;

@Category({ PerformanceTests.class })
public class JSIndexingPerformanceTest extends GlobalTimePerformanceTestCase
{

	private IParser fParser;

	/**
	 * getIndex
	 * 
	 * @return
	 */
	private Index getIndex()
	{
		URI indexURI = this.getIndexURI();
		Index result = null;

		if (indexURI != null)
		{
			result = getIndexManager().getIndex(indexURI);
		}

		return result;
	}

	/**
	 * getIndexURI
	 * 
	 * @return
	 */
	private URI getIndexURI()
	{
		return URI.create("inference.testing");
	}

	/**
	 * getSource
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	private String getSource(InputStream stream) throws IOException
	{
		return IOUtil.read(stream);
	}

	/**
	 * getSource
	 * 
	 * @param resourceName
	 * @return
	 * @throws IOException
	 */
	private String getSource(String resourceName) throws IOException
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle(JSCorePlugin.PLUGIN_ID), new Path(resourceName),
				false);
		return getSource(stream);
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		fParser = new GraalJSParser();
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		fParser = null;

		URI indexURI = this.getIndexURI();

		if (indexURI != null)
		{
			getIndexManager().removeIndex(indexURI);
		}

		super.tearDown();
	}

	/**
	 * testDojo
	 * 
	 * @throws Exception
	 */
	public void testDojoUncompressed() throws Exception
	{
		timeIndex(10, "performance/dojo.js.uncompressed.js");
	}

	/**
	 * testExt
	 * 
	 * @throws Exception
	 */
	public void testExt() throws Exception
	{
		timeIndex(20, "performance/ext/ext-core.js");
	}

	/**
	 * testJaxerFiles
	 * 
	 * @throws Exception
	 */
	public void testJaxerFiles() throws Exception
	{
		timeIndex(5, "performance/jaxer/11.2.2-1-n.js", "performance/jaxer/15.10.6.2-2.js",
				"performance/jaxer/15.5.4.7-2.js", "performance/jaxer/15.9.5.21-3.js",
				"performance/jaxer/ComposerCommands.js", "performance/jaxer/DBAPI.js",
				"performance/jaxer/DOMTestCase.js", "performance/jaxer/Microformats.js",
				"performance/jaxer/MochiKit_packed.js", "performance/jaxer/SimpleTest.js",
				"performance/jaxer/TestCachePerformance.js", "performance/jaxer/UDDITypes.js",
				"performance/jaxer/browser_bug_411172.js", "performance/jaxer/clientBothProperty.js",
				"performance/jaxer/commands.js", "performance/jaxer/crlManager.js", "performance/jaxer/dojo.js",
				"performance/jaxer/dom.js", "performance/jaxer/editor.js", "performance/jaxer/effects.js",
				"performance/jaxer/file-utils.js", "performance/jaxer/head_urlformatter.js",
				"performance/jaxer/httpd.js", "performance/jaxer/ifaceinfotest.js", "performance/jaxer/irc.js",
				"performance/jaxer/jquery-1.2.1.js", "performance/jaxer/jquery-1.2.6.min.js",
				"performance/jaxer/jquery-stable.js", "performance/jaxer/jquery.js", "performance/jaxer/lexical-008.js",
				"performance/jaxer/messages.js", "performance/jaxer/narcissus-exec.js",
				"performance/jaxer/nsDragAndDrop.js", "performance/jaxer/packed.js",
				"performance/jaxer/perlstress-001.js", "performance/jaxer/perlstress-002.js",
				"performance/jaxer/property_database.js", "performance/jaxer/prototype.js",
				"performance/jaxer/publishprefs.js",
				// "performance/jaxer/regress-100199.js", "performance/jaxer/regress-111557.js",
				// "performance/jaxer/regress-155081-2.js", "performance/jaxer/regress-192226.js",
				// "performance/jaxer/regress-244470.js", "performance/jaxer/regress-309925-02.js",
				// "performance/jaxer/regress-76054.js", "performance/jaxer/regress-98901.js",
				"performance/jaxer/scriptaculous.js", "performance/jaxer/split-002.js",
				"performance/jaxer/test_413784.js", "performance/jaxer/test_423515_forceCopyShortcuts.js",
				"performance/jaxer/test_bug364285-1.js", "performance/jaxer/test_bug374754.js",
				"performance/jaxer/test_multi_statements.js", "performance/jaxer/test_prepare_insert_update.js",
				"performance/jaxer/tip_followscroll.js", "performance/jaxer/tree-utils.js",
				"performance/jaxer/utils.js", "performance/jaxer/xpath.js", "performance/jaxer/xslt_script.js");
	}

	/**
	 * testTiMobile
	 * 
	 * @throws Exception
	 */
	public void testTiMobile() throws Exception
	{
		timeIndex(10, "performance/timobile.js");
	}

	/**
	 * testTinyMce
	 * 
	 * @throws Exception
	 */
	public void testTinyMce() throws Exception
	{
		timeIndex(50, "performance/tiny_mce.js");
	}

	/**
	 * time
	 * 
	 * @param resourceName
	 * @throws Exception
	 */
	private void timeIndex(int numRuns, String... resources) throws Exception
	{
		for (String resourceName : resources)
		{
			this.timeIndex(numRuns, resourceName, getSource(resourceName));
		}
		commitMeasurements();
		assertPerformance();
	}

	/**
	 * time
	 * 
	 * @param resourceName
	 * @throws Exception
	 */
	private void timeIndex(int numRuns, String resourceName, String src) throws Exception
	{
		// apply to parse state
		IParseState parseState = new ParseState(src);

		URL url = FileLocator.find(Platform.getBundle(JSCorePlugin.PLUGIN_ID), new Path(resourceName), null);
		url = FileLocator.toFileURL(url);
		IFileStore store = EFS.getStore(url.toURI());

		try
		{
			ParseResult parse = fParser.parse(parseState);

			final IParseRootNode root = parse.getRootNode();

			FileStoreBuildContext context = new FileStoreBuildContext(store)
			{
				@Override
				public synchronized IParseRootNode getAST() throws CoreException
				{
					return root;
				}
			};
			if (root instanceof JSParseRootNode)
			{
				IProgressMonitor monitor = new NullProgressMonitor();
				JSFileIndexingParticipant jsIndexer = new JSFileIndexingParticipant();
				for (int i = 0; i < numRuns; i++)
				{
					URI indexURI = this.getIndexURI();
					if (indexURI != null)
					{
						getIndexManager().removeIndex(indexURI);
					}
					Index index = this.getIndex();

					startMeasuring();
					jsIndexer.index(context, index, monitor);
					stopMeasuring();
				}
			}
			else
			{
				fail("No parse root node for " + resourceName);
			}
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	protected IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
	}
}
