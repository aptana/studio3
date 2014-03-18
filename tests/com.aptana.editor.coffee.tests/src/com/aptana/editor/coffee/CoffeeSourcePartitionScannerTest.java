package com.aptana.editor.coffee;

import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITypedRegion;

public class CoffeeSourcePartitionScannerTest
{

	private IDocumentPartitioner partitioner;

	private void assertPartition(String code, String contentType, int offset, int length) throws CoreException
	{
		ITypedRegion actual = getPartition(code, offset);
		String type = actual.getType();
		if (IDocument.DEFAULT_CONTENT_TYPE.equals(type))
		{
			type = CoffeeSourceConfiguration.DEFAULT;
		}
		String c = code.substring(offset, offset + length);

		assertEquals("Content type doesn't match expectations for: " + c, contentType, type);
		assertEquals("Partition offset doesn't match expectations for: " + c, offset, actual.getOffset());
		assertEquals("Parition length doesn't match expectations for: " + c, length, actual.getLength());
	}

//	@Override
	@After
	public void tearDown() throws Exception
	{
		partitioner = null;
//		super.tearDown();
	}

	private ITypedRegion getPartition(String content, int offset) throws CoreException
	{
		if (partitioner == null)
		{
			IDocument document = new Document(content);
			CoffeeDocumentProvider provider = new CoffeeDocumentProvider()
			{
				@Override
				public IDocument getDocument(Object element)
				{
					return (IDocument) element;
				}
			};
			provider.connect(document);
			partitioner = document.getDocumentPartitioner();
		}
		return partitioner.getPartition(offset);
	}

	@Test
	public void testDefaultPartition() throws Exception
	{
		String source = "number = 42\n";
		assertPartition(source, CoffeeSourceConfiguration.DEFAULT, 0, 12);
	}

	@Test
	public void testSingleLineComment() throws Exception
	{
		String source = "# This is a comment\n";
		assertPartition(source, CoffeeSourceConfiguration.SINGLELINE_COMMENT, 0, 20);
	}

	@Test
	public void testMultilineComment() throws Exception
	{
		String source = "###\n" + //
				"CoffeeScript Compiler v1.1.1\n" + //
				"Released under the MIT License\n" + //
				"###\n";

		assertPartition(source, CoffeeSourceConfiguration.MULTILINE_COMMENT, 0, 67);
		assertPartition(source, CoffeeSourceConfiguration.DEFAULT, 67, 1);
	}

	// http://jira.appcelerator.org/browse/APSTUD-4054
	@Test
	public void testMultilineCommentNotDetectedIfFourOrMoreHashes() throws Exception
	{
		String source = "#### This is a single line comment\n" + //
				"number = 42\n" + //
				"#### This is another single line comment\n";

		assertPartition(source, CoffeeSourceConfiguration.SINGLELINE_COMMENT, 0, 35);
		assertPartition(source, CoffeeSourceConfiguration.DEFAULT, 35, 12);
		assertPartition(source, CoffeeSourceConfiguration.SINGLELINE_COMMENT, 47, 41);
	}

	@Test
	public void testExtendedRegex() throws Exception
	{
		String source = "OPERATOR = /// ^ (\n" + //
				"  ?: [-=]>             # function\n" + //
				"   | [-+*/%<>&|^!?=]=  # compound assign / compare\n" + //
				"   | >>>=?             # zero-fill right shift\n" + //
				"   | ([-+:])\\1         # doubles\n" + //
				"   | ([&|<>])\\2=?      # logic / shift\n" + //
				"   | \\?\\.              # soak access\n" + //
				"   | \\.{2,3}           # range or splat\n" + //
				") ///\n";

		// TODO Heregex can contain comments. For our purposes we still count those comments as heregex partitions!
		// We should handle the comments in syntax coloring/scoping, though!
		assertPartition(source, CoffeeSourceConfiguration.DEFAULT, 0, 11);
		assertPartition(source, CoffeeSourceConfiguration.HEREGEX, 11, 294);
		assertPartition(source, CoffeeSourceConfiguration.DEFAULT, 305, 1);
	}

	@Test
	public void testSingleQuotedString() throws Exception
	{
		String source = "single_string = 'string value'\n";

		assertPartition(source, CoffeeSourceConfiguration.DEFAULT, 0, 16);
		assertPartition(source, CoffeeSourceConfiguration.STRING_SINGLE, 16, 14);
		assertPartition(source, CoffeeSourceConfiguration.DEFAULT, 30, 1);
	}

	@Test
	public void testDoubleQuotedString() throws Exception
	{
		String source = "double_string = \"string value\"\n";

		assertPartition(source, CoffeeSourceConfiguration.DEFAULT, 0, 16);
		assertPartition(source, CoffeeSourceConfiguration.STRING_DOUBLE, 16, 14);
		assertPartition(source, CoffeeSourceConfiguration.DEFAULT, 30, 1);
	}

	@Test
	public void testSingleQuotedHeredoc() throws Exception
	{
		String source = "html = '''\n" + //
				"       <strong>\n" + //
				"        cup of coffeescript\n" + //
				"       </strong>\n" + //
				"       '''\n";

		assertPartition(source, CoffeeSourceConfiguration.DEFAULT, 0, 7);
		assertPartition(source, CoffeeSourceConfiguration.HEREDOC, 7, 75);
		assertPartition(source, CoffeeSourceConfiguration.DEFAULT, 82, 1);
	}

	@Test
	public void testDoubleQuotedHeredoc() throws Exception
	{
		String source = "html = \"\"\"\n" + //
				"       <strong>\n" + //
				"        cup of coffeescript\n" + //
				"       </strong>\n" + //
				"       \"\"\"\n";

		assertPartition(source, CoffeeSourceConfiguration.DEFAULT, 0, 7);
		assertPartition(source, CoffeeSourceConfiguration.DOUBLE_HEREDOC, 7, 75);
		assertPartition(source, CoffeeSourceConfiguration.DEFAULT, 82, 1);
	}

	@Test
	public void testEmbeddedJS() throws Exception
	{
		String source = "hi = `function() {\n" + //
				"  return [document.title, \"Hello JavaScript\"].join(\": \");\n" + //
				"}`\n";

		assertPartition(source, CoffeeSourceConfiguration.DEFAULT, 0, 5);
		assertPartition(source, CoffeeSourceConfiguration.COMMAND, 5, 74);
		assertPartition(source, CoffeeSourceConfiguration.DEFAULT, 79, 1);
	}

	@Test
	public void testRegexp() throws Exception
	{
		String source = "/(\\w+)/g\n";

		assertPartition(source, CoffeeSourceConfiguration.REGEXP, 0, 8);
		assertPartition(source, CoffeeSourceConfiguration.DEFAULT, 8, 1);
	}

	@Test
	public void testAPSTUD3246() throws Exception
	{
		String source = "var wallInitX = Math.round(screenWidth / 2) - Math.round(wallWidth / 2);\n";

		assertPartition(source, CoffeeSourceConfiguration.DEFAULT, 0, source.length());
	}

}
