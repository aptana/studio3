/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text;

import static org.junit.Assert.assertEquals;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.junit.After;
import org.junit.Test;

import com.aptana.editor.common.AbstractPartitionTestCase;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.js.JSSourceConfiguration;

public class JSSourcePartitionScannerJFlexTest extends AbstractPartitionTestCase
{

	private IDocumentPartitioner partitioner;

	private void assertContentType(String contentType, String code, int offset)
	{
		// HACK: Not sure how to force the default content type of our Document to
		// JSSourceConfiguration.DEFAULT, so we map those values to IDocument.DEFAULT_CONTENT_TYPE
		// as a workaround
		if (contentType.equals(JSSourceConfiguration.DEFAULT))
		{
			contentType = IDocument.DEFAULT_CONTENT_TYPE;
		}

		assertEquals("Content type doesn't match expectations for: " + code.charAt(offset), contentType,
				getContentType(code, offset));
	}

	@After
	public void tearDown() throws Exception
	{
		partitioner = null;
	}

	protected IPartitionTokenScanner createPartitionScanner()
	{
		return new JSSourcePartitionScannerJFlex();
	}

	@Override
	protected String[] getContentTypes()
	{
		return JSSourceConfiguration.getDefault().getContentTypes();
	}

	private String getContentType(String content, int offset)
	{
		if (partitioner == null)
		{
			// NOTE: the following is based on SimpleDocumentProvider#connect(Object)
			IDocument document = new Document(content);
			IPartitioningConfiguration configuration = JSSourceConfiguration.getDefault();

			partitioner = new FastPartitioner(createPartitionScanner(), configuration.getContentTypes());
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);

			CommonEditorPlugin.getDefault().getDocumentScopeManager().registerConfiguration(document, configuration);
		}

