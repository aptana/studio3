package com.aptana.editor.js;

import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.test.performance.PerformanceTestCase;

import com.aptana.core.util.IOUtil;

public class JSCodeScannerPerformanceTest extends PerformanceTestCase
{
	private JSCodeScanner fScanner;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		fScanner = new JSCodeScanner();
	}

	@Override
	protected void tearDown() throws Exception
	{
		fScanner = null;

		super.tearDown();
	}

	public void testScanUncompressedDojo() throws Exception
	{
		// read in the file
		InputStream stream = FileLocator.openStream(Platform.getBundle("com.aptana.editor.js.tests"),
				Path.fromPortableString("performance/dojo.js.uncompressed.js"), false);
		String src = IOUtil.read(stream);
		IDocument document = new Document(src);

		// Ok now actually scan the thing, the real work
		for (int i = 0; i < 15; i++)
		{
			startMeasuring();
			fScanner.setRange(document, 0, src.length());
			while (fScanner.nextToken() != Token.EOF)
			{
				fScanner.getTokenOffset();
				fScanner.getTokenLength();
			}
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}
}
