/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.internal.peer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.junit.experimental.categories.Category;

import com.aptana.core.tests.GlobalTimePerformanceTestCase;
import com.aptana.testing.categories.PerformanceTests;

@Category({ PerformanceTests.class })
public class PeerCharacterCloserPerfTest extends GlobalTimePerformanceTestCase
{

	public void testCheckUnpairedClose() throws Exception
	{
		PeerCharacterCloser closer = new PeerCharacterCloser(null)
		{
			protected List<Character> getPairs(String scope)
			{
				List<Character> pairs = new ArrayList<Character>();
				pairs.add('(');
				pairs.add(')');
				pairs.add('"');
				pairs.add('"');
				return pairs;
			}
		};
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