		return partitioner.getContentType(offset);
	}

	@Test
	public void testPartitioningOfCommentSpanningSingleLine()
	{
		String source =
		// 1 2 3 4 5
		// 012345678901234567890123456789012345678901234567890
		"/* This is JS comment on one Line */\n";

		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 0);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 1);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 2);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 33);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 34);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 35);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 36);
	}

	@Test
	public void testPartitioningOfJSDocSpanningSingleLine()
	{
		String source =
		// 1 2 3 4 5
		// 012345678901234567890123456789012345678901234567890
		"/**This is JS comment on one Line */\n";

		assertContentType(JSSourceConfiguration.JS_DOC, source, 0);
		assertContentType(JSSourceConfiguration.JS_DOC, source, 1);
		assertContentType(JSSourceConfiguration.JS_DOC, source, 2);
		assertContentType(JSSourceConfiguration.JS_DOC, source, 33);
		assertContentType(JSSourceConfiguration.JS_DOC, source, 34);
		assertContentType(JSSourceConfiguration.JS_DOC, source, 35);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 36);
	}

	@Test
	public void testPartitioningOfEmptyComment()
	{
		String source =
		// 1 2 3 4 5
		// 012345678901234567890123456789012345678901234567890
		"/**/\n";

		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 0);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 1);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 2);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 3);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 4);
	}

	@Test
	public void testDivisionIsntPickedUpAsRegexp()
	{
		String source =
		// 1 2 3 4 5
		// 012345678901234567890123456789012345678901234567890
		"var width = Math.floor(viewWidth / characterWidth);\n";

		assertContentType(JSSourceConfiguration.DEFAULT, source, 0);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 1);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 35);
	}

	@Test
	public void testSimpleRegexp()
	{
		String source = "var regexp = /^ace$/;\n";

		assertContentType(JSSourceConfiguration.DEFAULT, source, 0);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 13);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 19);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 20);
	}

	@Test
	public void testRegexpWithEscapeCharacter()
	{
		String source = "var regexp = /^\\/ace$/;\n";

		assertContentType(JSSourceConfiguration.DEFAULT, source, 0);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 13);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 21);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 22);
	}

	@Test
	public void testComplexRegexp()
	{
		String source =
		// 1 2 3 4 5
		// 012345678901234567890123456789012345678901234567890
		"var regexp = /^\\/\\*-secure-([\\s\\S]*)\\*\\/\\s*$/;\n";

		assertContentType(JSSourceConfiguration.DEFAULT, source, 0);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 13);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 44);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 45);
	}

	@Test
	public void testComplexRegexp2()
	{

		String source =
		// 1 2 3
		// 01234 5678 90 123456789012 3 456789012345678
		"/^[^{\\[]*\\/\\*/.test();/\\\\/g// comment";

		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 0);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 13);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 14);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 21);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 22);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 26);
		assertContentType(JSSourceConfiguration.JS_SINGLELINE_COMMENT, source, 27);
	}

	@Test
	public void testDivisions()
	{
		String source = "if ( x / s >= 0) { x = x / 10; }";

		for (int i = 0; i < source.length(); i++)
		{
			assertContentType(JSSourceConfiguration.DEFAULT, source, i);
		}
	}

	@Test
	public void testEndDoubleSlashRegexp()
	{

		String source =
		// 1 2
		// 01234567890 123456789012
		"if (/Mobile\\//.test(){}";

		assertContentType(JSSourceConfiguration.DEFAULT, source, 0);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 4);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 13);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 14);
	}

	@Test
	public void testPartitioningOfCommentSpanningMultipleLines()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012 3456789012345678901234567890
		"/* This is JS comment\nspanning multiple lines */\n";

		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 0);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 1);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 2);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 45);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 46);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 47);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 48);
	}

	@Test
	public void testPartitioningOfSingleQuotedString()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012345678901234567 8901234567890
		"' This is a single quoted JS string'\n";
		for (int i = 0; i <= 35; i++)
		{
			assertContentType(JSSourceConfiguration.STRING_SINGLE, source, i);
		}
		assertContentType(JSSourceConfiguration.DEFAULT, source, 36);
	}

	@Test
	public void testPartitioningOfEmptySingleQuotedString()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012345678901234567 8901234567890
		"''\n";
		for (int i = 0; i <= 1; i++)
		{
			assertContentType(JSSourceConfiguration.STRING_SINGLE, source, i);
		}
		assertContentType(JSSourceConfiguration.DEFAULT, source, 2);
	}

	@Test
	public void testPartitioningOfSingleQuotedStringWithEscape()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012345678901234567 89012345678 9012
		"' This is a single quoted JS string with escape \\' '\n";

		for (int i = 0; i <= 51; i++)
		{
			assertContentType(JSSourceConfiguration.STRING_SINGLE, source, i);
		}
		assertContentType(JSSourceConfiguration.DEFAULT, source, 52);
	}

	@Test
	public void testPartitioningOfSingleQuotedStringWithDoubleQuote()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012345678901234567 89012345678 9012
		"' This is a single quoted JS string with double quote \" '\n";

		for (int i = 0; i <= 56; i++)
		{
			assertContentType(JSSourceConfiguration.STRING_SINGLE, source, i);
		}
		assertContentType(JSSourceConfiguration.DEFAULT, source, 57);
	}

	@Test
	public void testPartitioningOfDoubleQuotedString()
	{
		String source =
		// 1 2 3 4 5
		// 0 123456789012345678901234567890123456 7 8901234567890
		"\" This is a double quoted JS string\"\n";
		for (int i = 0; i <= 35; i++)
		{
			assertContentType(JSSourceConfiguration.STRING_DOUBLE, source, i);
		}
		assertContentType(JSSourceConfiguration.DEFAULT, source, 36);
	}

	@Test
	public void testPartitioningOfEmptyDoubleQuotedString()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012345678901234567 8901234567890
		"\"\"\n";
		for (int i = 0; i <= 1; i++)
		{
			assertContentType(JSSourceConfiguration.STRING_DOUBLE, source, i);
		}
		assertContentType(JSSourceConfiguration.DEFAULT, source, 2);
	}

	@Test
	public void testPartitioningOfDoubleQuotedStringWithEscape()
	{
		String source =
		// 1 2 3 4 5
		// 0 1234567890123456789012345678901234567 89012345678 9 012
		"\" This is a double quoted JS string with escape \\\" \"\n";

		for (int i = 0; i <= 51; i++)
		{
			assertContentType(JSSourceConfiguration.STRING_DOUBLE, source, i);
		}
		assertContentType(JSSourceConfiguration.DEFAULT, source, 52);
	}

	@Test
	public void testPartitioningOfDoubleQuotedStringWithSingleQuote()
	{
		String source =
		// 1 2 3 4 5
		// 0 12345678901234567890123456789012345678901234567890123456 7
		"\" This is a double quoted JS string with single quote ' \"\n";

		for (int i = 0; i <= 56; i++)
		{
			assertContentType(JSSourceConfiguration.STRING_DOUBLE, source, i);
		}
		assertContentType(JSSourceConfiguration.DEFAULT, source, 57);
	}
	
	@Test
	public void testPartitioningOfNoSubstitutionTemplate()
	{
		String source ="`This template has no substitution.`\n";

		assertContentType(JSSourceConfiguration.JS_TEMPLATE, source, 0);
		assertContentType(JSSourceConfiguration.JS_TEMPLATE, source, 1);
		assertContentType(JSSourceConfiguration.JS_TEMPLATE, source, 34);
		assertContentType(JSSourceConfiguration.JS_TEMPLATE, source, 35);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 36);
	}
	
	@Test
	public void testPartitioningOfSubstitutionTemplate()
	{
		String source ="`This template has ${ 1 } substitution.`\n";

		assertContentType(JSSourceConfiguration.JS_TEMPLATE, source, 0);
		assertContentType(JSSourceConfiguration.JS_TEMPLATE, source, 1);
		assertContentType(JSSourceConfiguration.JS_TEMPLATE, source, 19);
		assertContentType(JSSourceConfiguration.JS_TEMPLATE, source, 20);
		// FIXME: Doesn't transition to default partition inside code blocks!
//		assertContentType(JSSourceConfiguration.DEFAULT, source, 21);
//		assertContentType(JSSourceConfiguration.DEFAULT, source, 22);
//		assertContentType(JSSourceConfiguration.DEFAULT, source, 23);
		assertContentType(JSSourceConfiguration.JS_TEMPLATE, source, 24);
		assertContentType(JSSourceConfiguration.JS_TEMPLATE, source, 25);
		assertContentType(JSSourceConfiguration.JS_TEMPLATE, source, 38);
		assertContentType(JSSourceConfiguration.JS_TEMPLATE, source, 39);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 40);
	}

	@Test
	public void testFlexPartitioningOfCommentSpanningSingleLine2() throws BadLocationException
	{
		String source = "//comment\r\nsomething//comment\r\n";
		assertPartitions(source, "__js_singleline_comment:0:11", "__dftl_partition_content_type:11:20",
				"__js_singleline_comment:20:");
	}

	@Test
	public void testFlexPartitioningOfCommentSpanningSingleLine3() throws BadLocationException
	{
		String source = "//comment\rsomething//comment\r\nvar a=10;";
		assertPartitions(source, "__js_singleline_comment:0:10", "__dftl_partition_content_type:10:19",
				"__js_singleline_comment:19:30", "__dftl_partition_content_type:30:");
	}

	@Test
	public void testFlexPartitioningOfCommentSpanningSingleLine4() throws BadLocationException
	{
		String source = "//comment\nsomething//comment\n";
		assertPartitions(source, "__js_singleline_comment:0:10", "__dftl_partition_content_type:10:19",
				"__js_singleline_comment:19:");
	}

	@Test
	public void testFlexSimple() throws BadLocationException
	{
		String source = "var a=10;\r\nvar b=20;";
		assertPartitions(source, "__dftl_partition_content_type:0:");
	}

	@Test
	public void testFlexComment() throws BadLocationException
	{
		String source = "//comment\n    \na = 10;";
		assertPartitions(source, "__js_singleline_comment:0:10", "__dftl_partition_content_type:10:");
	}

	@Test
	public void testFlexComment2() throws BadLocationException
	{
		String source = "//comment\r\n\na = 10;";
		assertPartitions(source, "__js_singleline_comment:0:11", "__dftl_partition_content_type:11:");
	}

	@Test
	public void testFlexComment3() throws BadLocationException
	{
		String source = "//comment\r\r\na = 10;";
		assertPartitions(source, "__js_singleline_comment:0:10", "__dftl_partition_content_type:10:");
	}

	@Test
	public void testFlexMultiLineComment() throws BadLocationException
	{
		String source = "var /*comment*/ a=10;";
		assertPartitions(source, "__dftl_partition_content_type:0:4", "__js_multiline_comment:4:15",
				"__dftl_partition_content_type:15:");
	}

	@Test
	public void testFlexSingleLineComment() throws BadLocationException
	{
		String source = "//comment";
		assertPartitions(source, "__js_singleline_comment:0:");
	}

	@Test
	public void testFlexMultiLineComment2() throws BadLocationException
	{
		String source = "var /*comment\r\ncomment\rcomment\ncomment*/ a=10;";
		assertPartitions(source, "__dftl_partition_content_type:0:4", "__js_multiline_comment:4:40",
				"__dftl_partition_content_type:40:");
	}

	@Test
	public void testFlexMultiLineCommentEmpty() throws BadLocationException
	{
		String source = "var /**/ a=10;";
		assertPartitions(source, "__dftl_partition_content_type:0:4", "__js_multiline_comment:4:8",
				"__dftl_partition_content_type:8:");
	}

	@Test
	public void testFlexMultiLineCommentUnclosed() throws BadLocationException
	{
		String source = "var /*;";
		assertPartitions(source, "__dftl_partition_content_type:0:4", "__js_multiline_comment:4:");
	}

	@Test
	public void testSDocCommentUnclosed() throws BadLocationException
	{
		String source = "var /**;";
		assertPartitions(source, "__dftl_partition_content_type:0:4", "__js_sdoc_comment:4:");
	}

	@Test
	public void testFlexRegexp() throws BadLocationException
	{
		String source = "var regexp = /^\\/\\*-secure-([\\s\\S]*)\\*\\/\\s*$/;\n";

		assertPartitions(source, "__dftl_partition_content_type:0:13", "__js_regexp:13:45",
				"__dftl_partition_content_type:45:");
	}

	@Test
	public void testFlexRegexpUnfinished() throws BadLocationException
	{
		String source = "var regexp = /^*";

		assertPartitions(source, "__dftl_partition_content_type:0:");
	}

	@Test
	public void testTISTUD2678() throws BadLocationException
	{
		String source = "var I = F.selectors = {\r\n	filters : {\r\n		header : function(T) {\r\n			return /h\\d/i.test(T.nodeName);\r\n		},\r\n		text: function(T) {\r\n			return \"text\" === T.type;\r\n		}\r\n	}\r\n};";

		assertPartitions(source, "__dftl_partition_content_type:0:75", "__js_regexp:75:81",
				"__dftl_partition_content_type:81:140", "__js_string_double:140:146",
				"__dftl_partition_content_type:146:");
	}

	@Test
	public void testTISTUD2678_1() throws BadLocationException
	{
		String source = "var r = /h\\d/i;\r\n\"text\" === T.type;";

		assertPartitions(source, "__dftl_partition_content_type:0:8", "__js_regexp:8:14",
				"__dftl_partition_content_type:14:17", "__js_string_double:17:23", "__dftl_partition_content_type:23:");
	}

	@Test
	public void testTISTUD2678_2() throws BadLocationException
	{
		String source = "function(T) { return /h\\d/i.test(T.nodeName); };\r\nfunction(T) { return \"text\" === T.type;};";

		assertPartitions(source, "__dftl_partition_content_type:0:21", "__js_regexp:21:27",
				"__dftl_partition_content_type:27:71", "__js_string_double:71:77", "__dftl_partition_content_type:77:");
	}

	@Test
	public void testTISTUD2678_3() throws BadLocationException
	{
		String source = "/h\\d/i.test(T.nodeName);";

		assertPartitions(source, "__js_regexp:0:6", "__dftl_partition_content_type:6:");
	}

	@Test
	public void testTISTUD2678_4() throws BadLocationException
	{
		String source = "return /h\\d/i.test(T.nodeName);";

		assertPartitions(source, "__dftl_partition_content_type:0:7", "__js_regexp:7:13",
				"__dftl_partition_content_type:13:");
	}

	@Test
	public void testFlexString() throws BadLocationException
	{
		String source = "var string = \"aaa\"";

		assertPartitions(source, "__dftl_partition_content_type:0:13", "__js_string_double:13:");
	}

	@Test
	public void testFlexString2() throws BadLocationException
	{
		String source = "var string = 'aaa'";

		assertPartitions(source, "__dftl_partition_content_type:0:13", "__js_string_single:13:");
	}

	@Test
	public void testFlexString3() throws BadLocationException
	{
		String source = "\"aa\"";

		assertPartitions(source, "__js_string_double:0:");
	}

	@Test
	public void testFlexStringWithEscape() throws BadLocationException
	{
		String source = "\"a\\\"a\"";

		assertPartitions(source, "__js_string_double:0:");
	}

	@Test
	public void testFlexStringWithEscape2() throws BadLocationException
	{
		String source = "'a\\'\"a'";

		assertPartitions(source, "__js_string_single:0:");
	}

	@Test
	public void testFlexStringUnfinished() throws BadLocationException
	{
		String source = "var string = \"aaa\nvar b = 10";

		// Note: this is different in the offsets. The old parser reported offset 18 (adding new line)
		// which we don't do here.
		assertPartitions(source, "__dftl_partition_content_type:0:13", "__js_string_double:13:17",
				"__dftl_partition_content_type:17:");
	}

	@Test
	public void testFlexStringUnfinished2() throws BadLocationException
	{
		String source = "var string = \"aaa\r\nvar b = 10";

		// Note: this is different in the offsets. The old parser reported offset 18 (adding new line)
		// which we don't do here.
		assertPartitions(source, "__dftl_partition_content_type:0:13", "__js_string_double:13:17",
				"__dftl_partition_content_type:17:");
	}

	@Test
	public void testFlexStringUnfinished3() throws BadLocationException
	{
		String source = "var string = \"aaa\rvar b = 10";

		// Note: this is different in the offsets. The old parser reported offset 18 (adding new line)
		// which we don't do here.
		assertPartitions(source, "__dftl_partition_content_type:0:13", "__js_string_double:13:17",
				"__dftl_partition_content_type:17:");
	}

	@Test
	public void testFlexComments() throws BadLocationException
	{
		String source = "10; /*co*/ var //comm";

		assertPartitions(source, "__dftl_partition_content_type:0:4", "__js_multiline_comment:4:10",
				"__dftl_partition_content_type:10:15", "__js_singleline_comment:15:");
	}

	@Test
	public void testFlexPartial() throws BadLocationException
	{
		String source = "10; /*comm";

		IDocument doc = configDoc(source);
		assertPartitions(doc, "__dftl_partition_content_type:0:4", "__js_multiline_comment:4:");

		doc.replace(doc.getLength() - 2, 0, "*/ var //co");
		assertEquals("10; /*co*/ var //comm", doc.get());

		assertPartitions(doc, "__dftl_partition_content_type:0:4", "__js_multiline_comment:4:10",
				"__dftl_partition_content_type:10:15", "__js_singleline_comment:15:");
	}

	@Test
	public void testFlexPartial2() throws BadLocationException
	{
		String source = "10; /*comm*/bar";

		IDocument doc = configDoc(source);
		assertPartitions(doc, "__dftl_partition_content_type:0:4", "__js_multiline_comment:4:12",
				"__dftl_partition_content_type:12:");

		doc.replace(doc.getLength() - 6, 0, "*/ ");
		assertEquals("10; /*com*/ m*/bar", doc.get());

		assertPartitions(doc, "__dftl_partition_content_type:0:4", "__js_multiline_comment:4:11",
				"__dftl_partition_content_type:11:");
	}

	@Test
	public void testFlexPartial3() throws BadLocationException
	{
		//@formatter:off
		String source = "" +
				"/**\n" +
				" * comment\n" +
				" */\n" +
				"function func() {\n" +
				"    /**\n" + //start at pos=45
				"     *  |\n" + //we'll add some code here (pos=58)
				"     */\n" +
				"     * comment\n" +
				"     *\n" +
				"     *  comment that became code\n" +
				"     */\n" +
				"    var a = 10;\n" +
				"    /**\n" +
				"     * comment\n" +
				"     */\n" +
				"    var b = 10;\n" +
				"}\n" +
				"";
		//@formatter:on

		IDocument doc = configDoc(source);
		assertPartitions(doc, "__js_sdoc_comment:0:18", "__dftl_partition_content_type:18:41",
				"__js_sdoc_comment:41:62", "__dftl_partition_content_type:62:146", "__js_sdoc_comment:146:172",
				"__dftl_partition_content_type:172:");

		doc.replace(doc.get().indexOf('|') + 1, 0, "s");

		assertPartitions(doc, "__js_sdoc_comment:0:18", "__dftl_partition_content_type:18:41",
				"__js_sdoc_comment:41:63", "__dftl_partition_content_type:63:147", "__js_sdoc_comment:147:173",
				"__dftl_partition_content_type:173:");
	}

	@Test
	public void testFlexPartial4() throws BadLocationException
	{
		//@formatter:off
		String source = "" +
				"/**\n" +
				" * comment\n" +
				" */\n" +
				"function func() {\n" +
				"    /**\n" +
				"     * \n" +
				"     * comment\n" +
				"     |*/\n" + //we'll remove the '/' at the end of this comment.
				"    var a = 10;\n" +
				"    /**\n" +
				"     * comment\n" +
				"     */\n" +
				"    var b = 10;\n" +
				"}\n" +
				"";
		//@formatter:on

		IDocument doc = configDoc(source);
		assertPartitions(doc, "__js_sdoc_comment:0:18", "__dftl_partition_content_type:18:41",
				"__js_sdoc_comment:41:76", "__dftl_partition_content_type:76:97", "__js_sdoc_comment:97:123",
				"__dftl_partition_content_type:123:");

		doc.replace(doc.get().indexOf('|') + 2, 1, "");

		assertPartitions(doc, "__js_sdoc_comment:0:18", "__dftl_partition_content_type:18:41",
				"__js_sdoc_comment:41:122", "__dftl_partition_content_type:122:");
	}

	@Test
	public void testFlexUnfinished() throws Exception
	{
		String source = "" + "myObject = {\n" + "	/**|\"key\": function(one, two, three) {}\n" + "}\n" + "";
		assertPartitions(source, "__dftl_partition_content_type:0:14", "__js_sdoc_comment:14:");

	}

}
