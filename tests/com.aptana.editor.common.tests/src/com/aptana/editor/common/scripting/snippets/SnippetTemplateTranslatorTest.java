/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.scripting.snippets;

import org.junit.Test;
import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Position;

import com.aptana.scripting.model.SnippetElement;

public class SnippetTemplateTranslatorTest
{

	@Test
	public void testDollarZero()
	{
		assertTranslatesTo("$0", "${cursor}");
	}

	@Test
	public void testDollarZeroInBraces()
	{
		assertTranslatesTo("${0}", "${cursor}");
	}

	@Test
	public void testSimpleTabstop()
	{
		assertTranslatesTo("${1:name}", "${1:tabstop('name')}");
	}
	
	@Test
	public void testEscapedDollar()
	{
		assertTranslatesTo("\\${1:name}${0}", "$${1:name}${cursor}");
	}
	
	@Test
	public void testEscapedDollarAsValue()
	{
		assertTranslatesTo("${1:name \\$}${0}", "${1:tabstop('name $')}${cursor}");
	}
	
	@Test
	public void testTabstopWithSingleQuotedContent()
	{
		assertTranslatesTo("validates_presence_of :message => \"${4:can't be blank}\"", "validates_presence_of :message => \"${4:tabstop('can''t be blank')}\"");
	}

	@Test
	public void testtwoTabstopsWthoutBraces()
	{
		assertTranslatesTo("background-image: url($1);$0", "background-image: url(${1:tabstop('')});${cursor}");
	}

	@Test
	public void testPercentInTabstopValue()
	{
		assertTranslatesTo("font-size: ${1:100%};$0", "font-size: ${1:tabstop('100%')};${cursor}");
	}

	@Test
	public void testHyphenInTabstopValue()
	{
		assertTranslatesTo("${1:sans-}serif", "${1:tabstop('sans-')}serif");
	}

	@Test
	public void testExclamationInTabstopValue()
	{
		assertTranslatesTo("${2:!important}", "${2:tabstop('!important')}");

	}

	@Test
	public void testForwardSlashInterpretedAsListDelimeter()
	{
		// Interprets '/' as list delimeter
		assertTranslatesTo("${3:fixed/scroll}", "${3:tabstop('fixed','scroll')}");

	}

	@Test
	public void testEscapedForwardSlashIsntInterpretedAsListDelimeter()
	{
		// Doesn't interpret escaped '/' as list
		assertTranslatesTo("${3:fixed\\/scroll}", "${3:tabstop('fixed/scroll')}");
	}

	@Test
	public void testNumbersInTabstopValues()
	{
		assertTranslatesTo("margin: ${1:20px} ${2:0px} ${3:40px} ${4:0px};$0",
				"margin: ${1:tabstop('20px')} ${2:tabstop('0px')} ${3:tabstop('40px')} ${4:tabstop('0px')};${cursor}");
	}

	@Test
	public void testListsAndCursor()
	{
		assertTranslatesTo("background-attachment: ${1:scroll/fixed};$0",
				"background-attachment: ${1:tabstop('scroll','fixed')};${cursor}");
	}

	@Test
	public void testListsWithHyphensInIndividualValues()
	{
		assertTranslatesTo("background-repeat: ${1:repeat/repeat-x/repeat-y/no-repeat};$0",
				"background-repeat: ${1:tabstop('repeat','repeat-x','repeat-y','no-repeat')};${cursor}");
	}

	@Test
	public void testSpaceAndQuotesInTabstopValue()
	{
		assertTranslatesTo("font-family: ${1:Arial, \"MS Trebuchet\"}, ${2:sans-}serif;$0",
				"font-family: ${1:tabstop('Arial, \"MS Trebuchet\"')}, ${2:tabstop('sans-')}serif;${cursor}");
	}

	@Test
	public void testTwoLists()
	{
		assertTranslatesTo("font: ${1:normal/italic/oblique} ${2:normal/small-caps};$0",
				"font: ${1:tabstop('normal','italic','oblique')} ${2:tabstop('normal','small-caps')};${cursor}");
	}

