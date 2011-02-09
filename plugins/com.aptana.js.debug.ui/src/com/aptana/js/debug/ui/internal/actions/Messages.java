/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.actions;

import org.eclipse.osgi.util.NLS;

/**
 * @author Ingo Muschenetz
 */
public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.js.debug.ui.internal.actions.messages"; //$NON-NLS-1$

	private Messages() {
	}

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * OpenScriptSourceAction_GoToFile
	 */
	public static String OpenScriptSourceAction_GoToFile;

	/**
	 * OpenScriptSourceAction_GoToFileForScript
	 */
	public static String OpenScriptSourceAction_GoToFileForScript;

	/**
	 * OpenScriptSourceAction_Information
	 */
	public static String OpenScriptSourceAction_Information;

	/**
	 * OpenScriptSourceAction_SourceNotFoundFor_0
	 */
	public static String OpenScriptSourceAction_SourceNotFoundFor_0;

	/**
	 * OpenScriptSourceAction_ExceptionWhileOpeningScriptSource
	 */
	public static String OpenScriptSourceAction_ExceptionWhileOpeningScriptSource;

	public static String OpenURLAction_Open_URL;

	public static String OpenURLAction_Specify_URL_To_Open;

	/**
	 * BreakpointHitCountAction_EnableHitCount
	 */
	public static String BreakpointHitCountAction_EnableHitCount;

	/**
	 * BreakpointHitCountAction_SetBreakpointHitCount
	 */
	public static String BreakpointHitCountAction_SetBreakpointHitCount;

	/**
	 * BreakpointHitCountAction_EnterNewHitCountForBreakpoint
	 */
	public static String BreakpointHitCountAction_EnterNewHitCountForBreakpoint;

	/**
	 * BreakpointHitCountAction_HitCountPositiveInteger
	 */
	public static String BreakpointHitCountAction_HitCountPositiveInteger;

	/**
	 * BreakpointHitCountAction_ExceptionAttemptingToSetHitCount
	 */
	public static String BreakpointHitCountAction_ExceptionAttemptingToSetHitCount;

	/**
	 * WatchAction_CreateWatchExpressionFailed
	 */
	public static String WatchAction_CreateWatchExpressionFailed;

	/**
	 * ToggleBreakpointAdapter_ToggleLineBreakpoint
	 */
	public static String ToggleBreakpointAdapter_ToggleLineBreakpoint;

	/**
	 * AddExceptionBreakpointAction_AddJavaScriptExceptionBreakpoint
	 */
	public static String AddExceptionBreakpointAction_AddJavaScriptExceptionBreakpoint;

	/**
	 * AddExceptionBreakpointAction_ChooseException
	 */
	public static String AddExceptionBreakpointAction_ChooseException;

	/**
	 * RunToLineAdapter_EmptyEditor
	 */
	public static String RunToLineAdapter_EmptyEditor;

	/**
	 * RunToLineAdapter_MissingDocument
	 */
	public static String RunToLineAdapter_MissingDocument;

	/**
	 * RunToLineAdapter_UnableToLocateDebugTarget
	 */
	public static String RunToLineAdapter_UnableToLocateDebugTarget;

	/**
	 * RunToLineAdapter_SelectedLineIsNotValidLocationToRunTo
	 */
	public static String RunToLineAdapter_SelectedLineIsNotValidLocationToRunTo;

	/**
	 * RunToLineAdapter_CursorPositionIsNotValidLocationToRunTo
	 */
	public static String RunToLineAdapter_CursorPositionIsNotValidLocationToRunTo;

	/**
	 * BreakpointPropertiesRulerAction_BreakpointProperties
	 */
	public static String BreakpointPropertiesRulerAction_BreakpointProperties;
}
