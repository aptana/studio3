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
package com.aptana.editor.common.text.reconciler;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.test.performance.PerformanceTestCase;
import org.jruby.Ruby;
import org.jruby.RubyRegexp;

import com.aptana.core.util.IOUtil;

public class RubyRegexpFolderPerformanceTest extends PerformanceTestCase
{

	@SuppressWarnings("unused")
	public void testYUICSSFolding() throws Exception
	{
		Ruby runtime = Ruby.newInstance();
		final RubyRegexp endFolding = RubyRegexp.newRegexp(runtime, "(?<!\\*)\\*\\*\\/|^\\s*\\}", 0);
		final RubyRegexp startFolding = RubyRegexp.newRegexp(runtime,
				"\\/\\*\\*(?!\\*)|\\{\\s*($|\\/\\*(?!.*?\\*\\/.*\\S))", 0);

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
			List<Position> positions = folder.emitFoldingRegions(monitor);
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
