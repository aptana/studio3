package com.aptana.editor.html.formatter.tests;

import com.aptana.editor.common.formatting.AbstractFormatterTestCase;

public class FormattingTests extends AbstractFormatterTestCase
{
	// Turn this flag on for development only (used to generate the formatted files)
	// To generate formatted files, place html files under the 'formatting' folder and run these tests from the
	// com.aptana.editor.html.formatter.tests plugin
	// NOTE: Ensure that the contents section ends with a newline, or the generation may not work.
	private static boolean INITIALIZE_MODE = false;
	// Turning on the overwrite will re-generate the formatted block and overwrite it into the test files.
	// This is a drastic move that will require a review of the output right after to make sure we have the
	// right formatting for all the test file, so turn it on at your own risk.
	private static boolean OVERWRITE_MODE = false;

	private static String FORMATTER_FACTORY_ID = "com.aptana.editor.html.formatterFactory"; //$NON-NLS-1$
	private static String TEST_BUNDLE_ID = "com.aptana.editor.html.formatter.tests"; //$NON-NLS-1$
	private static String FILE_TYPE = "html"; //$NON-NLS-1$

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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.formatting.AbstractFormatterTestCase#isOverriteMode()
	 */
	@Override
	protected boolean isOverriteMode()
	{
		return OVERWRITE_MODE;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.formatting.AbstractFormatterTestCase#isInitializeMode()
	 */
	@Override
	protected boolean isInitializeMode()
	{
		return INITIALIZE_MODE;
	}

	/*
	 * Test 13, 21: We currently don't support <pre><pre>x</pre></pre> being on the same line (test still passes with
	 * current output)
	 */

	/*
	 * Test 17: The contents of the file under the formatted section looks incorrect, but it works fine in the editor
	 * (and the test passes)
	 */

	/*
	 * Test 3, 24: Comments do not format the same way as it did in studio 1.5
	 */

	/*
	 * Test 26: We currently don't support tags like "<![if ltIE5]>"
	 */

}
