package com.aptana.editor.html.formatter.tests;

import java.util.Arrays;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.junit.BeforeClass;
import org.junit.runners.Parameterized.Parameters;

import com.aptana.editor.common.formatting.AbstractFormatterTestCase;
import com.aptana.editor.html.HTMLPlugin;

public class FormattingTests extends AbstractFormatterTestCase
{
	@Parameters(name = "{0}")
	public static Iterable<Object[]> data()
	{
		return Arrays.asList(AbstractFormatterTestCase.getFiles(TEST_BUNDLE_ID, FILE_TYPE));
	}

	private static String FORMATTER_FACTORY_ID = "com.aptana.editor.html.formatterFactory"; //$NON-NLS-1$
	private static String TEST_BUNDLE_ID = "com.aptana.editor.html.formatter.tests"; //$NON-NLS-1$
	private static String FILE_TYPE = "html"; //$NON-NLS-1$

	@BeforeClass
	public static void initializePlugin() throws Exception
	{
		IPreferenceStore prefs = HTMLPlugin.getDefault().getPreferenceStore();
		prefs.setValue(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS, true);
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
