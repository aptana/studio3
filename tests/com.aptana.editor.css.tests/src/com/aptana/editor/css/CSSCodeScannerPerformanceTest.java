package com.aptana.editor.css;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.css.parsing.lexer.CSSTokenType;

public class CSSCodeScannerPerformanceTest extends TestCase
{

	private CSSCodeScanner fScanner;

	@Override
	protected void setUp() throws Exception
	{
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
	}

	public void testTime() throws Exception
	{
		InputStream stream = getClass().getResourceAsStream("wp-admin.css");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int read = -1;
		while ((read = stream.read()) != -1)
		{
			out.write(read);
		}
		stream.close();
		String src = new String(out.toByteArray());
		IDocument document = new Document(src);

		int numRuns = 100;
		long start = System.currentTimeMillis();
		for (int i = 0; i < numRuns; i++)
		{
			fScanner.setRange(document, 0, src.length());
			while (fScanner.nextToken() != Token.EOF)
			{
				fScanner.getTokenOffset();
				fScanner.getTokenLength();
			}
		}
		long diff = System.currentTimeMillis() - start;
		System.out.println("Total time: " + diff + "ms");
		System.out.println("Average time: " + (diff / numRuns) + "ms");
	}
}
