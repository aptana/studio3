package com.aptana.editor.html;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.test.performance.PerformanceTestCase;

public class HTMLTagScannerPerformanceTest extends PerformanceTestCase
{

	private HTMLTagScanner fScanner;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		fScanner = new HTMLTagScanner()
		{
			@Override
			protected IToken createToken(String string)
			{
				return new Token(string);
			}
		};
	}

	@Override
	protected void tearDown() throws Exception
	{
		fScanner = null;
		super.tearDown();
	}

	public void testAmazonHTML() throws Exception
	{
		// read in the file
		InputStream stream = FileLocator.openStream(Platform.getBundle("com.aptana.editor.html.tests"),
				Path.fromPortableString("performance/amazon.html"), false);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int read = -1;
		while ((read = stream.read()) != -1)
		{
			out.write(read);
		}
		stream.close();
		String src = new String(out.toByteArray());
		IDocument document = new Document(src);
		// Ok now actually scan the thing, the real work
		int numRuns = 100;
		for (int i = 0; i < numRuns; i++)
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
