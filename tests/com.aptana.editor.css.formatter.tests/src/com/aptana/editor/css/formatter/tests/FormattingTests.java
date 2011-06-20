package com.aptana.editor.css.formatter.tests;

import com.aptana.editor.common.formatting.AbstractFormatterTestCase;

public class FormattingTests extends AbstractFormatterTestCase
{
	// Turn this flag on for development only (used to generate the formatted files)
	// To generate formatted files, place css files under the 'formatting' folder and run these tests from the
	// com.aptana.editor.css.formatter.tests plugin
	// NOTE: Ensure that the contents section ends with a newline, or the generation may not work.
	private static boolean INITIALIZE_MODE = false;
	// Turning on the overwrite will re-generate the formatted block and overwrite it into the test files.
	// This is a drastic move that will require a review of the output right after to make sure we have the
	// right formatting for all the test file, so turn it on at your own risk.
	private static boolean OVERWRITE_MODE = false;

	private static String FORMATTER_FACTORY_ID = "com.aptana.editor.css.formatterFactory"; //$NON-NLS-1$
	private static String TEST_BUNDLE_ID = "com.aptana.editor.css.formatter.tests"; //$NON-NLS-1$
	private static String FILE_TYPE = "css"; //$NON-NLS-1$

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

	// Tests that are not working are named without the .css extension

	// Notes on Tests:

	/*
	 * Test 7: We are not able to parse invalid css, so this test does not work for now.
	 */

	/*
	 * Test 8: Corresponds to test 15 in studio 1.5 test. Result is different than 1.5 test since the newlines within an
	 * @media rule does not have its own setting
	 */

	/*
	 * Test 11: Studio 1.5 tests seems to move all the selectors to a new line... The current behavior matches Studio 2
	 * behavior.
	 */

	/*
	 * Test 14: The asterisk before a declaration is not valid CSS, which results in incorrect formatting (the parser is
	 * reading * as a different declaration). We may want to make it so it does not modify declarations with incorrect
	 * syntax like that
	 */

}
