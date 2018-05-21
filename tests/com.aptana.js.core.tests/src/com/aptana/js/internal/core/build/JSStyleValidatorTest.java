/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.build;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import com.aptana.buildpath.core.tests.AbstractValidatorTestCase;
import com.aptana.core.build.IBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.js.core.IJSConstants;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.parsing.JSParseState;

public class JSStyleValidatorTest extends JSLintValidatorTest
{

	@Override
	protected IBuildParticipant createValidator()
	{
		return new JSStyleValidator()
		{
			@Override
			protected String getPreferenceNode()
			{
				return JSCorePlugin.PLUGIN_ID;
			}

			@Override
			public String getId()
			{
				return ID;
			}

			@Override
			// Don't filter anything out
			public List<String> getFilters()
			{
				return Collections.emptyList();
			}

			@Override
			// Don't skip errors/warnings just because they're on same line!
			protected boolean hasErrorOrWarningOnLine(List<IProblem> items, int line)
			{
				return false;
			}
		};
	}
	
	@Override
	protected String getContentType()
	{
		return IJSConstants.CONTENT_TYPE_JS;
	}

	@Override
	protected String getFileExtension()
	{
		return "js";
	}

	protected List<IProblem> getParseErrors(String source) throws CoreException
	{
		return getParseErrors(source, new JSParseState(source), IJSConstants.JSLINT_PROBLEM_MARKER_TYPE);
	}

	// Warnings that we don't detect/implement yet!

	public void testALabel1() throws CoreException
	{
		// TODO Implement!
	}

	public void testAssignmentFunctionExpression() throws CoreException
	{
		// TODO Implement!
	}

	public void testBadConstructor() throws CoreException
	{
		// TODO Implement!
	}

	public void testBadInA() throws CoreException
	{
		// TODO Implement!
	}

	public void testBadInvocation1() throws CoreException
	{
		// TODO Implement!
	}

	public void testBadInvocation2() throws CoreException
	{
		// TODO Implement!
	}

	public void testBadOperand1() throws CoreException
	{
		// TODO Implement!
	}

	public void testBadOperand2() throws CoreException
	{
		// TODO Implement!
	}

	public void testConfusingABang1() throws CoreException
	{
		// TODO Implement!
	}

	public void testConfusingABang2() throws CoreException
	{
		// TODO Implement!
	}

	public void testConfusingAInfixMinusMinusMinus() throws CoreException
	{
		// TODO Implement!
	}

	public void testConfusingAInfixPlusPlusPlus() throws CoreException
	{
		// TODO Implement!
	}

	public void testConfusingAPrefixMinusMinusMinus() throws CoreException
	{
		// TODO Implement!
	}

	public void testConfusingAPrefixPlusPlusPlus() throws CoreException
	{
		// TODO Implement!
	}

	public void testConfusingRegexp1() throws CoreException
	{
		// TODO Implement!
	}

	public void testConfusingRegexp2() throws CoreException
	{
		// TODO Implement!
	}

	public void testDelete1() throws CoreException
	{
		// TODO Implement!
	}

	public void testDelete2() throws CoreException
	{
		// TODO Implement!
	}

	public void testDuplicateACase1() throws CoreException
	{
		// TODO Implement!
	}

	public void testDuplicateACase2() throws CoreException
	{
		// TODO Implement!
	}

	public void testDuplicateAGetterSetter() throws CoreException
	{
		// TODO Implement!
	}

	public void testDuplicateAJSONPropertyName() throws CoreException
	{
		// TODO Implement!
	}

	public void testEmptyCase() throws CoreException
	{
		// TODO Implement!
	}

	public void testES5() throws CoreException
	{
		// TODO Implement!
	}

	public void testES5_2() throws CoreException
	{
		// TODO Implement!
	}

	public void testExpectedAAtBC() throws CoreException
	{
		// TODO Implement!
	}

	public void testExpectedIdentifer() throws CoreException
	{
		// TODO Implement!
	}

	public void testForIf1() throws CoreException
	{
		// TODO Implement!
	}

