package com.aptana.editor.common.scripting.snippets;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class SnippetTemplateTranslatorTest extends TestCase
{

	public void testProcessExpansion()
	{
		Map<String, String> expectations = new HashMap<String, String>();
		expectations.put("$0", "${cursor}");
		expectations.put("${0}", "${cursor}");
		expectations.put("${1:name}", "${1:tabstop('name')}");
		expectations.put("background-image: url($1);$0", "background-image: url(${1:tabstop('')});${cursor}");
		expectations.put("font-size: ${1:100%};$0", "font-size: ${1:tabstop('100%')};${cursor}");
		expectations.put("${1:sans-}serif", "${1:tabstop('sans-')}serif");
		expectations.put("${2:!important}", "${2:tabstop('!important')}");
		// Interprets '/' as list delimeter
		expectations.put("${3:fixed/scroll}", "${3:tabstop('fixed','scroll')}");
		// Doesn't interpret escaped '/' as list
		expectations.put("${3:fixed\\/scroll}", "${3:tabstop('fixed/scroll')}");
		expectations.put("margin: ${1:20px} ${2:0px} ${3:40px} ${4:0px};$0",
				"margin: ${1:tabstop('20px')} ${2:tabstop('0px')} ${3:tabstop('40px')} ${4:tabstop('0px')};${cursor}");
		expectations.put("background-attachment: ${1:scroll/fixed};$0",
				"background-attachment: ${1:tabstop('scroll','fixed')};${cursor}");
		expectations.put("background-repeat: ${1:repeat/repeat-x/repeat-y/no-repeat};$0",
				"background-repeat: ${1:tabstop('repeat','repeat-x','repeat-y','no-repeat')};${cursor}");
		expectations.put("font-family: ${1:Arial, \"MS Trebuchet\"}, ${2:sans-}serif;$0",
				"font-family: ${1:tabstop('Arial, \"MS Trebuchet\"')}, ${2:tabstop('sans-')}serif;${cursor}");
		expectations
				.put(
						"font: ${1:normal/italic/oblique} ${2:normal/small-caps};$0",
						"font: ${1:tabstop('normal','italic','oblique')} ${2:tabstop('normal','small-caps')};${cursor}");
		expectations.put("$TM_SELECTED_TEXT", "${TM_SELECTED_TEXT:environment('')}");
		expectations.put("${TM_SELECTED_TEXT}", "${TM_SELECTED_TEXT:environment('')}");
		expectations.put("${TM_SELECTED_TEXT:in case there is no slection}", "${TM_SELECTED_TEXT:environment('in case there is no slection')}");
		expectations.put("${TM_SELECTED_TEXT:alt selection1/alt selection2}", "${TM_SELECTED_TEXT:environment('alt selection1','alt selection2')}");
		for (Map.Entry<String, String> pair : expectations.entrySet())
		{
			assertEquals(pair.getValue(), SnippetTemplateTranslator.processExpansion(pair.getKey()));
		}
	}

}
