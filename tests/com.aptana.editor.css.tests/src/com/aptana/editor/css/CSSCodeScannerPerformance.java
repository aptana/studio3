package com.aptana.editor.css;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class CSSCodeScannerPerformance
{

	public static void main(String[] args) throws Exception
	{
		InputStream stream = CSSCodeScannerPerformance.class.getResourceAsStream("yui.css");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int read = -1;
		while ((read = stream.read()) != -1)
		{
			out.write(read);
		}
		stream.close();
		String src = new String(out.toByteArray());
		CSSCodeScanner scanner = new CSSCodeScanner()
		{
			@Override
			protected IToken createToken(String string)
			{
				return new Token(string);
			}
		};
		IDocument document = new Document(src);

		int numRuns = 10;
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
