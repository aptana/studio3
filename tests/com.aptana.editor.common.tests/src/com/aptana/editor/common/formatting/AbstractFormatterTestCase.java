package com.aptana.editor.common.formatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import com.aptana.formatter.IScriptFormatter;
import com.aptana.formatter.IScriptFormatterFactory;

import junit.framework.TestCase;

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

	protected void formatterTest(FormatterTestFile file, String filename, String fileType) throws IOException
	{
		IScriptFormatter formatter = file.getFormatter();
		String source = file.getContent();
		TextEdit formattedTextEdit = formatter.format(source, 0, source.length(), 0, false, null);

		String expectedResult = file.getFormattedContent();
		IDocument document = new org.eclipse.jface.text.Document(source);

		try
		{
			assertNotNull("Could not format " + filename, formattedTextEdit); //$NON-NLS-1$
			formattedTextEdit.apply(document);
			assertTrue("Formatted contents of " + filename + " do not match expected contents", //$NON-NLS-1$ //$NON-NLS-2$
					compareWithWhiteSpace(document.get(), expectedResult));
		}
		catch (MalformedTreeException e)
		{
			assertNotNull("Could not format " + filename, formattedTextEdit); //$NON-NLS-1$
		}
		catch (BadLocationException e)
		{
			assertNotNull("Could not format " + filename, formattedTextEdit); //$NON-NLS-1$
		}

	}

	protected String[] getFiles(String directory, String fileType, String formatterId)
	{
		@SuppressWarnings("unchecked")
		Enumeration<String> entryPaths = Platform.getBundle(formatterId).getEntryPaths(directory);
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

	protected abstract boolean compareWithWhiteSpace(String original, String formattedText);

}