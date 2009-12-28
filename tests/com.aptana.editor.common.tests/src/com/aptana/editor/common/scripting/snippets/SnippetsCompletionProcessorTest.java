package com.aptana.editor.common.scripting.snippets;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class SnippetsCompletionProcessorTest extends TestCase
{

	public void testProcessExpansion()
	{
		// TODO Add test for expansion with two different tab stops having multiple choices
		Map<String, String> expectations = new HashMap<String, String>();
		expectations.put("$0", "${cursor}");
		expectations.put("${0}", "${cursor}");
		expectations.put("${1:name}", "${name:1}");
		expectations.put("${2:!important}", "${!important:2}");
		expectations.put("${3:fixed/scroll}", "${choices:3('fixed','scroll')}");
		expectations.put("background-attachment: ${1:scroll/fixed};$0", "background-attachment: ${choices:1('scroll','fixed')};${cursor}");
		expectations.put("background-repeat: ${1:repeat/repeat-x/repeat-y/no-repeat};$0", "background-repeat: ${choices:1('repeat','repeat-x','repeat-y','no-repeat')};${cursor}");
		for (Map.Entry<String, String> pair : expectations.entrySet())
		{
			assertEquals(pair.getValue(), SnippetsCompletionProcessor.processExpansion(pair.getKey()));
		}
	}

}
