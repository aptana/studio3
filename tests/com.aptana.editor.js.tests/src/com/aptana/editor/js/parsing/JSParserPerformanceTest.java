/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.parsing;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.test.performance.PerformanceTestCase;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.js.Activator;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseState;

public class JSParserPerformanceTest extends PerformanceTestCase
{

	private JSParser fParser;

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
		InputStream stream = FileLocator.openStream(Platform.getBundle(Activator.PLUGIN_ID), new Path(resourceName),
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
		fParser = new JSParser();
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		fParser = null;
		super.tearDown();
	}

	/**
	 * testDojo
	 * 
	 * @throws Exception
	 */
	public void testDojo() throws Exception
	{
		assertParse(10, "performance/dojo.js.uncompressed.js");
	}

	/**
	 * testExt
	 * 
	 * @throws Exception
	 */
	public void testExt() throws Exception
	{
		assertParse(15, "performance/ext-core.js");
	}

	/**
	 * testTiMobile
	 * 
	 * @throws Exception
	 */
	public void testTiMobile() throws Exception
	{
		assertParse(10, "performance/timobile.js");
	}

	/**
	 * testTinyMce
	 * 
	 * @throws Exception
	 */
	public void testTinyMce() throws Exception
	{
		assertParse(10, "performance/tiny_mce.js");
	}

	/**
	 * testJaxerFiles
	 * 
	 * @throws Exception
	 */
	public void testJaxerFiles() throws Exception
	{
		assertParse(5, "performance/jaxer/11.2.2-1-n.js", "performance/jaxer/15.10.6.2-2.js",
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
				"performance/jaxer/jquery-stable.js", "performance/jaxer/jquery.js",
				"performance/jaxer/lexical-008.js", "performance/jaxer/messages.js",
				"performance/jaxer/narcissus-exec.js", "performance/jaxer/nsDragAndDrop.js",
				"performance/jaxer/packed.js", "performance/jaxer/perlstress-001.js",
				"performance/jaxer/perlstress-002.js", "performance/jaxer/property_database.js",
				"performance/jaxer/prototype.js", "performance/jaxer/publishprefs.js",
				"performance/jaxer/regress-100199.js", "performance/jaxer/regress-111557.js",
				"performance/jaxer/regress-155081-2.js", "performance/jaxer/regress-192226.js",
				"performance/jaxer/regress-244470.js", "performance/jaxer/regress-309925-02.js",
				"performance/jaxer/regress-76054.js", "performance/jaxer/regress-98901.js",
				"performance/jaxer/scriptaculous.js", "performance/jaxer/split-002.js",
				"performance/jaxer/test_413784.js", "performance/jaxer/test_423515_forceCopyShortcuts.js",
				"performance/jaxer/test_bug364285-1.js", "performance/jaxer/test_bug374754.js",
				"performance/jaxer/test_multi_statements.js", "performance/jaxer/test_prepare_insert_update.js",
				"performance/jaxer/tip_followscroll.js", "performance/jaxer/tree-utils.js",
				"performance/jaxer/utils.js", "performance/jaxer/xpath.js", "performance/jaxer/xslt_script.js");
	}

	/**
	 * assertParse
	 * 
	 * @param resourceName
	 * @throws Exception
	 */
	private void assertParse(int numRuns, String... resources) throws Exception
	{
		for (String resourceName : resources)
		{
			timeParse(resourceName, numRuns);
		}
		commitMeasurements();
		assertPerformance();
	}

	/**
	 * time
	 * 
	 * @param resourceName
	 * @param numRuns
	 * @throws Exception
	 */
	private void timeParse(String resourceName, int numRuns) throws Exception
	{
		this.timeParse(resourceName, getSource(resourceName), numRuns);
	}

	/**
	 * time
	 * 
	 * @param resourceName
	 * @throws Exception
	 */
	private void timeParse(String resourceName, String src, int numRuns) throws Exception
	{
		// apply to parse state
		IParseState parseState = new ParseState();
		parseState.setEditState(src, src, 0, 0);

		for (int i = 0; i < numRuns; i++)
		{
			startMeasuring();
			try
			{
				fParser.parse(parseState);
			}
			catch (Exception e)
			{
				fail(e.getMessage());
			}
			stopMeasuring();
		}
	}
}
