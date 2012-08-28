/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import com.aptana.editor.common.IPartitioningConfiguration;

public class JSSourcePartitionScannerJFlexTest extends JSSourcePartitionScannerTest
// public class JSSourcePartitionScannerJFlexTest extends TestCase
{

	protected IPartitionTokenScanner createPartitionScanner()
	{
		// return new JSSourcePartitionScanner();
		return new JSSourcePartitionScannerJFlex();
	}

	public void testFlexPartitioningOfCommentSpanningSingleLine2() throws BadLocationException
	{
		String source = "//comment\r\nsomething//comment\r\n";
		assertPartitions(source, "__js_singleline_comment:0:11", "__dftl_partition_content_type:11:20",
				"__js_singleline_comment:20:");
	}

	public void testFlexPartitioningOfCommentSpanningSingleLine3() throws BadLocationException
	{
		String source = "//comment\rsomething//comment\r\nvar a=10;";
		assertPartitions(source, "__js_singleline_comment:0:10", "__dftl_partition_content_type:10:19",
				"__js_singleline_comment:19:30", "__dftl_partition_content_type:30:");
	}

	public void testFlexPartitioningOfCommentSpanningSingleLine4() throws BadLocationException
	{
		String source = "//comment\nsomething//comment\n";
		assertPartitions(source, "__js_singleline_comment:0:10", "__dftl_partition_content_type:10:19",
				"__js_singleline_comment:19:");
	}

	public void testFlexSimple() throws BadLocationException
	{
		String source = "var a=10;\r\nvar b=20;";
		assertPartitions(source, "__dftl_partition_content_type:0:");
	}

	public void testFlexComment() throws BadLocationException
	{
		String source = "//comment\n    \na = 10;";
		assertPartitions(source, "__js_singleline_comment:0:10", "__dftl_partition_content_type:10:");
	}

	public void testFlexComment2() throws BadLocationException
	{
		String source = "//comment\r\n\na = 10;";
		assertPartitions(source, "__js_singleline_comment:0:11", "__dftl_partition_content_type:11:");
	}

	public void testFlexComment3() throws BadLocationException
	{
		String source = "//comment\r\r\na = 10;";
		assertPartitions(source, "__js_singleline_comment:0:10", "__dftl_partition_content_type:10:");
	}

	public void testFlexMultiLineComment() throws BadLocationException
	{
		String source = "var /*comment*/ a=10;";
		assertPartitions(source, "__dftl_partition_content_type:0:4", "__js_multiline_comment:4:15",
				"__dftl_partition_content_type:15:");
	}

	public void testFlexSingleLineComment() throws BadLocationException
	{
		String source = "//comment";
		assertPartitions(source, "__js_singleline_comment:0:");
	}

	public void testFlexMultiLineComment2() throws BadLocationException
	{
		String source = "var /*comment\r\ncomment\rcomment\ncomment*/ a=10;";
		assertPartitions(source, "__dftl_partition_content_type:0:4", "__js_multiline_comment:4:40",
				"__dftl_partition_content_type:40:");
	}

	public void testFlexMultiLineCommentEmpty() throws BadLocationException
	{
		String source = "var /**/ a=10;";
		assertPartitions(source, "__dftl_partition_content_type:0:4", "__js_multiline_comment:4:8",
				"__dftl_partition_content_type:8:");
	}

	public void testFlexMultiLineCommentUnclosed() throws BadLocationException
	{
		String source = "var /*;";
		assertPartitions(source, "__dftl_partition_content_type:0:4", "__js_multiline_comment:4:");
	}

	public void testSDocCommentUnclosed() throws BadLocationException
	{
		String source = "var /**;";
		assertPartitions(source, "__dftl_partition_content_type:0:4", "__js_sdoc_comment:4:");
	}

	public void testFlexRegexp() throws BadLocationException
	{
		String source = "var regexp = /^\\/\\*-secure-([\\s\\S]*)\\*\\/\\s*$/;\n";

		assertPartitions(source, "__dftl_partition_content_type:0:13", "__js_regexp:13:45",
				"__dftl_partition_content_type:45:");
	}

	public void testFlexRegexpUnfinished() throws BadLocationException
	{
		String source = "var regexp = /^*";

		assertPartitions(source, "__dftl_partition_content_type:0:");
	}

	public void testFlexString() throws BadLocationException
	{
		String source = "var string = \"aaa\"";

		assertPartitions(source, "__dftl_partition_content_type:0:13", "__js_string_double:13:");
	}

	public void testFlexString2() throws BadLocationException
	{
		String source = "var string = 'aaa'";

		assertPartitions(source, "__dftl_partition_content_type:0:13", "__js_string_single:13:");
	}

	public void testFlexString3() throws BadLocationException
	{
		String source = "\"aa\"";

		assertPartitions(source, "__js_string_double:0:");
	}

	public void testFlexStringWithEscape() throws BadLocationException
	{
		String source = "\"a\\\"a\"";

		assertPartitions(source, "__js_string_double:0:");
	}

	public void testFlexStringWithEscape2() throws BadLocationException
	{
		String source = "'a\\'\"a'";

		assertPartitions(source, "__js_string_single:0:");
	}

	public void testFlexStringUnfinished() throws BadLocationException
	{
		String source = "var string = \"aaa\nvar b = 10";

		// Note: this is different in the offsets. The old parser reported offset 18 (adding new line)
		// which we don't do here.
		assertPartitions(source, "__dftl_partition_content_type:0:13", "__js_string_double:13:17",
				"__dftl_partition_content_type:17:");
	}

	public void testFlexStringUnfinished2() throws BadLocationException
	{
		String source = "var string = \"aaa\r\nvar b = 10";

		// Note: this is different in the offsets. The old parser reported offset 18 (adding new line)
		// which we don't do here.
		assertPartitions(source, "__dftl_partition_content_type:0:13", "__js_string_double:13:17",
				"__dftl_partition_content_type:17:");
	}

	public void testFlexStringUnfinished3() throws BadLocationException
	{
		String source = "var string = \"aaa\rvar b = 10";

		// Note: this is different in the offsets. The old parser reported offset 18 (adding new line)
		// which we don't do here.
		assertPartitions(source, "__dftl_partition_content_type:0:13", "__js_string_double:13:17",
				"__dftl_partition_content_type:17:");
	}

	public void testFlexComments() throws BadLocationException
	{
		String source = "10; /*co*/ var //comm";

		assertPartitions(source, "__dftl_partition_content_type:0:4", "__js_multiline_comment:4:10",
				"__dftl_partition_content_type:10:15", "__js_singleline_comment:15:");
	}

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

	protected String[] getContentTypes()
	{
		IPartitioningConfiguration configuration = JSSourceConfiguration.getDefault();
		String[] contentTypes = configuration.getContentTypes();
		return contentTypes;
	}

}
