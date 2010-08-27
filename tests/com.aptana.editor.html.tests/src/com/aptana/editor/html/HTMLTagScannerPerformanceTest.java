package com.aptana.editor.html;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class HTMLTagScannerPerformanceTest extends TestCase
{

	private HTMLTagScanner fScanner;

	@Override
	protected void setUp() throws Exception
	{
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
	}

	public void testTime() throws Exception
	{
		// read in the file
		InputStream stream = getClass().getResourceAsStream("amazon.html");
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
