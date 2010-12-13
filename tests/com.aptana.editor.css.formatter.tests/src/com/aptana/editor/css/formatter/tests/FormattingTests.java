package com.aptana.editor.css.formatter.tests;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.formatting.AbstractFormatterTestCase;
import com.aptana.editor.css.formatter.CSSFormatterConstants;
import com.aptana.formatter.IScriptFormatter;
import com.aptana.formatter.IScriptFormatterFactory;
import com.aptana.formatter.ScriptFormatterManager;
import com.aptana.formatter.ui.CodeFormatterConstants;

public class FormattingTests extends AbstractFormatterTestCase
{
	// Turn this flag on for development only (used to generate the formatted files)
	// To generate formatted files, place files named test1.css, test2.css etc into a folder named formatting inside
	// this plugin. The test files need to be named "test{number}.{filetype}" where number is an incremental integer and
	// filetype is the filetype extension (xml, html, js, css, etc.)
	private static boolean INITIALIZE_MODE = false;

	private static final Pattern whiteSpaceAsterisk = Pattern.compile("[\\s\\*]");
	private static String FORMATTER_FACTORY_ID = "com.aptana.editor.css.formatterFactory";
	private static String FORMATTER_ID = "com.aptana.editor.css.formatter.tests";
	private static String LINE_DELIMITER = "\n";
	private static String FILE_TYPE = "css";
	private Map<String, String> prefs;

	@Override
	protected void setUp() throws Exception
	{
		IScriptFormatterFactory factory = (IScriptFormatterFactory) ScriptFormatterManager.getInstance()
				.getContributionById(FORMATTER_FACTORY_ID);

		setDefaultPreferences();
		IScriptFormatter codeFormatter = factory.createFormatter(LINE_DELIMITER, prefs);
		setFormatter(codeFormatter, FORMATTER_ID);

		if (INITIALIZE_MODE)
		{
			generateFormattedFiles(FILE_TYPE);
		}

		super.setUp();
	}

	@Override
	protected boolean compareIgnoreWhiteSpace(String original, String formattedText)
	{
		if (original == null || formattedText == null)
		{
			return original == formattedText;
		}

		// Strips all asterisk and white space, and compare the two formatted text
		original = whiteSpaceAsterisk.matcher(original).replaceAll(StringUtil.EMPTY);
		formattedText = whiteSpaceAsterisk.matcher(formattedText).replaceAll(StringUtil.EMPTY);
		return original.equals(formattedText);
	}

	@Override
	protected boolean compareWithWhiteSpace(String original, String formattedText)
	{
		return original.equals(formattedText);
	}

	private void setDefaultPreferences()
	{

		prefs = new HashMap<String, String>();

		prefs.put(CSSFormatterConstants.FORMATTER_TAB_CHAR, CodeFormatterConstants.SPACE);
		prefs.put(CSSFormatterConstants.FORMATTER_TAB_SIZE, "4"); //$NON-NLS-1$
		prefs.put(CSSFormatterConstants.FORMATTER_INDENTATION_SIZE, "4"); //$NON-NLS-1$
		prefs.put(CSSFormatterConstants.WRAP_COMMENTS, "false");
		prefs.put(CSSFormatterConstants.WRAP_COMMENTS_LENGTH, "80");
		prefs.put(CSSFormatterConstants.NEW_LINES_BEFORE_BLOCKS, CodeFormatterConstants.SAME_LINE);
		prefs.put(CSSFormatterConstants.LINES_AFTER_ELEMENTS, "1");
		prefs.put(CSSFormatterConstants.LINES_AFTER_DECLARATION, "0");
		prefs.put(CSSFormatterConstants.PRESERVED_LINES, "1");

	}

	public void test1() throws Exception
	{
		formatterTest("test1.css", "test1_f.css");
	}

	public void test2() throws Exception
	{
		formatterTest("test2.css", "test2_f.css");
	}

	public void test3() throws Exception
	{
		formatterTest("test3.css", "test3_f.css");
	}

	public void test4() throws Exception
	{
		formatterTest("test4.css", "test4_f.css");
	}

	public void test5() throws Exception
	{
		formatterTest("test5.css", "test5_f.css");
	}

	public void test6() throws Exception
	{
		formatterTest("test6.css", "test6_f.css");
	}

	public void test7() throws Exception
	{
		// We are not able to parse invalid css, so this test does not work for now.
		// formatterTest("test7.css", "test7_f.css");
	}

	public void test8() throws Exception
	{
		// corresponds to test 15 in studio 1.5 test. Result is different than 1.5 test since the newlines within an
		// @media rule does not have its own setting
		formatterTest("test8.css", "test8_f.css");
	}

	public void test9() throws Exception
	{
		formatterTest("test9.css", "test9_f.css");
	}

	public void test10() throws Exception
	{
		formatterTest("test10.css", "test10_f.css");
	}

	public void test11() throws Exception
	{
		formatterTest("test11.css", "test11_f.css");
	}

	public void test12() throws Exception
	{
		// Studio 1.5 tests seems to move all the selectors to a new line... The current behavior matches Studio 2
		// behavior.
		formatterTest("test12.css", "test12_f.css");
	}

	public void test13() throws Exception
	{
		formatterTest("test13.css", "test13_f.css");
	}

	public void test14() throws Exception
	{
		// The asterisk before a declaration is not valid CSS, which results in incorrect formatting (the parser is
		// reading * as a different declaration)
		// We may want to make it so it does not modify declarations with incorrect syntax like that

		// formatterTest("test14.css", "test14_f.css");
	}

}
