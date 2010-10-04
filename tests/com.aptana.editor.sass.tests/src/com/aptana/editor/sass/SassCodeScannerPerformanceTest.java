package com.aptana.editor.sass;

import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.test.performance.PerformanceTestCase;

import com.aptana.core.util.IOUtil;

public class SassCodeScannerPerformanceTest extends PerformanceTestCase
{

	public void testTime() throws Exception
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle("com.aptana.editor.sass.tests"),
				Path.fromPortableString("performance/test.sass"), false);

		String src = IOUtil.read(stream);
		SassCodeScanner scanner = new SassCodeScanner()
		{
			@Override
			protected IToken createToken(String string)
			{
				return new Token(string);
			}
		};
		IDocument document = new Document(src);

		for (int i = 0; i < 500; i++)
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
