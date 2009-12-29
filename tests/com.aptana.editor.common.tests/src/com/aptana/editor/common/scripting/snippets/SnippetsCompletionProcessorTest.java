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
		expectations.put("${1:name}", "${value1:1('name')}");
		expectations.put("font-size: ${1:100%};$0", "font-size: ${value1:1('100%')};${cursor}");
		expectations.put("${1:sans-}serif", "${value1:1('sans-')}serif");
		expectations.put("${2:!important}", "${value2:2('!important')}");
		expectations.put("${3:fixed/scroll}", "${choices3:3('fixed','scroll')}");
		expectations.put("margin: ${1:20px} ${2:0px} ${3:40px} ${4:0px};$0", "margin: ${value1:1('20px')} ${value2:2('0px')} ${value3:3('40px')} ${value4:4('0px')};${cursor}");
		expectations.put("background-attachment: ${1:scroll/fixed};$0", "background-attachment: ${choices1:1('scroll','fixed')};${cursor}");
		expectations.put("background-repeat: ${1:repeat/repeat-x/repeat-y/no-repeat};$0", "background-repeat: ${choices1:1('repeat','repeat-x','repeat-y','no-repeat')};${cursor}");
		expectations.put("font-family: ${1:Arial, \"MS Trebuchet\"}, ${2:sans-}serif;$0", "font-family: ${value1:1('Arial, \"MS Trebuchet\"')}, ${value2:2('sans-')}serif;${cursor}");
		for (Map.Entry<String, String> pair : expectations.entrySet())
		{
			assertEquals(pair.getValue(), SnippetsCompletionProcessor.processExpansion(pair.getKey()));
		}
	}

}
