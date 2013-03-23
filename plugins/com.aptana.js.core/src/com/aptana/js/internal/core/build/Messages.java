/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.build;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.js.internal.core.build.messages"; //$NON-NLS-1$

	public static String JSLintValidator_ERR_FailToGetJSLint;

	public static String JSStyleValidator_AdsafeA;
	public static String JSStyleValidator_ALabel;
	public static String JSStyleValidator_AlreadyDefined;
	public static String JSStyleValidator_And;
	public static String JSStyleValidator_AScope;
	public static String JSStyleValidator_AssignException;
	public static String JSStyleValidator_AvoidA;
	public static String JSStyleValidator_BadAssignment;
	public static String JSStyleValidator_BadNew;
	public static String JSStyleValidator_BadNumber;
	public static String JSStyleValidator_BadWrap;
	public static String JSStyleValidator_CombineVar;
	public static String JSStyleValidator_ConditionalAssignment;
	public static String JSStyleValidator_ConstructorNameA;
	public static String JSStyleValidator_DangerousComment;
	public static String JSStyleValidator_DanglingA;
	public static String JSStyleValidator_EmptyBlock;
	public static String JSStyleValidator_EmptyClass;
	public static String JSStyleValidator_Evil;
	public static String JSStyleValidator_ExpectedNumberA;
	public static String JSStyleValidator_FunctionEval;
	public static String JSStyleValidator_ImpliedEvil;
	public static String JSStyleValidator_InsecureA;
	public static String JSStyleValidator_LeadingDecimalA;
	public static String JSStyleValidator_MissingA;
	public static String JSStyleValidator_MoveInvocation;
	public static String JSStyleValidator_NotAConstructor;
	public static String JSStyleValidator_NotGreater;
	public static String JSStyleValidator_ParameterArgumentsA;
	public static String JSStyleValidator_Radix;
	public static String JSStyleValidator_ReadOnly;
	public static String JSStyleValidator_ReservedA;
	public static String JSStyleValidator_StatementBlock;
	public static String JSStyleValidator_StrangeLoop;
	public static String JSStyleValidator_Subscript;
	public static String JSStyleValidator_Sync;
	public static String JSStyleValidator_TrailingDecimalA;
	public static String JSStyleValidator_UnexpectedA;
	public static String JSStyleValidator_UnnecessaryInitialize;
	public static String JSStyleValidator_UnreachableAB;
	public static String JSStyleValidator_UseArray;
	public static String JSStyleValidator_UseBraces;
	public static String JSStyleValidator_UsedBeforeA;
	public static String JSStyleValidator_UseObject;
	public static String JSStyleValidator_UseParam;
	public static String JSStyleValidator_VarANot;
	public static String JSStyleValidator_WeirdNew;
	public static String JSStyleValidator_WeirdProgram;
	public static String JSStyleValidator_WrapImmediate;
	public static String JSStyleValidator_WrapRegexp;
	public static String JSStyleValidator_WriteIsWrong;

	public static String NodeJSSourceContributor_Name;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
