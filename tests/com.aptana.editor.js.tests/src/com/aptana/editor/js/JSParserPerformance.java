package com.aptana.editor.js;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.aptana.editor.js.parsing.JSParser;
import com.aptana.editor.js.parsing.JSScanner;

public class JSParserPerformance
{

	public static void main(String[] args) throws Exception
	{
		InputStream stream = JSParserPerformance.class.getResourceAsStream("dojo.js.uncompressed.js");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int read = -1;
		while ((read = stream.read()) != -1)
		{
			out.write(read);
		}
		stream.close();
		String src = new String(out.toByteArray());

		JSParser parser = new JSParser();
		JSScanner scanner = new JSScanner();
		int numRuns = 1;
		long start = System.currentTimeMillis();
		for (int i = 0; i < numRuns; i++)
		{
			scanner.setSource(src);
			try
			{
				parser.parse(scanner);
			}
			catch (Exception e)
			{
			}
		}
		long diff = System.currentTimeMillis() - start;
		System.out.println("Total time: " + diff + "ms");
		System.out.println("Average time: " + (diff / numRuns) + "ms");
	}
}
