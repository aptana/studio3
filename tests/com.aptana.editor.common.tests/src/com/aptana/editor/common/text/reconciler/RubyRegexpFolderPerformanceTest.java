package com.aptana.editor.common.text.reconciler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.jruby.Ruby;
import org.jruby.RubyRegexp;

public class RubyRegexpFolderPerformanceTest extends TestCase
{

	public void testTime() throws Exception
	{
		Ruby runtime = Ruby.newInstance();
		final RubyRegexp endFolding = RubyRegexp.newRegexp(runtime, "(?<!\\*)\\*\\*\\/|^\\s*\\}", 0);
		final RubyRegexp startFolding = RubyRegexp.newRegexp(runtime,
				"\\/\\*\\*(?!\\*)|\\{\\s*($|\\/\\*(?!.*?\\*\\/.*\\S))", 0);

		String src = readFile("yui.css");
		IDocument document = new Document(src);
		List<Position> positions = new ArrayList<Position>();
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

		long start = System.currentTimeMillis();
		final int runs = 1000;
		for (int i = 0; i < runs; i++)
		{
			folder.emitFoldingRegions(positions, new NullProgressMonitor());
		}
		long end = System.currentTimeMillis();
		long diff = end - start;
		double avg = (double) diff / (double) runs;
		System.out.println("Took " + diff + "ms for " + runs + " runs. Avg: " + avg);
	}

	protected static String readFile(String fileName) throws IOException
	{
		InputStream stream = RubyRegexpFolderPerformanceTest.class.getResourceAsStream(fileName);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int read = -1;
		while ((read = stream.read()) != -1)
		{
			out.write(read);
		}
		stream.close();
		return new String(out.toByteArray());
	}
}
