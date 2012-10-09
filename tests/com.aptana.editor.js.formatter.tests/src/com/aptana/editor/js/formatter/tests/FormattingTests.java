package com.aptana.editor.js.formatter.tests;

import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

import com.aptana.editor.common.formatting.AbstractFormatterTestCase;
import com.aptana.editor.js.JSPlugin;

public class FormattingTests extends AbstractFormatterTestCase
{
	// Turn this flag on for development only (used to generate the formatted files)
	// To generate formatted files, place js files under the 'formatting' folder and run these tests from the
	// com.aptana.editor.js.formatter.tests plugin
	// NOTE: Ensure that the contents section ends with a newline, or the generation may not work.
	private static boolean INITIALIZE_MODE = false;
	// Turning on the overwrite will re-generate the formatted block and overwrite it into the test files.
	// This is a drastic move that will require a review of the output right after to make sure we have the
	// right formatting for all the test file, so turn it on at your own risk.
	private static boolean OVERWRITE_MODE = false;

	private static String FORMATTER_FACTORY_ID = "com.aptana.editor.js.formatterFactory"; //$NON-NLS-1$
	private static String TEST_BUNDLE_ID = "com.aptana.editor.js.formatter.tests"; //$NON-NLS-1$
	private static String FILE_TYPE = "js"; //$NON-NLS-1$

	@Override
	protected void setUpSuite() throws Exception
	{
		// force JS plugin to load and ensure we use spaces for tabs!
		JSPlugin.getDefault().getPreferenceStore()
				.setDefault(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS, true);
		super.setUpSuite();
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
	 * Test 11, 14, 21, 24, 3, 35, 36, 37, 38, 39, 40, 45, 48, 52, 61, 74, 79, 9, 73, 81, 83: Studio 1.5 seems to
	 * add/remove extra spaces/newlines in a line. (We currently don't do this)
	 */

	/*
	 * Test 16, 27, 8, 93, 49: Studio 1.5 moves some of the comments into separate lines
	 */

	/*
	 * Test 22, 46, 80, 83: Studio 1.5 moves '//' comments to the previous line
	 */

	/*
	 * Test 31, 44, 60: Studio 1.5 moves the last ';' to a separate line
	 */

	/*
	 * Test 2: The function declaration after 'new' is moved to the next line (this does not happen in Studio 1.5)
	 */

	/*
	 * Test 64: We move all excess spaces in front of single line comments (Studio 1.5 preserves them)
	 */

	/*
	 * Test 85, 86, 87, 91: Studio 1.5 interprets and formats if/else statements differently
	 */
}
