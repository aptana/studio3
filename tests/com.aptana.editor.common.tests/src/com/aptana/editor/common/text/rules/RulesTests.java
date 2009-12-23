package com.aptana.editor.common.text.rules;

import junit.framework.Test;
import junit.framework.TestSuite;

public class RulesTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(RulesTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(TagRuleTest.class);
		suite.addTestSuite(RegexpRuleTest.class);
		suite.addTestSuite(WordDetectorTest.class);
		suite.addTestSuite(WhitespaceDetectorTest.class);
		suite.addTestSuite(SingleCharacterRuleTest.class);
		suite.addTestSuite(SingleTagRuleTest.class);
		//$JUnit-END$
		return suite;
	}

}
