package com.aptana.editor.xml.formatter.tests;

import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.aptana.editor.common.formatting.tests.AbstractFormatterTestCase;
import com.aptana.editor.xml.XMLPlugin;

@RunWith(Parameterized.class)
public class FormattingTest extends AbstractFormatterTestCase
{

	@Parameters(name = "{0}")
	public static Iterable<Object[]> data()
	{
		return Arrays.asList(AbstractFormatterTestCase.getFiles(TEST_BUNDLE_ID, FILE_TYPE));
	}

	private static String FORMATTER_FACTORY_ID = "com.aptana.editor.xml.formatterFactory"; //$NON-NLS-1$
	private static String TEST_BUNDLE_ID = "com.aptana.editor.xml.formatter.tests"; //$NON-NLS-1$
	private static String FILE_TYPE = "xml"; //$NON-NLS-1$

	@BeforeClass
	public static void initializePlugin() throws Exception
	{
		XMLPlugin.getDefault();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.formatting.AbstractFormatterTestCase#getTestBundleId()
	 */
	@Override
	protected String getTestBundleId()
	{
		return TEST_BUNDLE_ID;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.formatting.AbstractFormatterTestCase#getFormatterId()
	 */
	@Override
	protected String getFormatterId()
	{
		return FORMATTER_FACTORY_ID;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.formatting.AbstractFormatterTestCase#getFileType()
	 */
	@Override
	protected String getFileType()
	{
		return FILE_TYPE;
	}

	// Tests that are not working no longer have the xml extension

	// Notes on Tests:

	/*
	 * We currently indent attributes of a tag with the same indentation as the whole tag. (Studio 1.5 does this
	 * differently) If we want to change this, we would need to modify how the writer writes the indentation. test2,
	 * test4, test5, test6, test7, test8, test15 , test19
	 */

	/*
	 * Test 12: Spaces inside tags need to work
	 */

	/*
	 * Test 16: This doesn't pass since we don't handle invalid xml.
	 */

	/*
	 * Test 17, 18: This does not work since comments aren't working yet in xml.
	 */

}
