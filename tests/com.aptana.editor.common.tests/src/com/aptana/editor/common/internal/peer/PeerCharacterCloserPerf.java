package com.aptana.editor.common.internal.peer;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

public class PeerCharacterCloserPerf
{

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		char[] pairs = new char[] { '(', ')', '"', '"' };
		PeerCharacterCloser closer = new PeerCharacterCloser(null, pairs);
		StringBuilder builder = new StringBuilder();
		int times = 50000;
		for (int i = 0; i < times; i++)
		{
			builder.append("((((((((((((((");
		}
		for (int i = 0; i < times; i++)
		{
			builder.append("))))))))))))))");
		}
		IDocument document = new Document(builder.toString());
		builder = null;
		int offset = times * 10;
		int iterations = 1000;
		long start = System.currentTimeMillis();
		for (int i = 0; i < iterations; i++)
		{
			if (closer.unpairedClose('(', ')', document, offset))
			{
				throw new Exception("bad!");
			}
		}
		long end = System.currentTimeMillis();
		System.out.println((end - start) / iterations);
	}

}
