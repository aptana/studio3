package com.aptana.editor.common.formatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import com.aptana.core.util.StringUtil;
import com.aptana.formatter.IScriptFormatter;
import com.aptana.formatter.IScriptFormatterFactory;
import com.aptana.formatter.ScriptFormatterManager;

/**
 * AbstractFormatterTestCase <br>
 * This class provides the basic functionality that should be used for all formatting tests. A formatting test should
 * extend this class and implement the method to compare the two formatted files (with white space).
 */
public abstract class AbstractFormatterTestCase extends TestCase
{
	// Folder where the formatting files are located
	protected static String FORMATTING_FOLDER = "formatting"; //$NON-NLS-1$
	protected IScriptFormatterFactory factory;

	/**
	 * Creates a test suite that loads a dynamic list of test-cases, one for each file in the working-directory.
	 * 
	 * @return A {@link Test} that wraps a {@link TestSuite}.
	 */
	public Test suite()
	{
		TestSuite suite = new TestSuite("Formatter Tests");
		String[] files = getFiles();
		for (final String fileName : files)
		{
			suite.addTest(new TestCase(fileName)
			{
				@Override
				protected void runTest() throws Throwable
				{
					FormatterTestFile file = new FormatterTestFile(factory, getTestBundleId(), fileName,
							getWorkingDirectory());
					if (isInitializeMode())
					{
						file.generateFormattedContent(isOverriteMode());
					}
					formatterTest(file, fileName, getFileType());
				}

			});
		}

		// Wrap everything in a TestSetup
		TestSetup setup = new TestSetup(suite)
		{
			protected void setUp() throws Exception
			{
				setUpSuite();
			}

			protected void tearDown() throws Exception
			{
				tearDownSuite();
			}
		};
		return setup;
	}

	/**
	 * Setup the test suite.
	 * 
	 * @throws Exception
	 */
	protected void setUpSuite() throws Exception
	{
		factory = (IScriptFormatterFactory) ScriptFormatterManager.getInstance().getContributionById(getFormatterId());
	}

	/**
	 * Tear-down the test suite.
	 * 
	 * @throws Exception
	 */
	protected void tearDownSuite()
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
			assertTrue("Formatted contents of " + filename + " do not match expected contents", //$NON-NLS-1$ //$NON-NLS-2$
					compareWithWhiteSpace(document.get().replaceAll("\r\n", "\n"), expectedResult));//$NON-NLS-1$ //$NON-NLS-2$
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
	 * Returns the file that will be tested.
	 */
	protected String[] getFiles()
	{
		String directory = getWorkingDirectory();
		String fileType = getFileType();
		String testBundleId = getTestBundleId();
		Enumeration<String> entryPaths = Platform.getBundle(testBundleId).getEntryPaths(directory);
		ArrayList<String> filePaths = new ArrayList<String>();
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

		return (String[]) filePaths.toArray(new String[filePaths.size()]);
	}

	/**
	 * Do a basic string comparison.
	 * 
	 * @param formattedText
	 * @param expectedResult
	 * @return True, if the formattedText and the expectedResults are equal.
	 */
	protected boolean compareWithWhiteSpace(String formattedText, String expectedResult)
	{
		return expectedResult.equals(formattedText);
	}

	/**
	 * Returns the directory that contains that files to test.
	 */
	protected String getWorkingDirectory()
	{
		return FORMATTING_FOLDER;
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

	/**
	 * Returns true if the tests are running in an overwrite mode.<br>
	 * The overwrite will re-generate the formatted block and overwrite it into the test files. This is a drastic move
	 * that will require a review of the output right after to make sure we have the right formatting for all the test
	 * file.<br>
	 * Note: Overwrite mode will only work when {@link #isInitializeMode()} returns <code>true</code> as well.
	 * 
	 * @see #isInitializeMode()
	 */
	protected abstract boolean isOverriteMode();

	/**
	 * Returns true if the tests are running in an initialization mode.<br>
	 * The initialize mode will generate the ==FORMATTED== block for files that don't have it.<br>
	 * NOTE: Ensure that the contents section ends with a newline, or the generation may not work.
	 * 
	 * @see #isOverriteMode()
	 */
	protected abstract boolean isInitializeMode();

}