package com.aptana.editor.html.parsing;

import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.test.performance.PerformanceTestCase;

import com.aptana.core.util.IOUtil;
import com.aptana.parsing.IParseState;

public class HTMLParserPerformanceTest extends PerformanceTestCase
{

	private HTMLParser fParser;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		fParser = new HTMLParser();
	}

	@Override
	protected void tearDown() throws Exception
	{
		fParser = null;
		super.tearDown();
	}

	public void testAmazonFrontPage() throws Exception
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle("com.aptana.editor.html.tests"),
				Path.fromPortableString("performance/amazon.html"), false);
		String src = IOUtil.read(stream);

		for (int i = 0; i < 500; i++)
		{
			IParseState parseState = new HTMLParseState();
			parseState.setEditState(src);
			startMeasuring();
			fParser.parse(parseState);
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}
}
