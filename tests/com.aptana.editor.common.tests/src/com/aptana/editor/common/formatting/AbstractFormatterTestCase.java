package com.aptana.editor.common.formatting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.core.util.IOUtil;
import com.aptana.formatter.IScriptFormatter;

import junit.framework.TestCase;

public abstract class AbstractFormatterTestCase extends TestCase
{
	// Folder where the formatting files are located
	protected static String FORMATTING_FOLDER = "formatting";

	protected IScriptFormatter formatter;
	protected String formatterId;

	protected void setFormatter(IScriptFormatter codeFormatter, String codeFormatterId)
	{
		formatter = codeFormatter;
		formatterId = codeFormatterId;
	}

	protected void formatterTest(String sourceFile, String resultFile) throws IOException
	{
		String source = getContent(sourceFile);
		assertNotNull(sourceFile + " is null", source);
		
		String formattedText = formatter.formatToString(source, 0, source.length(), 0, false, null);
		String expectedResult = getContent(resultFile);

		assertNotNull("Could not format " + resultFile, formattedText);
		assertTrue("contents of " + sourceFile + " do not match contents of " + resultFile + " without white spaces.",
				compareIgnoreWhiteSpace(formattedText, expectedResult));
		assertTrue("contents of " + sourceFile + " do not match contents of " + resultFile + " (with white spaces)",
				compareWithWhiteSpace(formattedText, expectedResult));
	}

	protected String getContent(String filename) throws IOException
	{
		filename = FORMATTING_FOLDER + "/" + filename;
		File file = new File(filename);

		if (file.exists())
		{
			InputStream stream = FileLocator.openStream(Platform.getBundle(formatterId),
					Path.fromPortableString(filename), false);

			// Add back a newline character since IOUtil.read() removes the last newline char from the file
			return IOUtil.read(stream) + "\n";
		}

		return null;
	}

	protected abstract boolean compareIgnoreWhiteSpace(String original, String formattedText);

	protected abstract boolean compareWithWhiteSpace(String original, String formattedText);

	protected void generateFormattedFiles(String fileType) throws IOException
	{

		// Search for test files with the name "test{number}.{filetype}" where number is an integer and filetype is the
		// filetype extension (xml, html, js, css, etc.)

		int i = 1;
		String source;
		String formattedText;
		FileWriter formattedStream;
		String formattedPath = new String("test1_f.");
		String sourcePath = new String("test1.");
		formattedPath = FORMATTING_FOLDER + "/" + formattedPath + fileType;
		sourcePath += fileType;

		while ((source = getContent(sourcePath)) != null)
		{

			// format the test files and create a formatted version test{number}_f.{filetype}
			// NOTE: formatted tests need to be reviewed manually to make sure they are correct before proceeding with
			// normal tests
			File formattedFile = new File(formattedPath);
			if (!formattedFile.exists())
			{
				formattedStream = new FileWriter(new File(formattedPath));
				formattedText = formatter.formatToString(source, 0, source.length(), 0, false, null);
				if (formattedText != null && compareIgnoreWhiteSpace(source, formattedText))
				{
					formattedStream.write(formattedText);
				}
				formattedStream.close();
			}
			formattedPath = formattedPath.replace(String.valueOf(i), String.valueOf(i + 1));
			sourcePath = sourcePath.replace(String.valueOf(i), String.valueOf(i + 1));

			i++;
		}
	}

}