	public void testForIf2() throws CoreException
	{
		// TODO Implement!
	}

	public void testForIf3() throws CoreException
	{
		// TODO Implement!
	}

	public void testForIf4() throws CoreException
	{
		// TODO Implement!
	}

	public void testForIf5() throws CoreException
	{
		// TODO Implement!
	}

	public void testForIf6() throws CoreException
	{
		// TODO Implement!
	}

	public void testForIf7() throws CoreException
	{
		// TODO Implement!
	}

	public void testForIf8() throws CoreException
	{
		// TODO Implement!
	}

	public void testFunctionBlock1() throws CoreException
	{
		// TODO Implement!
	}

	public void testFunctionLoop1() throws CoreException
	{
		// TODO Implement!
	}

	public void testFunctionLoop2() throws CoreException
	{
		// TODO Implement!
	}

	public void testFunctionStatement() throws CoreException
	{
		// TODO Implement!
	}

	public void testFunctionStrict1() throws CoreException
	{
		// TODO Implement!
	}

	public void testFunctionStrict2() throws CoreException
	{
		// TODO Implement!
	}

	public void testHTMLConfusionA1() throws CoreException
	{
		// TODO Implement!
	}

	public void testHTMLConfusionA2() throws CoreException
	{
		// TODO Implement!
	}

	public void testHTMLConfusionA3() throws CoreException
	{
		// TODO Implement!
	}

	public void testInfixIn() throws CoreException
	{
		// TODO Implement!
	}

	public void testIsNaN1() throws CoreException
	{
		// TODO Implement!
	}

	public void testIsNaN2() throws CoreException
	{
		// TODO Implement!
	}

	public void testIsNaN3() throws CoreException
	{
		// TODO Implement!
	}

	public void testIsNaN4() throws CoreException
	{
		// TODO Implement!
	}

	public void testIsNaN5() throws CoreException
	{
		// TODO Implement!
	}

	public void testIsNaN6() throws CoreException
	{
		// TODO Implement!
	}

	public void testIsNaN7() throws CoreException
	{
		// TODO Implement!
	}

	public void testIsNaN8() throws CoreException
	{
		// TODO Implement!
	}

	public void testMissingA3() throws CoreException
	{
		// TODO Implement! Our parser doesn't support getter/setter syntax yet!
	}

	public void testMissingProperty() throws CoreException
	{
		// TODO Implement! Our parser doesn't support getter/setter syntax yet!
	}

	public void testMoveInvocation() throws CoreException
	{
		// TODO Implement!
	}

	public void testMoveVar() throws CoreException
	{
		// TODO Implement!
	}

	public void testNestedComment() throws CoreException
	{
		// TODO Implement!
	}

	public void testNotAFunction1() throws CoreException
	{
		// TODO Implement!
	}

	public void testNotALabel1() throws CoreException
	{
		// TODO Implement!
	}

	public void testNotALabel2() throws CoreException
	{
		// TODO Implement!
	}

	public void testOctalA() throws CoreException
	{
		// TODO Implement!
	}

	public void testParameterAGetB() throws CoreException
	{
		// TODO Implement!
	}

	public void testParameterSetA() throws CoreException
	{
		// TODO Implement!
	}

	public void testParameterSetA2() throws CoreException
	{
		// TODO Implement!
	}

	public void testReadOnly() throws CoreException
	{
		// TODO Implement!
	}

	public void testSlashEqual() throws CoreException
	{
		// TODO Implement!
	}

	public void testStrict1() throws CoreException
	{
		// TODO Implement!
	}

	public void testStrict2() throws CoreException
	{
		// TODO Implement!
	}

	public void testUnexpectedAMinusMinus() throws CoreException
	{
		// TODO Implement!
	}

	public void testUnexpectedAPlusPlus() throws CoreException
	{
		// TODO Implement!
	}

	public void testUnexpectedAPlusPlus2() throws CoreException
	{
		// TODO Implement!
	}

	public void testUnexpectedPropertyA1() throws CoreException
	{
		// TODO Implement!
	}

