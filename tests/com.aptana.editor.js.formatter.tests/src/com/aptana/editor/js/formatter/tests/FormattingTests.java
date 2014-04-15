package com.aptana.editor.js.formatter.tests;

import java.util.Arrays;

import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.junit.BeforeClass;
import org.junit.runners.Parameterized.Parameters;

import com.aptana.editor.common.formatting.AbstractFormatterTestCase;
import com.aptana.editor.js.JSPlugin;

public class FormattingTests extends AbstractFormatterTestCase
{
	@Parameters(name = "{0}")
	public static Iterable<Object[]> data()
	{
		return Arrays.asList(AbstractFormatterTestCase.getFiles(TEST_BUNDLE_ID, FILE_TYPE));
	}

	private static String FORMATTER_FACTORY_ID = "com.aptana.editor.js.formatterFactory"; //$NON-NLS-1$
	private static String TEST_BUNDLE_ID = "com.aptana.editor.js.formatter.tests"; //$NON-NLS-1$
	private static String FILE_TYPE = "js"; //$NON-NLS-1$

	@BeforeClass
	public static void initializePlugin() throws Exception
	{
		// force JS plugin to load and ensure we use spaces for tabs!
		JSPlugin.getDefault().getPreferenceStore()
				.setDefault(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS, true);
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
