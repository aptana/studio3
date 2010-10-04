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
		int numPairs = 25000;

		IDocument document = createDocumentWithPairs(numPairs);
		for (int i = 0; i < 10; i++)
		{
			startMeasuring();
			if (closer.unpairedClose('(', ')', document, 0))
			{
				fail("bad!");
			}
			if (closer.unpairedClose('(', ')', document, numPairs * 2))
			{
				fail("bad!");
			}
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}

	private IDocument createDocumentWithPairs(int numPairs)
	{
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < numPairs; i++)
		{
			builder.append('(');
		}
		for (int i = 0; i < numPairs; i++)
		{
			builder.append(')');
		}
		return new Document(builder.toString());
	}

}