	@Test
	public void testENVVariable()
	{
		assertTranslatesTo("$TM_SELECTED_TEXT", "${TM_SELECTED_TEXT:environment('')}");
	}

	@Test
	public void testENVVariableInBraces()
	{
		assertTranslatesTo("${TM_SELECTED_TEXT}", "${TM_SELECTED_TEXT:environment('')}");
	}

	@Test
	public void testENVVariableWithDefaultValue()
	{
		assertTranslatesTo("${TM_SELECTED_TEXT:in case there is no slection}",
				"${TM_SELECTED_TEXT:environment('in case there is no slection')}");
	}

	@Test
	public void testENVVariableWithMultipleDefaultValues()
	{
		assertTranslatesTo("${TM_SELECTED_TEXT:alt selection1/alt selection2}",
				"${TM_SELECTED_TEXT:environment('alt selection1','alt selection2')}");
	}

	// escapes!
	@Test
	public void testEscapedBackticks()
	{
		assertTranslatesTo("# vars = \\`find cookbooks\\`${0}", "# vars = `find cookbooks`${cursor}");
	}
	
	@Test
	public void testEscapedBackticksInsideTabstopValue()
	{
		assertTranslatesTo("${1:# vars = \\`find cookbooks\\`}", "${1:tabstop('# vars = `find cookbooks`')}");
	}

	@Test
	public void testEscapedForwardSlashes()
	{
		assertTranslatesTo("# vars = \\/\\/attributes\\/", "# vars = //attributes/");
	}

	@Test
	public void testEscapedCurlies()
	{
		assertTranslatesTo("# vars = {}", "# vars = {}");
	}

	@Test
	public void testEscapedEscapeChar()
	{
		assertTranslatesTo("# vars = \\\\;", "# vars = \\;");
	}

	@Test
	public void testMultipleEscapes()
	{
		assertTranslatesTo(
				"    # vars = \\`find cookbooks\\/\\/attributes\\/ -exec grep set_unless {} \\\\;\\`.split(\"\\n\")${0}",
				"    # vars = `find cookbooks//attributes/ -exec grep set_unless {} \\;`.split(\"\\n\")${cursor}");
	}

	@Test
	public void testEscapedEndCurlyInTabstopValueRR3_160()
	{
		assertTranslatesTo("${1:\\/*body {height:10em;\\}*\\/}", "${1:tabstop('/*body {height:10em;}*/')}");
	}

	@Test
	public void testMultilineContentInTabstopValue()
	{
		assertTranslatesTo("${1:# test.execute <<END\n" + "#   WHEN a > 2 THEN DO SOMETHING\n" + "# END\n" + "}",
				"${1:tabstop('# test.execute <<END\n" + "#   WHEN a > 2 THEN DO SOMETHING\n" + "# END\n" + "')}");
	}

	@Test
	public void testMultilineContentWithEscapedEndCurlyInTabstopValueRR3_160()
	{
		assertTranslatesTo("${1:# test.execute <<END\n" + "#   WHEN a > 2 THEN #{a\\}\n" + "# END\n" + "}",
				"${1:tabstop('# test.execute <<END\n" + "#   WHEN a > 2 THEN #{a}\n" + "# END\n" + "')}");
	}

	@Test
	public void testSnippetTemplateEvaluation()
	{
		SnippetElement snippet = new SnippetElement(null);
		snippet.setExpansion("font-size: ${1:100%};$0 ${2:two/2/twice} ${3} ${4:four}");
		snippet.setScope("source.js");
		Document document = new Document();
		String evaluation = SnippetTemplateUtil.evaluateSnippet(snippet, document, new Position(0));
		assertEquals("Snippet evaluated incorrectly", "font-size: 100%; two  four", evaluation);
	}

	private void assertTranslatesTo(String input, String expected)
	{
		assertEquals(expected, SnippetTemplateTranslator.processExpansion(input));
	}

}
