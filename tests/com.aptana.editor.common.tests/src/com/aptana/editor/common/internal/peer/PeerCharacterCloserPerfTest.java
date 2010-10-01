package com.aptana.editor.common.internal.peer;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.test.performance.PerformanceTestCase;

public class PeerCharacterCloserPerfTest extends PerformanceTestCase
{

	public void testCheckUnpairedClose() throws Exception
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
		int iterations = 1285;

		for (int i = 0; i < iterations; i++)
		{
			startMeasuring();
			if (closer.unpairedClose('(', ')', document, offset))
			{
				fail("bad!");
			}
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}

}
