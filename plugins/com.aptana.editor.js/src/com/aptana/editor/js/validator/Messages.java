/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.validator;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.editor.js.validator.messages"; //$NON-NLS-1$

	public static String JSLintReplacementValidator_AdsafeA;
	public static String JSLintReplacementValidator_ALabel;
	public static String JSLintReplacementValidator_AlreadyDefined;

	public static String JSLintReplacementValidator_And;
	public static String JSLintReplacementValidator_AScope;
	public static String JSLintReplacementValidator_AssignException;
	public static String JSLintReplacementValidator_AvoidA;
	public static String JSLintReplacementValidator_BadAssignment;
	public static String JSLintReplacementValidator_BadNew;

	public static String JSLintReplacementValidator_BadNumber;

	public static String JSLintReplacementValidator_BadWrap;
	public static String JSLintReplacementValidator_ConditionalAssignment;

	public static String JSLintReplacementValidator_ConstructorNameA;
	public static String JSLintReplacementValidator_DangerousComment;

	public static String JSLintReplacementValidator_DanglingA;
	public static String JSLintReplacementValidator_EmptyBlock;

	public static String JSLintReplacementValidator_EmptyClass;
	public static String JSLintReplacementValidator_Evil;
	public static String JSLintReplacementValidator_ExpectedNumberA;
	public static String JSLintReplacementValidator_FunctionEval;
	public static String JSLintReplacementValidator_ImpliedEvil;
	public static String JSLintReplacementValidator_LeadingDecimalA;
	public static String JSLintReplacementValidator_NotAConstructor;
	public static String JSLintReplacementValidator_NotGreater;
	public static String JSLintReplacementValidator_ParameterArgumentsA;
	public static String JSLintReplacementValidator_Radix;
	public static String JSLintReplacementValidator_ReservedA;
	public static String JSLintReplacementValidator_StrangeLoop;

	public static String JSLintReplacementValidator_Subscript;
	public static String JSLintReplacementValidator_Sync;
	public static String JSLintReplacementValidator_TrailingDecimalA;
	public static String JSLintReplacementValidator_UnexpectedA;
	public static String JSLintReplacementValidator_UnnecessaryInitialize;

	public static String JSLintReplacementValidator_UnreachableAB;
	public static String JSLintReplacementValidator_UseArray;
	public static String JSLintReplacementValidator_UseBraces;
	public static String JSLintReplacementValidator_UsedBeforeA;
	public static String JSLintReplacementValidator_UseObject;
	public static String JSLintReplacementValidator_UseParam;
	public static String JSLintReplacementValidator_VarANot;
	public static String JSLintReplacementValidator_WeirdNew;

	public static String JSLintReplacementValidator_WeirdProgram;

	public static String JSLintReplacementValidator_WrapImmediate;

	public static String JSLintReplacementValidator_WriteIsWrong;
	public static String JSLintValidator_ERR_FailToGetJSLint;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
