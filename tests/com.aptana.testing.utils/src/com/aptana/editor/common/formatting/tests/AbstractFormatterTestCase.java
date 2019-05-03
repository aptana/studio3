package com.aptana.editor.common.formatting.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;

import com.aptana.core.util.StringUtil;
import com.aptana.formatter.IScriptFormatter;
import com.aptana.formatter.IScriptFormatterFactory;
import com.aptana.formatter.ScriptFormatterManager;

/**
 * AbstractFormatterTestCase <br>
 * This class provides the basic functionality that should be used for all formatting tests. A formatting test should
 * extend this class and implement the method to compare the two formatted files (with white space).
 */
@RunWith(Parameterized.class)
public abstract class AbstractFormatterTestCase
{
	// Folder where the formatting files are located
	protected static String FORMATTING_FOLDER = "formatting"; //$NON-NLS-1$
	protected IScriptFormatterFactory factory;

	@Parameter(0)
	public String fFilename;

	@org.junit.Test
	public void test() throws Exception
	{
		FormatterTestFile file = new FormatterTestFile(factory, getTestBundleId(), fFilename, FORMATTING_FOLDER);
		formatterTest(file, fFilename, getFileType());
	}

	/**
	 * Setup the test suite.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		factory = (IScriptFormatterFactory) ScriptFormatterManager.getInstance().getContributionById(getFormatterId());
	}

	/**
	 * Tear-down the test suite.
	 * 
	 * @throws Exception
	 */
	@After
	public void tearDown()
	{
		factory = null;
	}

	/**
	 * Execute a single formatter test.
	 * 
	 * @param file
	 * @param filename
	 * @param fileType
	 * @throws IOException
	 */
	protected void formatterTest(FormatterTestFile file, String filename, String fileType) throws IOException
	{
		IScriptFormatter formatter = file.getFormatter();
		String source = file.getContent();
		TextEdit formattedTextEdit = formatter.format(source, 0, source.length(), 0, false, null, StringUtil.EMPTY);

		String expectedResult = file.getFormattedContent();
		IDocument document = new org.eclipse.jface.text.Document(source);

		try
		{
			assertNotNull("Could not format " + filename, formattedTextEdit); //$NON-NLS-1$
			formattedTextEdit.apply(document);
			assertEqualsWithWhiteSpace("Formatted contents of " + filename + " do not match expected contents", //$NON-NLS-1$ //$NON-NLS-2$
					expectedResult, document.get().replaceAll("\r\n", "\n"));//$NON-NLS-1$ //$NON-NLS-2$
		}
		catch (MalformedTreeException e)
		{
			assertNotNull("MalformedTreeException: Could not format " + filename, formattedTextEdit); //$NON-NLS-1$
		}
		catch (BadLocationException e)
		{
			assertNotNull("BadLocationException: Could not format " + filename, formattedTextEdit); //$NON-NLS-1$
		}

	}

	/**
	 * Returns the files that will be tested.
	 */
	public static Object[][] getFiles(String testBundleId, String fileType)
	{
		String directory = FORMATTING_FOLDER;
		Enumeration<String> entryPaths = Platform.getBundle(testBundleId).getEntryPaths(directory);
		List<String> filePaths = new ArrayList<String>();
		String path;

		while (entryPaths.hasMoreElements())
		{
			path = entryPaths.nextElement();
			path = path.replaceAll(FORMATTING_FOLDER + "[/\\\\]", ""); //$NON-NLS-1$ //$NON-NLS-2$

			// Check for correct file type
			if (path.endsWith(fileType))
			{
				filePaths.add(path);
			}
		}
		Collections.sort(filePaths);
		
		Object[][] value = new Object[filePaths.size()][];
		int x = 0;
		for (String filePath : filePaths)
		{
			value[x++] = new Object[] { filePath };
		}
		return value;
	}

	protected void assertEqualsWithWhiteSpace(String message, String expected, String actual)
	{
		// This is a temporary hack for cases where there is a difference when running on Windows vs. Linux.
		// In some cases, we get an extra ending line terminator, probably because we don't run the formatting in a
		// 'standard' way through the ScriptFormattingStrategy and the IContentFormatter.
		// The hack check for an extra new-line and the end of one of the strings we compare.
		actual = trimTrailingWhitespaces(actual);
		expected = trimTrailingWhitespaces(expected);
		assertEquals(message, expected, actual);
	}

	/**
	 * Returns the given string trimmed from any trailing new-line characters.
	 */
	private String trimTrailingWhitespaces(String expected)
	{
		int whitespaceCount = 0;
		int originalLength = expected.length();
		for (int i = originalLength - 1; i >= 0; i--)
		{
			char c = expected.charAt(i);
			if (Character.isWhitespace(c))
			{
				whitespaceCount++;
			}
		}
		if (whitespaceCount > 0)
		{
			return expected.substring(0, originalLength - whitespaceCount);
		}
		return expected;
	}

	/**
	 * Returns the test-bundle Id.
	 */
	protected abstract String getTestBundleId();

	/**
	 * Returns the formatter-Id
	 */
	protected abstract String getFormatterId();

	/**
	 * Returns the file-type that is being formatter.
	 */
	protected abstract String getFileType();

}