	public void testUnexpectedPropertyA2() throws CoreException
	{
		// TODO Implement!
	}

	public void testUnnecessaryUseStrict1() throws CoreException
	{
		// TODO Implement!
	}

	public void testURL() throws CoreException
	{
		// TODO Implement!
	}

	public void testURL2() throws CoreException
	{
		// TODO Implement!
	}

	public void testURL3() throws CoreException
	{
		// TODO Implement!
	}

	public void testURL4() throws CoreException
	{
		// TODO Implement!
	}

	public void testURL5() throws CoreException
	{
		// TODO Implement!
	}

	public void testURL6() throws CoreException
	{
		// TODO Implement!
	}

	public void testURL7() throws CoreException
	{
		// TODO Implement!
	}

	public void testURL8() throws CoreException
	{
		// TODO Implement!
	}

	public void testUseOr() throws CoreException
	{
		// TODO Implement!
	}

	public void testuseStrict1() throws CoreException
	{
		// TODO Implement!
	}

	public void testuseStrict2() throws CoreException
	{
		// TODO Implement!
	}

	public void testWeirdAssignment() throws CoreException
	{
		// TODO Implement!
	}

	public void testWeirdCondition1() throws CoreException
	{
		// TODO Implement!
	}

	public void testWeirdCondition2() throws CoreException
	{
		// TODO Implement!
	}

	public void testWeirdCondition3() throws CoreException
	{
		// TODO Implement!
	}

	public void testWeirdCondition4() throws CoreException
	{
		// TODO Implement!
	}

	public void testWeirdRelation1() throws CoreException
	{
		// TODO Implement!
	}

	public void testWeirdRelation2() throws CoreException
	{
		// TODO Implement!
	}

	public void testWeirdRelation3() throws CoreException
	{
		// TODO Implement!
	}

	public void testWeirdRelation4() throws CoreException
	{
		// TODO Implement!
	}

	public void testWeirdRelation5() throws CoreException
	{
		// TODO Implement!
	}

	public void testWeirdRelation6() throws CoreException
	{
		// TODO Implement!
	}

	public void testWeirdRelation7() throws CoreException
	{
		// TODO Implement!
	}

	public void testWeirdRelation8() throws CoreException
	{
		// TODO Implement!
	}

	public void testWeirdTernary1() throws CoreException
	{
		// TODO Implement!
	}

	public void testWeirdTernary2() throws CoreException
	{
		// TODO Implement!
	}

	protected void setOption(String optionName, boolean value)
	{
		((JSStyleValidator) fValidator).setOption(optionName, value);
	}

	// Tests we are intentionally skipping -----------------------

	public void testExpectedIdentiferAReserved1() throws CoreException
	{
		// we don't so this, because we generate a parse error in parser
	}

	public void testExpectedIdentiferAReserved2() throws CoreException
	{
		// we don't so this, because we generate a parse error in parser
	}

	public void testNameFunction() throws CoreException
	{
		// we don't so this, because we generate a parse error in parser
	}

	public void testBadNameA1() throws CoreException
	{
		// we don't do this because it's an HTML check
	}

	public void testBadNameA2() throws CoreException
	{
		// we don't do this because it's an HTML check
	}

	public void testUnrecognizedStyleAttributeA() throws CoreException
	{
		// skip CSS in HTML check
	}

	public void testUnrecognizedTagA() throws CoreException
	{
		// skip HTML check
	}

	public void testNestedNot() throws CoreException
	{
		// skip CSS check
	}

	@SuppressWarnings("nls")
	@Test
	public void testHexNumber() throws CoreException
	{
		String text = "var i = 0x9999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999;";

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(
				items,
				"Bad number '0x9999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999'.",
				1, IMarker.SEVERITY_WARNING, 1550);
	}

	@SuppressWarnings("nls")
	@Test
	public void testOctalNumber() throws CoreException
	{
		String text = "var i = 07777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777;";

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(
				items,
				"Bad number '07777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777'.",
				1, IMarker.SEVERITY_WARNING, 1549);
	}

}
