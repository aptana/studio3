/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.junit.Test;

/**
 * Note: running all the tests from this class + tests from CSSSourcePartitionScannerTest
 */
public class CSSSourcePartitionScannerFlexTest extends CSSSourcePartitionScannerTest
// public class CSSSourcePartitionScannerFlexTest extends AbstractPartitionTestCase
{

	@Override
	protected IPartitionTokenScanner createPartitionScanner()
	{
		return new CSSSourcePartitionScannerJFlex();
		// return new CSSSourcePartitionScanner();
	}

	@Override
	protected String[] getContentTypes()
	{
		return CSSSourceConfiguration.getDefault().getContentTypes();
	}

	@Test
	public void testFlexPartitioning() throws BadLocationException
	{
		String source = " /* comment */ 'str' \"double\"\n";
		assertPartitions(source, "__dftl_partition_content_type:0:1", "__css_multiline_comment:1:14",
				"__dftl_partition_content_type:14:15", "__css_string_single:15:20",
				"__dftl_partition_content_type:20:21", "__css_string_double:21:29", "__dftl_partition_content_type:29:");
	}

	@Test
	public void testFlexPartitioningIncomplete() throws BadLocationException
	{
		String source = "'str\nfoo";
		assertPartitions(source, "__css_string_single:0:5", "__dftl_partition_content_type:5:");
	}

	@Test
	public void testFlexPartitioningIncomplete2() throws BadLocationException
	{
		String source = "\"str\nfoo";
		assertPartitions(source, "__css_string_double:0:5", "__dftl_partition_content_type:5:");
	}

	@Test
	public void testFlexCommentIncomplete() throws BadLocationException
	{
		String source = "/*comment\nfoo";
		assertPartitions(source, "__css_multiline_comment:0:");
	}

	@Test
	public void testFlexStringEscaping() throws BadLocationException
	{
		String source = "'aaa\\\nfoo' something";
		assertPartitions(source, "__css_string_single:0:10", "__dftl_partition_content_type:10:");
	}
}
