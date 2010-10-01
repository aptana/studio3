package com.aptana.editor.sass;

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

public class SassCodeScannerPerformanceTest extends PerformanceTestCase
{

	public void testTime() throws Exception
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle("com.aptana.editor.sass.tests"),
				Path.fromPortableString("performance/test.sass"), false);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int read = -1;
		while ((read = stream.read()) != -1)
		{
			out.write(read);
		}
		stream.close();
		String src = new String(out.toByteArray());
		SassCodeScanner scanner = new SassCodeScanner()
		{
			@Override
			protected IToken createToken(String string)
			{
				return new Token(string);
			}
		};
		IDocument document = new Document(src);

		int numRuns = 100;
		for (int i = 0; i < numRuns; i++)
		{
			startMeasuring();
			scanner.setRange(document, 0, src.length());
			while (scanner.nextToken() != Token.EOF)
			{
				scanner.getTokenOffset();
				scanner.getTokenLength();
			}
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}
}
