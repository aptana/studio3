package com.aptana.editor.common.formatting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import com.aptana.core.util.IOUtil;
import com.aptana.formatter.IScriptFormatter;

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
	protected static final String FORMATTING_PREFIX = "formatted_"; //$NON-NLS-1$

	protected IScriptFormatter formatter;
	protected String formatterId;

	protected void setFormatter(IScriptFormatter codeFormatter, String codeFormatterId)
	{
		formatter = codeFormatter;
		formatterId = codeFormatterId;
	}

	protected void formatterTest(String sourceFile, String fileType) throws IOException
	{
		String resultFile = FORMATTING_PREFIX + sourceFile;
		String[] formattedFiles = getFiles(FORMATTING_FOLDER, fileType, true);

		assertTrue(resultFile + "does not exist", Arrays.binarySearch(formattedFiles, resultFile) >= 0);

		String source = getContent(sourceFile);
		assertNotNull(sourceFile + " is null", source); //$NON-NLS-1$

		TextEdit formattedTextEdit = formatter.format(source, 0, source.length(), 0, false, null);
		String expectedResult = getContent(resultFile);
		IDocument document = new org.eclipse.jface.text.Document(source);

		try
		{
			assertNotNull("Could not format " + resultFile, formattedTextEdit); //$NON-NLS-1$
			formattedTextEdit.apply(document);
			assertTrue("contents of " + sourceFile + " do not match contents of " + resultFile, //$NON-NLS-1$ //$NON-NLS-2$
					compareWithWhiteSpace(document.get(), expectedResult));
		}
		catch (MalformedTreeException e)
		{
			assertNotNull("Could not format " + resultFile, formattedTextEdit); //$NON-NLS-1$
		}
		catch (BadLocationException e)
		{
			assertNotNull("Could not format " + resultFile, formattedTextEdit); //$NON-NLS-1$
		}

	}

	protected String getContent(String filename) throws IOException
	{
		filename = FORMATTING_FOLDER + "/" + filename; //$NON-NLS-1$
		InputStream stream = FileLocator.openStream(Platform.getBundle(formatterId), Path.fromPortableString(filename),
				false);

		// Add back a newline character since IOUtil.read() removes the last newline char from the file
		return IOUtil.read(stream) + "\n"; //$NON-NLS-1$
	}

	protected void generateFormattedFiles(String fileType) throws IOException
	{
		// Search for files of a particular fileType inside the formatting folder, format the files and create a
		// resulting file with the prefix 'formatted_'
		String source;
		FileWriter formattedStream;
		String formattedPath;
		String sourcePath;

		String[] files = getFiles(FORMATTING_FOLDER, fileType, false);
		String[] formattedFiles = getFiles(FORMATTING_FOLDER, fileType, true);

		for (String file : files)
		{
			// Don't overwrite the formatted file if it is already there
			if (Arrays.binarySearch(formattedFiles, FORMATTING_PREFIX + file) < 0)
			{
				sourcePath = file;
				formattedPath = FORMATTING_FOLDER + "/" + FORMATTING_PREFIX + file; //$NON-NLS-1$
				source = getContent(sourcePath);
				formattedStream = new FileWriter(new File(formattedPath));
				TextEdit formattedTextEdit = formatter.format(source, 0, source.length(), 0, false, null);
				if (formattedTextEdit instanceof ReplaceEdit)
				{
					formattedStream.write(((ReplaceEdit) formattedTextEdit).getText());
				}
				else if ((formattedTextEdit instanceof MultiTextEdit))
				{
					// write original content if the formatted text is same as original
					formattedStream.write(source);
				}
				formattedStream.close();
			}

		}
	}

	protected String[] getFiles(String directory, String fileType, boolean grabOnlyFormattedFiles)
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
				if (grabOnlyFormattedFiles)
				{
					if (path.startsWith(FORMATTING_PREFIX))
					{
						filePaths.add(path);
					}
				}
				else
				{
					if (!path.startsWith(FORMATTING_PREFIX))
					{
						filePaths.add(path);
					}
				}
			}
		}

		return (String[]) filePaths.toArray(new String[filePaths.size()]);
	}

	protected abstract boolean compareWithWhiteSpace(String original, String formattedText);

}