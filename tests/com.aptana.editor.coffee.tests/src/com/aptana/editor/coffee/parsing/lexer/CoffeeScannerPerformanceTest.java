package com.aptana.editor.coffee.parsing.lexer;

import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.test.performance.PerformanceTestCase;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.coffee.parsing.Terminals;

public class CoffeeScannerPerformanceTest extends PerformanceTestCase
{

	private CoffeeScanner fScanner;

	protected void setUp() throws Exception
	{
		super.setUp();

		fScanner = new CoffeeScanner();
	}

	protected void tearDown() throws Exception
	{
		fScanner = null;
		super.tearDown();
	}

	public void testANKI() throws Exception
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle("com.aptana.editor.coffee.tests"),
				Path.fromPortableString("performance/anki.coffee"), false);
		String src = IOUtil.read(stream);

		for (int i = 0; i < 50; i++)
		{
			startMeasuring();
			fScanner.setSource(src);
			CoffeeSymbol symbol = null;
			while ((symbol = fScanner.nextToken()) != null)
			{
				if (symbol.getId() == Terminals.EOF)
				{
					break;
				}
			}
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}

}
