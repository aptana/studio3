package com.aptana.editor.css;

import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.test.performance.PerformanceTestCase;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.css.parsing.CSSParser;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseState;

public class CSSParserPerformanceTest extends PerformanceTestCase
{

	private CSSParser fParser;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		fParser = new CSSParser();
	}

	@Override
	protected void tearDown() throws Exception
	{
		fParser = null;
		super.tearDown();
	}

	public void testWordpressAdminCSS() throws Exception
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle("com.aptana.editor.css.tests"),
				Path.fromPortableString("performance/wp-admin.css"), false);
		String src = IOUtil.read(stream);

		for (int i = 0; i < 25; i++)
		{
			IParseState parseState = new ParseState();
			startMeasuring();
			parseState.setEditState(src, src, 0, 0);
			fParser.parse(parseState);
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}
}
