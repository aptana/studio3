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

	public void testYUICSSFolding() throws Exception
	{
		Ruby runtime = Ruby.newInstance();
		final RubyRegexp endFolding = RubyRegexp.newRegexp(runtime, "(?<!\\*)\\*\\*\\/|^\\s*\\}", 0);
		final RubyRegexp startFolding = RubyRegexp.newRegexp(runtime,
				"\\/\\*\\*(?!\\*)|\\{\\s*($|\\/\\*(?!.*?\\*\\/.*\\S))", 0);

		String src = readFile("yui.css");
		IDocument document = new Document(src);
		RubyRegexpFolder folder = new RubyRegexpFolder(document)
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
