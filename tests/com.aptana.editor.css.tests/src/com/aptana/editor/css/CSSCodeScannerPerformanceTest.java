package com.aptana.editor.css;

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
import com.aptana.editor.css.parsing.lexer.CSSTokenType;

public class CSSCodeScannerPerformanceTest extends PerformanceTestCase
{

	private CSSCodeScanner fScanner;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		fScanner = new CSSCodeScanner()
		{
			@Override
			protected IToken createToken(CSSTokenType string)
			{
				return new Token(string.getScope());
			}
		};
	}

	@Override
	protected void tearDown() throws Exception
	{
		fScanner = null;
		super.tearDown();
	}

	public void testScanningWordpressAdminCSS() throws Exception
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle("com.aptana.editor.css.tests"),
				Path.fromPortableString("performance/wp-admin.css"), false);
		String src = IOUtil.read(stream);
		IDocument document = new Document(src);

		for (int i = 0; i < 80; i++)
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
