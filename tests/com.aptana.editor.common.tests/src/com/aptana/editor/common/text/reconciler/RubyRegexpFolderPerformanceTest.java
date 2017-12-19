/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.reconciler;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.jruby.Ruby;
import org.jruby.RubyRegexp;
import org.jruby.util.RegexpOptions;

import com.aptana.core.tests.GlobalTimePerformanceTestCase;
import com.aptana.core.util.IOUtil;

public class RubyRegexpFolderPerformanceTest extends GlobalTimePerformanceTestCase
{

	public void testYUICSSFolding() throws Exception
	{
		Ruby runtime = Ruby.newInstance();
		final RubyRegexp endFolding = RubyRegexp.newRegexp(runtime, "(?<!\\*)\\*\\*\\/|^\\s*\\}",
				RegexpOptions.NULL_OPTIONS);
		final RubyRegexp startFolding = RubyRegexp.newRegexp(runtime,
				"\\/\\*\\*(?!\\*)|\\{\\s*($|\\/\\*(?!.*?\\*\\/.*\\S))", RegexpOptions.NULL_OPTIONS);

		String src = readFile("yui.css");
		IDocument document = new Document(src);
		RubyRegexpFolder folder = new RubyRegexpFolder(null, document)
		{
			@Override
			protected RubyRegexp getEndFoldRegexp(String scope)
			{
				return endFolding;
			}

			@Override
			protected RubyRegexp getStartFoldRegexp(String scope)
			{
				return startFolding;
			}

			@Override
			protected String getScopeAtOffset(int offset) throws BadLocationException
			{
				return "source.css";
			}
		};

		// Now do the work!
		for (int i = 0; i < 400; i++)
		{
			IProgressMonitor monitor = new NullProgressMonitor();
			startMeasuring();
			folder.emitFoldingRegions(false, monitor, null);
			stopMeasuring();
			// TODO Verify the positions?
		}
		commitMeasurements();
		assertPerformance();
	}

	protected static String readFile(String fileName) throws IOException
	{
		InputStream stream = RubyRegexpFolderPerformanceTest.class.getResourceAsStream(fileName);
		return IOUtil.read(stream);
	}
}
