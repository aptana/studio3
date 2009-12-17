package com.aptana.editor.html;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class HTMLTagScannerPerformance
{

	public static void main(String[] args) throws Exception
	{
		// read in the file
		InputStream stream = HTMLTagScannerPerformance.class.getResourceAsStream("amazon.html");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int read = -1;
		while ((read = stream.read()) != -1)
		{
			out.write(read);
		}
		stream.close();
		String src = new String(out.toByteArray());
		HTMLTagScanner scanner = new HTMLTagScanner()
		{
			@Override
			protected IToken createToken(String string)
			{
				return new Token(string);
			}
		};
		IDocument document = new Document(src);
		// Ok now actually scan the thing, the real work
		int numRuns = 100;
		long start = System.currentTimeMillis();
		for (int i = 0; i < numRuns; i++)
		{
			scanner.setRange(document, 0, src.length());
			while (scanner.nextToken() != Token.EOF)
			{
				scanner.getTokenOffset();
				scanner.getTokenLength();
			}
		}
		long diff = System.currentTimeMillis() - start;
		System.out.println("Total time: " + diff + "ms");
		System.out.println("Average time: " + (diff / numRuns) + "ms");
	}
}
